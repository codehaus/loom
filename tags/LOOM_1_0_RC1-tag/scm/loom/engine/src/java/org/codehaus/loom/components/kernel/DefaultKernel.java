/* ====================================================================
 * Loom Software License, version 1.1
 *
 * Copyright (c) 2003, Loom Group. All rights reserved.
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
 * 3. Neither the name of the Loom Group nor the name "Loom" nor
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
 * Loom Loom includes code from the Apache Software Foundation
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
package org.codehaus.loom.components.kernel;

import java.io.File;
import java.util.HashMap;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.excalibur.instrument.InstrumentManager;
import org.codehaus.loom.components.application.DefaultApplication;
import org.codehaus.loom.components.util.profile.PartitionProfile;
import org.codehaus.loom.interfaces.Application;
import org.codehaus.loom.interfaces.ApplicationContext;
import org.codehaus.loom.interfaces.ConfigurationInterceptor;
import org.codehaus.loom.interfaces.ConfigurationValidator;
import org.codehaus.loom.interfaces.Kernel;
import org.codehaus.loom.interfaces.LoomException;
import org.codehaus.loom.interfaces.SystemManager;
import org.codehaus.spice.loggerstore.LoggerStore;
import org.codehaus.spice.salt.i18n.ResourceManager;
import org.codehaus.spice.salt.i18n.Resources;
import org.codehaus.dna.AbstractLogEnabled;
import org.codehaus.dna.Active;
import org.codehaus.dna.Composable;
import org.codehaus.dna.Configurable;
import org.codehaus.dna.Configuration;
import org.codehaus.dna.ConfigurationException;
import org.codehaus.dna.Logger;
import org.codehaus.dna.MissingResourceException;
import org.codehaus.dna.ResourceLocator;
import org.codehaus.dna.impl.DefaultResourceLocator;

/**
 * The ServerKernel is the core of the container system. The kernel is
 * responsible for orchestrating low level services such as loading, configuring
 * and destroying blocks. It also gives access to basic facilities such as
 * scheduling sub-systems, protected execution contexts, naming and directory
 * services etc.
 *
 * Note that no facilities are available until after the Kernel has been
 * configured and initialized.
 *
 * @author Peter Donald
 * @author Leo Simons
 * @dna.component
 * @mx.component
 */
public class DefaultKernel
    extends AbstractLogEnabled
    implements Kernel, Active, Composable, Configurable
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( DefaultKernel.class );

    ///SystemManager provided by Embeddor
    private SystemManager m_systemManager;

    private SystemManager m_applicationManager;

    ///Configuration Repository
    private ConfigurationInterceptor m_repository;

    //Configuration Validator
    private ConfigurationValidator m_validator;

    //Instrument Manager
    private InstrumentManager m_instrumentManager;

    private final HashMap m_entries = new HashMap();

    //Allow applications that fail to startup to remain in the kernel in a stopped state?
    private boolean m_addInvalidApplications;

    //Counter to provide simple locking. when the count is 0, the kernel is unlocked
    private int m_lockCount;

    //List of applications to return when the kernel is locked
    private String[] m_lockedApplications;

    /**
     * @dna.dependency type="InstrumentManager"
     * @dna.dependency type="SystemManager"
     * @dna.dependency type="ConfigurationInterceptor"
     * @dna.dependency type="ConfigurationValidator"
     */
    public void compose( final ResourceLocator locator )
        throws MissingResourceException
    {
        m_systemManager = (SystemManager)locator.
            lookup( SystemManager.class.getName() );
        m_repository = (ConfigurationInterceptor)locator.
            lookup( ConfigurationInterceptor.class.getName() );
        m_validator = (ConfigurationValidator)locator.
            lookup( ConfigurationValidator.class.getName() );
        m_instrumentManager = (InstrumentManager)locator.
            lookup( InstrumentManager.class.getName() );
    }

    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        m_addInvalidApplications =
        configuration.getChild( "add-invalid-applications" ).getValueAsBoolean(
            false );
    }

    public void initialize()
        throws Exception
    {
        m_applicationManager =
        m_systemManager.getSubContext( null, "application" );
    }

    public void dispose()
    {
        final String[] names = getApplicationNames();
        for( int i = 0; i < names.length; i++ )
        {
            try
            {
                final SarEntry entry = (SarEntry)m_entries.get( names[ i ] );
                shutdown( entry );
            }
            catch( final Exception e )
            {
                final String message = REZ.format(
                    "kernel.error.entry.dispose", names[ i ] );
                getLogger().warn( message, e );
            }
        }
    }

    /**
     * Lock the kernel, temporarily preserving the list of applications running
     * in the container
     */
    public void lock()
    {
        synchronized( this )
        {
            m_lockedApplications = getApplicationNames();
            m_lockCount++;

            if( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "Kernel locked [count:" + m_lockCount + "]" );
            }
        }
    }

    /**
     * Unlock the kernel, restoring the list of applications to be the current
     * active list
     */
    public void unlock()
    {
        synchronized( this )
        {
            m_lockCount--;

            if( m_lockCount < 0 )
            {
                throw new IllegalStateException(
                    REZ.getString( "kernel.error.negativelock" ) );
            }

            if( getLogger().isDebugEnabled() )
            {
                getLogger().debug(
                    "Kernel unlocked [count:" + m_lockCount + "]" );
            }
        }
    }

    /**
     * @mx.attribute description="the list of applications running in the
     * container"
     */
    public String[] getApplicationNames()
    {
        if( isLocked() )
        {
            return m_lockedApplications;
        }
        else
        {
            return (String[])m_entries.keySet().toArray( new String[ 0 ] );
        }
    }

    private boolean isLocked()
    {
        return m_lockCount > 0;
    }

    public Application getApplication( final String name )
    {
        final SarEntry entry = (SarEntry)m_entries.get( name );
        if( null == entry )
        {
            return null;
        }
        else
        {
            return entry.getApplication();
        }
    }

    /**
     * Create and initialize the application instance if it is not already
     * initialized.
     *
     * @param entry the entry for application
     * @throws Exception if an error occurs
     */
    private void startup( final SarEntry entry )
        throws Exception
    {
        //lock for application startup and shutdown
        synchronized( entry )
        {
            final String name = entry.getProfile().getMetaData().getName();

            Application application = entry.getApplication();
            if( null == application )
            {
                try
                {
                    final Application newApp = new DefaultApplication();
                    final Logger childLogger =
                        getLogger().getChildLogger( name );
                    org.codehaus.dna.impl.ContainerUtil.enableLogging(
                        newApp, childLogger );

                    final ApplicationContext context =
                        createApplicationContext( entry );
                    newApp.setApplicationContext( context );

                    org.codehaus.dna.impl.ContainerUtil.initialize( newApp );

                    application = newApp;
                }
                catch( final Throwable t )
                {
                    //Initialization failed so clean entry
                    //so invalid instance is not used
                    entry.setApplication( null );

                    final String message =
                        REZ.format( "kernel.error.entry.initialize",
                                    entry.getProfile().getMetaData().getName() );
                    throw new LoomException( message, t );
                }

                try
                {
                    ContainerUtil.start( application );
                }
                catch( final Throwable t )
                {
                    final String message =
                        REZ.format( "kernel.error.entry.start",
                                    entry.getProfile().getMetaData().getName() );

                    if( m_addInvalidApplications )
                    {
                        getLogger().warn( message, t );
                    }
                    else
                    {
                        //Initialization failed so clean entry
                        //so invalid instance is not used
                        entry.setApplication( null );

                        throw new LoomException( message, t );
                    }
                }

                entry.setApplication( application );

                // manage application
                try
                {
                    m_applicationManager.register( name, application );
                }
                catch( final Throwable t )
                {
                    final String message =
                        REZ.format( "kernel.error.entry.manage", name );
                    throw new LoomException( message, t );
                }
            }
        }
    }

    private void shutdown( final SarEntry entry )
        throws Exception
    {
        //lock for application startup and shutdown
        synchronized( entry )
        {
            final Application application = entry.getApplication();
            if( null != application )
            {
                entry.setApplication( null );
                ContainerUtil.stop( application );
                org.codehaus.dna.impl.ContainerUtil.dispose( application );
            }
            else
            {
                final String message =
                    REZ.format( "kernel.error.entry.nostop",
                                entry.getProfile().getMetaData().getName() );
                getLogger().warn( message );
            }
        }
    }

    public void addApplication( final PartitionProfile profile,
                                final File homeDirectory,
                                final File workDirectory,
                                final ClassLoader classLoader,
                                final LoggerStore store )
        throws Exception
    {

        final String name = profile.getMetaData().getName();
        final SarEntry entry =
            new SarEntry( profile, homeDirectory,
                          workDirectory, classLoader,
                          store );
        m_entries.put( name, entry );

        try
        {
            startup( entry );
        }
        catch( final Exception e )
        {
            final String message =
                REZ.format( "kernel.error.entry.start", name );
            getLogger().warn( message, e );
            throw e;
        }
    }

    private ApplicationContext createApplicationContext( final SarEntry entry )
        throws Exception
    {
        final String name = entry.getProfile().getMetaData().getName();

        final DefaultApplicationContext context =
            new DefaultApplicationContext( entry.getProfile(),
                                           entry.getHomeDirectory(),
                                           entry.getWorkDirectory(),
                                           entry.getClassLoader(),
                                           entry.getLoggerStore() );

        org.codehaus.dna.impl.ContainerUtil.
            enableLogging( context, createContextLogger( name ) );
        org.codehaus.dna.impl.ContainerUtil.
            compose( context, createResourceLocator() );
        org.codehaus.dna.impl.ContainerUtil.initialize( context );
        return context;
    }

    /**
     * Create a logger for specified ApplicationContext.
     *
     * @param name the name of application name
     * @return the Logger for context
     */
    private Logger createContextLogger( final String name )
    {
        final String loggerName = name + ".frame";
        final Logger childLogger =
            getLogger().getChildLogger( loggerName );
        return childLogger;
    }

    private ResourceLocator createResourceLocator()
    {
        final DefaultResourceLocator serviceManager = new DefaultResourceLocator();
        serviceManager.put( SystemManager.class.getName(), m_systemManager );
        serviceManager.put( ConfigurationInterceptor.class.getName(),
                            m_repository );
        serviceManager.put( ConfigurationValidator.class.getName(),
                            m_validator );
        serviceManager.put( InstrumentManager.class.getName(),
                            m_instrumentManager );
        serviceManager.put( Kernel.class.getName(), this );
        serviceManager.makeReadOnly();
        return serviceManager;
    }

    /**
     * @mx.operation description="Removes the application from the container"
     * @mx.parameter name="name" description="the name of application to
     * remove"
     */
    public void removeApplication( final String name )
        throws Exception
    {
        final SarEntry entry = (SarEntry)m_entries.remove( name );
        if( null == entry )
        {
            final String message =
                REZ.format( "kernel.error.entry.initialize", name );
            throw new Exception( message );
        }
        else
        {
            // un-manage application
            try
            {
                m_applicationManager.unregister( name );
            }
            catch( final Throwable t )
            {
                final String message =
                    REZ.format( "kernel.error.entry.unmanage", name );
                throw new LoomException( message, t );
            }

            shutdown( entry );
        }
    }
}
