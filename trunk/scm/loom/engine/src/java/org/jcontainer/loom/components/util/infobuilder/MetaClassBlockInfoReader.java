/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.components.util.infobuilder;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.jcontainer.loom.components.util.info.ComponentInfo;
import org.jcontainer.loom.components.util.info.DependencyDescriptor;
import org.jcontainer.loom.components.util.info.SchemaDescriptor;
import org.jcontainer.loom.components.util.info.ServiceDescriptor;
import org.codehaus.metaclass.Attributes;
import org.codehaus.metaclass.model.Attribute;

/**
 * A BlockInfoReader is responsible for building ComponentInfo objects from DNA MetaClass descriptors.
 *
 * @author Peter Donald
 * @version $Revision: 1.2 $ $Date: 2004-04-18 18:53:02 $
 */
public final class MetaClassBlockInfoReader implements BlockInfoReader
{
    public ComponentInfo buildComponentInfo( final Class type )
        throws Exception
    {
        final Attribute attribute =
            Attributes.getAttribute( type, "dna.component" );
        if( null == attribute )
        {
            final String message =
                "Type " +
                type.getName() +
                " does not specify the " +
                "required MetaClass attributes to be a Component";
            throw new Exception( message );
        }
        final ServiceDescriptor[] services = buildServices( type );

        final DependencyDescriptor[] dependencies =
            buildDependencies( type );

        final SchemaDescriptor schema = buildConfigurationSchema( type );

        return new ComponentInfo( type,
                                  services,
                                  dependencies,
                                  schema );
    }

    /**
     * A utility method to build a descriptor for SchemaDescriptor,
     *
     * @param type the component type
     *
     * @return the a descriptor for the SchemaDescriptor,
     */
    private SchemaDescriptor buildConfigurationSchema( final Class type )
    {
        final Class[] types =
            new Class[]{
                org.apache.avalon.framework.configuration.Configuration.class};
        try
        {
            final Method method = type.getMethod( "configure", types );
            final Attribute attribute =
                Attributes.getAttribute( method, "dna.configuration" );
            if( null == attribute )
            {
                return null;
            }
            final String location = attribute.getParameter( "location" );
            final String schemaType = attribute.getParameter( "type" );
            return new SchemaDescriptor( location, schemaType );
        }
        catch( NoSuchMethodException e )
        {
            return null;
        }
    }

    /**
     * A utility method to build an array of DependencyDescriptor objects from specified configuration and classname.
     *
     * @param type the component type
     *
     * @return the created DependencyDescriptor
     */
    private DependencyDescriptor[] buildDependencies( final Class type )
    {
        final Method method = getDependencyMethod( type );
        if( null == method )
        {
            return DependencyDescriptor.EMPTY_SET;
        }
        final List deps = new ArrayList();

        final Attribute[] attributes =
            Attributes.getAttributes( method, "dna.dependency" );
        for( int i = 0; i < attributes.length; i++ )
        {
            final Attribute attribute = attributes[i];
            final String key = attribute.getParameter( "key" );
            final String depType = attribute.getParameter( "type" );
            final boolean optional =
                attribute.getParameter( "optional" ).equals( "true" );
            deps.add( new DependencyDescriptor( key, depType, optional ) );
        }

        return (DependencyDescriptor[])deps.toArray( DependencyDescriptor.EMPTY_SET );
    }

    /**
     * Return the method that dependency information attached to.
     *
     * @param type the component type
     *
     * @return the method that dependency information attached to.
     */
    private Method getDependencyMethod( final Class type )
    {
        try
        {
            final Class[] types1 =
                new Class[]{
                    org.apache.avalon.framework.component.ComponentManager.class};
            return type.getMethod( "compose", types1 );
        }
        catch( final NoSuchMethodException nsme )
        {
        }
        try
        {
            final Class[] types2 =
                new Class[]{
                    org.apache.avalon.framework.service.ServiceManager.class};
            return type.getMethod( "service", types2 );
        }
        catch( NoSuchMethodException e )
        {
        }
        return null;
    }

    /**
     * A utility method to build an array of ServiceDescriptor objects from specified configuraiton.
     *
     * @param type the type
     *
     * @return the created ServiceDescriptor
     */
    private ServiceDescriptor[] buildServices( final Class type )
    {
        final List services = new ArrayList();

        final Attribute[] attributes = Attributes.getAttributes( type,
                                                                 "dna.service" );
        for( int i = 0; i < attributes.length; i++ )
        {
            final Attribute attribute = attributes[i];
            final String serviceType = attribute.getParameter( "type" );
            services.add( new ServiceDescriptor( serviceType ) );
        }

        return (ServiceDescriptor[])services.toArray( ServiceDescriptor.EMPTY_SET );
    }
}
