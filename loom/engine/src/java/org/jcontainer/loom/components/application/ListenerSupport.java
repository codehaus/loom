/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 *
 * This product includes software developed by the
 * Apache Software Foundation (http://www.apache.org/).
 */
package org.jcontainer.loom.components.application;

import org.apache.avalon.phoenix.ApplicationEvent;
import org.apache.avalon.phoenix.ApplicationListener;
import org.apache.avalon.phoenix.BlockEvent;
import org.apache.avalon.phoenix.BlockListener;
import org.jcontainer.loom.components.util.ComponentInfoConverter;
import org.apache.avalon.phoenix.metadata.SarMetaData;
import org.apache.avalon.phoenix.metainfo.BlockInfo;

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
        final org.jcontainer.loom.tools.profile.ComponentProfile profile = entry.getProfile();
        final BlockInfo blockInfo = ComponentInfoConverter.toBlockInfo( profile.getInfo() );
        final BlockEvent event =
            new BlockEvent( profile.getMetaData().getName(),
                            entry.getProxy(),
                            blockInfo );
        return event;
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
