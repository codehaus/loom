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
import org.jcontainer.loom.tools.info.EntryDescriptor;
import org.jcontainer.loom.tools.info.LoggerDescriptor;
import org.jcontainer.loom.tools.info.SchemaDescriptor;
import org.jcontainer.loom.tools.info.ServiceDescriptor;
import org.jcontainer.loom.tools.qdox.AbstractInfoBuilder;

/**
 * This is a utility class that is used to build a ComponentInfo object
 * from QDoxs JavaClass object model. This essentially involves interpreting
 * all of the javadoc tags present in JavaClass object model.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003-06-26 08:42:06 $
 */
public class DefaultInfoBuilder
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
        final ContextDescriptor context = buildContext( javaClass );
        final LoggerDescriptor[] loggers = buildLoggers( javaClass );
        final SchemaDescriptor configurationSchema = buildConfigurationSchema( javaClass );
        final SchemaDescriptor parametersSchema = buildParametersSchema( javaClass );

        final DependencyDescriptor[] dependencies = buildDependencies( javaClass );

        return new ComponentInfo( component, services, loggers,
                                  context, dependencies,
                                  configurationSchema,
                                  parametersSchema );
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
        final DocletTag[] tags = javaClass.getTagsByName( "phoenix.service" );
        for( int i = 0; i < tags.length; i++ )
        {
            final DocletTag tag = tags[ i ];
            final String unresolvedType = getNamedParameter( tag, "type" );
            final String type = resolveType( javaClass, unresolvedType );
            final ServiceDescriptor service = new ServiceDescriptor( type, Attribute.EMPTY_SET );
            services.add( service );
        }
        return (ServiceDescriptor[])services.toArray( new ServiceDescriptor[ services.size() ] );
    }

    /**
     * Build the set of logger descriptors for specified class.
     *
     * @param javaClass the class
     * @return the set of logger descriptors
     */
    private LoggerDescriptor[] buildLoggers( final JavaClass javaClass )
    {
        final JavaMethod method =
            getLifecycleMethod( javaClass, "enableLogging", LOGGER_CLASS );
        if( null == method )
        {
            return LoggerDescriptor.EMPTY_SET;
        }
        else
        {
            final ArrayList loggers = new ArrayList();
            final DocletTag[] tags = method.getTagsByName( "phoenix.logger" );
            for( int i = 0; i < tags.length; i++ )
            {
                final String name =
                    getNamedParameter( tags[ i ], "name", "" );
                final LoggerDescriptor logger =
                    new LoggerDescriptor( name, Attribute.EMPTY_SET );
                loggers.add( logger );
            }
            return (LoggerDescriptor[])loggers.toArray( new LoggerDescriptor[ loggers.size() ] );
        }
    }

    /**
     * Build the context descriptor for specified class.
     *
     * @param javaClass the class
     * @return the context descriptor
     */
    private ContextDescriptor buildContext( final JavaClass javaClass )
    {
        final JavaMethod method =
            getLifecycleMethod( javaClass, "contextualize", CONTEXT_CLASS );
        if( null == method )
        {
            return ContextDescriptor.EMPTY_CONTEXT;
        }
        else
        {
            String type = CONTEXT_CLASS;
            final DocletTag tag = method.getTagByName( "phoenix.context" );
            if( null != tag && null != tag.getNamedParameter( "type" ) )
            {
                final String value = getNamedParameter( tag, "type" );
                type = resolveType( javaClass, value );
            }

            final ArrayList entrySet = new ArrayList();
            final DocletTag[] tags = method.getTagsByName( "phoenix.entry" );
            for( int i = 0; i < tags.length; i++ )
            {
                final String key = getNamedParameter( tags[ i ], "key" );
                final String entryType = getNamedParameter( tags[ i ], "type" );
                final String optional = getNamedParameter( tags[ i ], "optional", "false" );
                final boolean isOptional = "true".equals( optional );
                final EntryDescriptor entry =
                    new EntryDescriptor( key, entryType, isOptional, Attribute.EMPTY_SET );
                entrySet.add( entry );
            }
            final EntryDescriptor[] entrys =
                (EntryDescriptor[])entrySet.toArray( new EntryDescriptor[ entrySet.size() ] );

            return new ContextDescriptor( type, entrys, Attribute.EMPTY_SET );
        }
    }

    /**
     * Build the configuration schema descriptor for specified class.
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

        final DocletTag tag = method.getTagByName( "phoenix.configuration" );
        if( null == tag )
        {
            return null;
        }
        else
        {
            final String location = getNamedParameter( tag, "location", "" );
            final String type = getNamedParameter( tag, "type", "" );

            return new SchemaDescriptor( location, type, Attribute.EMPTY_SET );
        }
    }

    /**
     * Build the parameters schema descriptor for specified class.
     *
     * @param javaClass the class
     * @return the schema descriptor
     */
    private SchemaDescriptor buildParametersSchema( final JavaClass javaClass )
    {
        final JavaMethod method =
            getLifecycleMethod( javaClass, "parameterize", PARAMETERS_CLASS );
        if( null == method )
        {
            return null;
        }

        final DocletTag tag = method.getTagByName( "phoenix.parameters" );
        if( null == tag )
        {
            return null;
        }
        else
        {
            final String location = getNamedParameter( tag, "location", "" );
            final String type = getNamedParameter( tag, "type", "" );

            return new SchemaDescriptor( location, type, Attribute.EMPTY_SET );
        }
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
            final DocletTag[] tags = method.getTagsByName( "phoenix.dependency" );
            for( int i = 0; i < tags.length; i++ )
            {
                final DocletTag tag = tags[ i ];
                final String unresolvedType = getNamedParameter( tag, "type" );
                final String type = resolveType( javaClass, unresolvedType );
                final String key = getNamedParameter( tag, "key", type );
                final String optional = getNamedParameter( tag, "optional", "false" );
                final boolean isOptional = "true".equals( optional );
                final DependencyDescriptor dependency =
                    new DependencyDescriptor( key, type, isOptional, Attribute.EMPTY_SET );
                deps.add( dependency );
            }
            return (DependencyDescriptor[])deps.toArray( new DependencyDescriptor[ deps.size() ] );
        }
    }
}
