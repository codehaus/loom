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

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.apache.avalon.phoenix.ApplicationListener;
import org.apache.avalon.phoenix.BlockListener;
import org.apache.avalon.phoenix.metadata.SarMetaData;
import org.codehaus.spice.salt.i18n.ResourceManager;
import org.codehaus.spice.salt.i18n.Resources;
import org.jcontainer.dna.AbstractLogEnabled;
import org.jcontainer.dna.Active;
import org.jcontainer.dna.Logger;
import org.jcontainer.loom.components.util.ComponentMetaDataConverter;
import org.jcontainer.loom.components.util.lifecycle.LifecycleHelper;
import org.jcontainer.loom.components.util.profile.ComponentProfile;
import org.jcontainer.loom.components.util.profile.PartitionProfile;
import org.jcontainer.loom.interfaces.Application;
import org.jcontainer.loom.interfaces.ApplicationContext;
import org.jcontainer.loom.interfaces.ContainerConstants;
import org.jcontainer.loom.interfaces.LoomException;

/**
 * This is the basic container of blocks. A server application represents an
 * aggregation of blocks that act together to form an application.
 *
 * @author Peter Donald
 * @author Leo Simons
 * @dna.component
 * @mx.component
 */
public final class DefaultApplication
    extends AbstractLogEnabled
    implements Application, Active
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( DefaultApplication.class );

    private static final String PHASE_STARTUP = "startup";

    private static final String PHASE_SHUTDOWN = "shutdown";

    private boolean m_running = false;

    private ApplicationContext m_context;

    private final HashMap m_entries = new HashMap();

    /** ResourceProvider for blocks. */
    private BlockResourceProvider m_blockAccessor;

    /** Object to support notification of ApplicationListeners. */
    private final ListenerSupport m_listenerSupport = new ListenerSupport();

    /** Object to support running objects through lifecycle phases. */
    private final LifecycleHelper m_lifecycleHelper = new LifecycleHelper();

    ///////////////////////
    // LifeCycle Methods //
    ///////////////////////
    public void enableLogging( final Logger logger )
    {
        super.enableLogging( logger );
        setupLogger( m_lifecycleHelper );
    }

    public void initialize()
        throws Exception
    {
        try
        {
            // load block listeners
            loadBlockListeners();
        }
        catch( final Throwable t )
        {
            getLogger().info(
                "Exception loading listeners:" + t.getMessage() + "\n", t );
            throw new LoomException( t.getMessage(), t );
        }
        try
        {
            final PartitionProfile partition =
                m_context.getPartitionProfile().getPartition(
                    ContainerConstants.BLOCK_PARTITION );
            final ComponentProfile[] blocks = partition.getComponents();
            for( int i = 0; i < blocks.length; i++ )
            {
                final String blockName = blocks[ i ].getTemplate().getName();
                final BlockEntry blockEntry = new BlockEntry( blocks[ i ] );
                m_entries.put( blockName, blockEntry );
            }

            // load blocks
            runPhase( PHASE_STARTUP );
        }
        catch( final Throwable t )
        {
            getLogger().info(
                "exception while starting:" + t.getMessage() + "\n" );
            t.printStackTrace();
            throw new LoomException( t.getMessage(), t );
        }

        m_running = true;
    }

    public void dispose()
    {
        try
        {
            runPhase( PHASE_SHUTDOWN );
        }
        catch( final Throwable t )
        {
            getLogger().info( "Exception stopping:" + t.getMessage() + "\n",
                              t );
        }

        m_running = false;
        m_entries.clear();
    }

    ////////////////////////////
    // Public Utility Methods //
    ////////////////////////////
    public void setApplicationContext( final ApplicationContext context )
    {
        m_context = context;
        m_blockAccessor = new BlockResourceProvider( context, this );
        setupLogger( m_blockAccessor, "lifecycle" );
    }

    /**
     * @mx.attribute description="the names of the blocks that compose this
     * Application"
     */
    public String[] getBlockNames()
    {
        return (String[])m_entries.keySet().toArray( new String[ 0 ] );
    }

    public Object getBlock( final String name )
    {
        final BlockEntry entry = (BlockEntry)m_entries.get( name );
        if( null == entry )
        {
            return null;
        }
        else
        {
            return entry.getProxy();
        }
    }

    /**
     * @mx.attribute description="the name of the application."
     */
    public String getName()
    {
        return m_context.getPartitionProfile().getMetaData().getName();
    }

    /**
     * @mx.attribute description="the name to display in Management UI."
     */
    public String getDisplayName()
    {
        return m_context.getPartitionProfile().getMetaData().getName();
    }

    /**
     * @mx.attribute description="the string used to describe the application in
     * the UI."
     */
    public String getDescription()
    {
        return "The " + getDisplayName() + " application.";
    }

    /**
     * @mx.attribute description="the location of Application installation"
     */
    public String getHomeDirectory()
    {
        return m_context.getHomeDirectory().getPath();
    }

    /**
     * @mx.attribute description="True if the application is running or false
     * otherwise."
     */
    public boolean isRunning()
    {
        return m_running;
    }

    /////////////////////////////
    // Private Utility Methods //
    /////////////////////////////

    private void loadBlockListeners()
        throws Exception
    {
        //Setup thread context for calling visitors
        final ClassLoader loader = Thread.currentThread()
            .getContextClassLoader();
        Thread.currentThread().setContextClassLoader(
            m_context.getClassLoader() );

        try
        {
            doLoadBlockListeners();
        }
        finally
        {
            Thread.currentThread().setContextClassLoader( loader );
        }
    }

    /**
     * Actually perform loading of each individual Listener. Note that by this
     * stage it is assumed that the Thread Context has already been setup
     * correctly.
     */
    private void doLoadBlockListeners()
        throws Exception
    {
        final ComponentProfile[] listeners =
            getComponentsInPartition( ContainerConstants.LISTENER_PARTITION );
        for( int i = 0; i < listeners.length; i++ )
        {
            try
            {
                startupListener( listeners[ i ] );
            }
            catch( final Exception e )
            {
                final String name = listeners[ i ].getTemplate().getName();
                final String message =
                    REZ.format( "bad-listener",
                                "startup",
                                name,
                                e.getMessage() );
                getLogger().error( message, e );
                throw e;
            }
        }
    }

    private ComponentProfile[] getComponentsInPartition( final String key )
    {
        final PartitionProfile partition =
            m_context.getPartitionProfile().getPartition( key );
        return partition.getComponents();
    }

    /**
     * Run a phase for application. Each phase transitions application into new
     * state and processes all the blocks to make sure they are in that state
     * aswell. Exceptions leave the blocks in an indeterminate state.
     *
     * @param name the name of phase (for logging purposes)
     * @throws Exception if an error occurs
     */
    private final void runPhase( final String name )
        throws Exception
    {
        //Setup thread context for calling visitors
        final ClassLoader loader = Thread.currentThread()
            .getContextClassLoader();
        Thread.currentThread().setContextClassLoader(
            m_context.getClassLoader() );

        try
        {
            doRunPhase( name );
        }
        finally
        {
            Thread.currentThread().setContextClassLoader( loader );
        }
    }

    /**
     * Actually run applications phas. By this methods calling it is assumed
     * that Thread Context has already been setup.
     *
     * @param name the name of phase (for logging purposes)
     * @throws Exception if an error occurs
     */
    private final void doRunPhase( final String name )
        throws Exception
    {
        final ComponentProfile[] blocks =
            getComponentsInPartition( ContainerConstants.BLOCK_PARTITION );
        final String[] order = DependencyGraph.walkGraph(
            PHASE_STARTUP == name, blocks );

        //Log message describing the number of blocks
        //the phase in and the order in which they will be
        //processed
        if( getLogger().isInfoEnabled() )
        {
            final Integer count = new Integer( blocks.length );
            final List pathList = Arrays.asList( order );
            final String message =
                REZ.format( "blocks-processing", count, name, pathList );
            getLogger().info( message );
        }

        //All blocks about to be processed ...
        if( PHASE_STARTUP == name )
        {
            //... for startup, so indicate to applicable listeners
            final PartitionProfile partition = m_context.getPartitionProfile();
            final File homeDirectory = m_context.getHomeDirectory();
            final SarMetaData sarMetaData =
                ComponentMetaDataConverter.toSarMetaData( partition,
                                                          homeDirectory );
            m_listenerSupport.fireApplicationStartingEvent( sarMetaData );
        }
        else
        {
            //... for shutdown, so indicate to applicable listeners
            m_listenerSupport.applicationStopping();
        }

        //Process blocks, one by one.

        for( int i = 0; i < order.length; i++ )
        {
            final String block = order[ i ];

            //Log message saying we are processing block
            if( getLogger().isDebugEnabled() )
            {
                final String message = REZ.format( "process-block",
                                                   block,
                                                   name );
                getLogger().debug( message );
            }

            try
            {
                final BlockEntry entry = (BlockEntry)m_entries.get( block );
                if( PHASE_STARTUP == name )
                {
                    startup( entry );
                }
                else
                {
                    shutdown( entry );
                }
            }
            catch( final Exception e )
            {
                final String message =
                    REZ.format( "app.error.run-phase",
                                name,
                                block,
                                e.getMessage() );
                getLogger().error( message, e );
                m_listenerSupport.applicationFailure( e );
                throw e;
            }

            //Log message saying we have processed block
            if( getLogger().isDebugEnabled() )
            {
                final String message = REZ.format( "processed-block",
                                                   block,
                                                   name );
                getLogger().debug( message );
            }
        }

        //All blocks processed ...
        if( PHASE_STARTUP == name )
        {
            //... for startup, so indicate to applicable listeners
            m_listenerSupport.applicationStarted();
        }
        else
        {
            //... for shutdown, so indicate to applicable listeners
            m_listenerSupport.applicationStopped();
        }
    }

    /**
     * Method to run a Block through it's startup phase. This will involve
     * notification of {@link ApplicationListener} objects, creation of the
     * Block/Block Proxy object, calling the startup Avalon Lifecycle methods
     * and updating State property of BlockEntry. Errors that occur during
     * shutdown will be logged appropriately and cause exceptions with useful
     * messages to be raised.
     *
     * @param entry the entry containing Block
     * @throws Exception if an error occurs when block passes through a specific
     * lifecycle stage
     */
    private void startup( final BlockEntry entry )
        throws Exception
    {
        final Object block =
            m_lifecycleHelper.startup( entry.getName(),
                                       entry,
                                       m_blockAccessor );

        m_context.exportObject( entry.getName(), block );
        entry.setObject( block );

        m_listenerSupport.fireBlockAddedEvent( entry );
    }

    /**
     * Method to run a Block through it's shutdown phase. This will involve
     * notification of {@link ApplicationListener} objects, invalidating the
     * proxy object, calling the shutdown Avalon Lifecycle methods and updating
     * State property of BlockEntry. Errors that occur during shutdown will be
     * logged appropraitely.
     *
     * @param entry the entry containing Block
     */
    private void shutdown( final BlockEntry entry )
        throws Exception
    {
        m_listenerSupport.fireBlockRemovedEvent( entry );

        final Object object = entry.getObject();
        try
        {
            //Remove block from Management system
            try
            {
                m_context.unexportObject( entry.getName() );
            }
            finally
            {
                entry.invalidate();
                m_lifecycleHelper.shutdown( entry.getName(),
                                            object );
            }
        }
        finally
        {
            entry.setObject( null );
        }
    }

    /**
     * Method to run a {@link ApplicationListener} through it's startup phase.
     * This will involve creation of BlockListener object and configuration of
     * object if appropriate.
     *
     * @param profile the BlockListenerMetaData
     * @throws Exception if an error occurs when listener passes through a
     * specific lifecycle stage
     */
    private void startupListener( final ComponentProfile profile )
        throws Exception
    {
        final String name = profile.getTemplate().getName();
        final Object listener =
            m_lifecycleHelper.startup( name,
                                       profile,
                                       m_blockAccessor );

        // However onky ApplicationListners can avail of block events.
        if( listener instanceof ApplicationListener )
        {
            m_listenerSupport.addApplicationListener(
                (ApplicationListener)listener );
        }
        else
        {
            // As ApplicationListners are BlockListeners then
            //this is applicable for all
            m_listenerSupport.addBlockListener( (BlockListener)listener );

            final String message =
                REZ.format( "helper.isa-blocklistener.error",
                            name,
                            profile.getTemplate().getImplementationKey() );
            getLogger().error( message );
            System.err.println( message );
        }
    }
}
