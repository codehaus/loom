/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1997-2003 The Apache Software Foundation. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *    "This product includes software developed by the
 *    Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software
 *    itself, if and wherever such third-party acknowledgments
 *    normally appear.
 *
 * 4. The names "Jakarta", "Avalon", and "Apache Software Foundation"
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */
package org.apache.avalon.phoenix;

import java.util.EventObject;
import org.apache.avalon.phoenix.metainfo.BlockInfo;

/**
 * This is the class that is used to deliver notifications
 * about Blocks state changes to the {@link org.apache.avalon.phoenix.BlockListener}s
 * of a Server Application.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
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
