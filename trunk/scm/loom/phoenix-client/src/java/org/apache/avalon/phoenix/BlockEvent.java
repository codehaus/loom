/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.apache.avalon.phoenix;

import java.util.EventObject;
import org.apache.avalon.phoenix.metainfo.BlockInfo;

/**
 * This is the class that is used to deliver notifications
 * about Blocks state changes to the {@link org.apache.avalon.phoenix.BlockListener}s
 * of a Server Application.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 */
public final class BlockEvent
    extends EventObject
{
    private final String m_name;
    private final Object m_block;
    private final BlockInfo m_blockInfo;

    /**
     * Construct the <tt>BlockEvent</tt>.
     *
     * @param name the name of block
     * @param block the block object
     * @param blockInfo the BlockInfo object for block
     */
    public BlockEvent( final String name,
                       final Object block,
                       final BlockInfo blockInfo )
    {
        super( name );

        if( null == name )
        {
            throw new NullPointerException( "name" );
        }
        if( null == block )
        {
            throw new NullPointerException( "block" );
        }
        if( null == blockInfo )
        {
            throw new NullPointerException( "blockInfo" );
        }

        m_name = name;
        m_block = block;
        m_blockInfo = blockInfo;
    }

    /**
     * Retrieve name of block.
     *
     * @return the name of block
     */
    public String getName()
    {
        return m_name;
    }

    /**
     * Retrieve the block instance.
     *
     * @return the block instance
     */
    public Object getObject()
    {
        return m_block;
    }

    /**
     * Retrieve the block instance.
     *
     * @return the block instance
     * @deprecated Use getObject() instead as this may
     *             cause a ClassCastException
     */
    public Block getBlock()
    {
        return (Block)getObject();
    }

    /**
     * Retrieve the BlockInfo for block.
     *
     * @return the BlockInfo for block
     */
    public BlockInfo getBlockInfo()
    {
        return m_blockInfo;
    }
}
