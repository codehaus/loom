/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.components.util.verifier;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.activity.Suspendable;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.component.Recomposable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Reconfigurable;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.context.Recontextualizable;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Reparameterizable;
import org.apache.avalon.framework.service.Serviceable;
import org.realityforge.metaclass.Attributes;
import org.realityforge.metaclass.model.Attribute;
import org.codehaus.spice.salt.i18n.ResourceManager;
import org.codehaus.spice.salt.i18n.Resources;

/**
 * Utility class to help verify that component respects the rules of an Avalon
 * component.
 *
 * @author Peter Donald
 * @version $Revision: 1.3 $ $Date: 2003-12-03 02:54:43 $
 */
public class ComponentVerifier
{
    /** I18n utils. */
    private static final Resources REZ =
        ResourceManager.getPackageResources( ComponentVerifier.class );

    /**
     * Constant for array of 0 classes. Saves recreating array everytime look up
     * constructor with no args.
     */
    private static final Class[] EMPTY_TYPES = new Class[ 0 ];

    /** The interfaces representing lifecycle stages. */
    private static final Class[] FRAMEWORK_CLASSES = new Class[]
    {
        LogEnabled.class,
        Contextualizable.class,
        Recontextualizable.class,
        Composable.class,
        Recomposable.class,
        Serviceable.class,
        Configurable.class,
        Reconfigurable.class,
        Parameterizable.class,
        Reparameterizable.class,
        Initializable.class,
        Startable.class,
        Suspendable.class,
        Disposable.class
    };

    /**
     * Verfiy that specified components designate classes that implement the
     * advertised interfaces.
     *
     * @param name the name of component
     * @param type the component type
     * @throws java.lang.Exception if an error occurs
     */
    public void verifyType( final String name, final Class type )
        throws Exception
    {
        final Attribute attribute =
            Attributes.getAttribute( type, "dna.component" );
        if( null == attribute )
        {
            final String message =
                "Component " + name + " does not specify correct metadata";
            throw new Exception( message );
        }
        final Class[] interfaces = getServiceClasses( name, type );
        verifyComponent( name, type, interfaces, false );
    }

    /**
     * Verify that the supplied implementation class and service classes are
     * valid for a component.
     *
     * @param name the name of component
     * @param implementation the implementation class of component
     * @param services the classes representing services
     * @param buildable if true will verify that it is instantiateable via
     * class.newInstance(). May not be required for some components that are
     * created via a factory.
     * @throws java.lang.Exception if error thrown on failure and component
     * fails check
     */
    public void verifyComponent( final String name,
                                 final Class implementation,
                                 final Class[] services,
                                 final boolean buildable )
        throws Exception
    {
        if( buildable )
        {
            verifyClass( name, implementation );
        }
        verifyLifecycles( name, implementation );
        verifyServices( name, services );
        verifyImplementsServices( name, implementation, services );
    }

    /**
     * Verify that the supplied implementation implements the specified
     * services.
     *
     * @param name the name of component
     * @param implementation the class representign component
     * @param services the services that the implementation must provide
     * @throws java.lang.Exception if error thrown on failure and component
     * fails check
     */
    public void verifyImplementsServices( final String name,
                                          final Class implementation,
                                          final Class[] services )
        throws Exception
    {
        for( int i = 0; i < services.length; i++ )
        {
            if( !services[ i ].isAssignableFrom( implementation ) )
            {
                final String message =
                    REZ.format( "verifier.noimpl-service.error",
                                name,
                                implementation.getName(),
                                services[ i ].getName() );
                throw new Exception( message );
            }
        }
    }

    /**
     * Verify that the supplied class is a valid class for a Component.
     *
     * @param name the name of component
     * @param clazz the class representing component
     * @throws java.lang.Exception if error thrown on failure and component
     * fails check
     */
    public void verifyClass( final String name,
                             final Class clazz )
        throws Exception
    {
        verifyNoArgConstructor( name, clazz );
        verifyNonAbstract( name, clazz );
        verifyNonArray( name, clazz );
        verifyNonInterface( name, clazz );
        verifyNonPrimitive( name, clazz );
        verifyPublic( name, clazz );
    }

    /**
     * Verify that the supplied classes are valid classes for a service.
     *
     * @param name the name of component
     * @param classes the classes representign services
     * @throws java.lang.Exception if error thrown on failure and component
     * fails check
     */
    public void verifyServices( final String name,
                                final Class[] classes )
        throws Exception
    {
        for( int i = 0; i < classes.length; i++ )
        {
            verifyService( name, classes[ i ] );
        }
    }

    /**
     * Verify that the supplied class is a valid class for a service.
     *
     * @param name the name of component
     * @param clazz the class representign service
     * @throws java.lang.Exception if error thrown on failure and component
     * fails check
     */
    public void verifyService( final String name,
                               final Class clazz )
        throws Exception
    {
        verifyServiceIsaInterface( name, clazz );
        verifyServiceIsPublic( name, clazz );
        verifyServiceNotALifecycle( name, clazz );
    }

    /**
     * Verify that the implementation class does not implement incompatible
     * lifecycle interfaces.
     *
     * @param name the name of component
     * @param implementation the implementation class
     * @throws java.lang.Exception if error thrown on failure and component
     * fails check
     */
    public void verifyLifecycles( final String name,
                                  final Class implementation )
        throws Exception
    {
        final boolean composable =
            Composable.class.isAssignableFrom( implementation ) ||
            Recomposable.class.isAssignableFrom( implementation );
        final boolean serviceable = Serviceable.class.isAssignableFrom(
            implementation );
        if( serviceable && composable )
        {
            final String message =
                REZ.format( "verifier.incompat-serviceable.error",
                            name,
                            implementation.getName() );
            throw new Exception( message );
        }

        final boolean configurable =
            Configurable.class.isAssignableFrom( implementation ) ||
            Reconfigurable.class.isAssignableFrom( implementation );
        final boolean parameterizable =
            Parameterizable.class.isAssignableFrom( implementation ) ||
            Reparameterizable.class.isAssignableFrom( implementation );
        if( parameterizable && configurable )
        {
            final String message =
                REZ.format( "verifier.incompat-config.error",
                            name,
                            implementation.getName() );
            throw new Exception( message );
        }
    }

    /**
     * Verify that the service implemented by specified component is an
     * interface.
     *
     * @param name the name of component
     * @param clazz the class representign service
     * @throws java.lang.Exception if error thrown on failure and component
     * fails check
     */
    public void verifyServiceIsaInterface( final String name,
                                           final Class clazz )
        throws Exception
    {
        if( !clazz.isInterface() )
        {
            final String message =
                REZ.format( "verifier.non-interface-service.error",
                            name,
                            clazz.getName() );
            throw new Exception( message );
        }
    }

    /**
     * Verify that the service implemented by specified component is public.
     *
     * @param name the name of component
     * @param clazz the class representign service
     * @throws java.lang.Exception if error thrown on failure and component
     * fails check
     */
    public void verifyServiceIsPublic( final String name,
                                       final Class clazz )
        throws Exception
    {
        final boolean isPublic =
            Modifier.isPublic( clazz.getModifiers() );
        if( !isPublic )
        {
            final String message =
                REZ.format( "verifier.non-public-service.error",
                            name,
                            clazz.getName() );
            throw new Exception( message );
        }
    }

    /**
     * Verify that the service implemented by specified component does not
     * extend any lifecycle interfaces.
     *
     * @param name the name of component
     * @param clazz the class representign service
     * @throws java.lang.Exception if error thrown on failure and component
     * fails check
     */
    public void verifyServiceNotALifecycle( final String name,
                                            final Class clazz )
        throws Exception
    {
        for( int i = 0; i < FRAMEWORK_CLASSES.length; i++ )
        {
            final Class lifecycle = FRAMEWORK_CLASSES[ i ];
            if( lifecycle.isAssignableFrom( clazz ) )
            {
                final String message =
                    REZ.format( "verifier.service-isa-lifecycle.error",
                                name,
                                clazz.getName(),
                                lifecycle.getName() );
                throw new Exception( message );
            }
        }
    }

    /**
     * Verify that the component has a no-arg aka default constructor.
     *
     * @param name the name of component
     * @param clazz the class representign component
     * @throws java.lang.Exception if error thrown on failure and component
     * fails check
     */
    public void verifyNoArgConstructor( final String name,
                                        final Class clazz )
        throws Exception
    {
        try
        {
            final Constructor ctor = clazz.getConstructor( EMPTY_TYPES );
            if( !Modifier.isPublic( ctor.getModifiers() ) )
            {
                final String message =
                    REZ.format( "verifier.non-public-ctor.error",
                                name,
                                clazz.getName() );
                throw new Exception( message );
            }
        }
        catch( final NoSuchMethodException nsme )
        {
            final String message =
                REZ.format( "verifier.missing-noargs-ctor.error",
                            name,
                            clazz.getName() );
            throw new Exception( message );
        }
    }

    /**
     * Verify that the component is not represented by abstract class.
     *
     * @param name the name of component
     * @param clazz the class representign component
     * @throws java.lang.Exception if error thrown on failure and component
     * fails check
     */
    public void verifyNonAbstract( final String name,
                                   final Class clazz )
        throws Exception
    {
        final boolean isAbstract =
            Modifier.isAbstract( clazz.getModifiers() );
        if( isAbstract )
        {
            final String message =
                REZ.format( "verifier.abstract-class.error",
                            name,
                            clazz.getName() );
            throw new Exception( message );
        }
    }

    /**
     * Verify that the component is not represented by abstract class.
     *
     * @param name the name of component
     * @param clazz the class representign component
     * @throws java.lang.Exception if error thrown on failure and component
     * fails check
     */
    public void verifyPublic( final String name,
                              final Class clazz )
        throws Exception
    {
        final boolean isPublic =
            Modifier.isPublic( clazz.getModifiers() );
        if( !isPublic )
        {
            final String message =
                REZ.format( "verifier.nonpublic-class.error",
                            name,
                            clazz.getName() );
            throw new Exception( message );
        }
    }

    /**
     * Verify that the component is not represented by primitive class.
     *
     * @param name the name of component
     * @param clazz the class representign component
     * @throws java.lang.Exception if error thrown on failure and component
     * fails check
     */
    public void verifyNonPrimitive( final String name,
                                    final Class clazz )
        throws Exception
    {
        if( clazz.isPrimitive() )
        {
            final String message =
                REZ.format( "verifier.primitive-class.error",
                            name,
                            clazz.getName() );
            throw new Exception( message );
        }
    }

    /**
     * Verify that the component is not represented by interface class.
     *
     * @param name the name of component
     * @param clazz the class representign component
     * @throws java.lang.Exception if error thrown on failure and component
     * fails check
     */
    public void verifyNonInterface( final String name,
                                    final Class clazz )
        throws Exception
    {
        if( clazz.isInterface() )
        {
            final String message =
                REZ.format( "verifier.interface-class.error",
                            name,
                            clazz.getName() );
            throw new Exception( message );
        }
    }

    /**
     * Verify that the component is not represented by an array class.
     *
     * @param name the name of component
     * @param clazz the class representign component
     * @throws java.lang.Exception if error thrown on failure and component
     * fails check
     */
    public void verifyNonArray( final String name,
                                final Class clazz )
        throws Exception
    {
        if( clazz.isArray() )
        {
            final String message =
                REZ.format( "verifier.array-class.error",
                            name,
                            clazz.getName() );
            throw new Exception( message );
        }
    }

    /**
     * Retrieve an array of Classes for all the services that a Component
     * offers. This method also makes sure all services offered are interfaces.
     *
     * @param name the name of component
     * @param type the component type
     * @return an array of Classes for all the services
     * @throws java.lang.Exception if an error occurs
     */
    protected Class[] getServiceClasses( final String name,
                                         final Class type )
        throws Exception
    {
        final ClassLoader classLoader = type.getClassLoader();
        final Attribute[] attributes =
            Attributes.getAttributes( type, "dna.service" );
        final Class[] classes = new Class[ attributes.length ];
        for( int i = 0; i < attributes.length; i++ )
        {
            final String classname = attributes[ i ].getParameter( "type" );
            try
            {
                classes[ i ] = classLoader.loadClass( classname );
            }
            catch( final Throwable t )
            {
                final String message =
                    "Unable to load service class \"" +
                    classname +
                    "\" for Component named \"" +
                    name + "\". (Reason: " + t + ").";
                throw new Exception( message );
            }
        }

        return classes;
    }
}
