/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.components.util.info;

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
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003-10-16 14:45:46 $
 */
public class ComponentInfo
    implements Serializable
{
    /**
     * The type of the component.
     */
    private final Class m_type;

    /**
     * Descriptors for the services exported by component.
     */
    private final ServiceDescriptor[] m_services;

    /**
     * Descriptor for the schema of components parameters.
     */
    private final SchemaDescriptor m_configurationSchema;

    /**
     * Descriptor for the service dependencies of component.
     */
    private final DependencyDescriptor[] m_dependencies;

    /**
     * Basic constructor that takes as parameters all parts.
     */
    public ComponentInfo( final Class type,
                          final ServiceDescriptor[] services,
                          final DependencyDescriptor[] dependencies,
                          final SchemaDescriptor configurationSchema )
    {
        if( null == type )
        {
            throw new NullPointerException( "type" );
        }
        if( null == services )
        {
            throw new NullPointerException( "services" );
        }
        if( null == dependencies )
        {
            throw new NullPointerException( "dependencies" );
        }
        m_type = type;
        m_services = services;
        m_dependencies = dependencies;
        m_configurationSchema = configurationSchema;
    }

    /**
     * Return the components type.
     *
     * @return the components type.
     */
    public Class getType()
    {
        return m_type;
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
     * Return the schema for the configuration.
     *
     * @return the schema for the configuration.
     */
    public SchemaDescriptor getConfigurationSchema()
    {
        return m_configurationSchema;
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
     * Retrieve a dependency with a particular key.
     *
     * @param key the key
     * @return the dependency or null if it does not exist
     */
    public DependencyDescriptor getDependency( final String key )
    {
        for( int i = 0; i < m_dependencies.length; i++ )
        {
            if( m_dependencies[ i ].getKey().equals( key ) )
            {
                return m_dependencies[ i ];
            }
        }

        return null;
    }
}
