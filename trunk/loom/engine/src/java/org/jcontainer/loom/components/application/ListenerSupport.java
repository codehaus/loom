/* ====================================================================
 * JContainer Software License, version 1.1
 *
 * Copyright (c) 2003, JContainer Group. All rights reserved.
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
 * 3. Neither the name of the JContainer Group nor the name "Loom" nor
 *    the names of its contributors may be used to endorse or promote
 *    products derived from this software without specific prior
 *    written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * ====================================================================
 *
 * JContainer Loom includes code from the Apache Software Foundation
 *
 * ====================================================================
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
package org.jcontainer.loom.components.application;

import org.apache.avalon.phoenix.ApplicationEvent;
import org.apache.avalon.phoenix.ApplicationListener;
import org.apache.avalon.phoenix.BlockEvent;
import org.apache.avalon.phoenix.BlockListener;
import org.apache.avalon.phoenix.metadata.SarMetaData;
import org.apache.avalon.phoenix.metainfo.BlockInfo;
import org.jcontainer.loom.components.util.ComponentInfoConverter;
import org.jcontainer.loom.components.util.profile.ComponentProfile;

/**
 * Manage a set of {@link ApplicationListener} objects and propogate
 * {@link ApplicationEvent} notifications to these listeners.  Not all
 * events pass an Applicationevent parameter.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @author <a href="mailto:Paul_Hammant@yahoo.com">Paul Hammant</a>
 */
final class ListenerSupport
{
    //Set of block listeners. Must be accessed from synchronized code
    private BlockListener[] m_blockListeners = new BlockListener[ 0 ];

    //Set of listeners. Must be accessed from synchronized code
    private ApplicationListener[] m_listeners = new ApplicationListener[ 0 ];

    /**
     * fire Event indicating that the Application represented
     * by specified metaData is starting.
     *
     * @param metaData the metaData
     */
    void fireApplicationStartingEvent( final SarMetaData metaData )
        throws Exception
    {
        final ApplicationEvent event =
            new ApplicationEvent( metaData.getName(), metaData );
        applicationStarting( event );
    }

    /**
     * fire Event indicating that Block represented by
     * specific entry has been added.
     *
     * @param entry the entry
     */
    void fireBlockAddedEvent( final BlockEntry entry )
    {
        blockAdded( createEvent( entry ) );
    }

    /**
     * fire Event indicating that Block represented by
     * specific entry is being removed.
     *
     * @param entry the entry
     */
    void fireBlockRemovedEvent( final BlockEntry entry )
    {
        blockRemoved( createEvent( entry ) );
    }

    /**
     * Utility method to create an event for a
     * specific entry.
     *
     * @param entry the entry
     * @return the new event
     */
    private BlockEvent createEvent( final BlockEntry entry )
    {
        final BlockInfo blockInfo = 
            ComponentInfoConverter.toBlockInfo( entry.getInfo() );
        return new BlockEvent( entry.getName(),
                            entry.getProxy(),
                            blockInfo );
    }

    /**
     * Add a ApplicationListener to those requiring notification of
     * {@link ApplicationEvent}s.
     *
     * @param listener the ApplicationListener
     */
    public synchronized void addApplicationListener( final ApplicationListener listener )
    {
        final ApplicationListener[] listeners = new ApplicationListener[ 1 + m_listeners.length ];
        System.arraycopy( m_listeners, 0, listeners, 0, m_listeners.length );
        listeners[ m_listeners.length ] = listener;
        m_listeners = listeners;
    }

    /**
     * Remove a ApplicationListener from those requiring notification of
     * {@link ApplicationEvent}s.
     *
     * @param listener the ApplicationListener
     */
    public synchronized void removeApplicationListener( final ApplicationListener listener )
    {
        int index = 0;
        while( index < m_listeners.length )
        {
            if( m_listeners[ index ] == listener )
            {
                break;
            }
            index++;
        }

        if( m_listeners.length != index )
        {
            final ApplicationListener[] listeners =
                new ApplicationListener[ m_listeners.length - 1 ];
            System.arraycopy( m_listeners, 0, listeners, 0, index );
            final int length = m_listeners.length - index - 1;
            System.arraycopy( m_listeners, index + 1, listeners, index, length );
        }
    }

    /**
     * Add a BlockListener to those requiring notification of
     * {@link BlockEvent}s.
     *
     * @param listener the BlockListener
     */
    public synchronized void addBlockListener( final BlockListener listener )
    {
        final BlockListener[] listeners = new BlockListener[ 1 + m_blockListeners.length ];
        System.arraycopy( m_blockListeners, 0, listeners, 0, m_blockListeners.length );
        listeners[ m_listeners.length ] = listener;
        m_blockListeners = listeners;
    }

    /**
     * Remove a BlockListener from those requiring notification of
     * {@link BlockEvent}s.
     *
     * @param listener the BlockListener
     */
    public synchronized void removeBlockListener( final BlockListener listener )
    {
        int index = 0;
        while( index < m_blockListeners.length )
        {
            if( m_blockListeners[ index ] == listener )
            {
                break;
            }
            index++;
        }

        if( m_blockListeners.length != index )
        {
            final BlockListener[] listeners =
                new BlockListener[ m_blockListeners.length - 1 ];
            System.arraycopy( m_blockListeners, 0, listeners, 0, index );
            final int length = m_blockListeners.length - index - 1;
            System.arraycopy( m_blockListeners, index + 1, listeners, index, length );
        }
    }

    /**
     * Notification that the application is starting
     *
     * @param event the ApplicationEvent
     */
    private synchronized void applicationStarting( final ApplicationEvent event )
        throws Exception
    {
        for( int i = 0; i < m_listeners.length; i++ )
        {
            m_listeners[ i ].applicationStarting( event );
        }
    }

    /**
     * Notification that the application has started.
     *
     */
    public synchronized void applicationStarted()
    {
        for( int i = 0; i < m_listeners.length; i++ )
        {
            m_listeners[ i ].applicationStarted();
        }
    }

    /**
     * Notification that the application is stopping
     *
     */
    public synchronized void applicationStopping()
    {
        for( int i = 0; i < m_listeners.length; i++ )
        {
            m_listeners[ i ].applicationStopping();
        }
    }

    /**
     * Notification that the application has stopped
     *
     */
    public synchronized void applicationStopped()
    {
        for( int i = 0; i < m_listeners.length; i++ )
        {
            m_listeners[ i ].applicationStopped();
        }
    }

    /**
     * Notification that the application has failed
     *
     */
    public synchronized void applicationFailure( final Exception causeOfFailure )
    {
        for( int i = 0; i < m_listeners.length; i++ )
        {
            m_listeners[ i ].applicationFailure( causeOfFailure );
        }
    }

    /**
     * Notification that a block has just been added
     * to Server Application.
     *
     * @param event the BlockEvent
     */
    private synchronized void blockAdded( final BlockEvent event )
    {
        for( int i = 0; i < m_listeners.length; i++ )
        {
            m_listeners[ i ].blockAdded( event );
        }

        //Now notify the plain BlockListeners
        for( int i = 0; i < m_blockListeners.length; i++ )
        {
            m_blockListeners[ i ].blockAdded( event );
        }
    }

    /**
     * Notification that a block is just about to be
     * removed from Server Application.
     *
     * @param event the BlockEvent
     */
    private synchronized void blockRemoved( final BlockEvent event )
    {
        for( int i = 0; i < m_listeners.length; i++ )
        {
            m_listeners[ i ].blockRemoved( event );
        }

        //Now notify the plain BlockListeners
        for( int i = 0; i < m_blockListeners.length; i++ )
        {
            m_blockListeners[ i ].blockRemoved( event );
        }
    }
}
