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
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.phoenix.ApplicationListener;
import org.apache.avalon.phoenix.BlockListener;
import org.apache.avalon.phoenix.metadata.SarMetaData;
import org.jcontainer.dna.Active;
import org.jcontainer.loom.components.util.ComponentMetaDataConverter;
import org.jcontainer.loom.interfaces.Application;
import org.jcontainer.loom.interfaces.ApplicationContext;
import org.jcontainer.loom.interfaces.ApplicationMBean;
import org.jcontainer.loom.interfaces.LoomException;
import org.jcontainer.loom.tools.LoomToolConstants;
import org.jcontainer.loom.tools.lifecycle.LifecycleException;
import org.jcontainer.loom.tools.lifecycle.LifecycleHelper;
import org.realityforge.salt.i18n.ResourceManager;
import org.realityforge.salt.i18n.Resources;

/**
 * This is the basic container of blocks. A server application
 * represents an aggregation of blocks that act together to form
 * an application.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @author Leo Simons
 */
public final class DefaultApplication
    extends AbstractLogEnabled
    implements Application, ApplicationMBean, Active, Startable
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( DefaultApplication.class );

    private static final String PHASE_STARTUP = "startup";

    private static final String PHASE_SHUTDOWN = "shutdown";

    private boolean m_running = false;

    private ApplicationContext m_context;

    private final HashMap m_entries = new HashMap();

    /**
     * ResourceProvider for blocks.
     */
    private BlockResourceProvider m_blockAccessor;

    /**
     * Object to support notification of ApplicationListeners.
     */
    private final ListenerSupport m_listenerSupport = new ListenerSupport();

    /**
     * Object to support running objects through lifecycle phases.
     */
    private final LifecycleHelper m_lifecycleHelper = new LifecycleHelper();

    /**
     * Object to help exporting object.
     */
    private final ExportHelper m_exportHelper = new ExportHelper();

    ///////////////////////
    // LifeCycle Methods //
    ///////////////////////
    public void enableLogging( final Logger logger )
    {
        super.enableLogging( logger );
        setupLogger( m_lifecycleHelper );
        setupLogger( m_exportHelper );
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
            getLogger().info( "exception while loading listeners:" + t.getMessage() + "\n" );
            t.printStackTrace();
            throw new LoomException( t.getMessage(), t );
        }
    }

    /**
     * Start the application running.
     * This is only valid when isRunning() returns false,
     * otherwise it will generate an IllegalStateException.
     *
     * @throws IllegalStateException if application is already running
     * @throws LoomException if the application failed to start.
     *            the message part of exception will contain more information
     *            pertaining to why the application failed to startup
     */
    public void start()
        throws IllegalStateException, LoomException
    {
        if( isRunning() )
        {
            throw new IllegalStateException();
        }
        else
        {
            try
            {
                final org.jcontainer.loom.tools.profile.PartitionProfile partition =
                    m_context.getPartitionProfile().getPartition( LoomToolConstants.BLOCK_PARTITION );
                final org.jcontainer.loom.tools.profile.ComponentProfile[] blocks = partition.getComponents();
                for( int i = 0; i < blocks.length; i++ )
                {
                    final String blockName = blocks[ i ].getMetaData().getName();
                    final BlockEntry blockEntry = new BlockEntry( blocks[ i ] );
                    m_entries.put( blockName, blockEntry );
                }

                // load blocks
                runPhase( PHASE_STARTUP );
            }
            catch( final Throwable t )
            {
                getLogger().info( "exception while starting:" + t.getMessage() + "\n" );
                t.printStackTrace();
                throw new LoomException( t.getMessage(), t );
            }

            m_running = true;
        }
    }

    /**
     * Shutdown and restart the application running.
     * This is only valid when isRunning() returns true,
     * otherwise it will generate an IllegalStateException.
     * This is equivelent to  calling stop() and then start()
     * in succession.
     *
     * @throws IllegalStateException if application is not already running
     * @throws LoomException if the application failed to stop or start.
     *            the message part of exception will contain more information
     *            pertaining to why the application failed to startup/shutdown
     */
    public void restart()
        throws IllegalStateException, LoomException
    {
        stop();
        start();
    }

    /**
     * Stop the application running.
     * This is only valid when isRunning() returns true,
     * otherwise it will generate an IllegalStateException.
     *
     * @throws IllegalStateException if application is not already running
     * @throws LoomException if the application failed to shutdown.
     *            the message part of exception will contain more information
     *            pertaining to why the application failed to shutodwn
     */
    public void stop()
        throws IllegalStateException, LoomException
    {
        if( !isRunning() )
        {
            throw new IllegalStateException();
        }
        else
        {
            try
            {
                runPhase( PHASE_SHUTDOWN );
            }
            catch( final Throwable t )
            {
                getLogger().info( "exception while stopping:" + t.getMessage() + "\n" );
                t.printStackTrace();
                throw new LoomException( t.getMessage(), t );
            }

            m_running = false;
        }
    }

    public void dispose()
    {
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

    public String[] getBlockNames()
    {
        return (String[])m_entries.keySet().toArray( new String[ 0 ] );
    }

    /**
     *
     *
     * @param name
     * @return
     */
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
     * Get the name of the application.
     *
     * @return the name of the application
     */
    public String getName()
    {
        return m_context.getPartitionProfile().getMetaData().getName();
    }

    /**
     * Get the name to display in Management UI.
     *
     * @return the name of the application to display in UI
     */
    public String getDisplayName()
    {
        return m_context.getPartitionProfile().getMetaData().getName();
    }

    /**
     * Get the string used to describe the application in the UI.
     *
     * @return a short description of the application
     */
    public String getDescription()
    {
        return "The " + getDisplayName() + " application.";
    }

    /**
     * Get location of Application installation
     *
     * @return the home directory of application
     */
    public String getHomeDirectory()
    {
        return m_context.getHomeDirectory().getPath();
    }

    /**
     * Return true if the application is
     * running or false otherwise.
     *
     * @return true if application is running, false otherwise
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
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader( m_context.getClassLoader() );

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
     * Actually perform loading of each individual Listener.
     * Note that by this stage it is assumed that the Thread Context
     * has already been setup correctly.
     */
    private void doLoadBlockListeners()
        throws Exception
    {
        final org.jcontainer.loom.tools.profile.ComponentProfile[] listeners =
            getComponentsInPartition( LoomToolConstants.LISTENER_PARTITION );
        for( int i = 0; i < listeners.length; i++ )
        {
            try
            {
                startupListener( listeners[ i ] );
            }
            catch( final Exception e )
            {
                final String name = listeners[ i ].getMetaData().getName();
                final String message =
                    REZ.format( "bad-listener", "startup", name, e.getMessage() );
                getLogger().error( message, e );
                throw e;
            }
        }
    }

    private org.jcontainer.loom.tools.profile.ComponentProfile[] getComponentsInPartition( final String key )
    {
        final org.jcontainer.loom.tools.profile.PartitionProfile partition =
            m_context.getPartitionProfile().getPartition( key );
        return partition.getComponents();
    }

    /**
     * Run a phase for application.
     * Each phase transitions application into new state and processes
     * all the blocks to make sure they are in that state aswell.
     * Exceptions leave the blocks in an indeterminate state.
     *
     * @param name the name of phase (for logging purposes)
     * @throws Exception if an error occurs
     */
    private final void runPhase( final String name )
        throws Exception
    {
        //Setup thread context for calling visitors
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader( m_context.getClassLoader() );

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
     * Actually run applications phas.
     * By this methods calling it is assumed that Thread Context
     * has already been setup.
     *
     * @param name the name of phase (for logging purposes)
     * @throws Exception if an error occurs
     */
    private final void doRunPhase( final String name )
        throws Exception
    {
        final org.jcontainer.loom.tools.profile.ComponentProfile[] blocks =
            getComponentsInPartition( LoomToolConstants.BLOCK_PARTITION );
        final String[] order = DependencyGraph.walkGraph( PHASE_STARTUP == name, blocks );

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
            final org.jcontainer.loom.tools.profile.PartitionProfile partition = m_context.getPartitionProfile();
            final File homeDirectory = m_context.getHomeDirectory();
            final SarMetaData sarMetaData =
                ComponentMetaDataConverter.toSarMetaData( partition, homeDirectory );
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
                final String message = REZ.format( "process-block", block, name );
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
                    REZ.format( "app.error.run-phase", name, block, e.getMessage() );
                getLogger().error( message, e );
                m_listenerSupport.applicationFailure( e );
                throw e;
            }

            //Log message saying we have processed block
            if( getLogger().isDebugEnabled() )
            {
                final String message = REZ.format( "processed-block", block, name );
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
     * Method to run a Block through it's startup phase.
     * This will involve notification of {@link ApplicationListener}
     * objects, creation of the Block/Block Proxy object, calling the startup
     * Avalon Lifecycle methods and updating State property of BlockEntry.
     * Errors that occur during shutdown will be logged appropriately and
     * cause exceptions with useful messages to be raised.
     *
     * @param entry the entry containing Block
     * @throws Exception if an error occurs when block passes
     *            through a specific lifecycle stage
     */
    private void startup( final BlockEntry entry )
        throws Exception
    {
        final Object block =
            m_lifecycleHelper.startup( entry.getName(),
                                       entry.getProfile(),
                                       m_blockAccessor );

        m_exportHelper.exportBlock( m_context,
                                    entry.getProfile(),
                                    block );

        entry.setObject( block );

        m_listenerSupport.fireBlockAddedEvent( entry );
    }

    /**
     * Method to run a Block through it's shutdown phase.
     * This will involve notification of {@link ApplicationListener}
     * objects, invalidating the proxy object, calling the shutdown
     * Avalon Lifecycle methods and updating State property of BlockEntry.
     * Errors that occur during shutdown will be logged appropraitely.
     *
     * @param entry the entry containing Block
     */
    private void shutdown( final BlockEntry entry )
        throws LifecycleException
    {
        m_listenerSupport.fireBlockRemovedEvent( entry );

        final Object object = entry.getObject();
        try
        {
            //Remove block from Management system
            m_exportHelper.unexportBlock( m_context,
                                          entry.getProfile() );
            entry.invalidate();

            m_lifecycleHelper.shutdown( entry.getName(),
                                        object );
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
     * @throws Exception if an error occurs when listener passes
     *            through a specific lifecycle stage
     */
    private void startupListener( final org.jcontainer.loom.tools.profile.ComponentProfile profile )
        throws Exception
    {
        final String name = profile.getMetaData().getName();
        final Object listener =
            m_lifecycleHelper.startup( name,
                                       profile,
                                       m_blockAccessor );

        // However onky ApplicationListners can avail of block events.
        if( listener instanceof ApplicationListener )
        {
            m_listenerSupport.addApplicationListener( (ApplicationListener)listener );
        }
        else
        {
            // As ApplicationListners are BlockListeners then
            //this is applicable for all
            m_listenerSupport.addBlockListener( (BlockListener)listener );

            final String message =
                REZ.format( "helper.isa-blocklistener.error",
                            name,
                            profile.getMetaData().getImplementationKey() );
            getLogger().error( message );
            System.err.println( message );
        }
    }
}
