/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.tools.profile;

import org.apache.avalon.phoenix.containerkit.metadata.PartitionMetaData;

/**
 * The PartitionProfile contains the set of data required
 * to construct a specific instance of a Profile. It contains
 * a set of child PartitionProfile and {@link org.jcontainer.loom.tools.profile.ComponentProfile}
 * objects.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.2 $ $Date: 2003-06-29 00:45:53 $
 */
public class PartitionProfile
{
    /**
     * Constant for an empty set of partitions.
     */
    public static final PartitionProfile[] EMPTY_SET = new PartitionProfile[ 0 ];

    /**
     * The {@link org.apache.avalon.phoenix.containerkit.metadata.PartitionMetaData} for this partition.
     */
    private final PartitionMetaData m_metaData;

    /**
     * An array of partitions that are contained by this
     * object.
     */
    private final PartitionProfile[] m_partitions;

    /**
     * An array of partitions that are contained by this
     * object.
     */
    private final ComponentProfile[] m_components;

    /**
     * Create a PartitionProfile.
     *
     * @param metaData the meta data about this profile
     * @param partitions the partitions contained by this partition
     * @param components the components contained by this partition
     */
    public PartitionProfile( final PartitionMetaData metaData,
                             final PartitionProfile[] partitions,
                             final ComponentProfile[] components )
    {
        if( null == metaData )
        {
            throw new NullPointerException( "metaData" );
        }
        if( null == partitions )
        {
            throw new NullPointerException( "partitions" );
        }
        if( null == components )
        {
            throw new NullPointerException( "components" );
        }

        m_metaData = metaData;
        m_partitions = partitions;
        m_components = components;
    }

    /**
     * Return the metaData about this profile.
     *
     * @return the metaData about this profile.
     */
    public PartitionMetaData getMetaData()
    {
        return m_metaData;
    }

    /**
     * Return the set of partitions contained in this partition.
     *
     * @return the set of partitions contained in this partition.
     */
    public PartitionProfile[] getPartitions()
    {
        return m_partitions;
    }

    /**
     * Return the set of components contained in this partition.
     *
     * @return the set of components contained in this partition.
     */
    public ComponentProfile[] getComponents()
    {
        return m_components;
    }

    /**
     * Return the partition with specified name.
     *
     * @return the partition with specified name.
     */
    public PartitionProfile getPartition( final String name )
    {
        for( int i = 0; i < m_partitions.length; i++ )
        {
            final PartitionProfile partition = m_partitions[ i ];
            if( partition.getMetaData().getName().equals( name ) )
            {
                return partition;
            }
        }
        return null;
    }

    /**
     * Return the component with specified name.
     *
     * @return the component with specified name.
     */
    public ComponentProfile getComponent( final String name )
    {
        for( int i = 0; i < m_components.length; i++ )
        {
            final ComponentProfile component = m_components[ i ];
            if( component.getMetaData().getName().equals( name ) )
            {
                return component;
            }
        }
        return null;
    }
}
