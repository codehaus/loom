/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.components.util.metadata;

/**
 * In each Assembly there may be groups of components that are activated
 * together and treated as a group. These components are all "visible" to each
 * other as peers. The group will have a name and may use resources from other
 * partitions. Partitions can also be nested one inside each other.
 *
 * @author Peter Donald
 * @version $Revision: 1.3 $ $Date: 2003-12-03 10:44:43 $
 */
public class PartitionTemplate
{
    /** Constant for an empty set of partitions. */
    public static final PartitionTemplate[] EMPTY_SET = new PartitionTemplate[ 0 ];

    /**
     * The name of the partition. This is an abstract name used during
     * assembly.
     */
    private final String m_name;

    /**
     * An array listing the set of other partitions required by this partition.
     * The required partitions must be initialized and in ready state prior to
     * this partition starting and this partition must be shutdown prior
     */
    private final String[] m_depends;

    /** AN array of partitions that are contained by this object. */
    private final PartitionTemplate[] m_partitions;

    /** AN array of components that are contained by this object. */
    private final ComponentTemplate[] m_components;

    /**
     * Create a PartitionTemplate.
     *
     * @param name the abstract name of component meta data instance
     * @param depends the partitions depended upon by this parition
     * @param partitions the partitions contained by this partition
     * @param components the components contained by this partition
     */
    public PartitionTemplate( final String name,
                              final String[] depends,
                              final PartitionTemplate[] partitions,
                              final ComponentTemplate[] components )
    {
        if( null == name )
        {
            throw new NullPointerException( "name" );
        }
        if( null == depends )
        {
            throw new NullPointerException( "depends" );
        }
        if( null == partitions )
        {
            throw new NullPointerException( "partitions" );
        }
        if( null == components )
        {
            throw new NullPointerException( "components" );
        }

        m_name = name;
        m_depends = depends;
        m_partitions = partitions;
        m_components = components;
    }

    /**
     * Return the name of component profile.
     *
     * @return the name of the component profile.
     */
    public String getName()
    {
        return m_name;
    }

    /**
     * Return the set of prereqs for this partition.
     *
     * @return the set of prereqs for this partition.
     */
    public String[] getDepends()
    {
        return m_depends;
    }

    /**
     * Return the set of partitions contained in this partition.
     *
     * @return the set of partitions contained in this partition.
     */
    public PartitionTemplate[] getPartitions()
    {
        return m_partitions;
    }

    /**
     * Return the set of components contained in this partition.
     *
     * @return the set of components contained in this partition.
     */
    public ComponentTemplate[] getComponents()
    {
        return m_components;
    }

    /**
     * Return the partition with specified name.
     *
     * @return the partition with specified name.
     */
    public PartitionTemplate getPartition( final String name )
    {
        for( int i = 0; i < m_partitions.length; i++ )
        {
            final PartitionTemplate partition = m_partitions[ i ];
            if( partition.getName().equals( name ) )
            {
                return partition;
            }
        }

        throw new IllegalArgumentException( "Missing partition named " + name );
    }

    /**
     * Return the component with specified name.
     *
     * @return the component with specified name.
     */
    public ComponentTemplate getComponent( final String name )
    {
        for( int i = 0; i < m_components.length; i++ )
        {
            final ComponentTemplate component = m_components[ i ];
            if( component.getName().equals( name ) )
            {
                return component;
            }
        }
        return null;
    }
}
