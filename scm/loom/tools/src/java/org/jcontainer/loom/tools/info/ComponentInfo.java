/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.tools.info;

import java.io.Serializable;

/**
 * This class contains the meta information about a particular
 * component type. It describes;
 *
 * <ul>
 *   <li>Human presentable meta data such as name, version, description etc
 *   useful when assembling the system.</li>
 *   <li>the context object capabilities that this component requires</li>
 *   <li>the services that this component type is capable of providing</li>
 *   <li>the services that this component type requires to operate (and the
 *   names via which services are accessed)</li>
 * </ul>
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003-06-26 08:37:43 $
 */
public class ComponentInfo
    implements Serializable
{
    /**
     * descriptor for the component.
     */
    private final ComponentDescriptor m_descriptor;

    /**
     * Descriptors for the services exported by component.
     */
    private final ServiceDescriptor[] m_services;

    /**
     * Descriptors for the loggers used by component.
     */
    private final LoggerDescriptor[] m_loggers;

    /**
     * Descriptor for the context (and entrys) used by component.
     */
    private final ContextDescriptor m_context;

    /**
     * Descriptor for the schema of components parameters.
     */
    private final SchemaDescriptor m_configurationSchema;

    /**
     * Descriptor for the schema of components parameters.
     */
    private final SchemaDescriptor m_parametersSchema;

    /**
     * Descriptor for the service dependencies of component.
     */
    private final DependencyDescriptor[] m_dependencies;

    /**
     * Basic constructor that takes as parameters all parts.
     */
    public ComponentInfo( final ComponentDescriptor descriptor,
                          final ServiceDescriptor[] services,
                          final LoggerDescriptor[] loggers,
                          final ContextDescriptor context,
                          final DependencyDescriptor[] dependencies,
                          final SchemaDescriptor configurationSchema,
                          final SchemaDescriptor parametersSchema )
    {
        if( null == descriptor )
        {
            throw new NullPointerException( "descriptor" );
        }
        if( null == services )
        {
            throw new NullPointerException( "services" );
        }
        if( null == loggers )
        {
            throw new NullPointerException( "loggers" );
        }
        if( null == context )
        {
            throw new NullPointerException( "context" );
        }
        if( null == dependencies )
        {
            throw new NullPointerException( "dependencies" );
        }
        m_descriptor = descriptor;
        m_services = services;
        m_loggers = loggers;
        m_context = context;
        m_dependencies = dependencies;
        m_configurationSchema = configurationSchema;
        m_parametersSchema = parametersSchema;
    }

    /**
     * Return the Component descriptor.
     *
     * @return the Component descriptor.
     */
    public ComponentDescriptor getDescriptor()
    {
        return m_descriptor;
    }

    /**
     * Return the set of Services that this Component is capable of providing.
     *
     * @return the set of Services that this Component is capable of providing.
     */
    public ServiceDescriptor[] getServices()
    {
        return m_services;
    }

    /**
     * Return the set of Logger that this Component will use.
     *
     * @return the set of Logger that this Component will use.
     */
    public LoggerDescriptor[] getLoggers()
    {
        return m_loggers;
    }

    /**
     * Return the ContextDescriptor for Component, may be null.
     * If null then this component does not implement Contextualizable.
     *
     * @return the ContextDescriptor for Component, may be null.
     */
    public ContextDescriptor getContext()
    {
        return m_context;
    }

    /**
     * Return the schema for the configuration.
     *
     * @return the schema for the configuration.
     */
    public SchemaDescriptor getConfigurationSchema()
    {
        return m_configurationSchema;
    }

    /**
     * Return the schema for the parameters.
     *
     * @return the schema for the parameters.
     */
    public SchemaDescriptor getParametersSchema()
    {
        return m_parametersSchema;
    }

    /**
     * Return the set of Dependencies that this Component requires to operate.
     *
     * @return the set of Dependencies that this Component requires to operate.
     */
    public DependencyDescriptor[] getDependencies()
    {
        return m_dependencies;
    }

    /**
     * Retrieve a dependency with a particular role.
     *
     * @param role the role
     * @return the dependency or null if it does not exist
     */
    public DependencyDescriptor getDependency( final String role )
    {
        for( int i = 0; i < m_dependencies.length; i++ )
        {
            if( m_dependencies[ i ].getKey().equals( role ) )
            {
                return m_dependencies[ i ];
            }
        }

        return null;
    }

    /**
     * Retrieve a service matching the supplied classname.
     *
     * @param classname the service classname
     * @return the service descriptor or null if it does not exist
     */
    public ServiceDescriptor getService( final String classname )
    {
        for( int i = 0; i < m_services.length; i++ )
        {
            final String otherClassname = m_services[ i ].getType();
            if( otherClassname.equals( classname ) )
            {
                return m_services[ i ];
            }
        }
        return null;
    }
}