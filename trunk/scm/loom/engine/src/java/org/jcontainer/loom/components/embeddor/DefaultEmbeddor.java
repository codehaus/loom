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
package org.jcontainer.loom.components.embeddor;

import java.io.File;
import java.util.Date;
import java.util.Observer;
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
import org.codehaus.dna.impl.ContainerUtil;
import org.codehaus.dna.impl.DefaultResourceLocator;
import org.jcontainer.loom.interfaces.ContainerConstants;
import org.jcontainer.loom.interfaces.Deployer;
import org.jcontainer.loom.interfaces.Embeddor;
import org.jcontainer.loom.interfaces.Kernel;
import org.jcontainer.loom.interfaces.LoomException;
import org.jcontainer.loom.interfaces.SystemManager;

/**
 * This is the object that is interacted with to create, manage and dispose of
 * the kernel and related resources.
 *
 * @author Leo Simons
 * @author Peter Donald
 * @author Joerg Bauer
 * @dna.component
 * @mx.component
 */
public class DefaultEmbeddor
    extends AbstractLogEnabled
    implements Embeddor, Composable, Configurable, Active
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( DefaultEmbeddor.class );

    private final EmbeddorObservable m_observable = new EmbeddorObservable();

    private File m_loomHome;

    private EmbeddorEntry[] m_entries;

    /**
     * If true, flag indicates that the Embeddor should continue running even
     * when there are no applications in kernel. Otherwise the Embeddor will
     * shutdown when it detects there is no longer any applications running.
     */
    private boolean m_persistent;

    /**
     * Flag is set to true when the embeddor should  shut itself down. It is set
     * to true as a result of a call to shutdown() method.
     *
     * @see Embeddor#shutdown()
     */
    private boolean m_shutdown;

    /** Time at which the embeddor was started. */
    private long m_startTime;

    /** The default directory in which applications are deployed from. */
    private ClassLoader m_commonClassLoader;
    private ClassLoader m_containerClassLoader;

    /**
     * @dna.dependency type="Observer" optional="true"
     * @dna.dependency type="ClassLoader"  qualifier="common"
     * @dna.dependency type="ClassLoader" qualifier="container"
     * @dna.dependency type="File" qualifier="home"
     * @dna.dependency type="File" qualifier="apps"
     * @dna.dependency type="Boolean" qualifier="persistent"
     */
    public void compose( final ResourceLocator locator )
        throws MissingResourceException
    {
        if( locator.contains( Observer.class.getName() ) )
        {
            final Observer observer = (Observer)locator.lookup(
                Observer.class.getName() );
            m_observable.addObserver( observer );
        }
        else
        {
            final String message = REZ.getString( "embeddor.notice.no-restart" );
            getLogger().warn( message );
        }
        m_commonClassLoader = (ClassLoader)
            locator.lookup( ClassLoader.class.getName() + "/common" );
        m_containerClassLoader = (ClassLoader)
            locator.lookup( ClassLoader.class.getName() + "/container" );
        m_loomHome = (File)locator.lookup( File.class.getName() + "/home" );
        final Boolean persistent =
            (Boolean)locator.lookup( Boolean.class.getName() + "/persistent" );
        m_persistent = persistent.booleanValue();
    }

    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        final Configuration[] children = configuration.getChildren(
            "component" );
        m_entries = new EmbeddorEntry[ children.length ];
        for( int i = 0; i < children.length; i++ )
        {
            final String role = children[ i ].getAttribute( "role", null );
            final String classname = children[ i ].getAttribute( "class" );
            final String logger = children[ i ].getAttribute( "logger" );
            m_entries[ i ] =
            new EmbeddorEntry( role, classname, logger, children[ i ] );
        }
    }

    /**
     * Creates the core handlers - logger, deployer, Manager and Kernel. Note
     * that these are not set up properly until you have called the {@link
     * #execute()} method.
     */
    public void initialize()
        throws Exception
    {
        m_startTime = System.currentTimeMillis();
        try
        {
            createComponents();
            setupComponents();
            registerComponents();
        }
        catch( final Exception e )
        {
            // whoops!
            final String message = REZ.getString(
                "embeddor.error.start.failed" );
            getLogger().error( message, e );
            throw e;
        }
    }

    /**
     * This is the main method of the embeddor. It sets up the core components,
     * and then deploys the <code>Facilities</code>. These are registered with
     * the Kernel and the Manager. The same happens for the {@link
     * org.jcontainer.loom.interfaces.Application}s. Now, the Kernel is taken
     * through its lifecycle. When it is finished, as well as all the
     * applications running in it, it is shut down, after which the Embeddor is
     * as well.
     */
    public void execute()
        throws Exception
    {
        //  If the kernel is empty at this point, it is because the server was
        //  started without supplying any applications, display a message to
        //  give the user a clue as to why the server is shutting down
        //  immediately.
        if( emptyKernel() )
        {
            final String message = REZ.getString(
                "embeddor.error.start.no-apps" );
            getLogger().error( message );
        }
        else
        {
            // loop until Shutdown occurs.
            while( true )
            {
                // wait() for shutdown() to take action...
                if( m_shutdown
                    || ( emptyKernel() && !m_persistent ) )
                {
                    // The server will shut itself down when all applications are disposed.
                    if( emptyKernel() )
                    {
                        final String message =
                            REZ.getString(
                                "embeddor.shutdown.all-apps-disposed" );
                        getLogger().info( message );
                    }
                    break;
                }
                gotoSleep();
            }
        }
    }

    private boolean emptyKernel()
    {
        final Kernel kernel = (Kernel)getEmbeddorComponent(
            Kernel.class.getName() );
        if( null != kernel )
        {
            final String[] names = kernel.getApplicationNames();
            return ( 0 == names.length );
        }
        else
        {
            //Consider the kernel empty
            //if it has been shutdown
            return true;
        }
    }

    private void gotoSleep()
    {
        try
        {
            synchronized( this )
            {
                wait( 1000 );
            }
        }
        catch( final InterruptedException e )
        {
            //NOOP
        }
    }

    /**
     * Release all the resources associated with kernel.
     */
    public synchronized void dispose()
    {
        shutdown();
        try
        {
            unregisterComponents();
            shutdownComponents();
        }
        catch( final Exception e )
        {
            // whoops!
            final String message = REZ.getString(
                "embeddor.error.shutdown.failed" );
            getLogger().error( message, e );
        }
        for( int i = 0; i < m_entries.length; i++ )
        {
            m_entries[ i ].setObject( null );
        }
        System.gc(); // make sure resources are released
    }

    /**
     * @mx.operation description="Request the Embeddor shutsdown."
     */
    public void shutdown()
    {
        m_shutdown = true;
        synchronized( this )
        {
            notifyAll();
        }
    }

    /**
     * @throws UnsupportedOperationException if restart not a supported
     * operation
     * @mx.operation description="Request the Embeddor restart."
     */
    public void restart()
        throws UnsupportedOperationException
    {
        try
        {
            m_observable.change();
            m_observable.notifyObservers( "restart" );
        }
        catch( final Exception e )
        {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Get name by which the server is known. Usually this defaults to {@link
     * ContainerConstants#SOFTWARE} but the admin may assign another name. This
     * is useful when you are managing a cluster of servers.
     *
     * @return the name of server
     * @mx.attribute description="The name by which the server is known."
     */
    public String getName()
    {
        return ContainerConstants.SOFTWARE;
    }

    /**
     * Get location of container installation
     *
     * @return the home directory of container
     * @mx.attribute description="The location of container installation."
     */
    public String getHomeDirectory()
    {
        return m_loomHome.getPath();
    }

    /**
     * Get the date at which this server started.
     *
     * @return the date at which this server started
     * @mx.attribute description="the date at which this server started."
     */
    public Date getStartTime()
    {
        return new Date( m_startTime );
    }

    /**
     * Retrieve the number of millisecond the server has been up.
     *
     * @return the the number of millisecond the server has been up
     * @mx.attribute description="the number of millisecond the server has been
     * up."
     */
    public long getUpTimeInMillis()
    {
        return System.currentTimeMillis() - m_startTime;
    }

    /**
     * Retrieve a string identifying version of server. Usually looks like
     * "x.y.z".
     *
     * @return version string of server.
     * @mx.attribute description="Retrieve a string identifying version of
     * server."
     */
    public String getVersion()
    {
        return ContainerConstants.VERSION;
    }

    /**
     * Get a string defining the build. Possibly the date on which it was built,
     * where it was built, with what features it was built and so forth.
     *
     * @return the string describing build
     * @mx.attribute description="a string defining the build."
     */
    public String getBuild()
    {
        return "(" + ContainerConstants.DATE + ")";
    }

    //////////////////////
    /// HELPER METHODS ///
    //////////////////////
    /**
     * Create the logger, deployer and kernel components. Note that these
     * components are not ready to be used until setupComponents() is called.
     */
    private synchronized void createComponents()
        throws Exception
    {
        try
        {
            for( int i = 0; i < m_entries.length; i++ )
            {
                final String className = m_entries[ i ].getClassName();
                final Class clazz = Class.forName( className );
                final Object object = createObject( className, clazz );
                m_entries[ i ].setObject( object );
            }
        }
        catch( final Exception e )
        {
            final String message =
                REZ.getString( "embeddor.error.createComponents.failed" );
            getLogger().error( message, e );
            throw new LoomException( message, e );
        }
    }

    protected final synchronized void deployFile( final String name,
                                                  final File file )
        throws Exception
    {
        final Deployer deployer = (Deployer)getEmbeddorComponent(
            Deployer.class.getName() );
        deployer.deploy( name, file.toURL() );
    }

    private void setupComponents()
        throws Exception
    {
        for( int i = 0; i < m_entries.length; i++ )
        {
            final EmbeddorEntry entry = m_entries[ i ];
            setupComponent( entry.getObject(),
                            entry.getLoggerName(),
                            entry.getConfiguration() );
        }
    }

    /**
     * Setup a component and run it through al of it's setup lifecycle stages.
     *
     * @param object the component
     * @throws Exception if an error occurs
     */
    private void setupComponent( final Object object,
                                 final String loggerName,
                                 final Configuration config )
        throws Exception
    {
        final Logger childLogger = getLogger().getChildLogger( loggerName );
        ContainerUtil.enableLogging( object, childLogger );
        ContainerUtil.compose( object, getResourceLocator() );
        ContainerUtil.configure( object, config );
        ContainerUtil.initialize( object );
    }

    private void shutdownComponents()
        throws Exception
    {
        //for( int i = m_entries.length - 1; i >= 0; i-- )
        for( int i = 0; i < m_entries.length; i++ )
        {
            final Object object = m_entries[ i ].getObject();
            if( null == object )
            {
                continue;
            }
            ContainerUtil.dispose( object );
        }
    }

    /**
     * Create a component that implements an interface.
     *
     * @param classname the name of the objects class
     * @param service the name of interface/type
     * @return the created object
     * @throws Exception if an error occurs
     */
    private Object createObject( final String classname,
                                 final Class service )
        throws Exception
    {
        try
        {
            final Object object = Class.forName( classname ).newInstance();
            if( !service.isInstance( object ) )
            {
                final String message =
                    REZ.format( "bad-type.error",
                                classname,
                                service.getName() );
                throw new Exception( message );
            }
            return object;
        }
        catch( final IllegalAccessException iae )
        {
            final String message = REZ.format( "bad-ctor.error",
                                               service.getName(),
                                               classname );
            throw new LoomException( message, iae );
        }
        catch( final InstantiationException ie )
        {
            final String message =
                REZ.format( "no-instantiate.error",
                            service.getName(),
                            classname );
            throw new LoomException( message, ie );
        }
        catch( final ClassNotFoundException cnfe )
        {
            final String message = REZ.format( "no-class.error",
                                               service.getName(),
                                               classname );
            throw new LoomException( message, cnfe );
        }
    }

    /**
     * Register embeddor and it's components to <code>SystemManager</code>.
     */
    private void registerComponents()
        throws Exception
    {
        final SystemManager systemManager =
            (SystemManager)getResourceLocator().lookup(
                SystemManager.class.getName() );

        final SystemManager mxExporter =
            systemManager.getSubContext( null, "component" );

        mxExporter.register( "Embeddor", this );
        for( int i = 0; i < m_entries.length; i++ )
        {
            final EmbeddorEntry entry = m_entries[ i ];
            mxExporter.register( entry.getLoggerName(),
                                 entry.getObject() );
        }
    }

    /**
     * Unregister embeddor and it's components from {@link SystemManager}.
     */
    private void unregisterComponents()
        throws Exception
    {
        final SystemManager systemManager =
            (SystemManager)getResourceLocator().lookup(
                SystemManager.class.getName() );

        final SystemManager mxExporter =
            systemManager.getSubContext( null, "component" );

        mxExporter.unregister( "Embeddor" );

        for( int i = 0; i < m_entries.length; i++ )
        {
            final EmbeddorEntry entry = m_entries[ i ];
            mxExporter.unregister( entry.getLoggerName() );
        }
    }

    private ResourceLocator getResourceLocator()
        throws Exception
    {
        final DefaultResourceLocator locator = new DefaultResourceLocator();
        locator.put( Embeddor.class.getName(), this );
        for( int i = 0; i < m_entries.length; i++ )
        {
            final String role = m_entries[ i ].getRole();
            if( null == role )
            {
                continue;
            }
            final Object component = getEmbeddorComponent( role );
            if( null != component )
            {
                locator.put( role, component );
            }
        }

        locator.put( File.class.getName() + "/home", m_loomHome );
        locator.put( ClassLoader.class.getName() + "/common",
                     m_commonClassLoader );
        locator.put( ClassLoader.class.getName() + "/container",
                     m_containerClassLoader );

        return locator;
    }

    private Object getEmbeddorComponent( final String role )
    {
        for( int i = 0; i < m_entries.length; i++ )
        {
            final EmbeddorEntry entry = m_entries[ i ];
            final String candidate = entry.getRole();
            if( null == candidate )
            {
                continue;
            }
            if( candidate.equals( role ) )
            {
                return m_entries[ i ].getObject();
            }
        }
        // Should never happen
        // TODO: create error / warning
        return null;
    }
}


