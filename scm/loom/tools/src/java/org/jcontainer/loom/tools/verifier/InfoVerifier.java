/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.tools.verifier;

import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.service.Serviceable;
import org.jcontainer.loom.tools.info.ComponentInfo;
import org.jcontainer.loom.tools.info.ContextDescriptor;
import org.jcontainer.loom.tools.info.SchemaDescriptor;
import org.jcontainer.loom.tools.info.ServiceDescriptor;
import org.jcontainer.dna.AbstractLogEnabled;
import org.jcontainer.dna.Logger;
import org.realityforge.salt.i18n.ResourceManager;
import org.realityforge.salt.i18n.Resources;

/**
 * This Class verifies that an implementation is valid wrt the
 * ComponentMetaData. It performs a number of checks to make sure
 * that the implementation class is consistent with ComponentMetaData.
 * Some of the checks it performs include;
 *
 * <ul>
 *   <li>Verify that the Class objects for Component implement the
 *       service interfaces.</li>
 *   <li>Verify that the Class is a valid Avalon Component as per the
 *       rules in {@link org.jcontainer.loom.tools.verifier.ComponentVerifier} object.</li>
 *   <li>Verify that the Class is Composable/Serviceable if and only if
 *       dependencies are declared.</li>
 *   <li>Verify that the Class is Contextualizable if any context
 *       entrys are declared.</li>
 *   <li>Verify that the Class is {@link org.apache.avalon.framework.configuration.Configurable} if a Configuration
 *       schema is are declared.</li>
 *   <li>Verify that the Class is {@link org.apache.avalon.framework.parameters.Parameterizable} if a Parameters
 *       schema is are declared.</li>
 * </ul>
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.7 $ $Date: 2003-10-05 10:07:05 $
 */
public class InfoVerifier
    extends AbstractLogEnabled
{
    /**
     * I18n utils.
     */
    private static final Resources REZ =
        ResourceManager.getPackageResources( InfoVerifier.class );

    /**
     * The verifier for components in assembly.
     */
    private final ComponentVerifier m_verifier;

    /**
     * Create an InfoVerifier using base Componern ComponentVerifier.
     */
    public InfoVerifier()
    {
        this( new ComponentVerifier() );
    }

    /**
     * Create an AssemblyVerifier using specified Component ComponentVerifier.
     */
    public InfoVerifier( final ComponentVerifier verifier )
    {
        if( null == verifier )
        {
            throw new NullPointerException( "verifier" );
        }

        m_verifier = verifier;
    }

    public void enableLogging( final Logger logger )
    {
        super.enableLogging( logger );
        setupLogger( m_verifier );
    }

    /**
     * Verfiy that specified components designate classes that implement the
     * advertised interfaces. And confrorm to expectations of {@link org.jcontainer.loom.tools.info.ComponentInfo}.
     *
     * @param name the name of component
     * @param implementationKey the implementationKey of component
     * @param classLoader the ClassLoader to load component from
     * @throws org.jcontainer.loom.tools.verifier.VerifyException if an error occurs
     */
    public void verifyType( final String name,
                            final String implementationKey,
                            final ComponentInfo info,
                            final ClassLoader classLoader )
        throws VerifyException
    {
        final Class clazz = getClass( classLoader, name, implementationKey );
        verifyType( name, implementationKey, info, clazz );
    }

    /**
     * Verfiy that specified components designate classes that implement the
     * advertised interfaces. And confrorm to expectations of {@link org.jcontainer.loom.tools.info.ComponentInfo}.
     *
     * @param name the name of component
     * @param implementationKey the implementationKey of component
     * @throws org.jcontainer.loom.tools.verifier.VerifyException if an error occurs
     */
    public void verifyType( final String name,
                            final String implementationKey,
                            final ComponentInfo info,
                            final Class implementation )
        throws VerifyException
    {
        final Class[] interfaces =
            getServiceClasses( name,
                               info.getServices(),
                               implementation.getClassLoader() );

        m_verifier.verifyComponent( name, implementation, interfaces, false );

        verifyDependencyPresence( name, implementationKey, info, implementation );
        verifyContextPresence( name, implementationKey, info, implementation );
        verifyConfigurationSchemaPresence( name, implementationKey, info, implementation );
        //verifyParametersSchemaPresence( name, implementationKey, info, implementation );
    }

    /**
     * Verify that the if  the component is not
     * Configurable that it does not declare configuraiton schema.
     *
     * @param name the name of component
     * @param implementationKey the implementationKey of component
     * @param implementation the class implementing component
     * @throws org.jcontainer.loom.tools.verifier.VerifyException if fails verification check
     */
    protected void verifyConfigurationSchemaPresence( final String name,
                                                      final String implementationKey,
                                                      final ComponentInfo info,
                                                      final Class implementation )
        throws VerifyException
    {
        final SchemaDescriptor schema = info.getConfigurationSchema();
        if( null == schema )
        {
            return;
        }
        else
        {
            if( !Configurable.class.isAssignableFrom( implementation ) )
            {
                final String message =
                    REZ.format( "metadata.declare-uneeded-configuration-schema.error",
                                name,
                                implementationKey );
                throw new VerifyException( message );
            }
        }
    }

    /**
     * Verify that the if  the component is not
     * Parameterizable that it does not declare parameters schema.
     *
     * @param name the name of component
     * @param implementationKey the implementationKey of component
     * @param implementation the class implementing component
     * @throws org.jcontainer.loom.tools.verifier.VerifyException if fails verification check
     */
/*
    protected void verifyParametersSchemaPresence( final String name,
                                                   final String implementationKey,
                                                   final ComponentInfo info,
                                                   final Class implementation )
        throws VerifyException
    {
        final SchemaDescriptor schema = info.getParametersSchema();
        if( null == schema )
        {
            return;
        }
        else
        {
            if( !Parameterizable.class.isAssignableFrom( implementation ) )
            {
                final String message =
                    REZ.format( "metadata.declare-uneeded-parameter-schema.error",
                                name,
                                implementationKey );
                throw new VerifyException( message );
            }
        }
    }
*/

    /**
     * Verify that the if  the component is not Contextualizable that it
     * does not declare Context Entrys.
     *
     * @param name the name of component
     * @param implementationKey the implementationKey of component
     * @param implementation the class implementing component
     * @throws org.jcontainer.loom.tools.verifier.VerifyException if fails verification check
     */
    protected void verifyContextPresence( final String name,
                                          final String implementationKey,
                                          final ComponentInfo info,
                                          final Class implementation )
        throws VerifyException
    {
        final ContextDescriptor context = info.getContext();
        final int count = context.getEntrys().length;

        if( !Contextualizable.class.isAssignableFrom( implementation ) )
        {
            if( 0 != count )
            {
                final String message =
                    REZ.format( "metadata.declare-uneeded-entrys.error",
                                name,
                                implementationKey );
                throw new VerifyException( message );
            }
        }
    }

    /**
     * Verify the component assembly logic.
     * The implications verifies that the component:
     * <p>Is not Composable/Serviceable and does not declare dependencys</p>
     * <p>or</p>
     * <p>Is Composable/Serviceable and does declare dependencys</p>
     *
     * @param name the name of component
     * @param implementationKey the implementationKey of component
     * @param implementation the class implementing component
     * @throws org.jcontainer.loom.tools.verifier.VerifyException if fails verification check
     */
    protected void verifyDependencyPresence( final String name,
                                             final String implementationKey,
                                             final ComponentInfo info,
                                             final Class implementation )
        throws VerifyException
    {
        final int count = info.getDependencies().length;
        final boolean aquiresServices =
            Composable.class.isAssignableFrom( implementation ) ||
            Serviceable.class.isAssignableFrom( implementation );
        if( !aquiresServices )
        {
            if( 0 != count )
            {
                final String message =
                    REZ.format( "metadata.declare-uneeded-deps.error",
                                name,
                                implementationKey );
                throw new VerifyException( message );
            }
        }
    }

    /**
     * Retrieve an array of Classes for all the services that a Component
     * offers. This method also makes sure all services offered are
     * interfaces.
     *
     * @param name the name of component
     * @param services the services the component offers
     * @param classLoader the classLoader
     * @return an array of Classes for all the services
     * @throws org.jcontainer.loom.tools.verifier.VerifyException if an error occurs
     */
    protected Class[] getServiceClasses( final String name,
                                         final ServiceDescriptor[] services,
                                         final ClassLoader classLoader )
        throws VerifyException
    {
        final Class[] classes = new Class[ services.length ];
        for( int i = 0; i < services.length; i++ )
        {
            final String classname = services[ i ].getType();
            try
            {
                classes[ i ] = classLoader.loadClass( classname );
            }
            catch( final Throwable t )
            {
                final String message =
                    REZ.format( "metadata.bad-service-class.error",
                                name,
                                classname,
                                t.toString() );
                throw new VerifyException( message, t );
            }
        }

        return classes;
    }

    /**
     * Load class object for specified component.
     *
     * @param classLoader the ClassLoader to use
     * @param name the name of component
     * @param implementationKey the implementationKey of component
     * @return the Class object
     * @throws org.jcontainer.loom.tools.verifier.VerifyException if unable to aquire class object
     */
    private Class getClass( final ClassLoader classLoader,
                            final String name,
                            final String implementationKey )
        throws VerifyException
    {
        Class clazz = null;
        try
        {
            clazz = classLoader.loadClass( implementationKey );
        }
        catch( final Exception e )
        {
            final String message =
                REZ.format( "assembly.bad-class.error",
                            name,
                            implementationKey,
                            e.toString() );
            throw new VerifyException( message );
        }
        return clazz;
    }
}
