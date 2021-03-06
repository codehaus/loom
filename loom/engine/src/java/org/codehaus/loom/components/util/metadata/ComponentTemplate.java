/*
 * Copyright (C) The Loom Group. All rights reserved.
 *
 * This software is published under the terms of the Loom
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.codehaus.loom.components.util.metadata;

import java.util.ArrayList;
import java.util.List;
import org.apache.avalon.framework.parameters.Parameters;
import org.codehaus.dna.Configuration;

/**
 * Each component declared in the application is represented by a
 * ComponentTemplate. Note that this does not necessarily imply that there is
 * only one instance of actual component. The ComponentTemplate could represent
 * a pool of components, a single component or a component prototype that is
 * reused to create new components as needed.
 *
 * @author Peter Donald
 * @version $Revision: 1.2 $ $Date: 2004-05-01 12:48:35 $
 */
public class ComponentTemplate
{
    /** Empty set of component metadata. */
    public static final ComponentTemplate[] EMPTY_SET = new ComponentTemplate[ 0 ];

    /**
     * The name of the component. This is an abstract name used during
     * assembly.
     */
    private final String m_name;

    /**
     * The implementationKey for this component. Usually this represents a
     * classname but alternative mechanisms could be used (ie URL of
     * webservice).
     */
    private final String m_implementationKey;

    /** The resolution of any dependencies required by the component type. */
    private final DependencyDirective[] m_dependencies;

    /** The parameters for component (if any). */
    private final Parameters m_parameters;

    /** The configuration for component (if any). */
    private final Configuration m_configuration;

    /** True if proxy should be disabled. */
    private final boolean m_disableProxy;

    /**
     * Create a ComponentTemplate.
     *
     * @param name the abstract name of component meta data instance
     * @param implementationKey the key used to create component (usually a
     * classname)
     * @param dependencies the meta data for any dependencies
     * @param parameters the parameters that the component will be provided (may
     * be null)
     * @param configuration the configuration that the component will be
     * provided (may be null)
     * @param disableProxy true if proxy should be disabled
     */
    public ComponentTemplate( final String name,
                              final String implementationKey,
                              final DependencyDirective[] dependencies,
                              final Parameters parameters,
                              final Configuration configuration,
                              final boolean disableProxy )
    {
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
        m_disableProxy = disableProxy;
    }

    /**
     * Return true if proxy should not be created for object.
     *
     * @return true if proxy should not be created for object.
     */
    public boolean isDisableProxy()
    {
        return m_disableProxy;
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
    public DependencyDirective[] getDependencies()
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
    public DependencyDirective getDependency( final String key )
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
     * Return all the dependencies for key. Used for Map and array
     * dependencies.
     *
     * @return all the dependencies for key
     */
    public DependencyDirective[] getDependencies( final String key )
    {
        final List result = new ArrayList();

        for( int i = 0; i < m_dependencies.length; i++ )
        {
            final DependencyDirective dependency = m_dependencies[ i ];
            if( dependency.getKey().equals( key ) )
            {
                result.add( dependency );
            }
        }

        return (DependencyDirective[])result.
            toArray( new DependencyDirective[ result.size() ] );
    }
}
