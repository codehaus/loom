/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.tools.qdox;

import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import java.util.ArrayList;
import org.jcontainer.loom.tools.info.Attribute;
import org.jcontainer.loom.tools.info.ComponentDescriptor;
import org.jcontainer.loom.tools.info.ComponentInfo;
import org.jcontainer.loom.tools.info.ContextDescriptor;
import org.jcontainer.loom.tools.info.DependencyDescriptor;
import org.jcontainer.loom.tools.info.LoggerDescriptor;
import org.jcontainer.loom.tools.info.SchemaDescriptor;
import org.jcontainer.loom.tools.info.ServiceDescriptor;
import org.jcontainer.loom.tools.qdox.AbstractInfoBuilder;
import org.apache.avalon.phoenix.framework.tools.infobuilder.LegacyUtil;

/**
 * Build a ComponentInfo object by interpreting Phoenix style javadoc
 * markup in source.
 *
 * @author Paul Hammant
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003-06-26 08:42:06 $
 */
public class LegacyInfoBuilder
    extends AbstractInfoBuilder
{
    /**
     * Build a ComponentInfo object for specified class.
     *
     * @param javaClass the class
     * @return the ComponentInfo object
     */
    public ComponentInfo buildComponentInfo( final JavaClass javaClass )
    {
        final ComponentDescriptor component = buildComponent( javaClass );
        final ServiceDescriptor[] services = buildServices( javaClass );
        final ContextDescriptor context = LegacyUtil.CONTEXT_DESCRIPTOR;
        final LoggerDescriptor[] loggers = LoggerDescriptor.EMPTY_SET;
        final SchemaDescriptor schema = buildConfigurationSchema( javaClass );
        final DependencyDescriptor[] dependencies = buildDependencies( javaClass );

        return new ComponentInfo( component, services, loggers, context,
                                  dependencies, schema, null );
    }

    /**
     * Build the component descriptor for specified class.
     *
     * @param javaClass the class
     * @return the component descriptor
     */
    private ComponentDescriptor buildComponent( final JavaClass javaClass )
    {
        final String type = javaClass.getFullyQualifiedName();
        return new ComponentDescriptor( type, Attribute.EMPTY_SET );
    }

    /**
     * Build the set of service descriptors for specified class.
     *
     * @param javaClass the class
     * @return the set of service descriptors
     */
    private ServiceDescriptor[] buildServices( final JavaClass javaClass )
    {
        final ArrayList services = new ArrayList();
        final DocletTag[] serviceTags = javaClass.getTagsByName( "phoenix:service" );
        for( int i = 0; i < serviceTags.length; i++ )
        {
            final String type = getNamedParameter( serviceTags[ i ], "name" );
            final ServiceDescriptor service = new ServiceDescriptor( type, Attribute.EMPTY_SET );
            services.add( service );
        }
        final DocletTag[] mxTags = javaClass.getTagsByName( "phoenix:mx" );
        for( int i = 0; i < mxTags.length; i++ )
        {
            final String type = getNamedParameter( mxTags[ i ], "name" );
            final ServiceDescriptor service =
                new ServiceDescriptor( type, new Attribute[]{LegacyUtil.MX_ATTRIBUTE} );
            services.add( service );
        }
        return (ServiceDescriptor[])services.toArray( new ServiceDescriptor[ services.size() ] );
    }

    /**
     * Build the schema descriptor for specified class.
     *
     * @param javaClass the class
     * @return the schema descriptor
     */
    private SchemaDescriptor buildConfigurationSchema( final JavaClass javaClass )
    {
        final JavaMethod method =
            getLifecycleMethod( javaClass, "configure", CONFIGURATION_CLASS );
        if( null == method )
        {
            return null;
        }

        final DocletTag tag =
            method.getTagByName( "phoenix:configuration-schema" );
        if( null == tag )
        {
            return null;
        }
        final String type = getNamedParameter( tag, "type" );
        final String classname = javaClass.getFullyQualifiedName();
        final String location = LegacyUtil.getSchemaLocationFor( classname );
        return new SchemaDescriptor( location, type, Attribute.EMPTY_SET );
    }

    /**
     * Build the set of dependency descriptors for specified class.
     *
     * @param javaClass the class
     * @return the set of dependency descriptors
     */
    private DependencyDescriptor[] buildDependencies( final JavaClass javaClass )
    {
        JavaMethod method =
            getLifecycleMethod( javaClass, "compose", COMPONENT_MANAGER_CLASS );

        //If no compose then try for a service method ...
        if( null == method )
        {
            method =
                getLifecycleMethod( javaClass, "service", SERVICE_MANAGER_CLASS );
        }

        if( null == method )
        {
            return DependencyDescriptor.EMPTY_SET;
        }
        else
        {
            final ArrayList deps = new ArrayList();
            final DocletTag[] tags = method.getTagsByName( "phoenix:dependency" );
            for( int i = 0; i < tags.length; i++ )
            {
                final DocletTag tag = tags[ i ];
                final String unresolvedType = getNamedParameter( tag, "name" );
                final String type = resolveType( javaClass, unresolvedType );
                final String key = getNamedParameter( tag, "role", type );
                final DependencyDescriptor dependency =
                    new DependencyDescriptor( key, type, false, Attribute.EMPTY_SET );
                deps.add( dependency );
            }
            return (DependencyDescriptor[])deps.toArray( new DependencyDescriptor[ deps.size() ] );
        }
    }
}
