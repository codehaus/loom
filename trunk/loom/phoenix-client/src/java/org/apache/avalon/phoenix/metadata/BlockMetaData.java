/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.metadata;

import org.apache.avalon.phoenix.metainfo.BlockInfo;

/**
 * This is the structure describing each block.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 */
public class BlockMetaData
{
    private final String m_name;
    private final DependencyMetaData[] m_dependencies;
    private final BlockInfo m_blockInfo;

    public BlockMetaData( final String name,
                          final DependencyMetaData[] dependencies,
                          final BlockInfo blockInfo )
    {
        m_name = name;
        m_dependencies = dependencies;
        m_blockInfo = blockInfo;
    }

    public String getName()
    {
        return m_name;
    }

    /**
     * @deprecated Please use {@link #getImplementationKey} instead.
     */
    public String getClassname()
    {
        return getImplementationKey();
    }

    public String getImplementationKey()
    {
        return getBlockInfo().getBlockDescriptor().getImplementationKey();
    }

    public BlockInfo getBlockInfo()
    {
        return m_blockInfo;
    }

    public DependencyMetaData getDependency( final String name )
    {
        for( int i = 0; i < m_dependencies.length; i++ )
        {
            if( m_dependencies[ i ].getRole().equals( name ) )
            {
                return m_dependencies[ i ];
            }
        }

        return null;
    }

    public DependencyMetaData[] getDependencies()
    {
        return m_dependencies;
    }
}
