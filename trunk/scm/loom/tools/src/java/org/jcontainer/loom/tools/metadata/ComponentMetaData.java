/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.tools.metadata;

import java.util.ArrayList;
import java.util.List;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.parameters.Parameters;
import org.jcontainer.loom.tools.info.Attribute;
import org.jcontainer.loom.tools.info.FeatureDescriptor;

/**
 * Each component declared in the application is represented by
 * a ComponentMetaData. Note that this does not necessarily imply
 * that there is only one instance of actual component. The
 * ComponentMetaData could represent a pool of components, a single
 * component or a component prototype that is reused to create
 * new components as needed.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.4 $ $Date: 2003-07-05 05:29:02 $
 */
public class ComponentMetaData
    extends FeatureDescriptor
{
    /**
     * Empty set of component metadata.
     */
    public static final ComponentMetaData[] EMPTY_SET = new ComponentMetaData[ 0 ];

    /**
     * The name of the component. This is an
     * abstract name used during assembly.
     */
    private final String m_name;

    /**
     * The implementationKey for this component.
     * Usually this represents a classname but
     * alternative mechanisms could be used (ie URL
     * of webservice).
     */
    private final String m_implementationKey;

    /**
     * The resolution of any dependencies required by
     * the component type.
     */
    private final DependencyMetaData[] m_dependencies;

    /**
     * The parameters for component (if any).
     */
    private final Parameters m_parameters;

    /**
     * The configuration for component (if any).
     */
    private final Configuration m_configuration;

    /**
     * Create a ComponentMetaData.
     *
     * @param name the abstract name of component meta data instance
     * @param implementationKey the key used to create component (usually a classname)
     * @param dependencies the meta data for any dependencies
     * @param parameters the parameters that the component will be provided (may be null)
     * @param configuration the configuration that the component will be provided (may be null)
     * @param attributes the extra attributes that are used to describe component
     */
    public ComponentMetaData( final String name,
                              final String implementationKey,
                              final DependencyMetaData[] dependencies,
                              final Parameters parameters,
                              final Configuration configuration,
                              final Attribute[] attributes )
    {
        super( attributes );
        if( null == name )
        {
            throw new NullPointerException( "name" );
        }
        if( null == dependencies )
        {
            throw new NullPointerException( "dependencies" );
        }
        if( null == implementationKey )
        {
            throw new NullPointerException( "implementationKey" );
        }

        m_name = name;
        m_dependencies = dependencies;
        m_parameters = parameters;
        m_configuration = configuration;
        m_implementationKey = implementationKey;
    }

    /**
     * Return the name of component metadata.
     *
     * @return the name of the component metadata.
     */
    public String getName()
    {
        return m_name;
    }

    /**
     * Return the implementationKey for component.
     *
     * @return the implementationKey for component.
     */
    public String getImplementationKey()
    {
        return m_implementationKey;
    }

    /**
     * Return the dependency for component.
     *
     * @return the dependency for component.
     */
    public DependencyMetaData[] getDependencies()
    {
        return m_dependencies;
    }

    /**
     * Return the Parameters for Component (if any).
     *
     * @return the Parameters for Component (if any).
     */
    public Parameters getParameters()
    {
        return m_parameters;
    }

    /**
     * Return the Configuration for Component (if any).
     *
     * @return the Configuration for Component (if any).
     */
    public Configuration getConfiguration()
    {
        return m_configuration;
    }

    /**
     * Return the dependency for component with specified key.
     *
     * @return the dependency for component with specified key.
     */
    public DependencyMetaData getDependency( final String key )
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

    /**
     * Return all the dependencies for key. Used for Map and array dependencies.
     *
     * @return all the dependencies for key
     */
    public DependencyMetaData[] getDependencies( final String key )
    {
        final List result = new ArrayList();

        for( int i = 0; i < m_dependencies.length; i++ )
        {
            final DependencyMetaData dependency = m_dependencies[ i ];
            if( dependency.getKey().equals( key ) )
            {
                result.add( dependency );
            }
        }

        return (DependencyMetaData[])result.
            toArray( new DependencyMetaData[ result.size() ] );
    }
}
