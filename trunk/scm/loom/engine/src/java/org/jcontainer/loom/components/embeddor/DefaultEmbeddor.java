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
import java.util.Arrays;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.jcontainer.dna.Active;
import org.jcontainer.dna.Configurable;
import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.jcontainer.dna.ResourceLocator;
import org.jcontainer.dna.impl.DefaultResourceLocator;
import org.jcontainer.loom.components.util.ExtensionFileFilter;
import org.jcontainer.loom.interfaces.ContainerConstants;
import org.jcontainer.loom.interfaces.Deployer;
import org.jcontainer.loom.interfaces.Embeddor;
import org.jcontainer.loom.interfaces.EmbeddorMBean;
import org.jcontainer.loom.interfaces.Kernel;
import org.jcontainer.loom.interfaces.LoomException;
import org.jcontainer.loom.interfaces.SystemManager;
import org.realityforge.salt.i18n.ResourceManager;
import org.realityforge.salt.i18n.Resources;

/**
 * This is the object that is interacted with to create, manage and
 * dispose of the kernel and related resources.
 *
 * @author <a href="mail@leosimons.com">Leo Simons</a>
 * @author <a href="peter at realityforge.org">Peter Donald</a>
 * @author <a href="bauer@denic.de">Joerg Bauer</a>
 */
public class DefaultEmbeddor
    extends AbstractLogEnabled
    implements Embeddor, EmbeddorMBean, Contextualizable,
    Parameterizable, Configurable, Active
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( DefaultEmbeddor.class );

    private static final String DEFAULT_APPS_PATH = "/apps";

    private final EmbeddorObservable m_observable = new EmbeddorObservable();

    private Parameters m_parameters;

    /**
     * Context passed to embeddor. See the contextualize() method
     * for details on what is stored in context.
     *
     * @see DefaultEmbeddor#contextualize(Context)
     */
    private Context m_context;

    private String m_loomHome;

    private EmbeddorEntry[] m_entries;

    /**
     * If true, flag indicates that the Embeddor should continue running
     * even when there are no applications in kernel. Otherwise the
     * Embeddor will shutdown when it detects there is no longer any
     * applications running.
     */
    private boolean m_persistent;

    /**
     * Flag is set to true when the embeddor should  shut itself down.
     * It is set to true as a result of a call to shutdown() method.
     *
     * @see Embeddor#shutdown()
     */
    private boolean m_shutdown;

    /**
     * Time at which the embeddor was started.
     */
    private long m_startTime;

    /**
     * The default directory in which applications are deployed from.
     */
    private String m_appDir;

    /**
     * Pass the Context to the embeddor.
     * It is expected that the following will be entries in context;
     * <ul>
     *   <li><b>common.classloader</b>: ClassLoader shared betweeen
     *      container and applications</li>
     *   <li><b>container.classloader</b>: ClassLoader used to load
     *      container</li>
     * </ul>
     *
     * @param context
     * @throws ContextException
     */
    public void contextualize( final Context context )
        throws ContextException
    {
        m_context = context;
        try
        {
            final Observer observer = (Observer)context.get( Observer.class.getName() );
            m_observable.addObserver( observer );
        }
        catch( final ContextException ce )
        {
            final String message = REZ.getString( "embeddor.notice.no-restart" );
            getLogger().warn( message );
        }
    }

    /**
     * Set parameters for this component.
     * This must be called after contextualize() and before initialize()
     *
     * Make sure to provide all the neccessary information through
     * these parameters. All information it needs consists of strings.
     * There are two types of strings included in parameters. The first
     * type include parameters used to setup proeprties of the embeddor.
     * The second type include the implementation names of the components
     * that the Embeddor manages. For instance if you want to replace the
     * {@link org.jcontainer.loom.interfaces.ConfigurationInterceptor}
     * with your own repository you would pass in a parameter such as;</p>
     * <p>org.jcontainer.loom.interfaces.ConfigurationInterceptor =
     * com.biz.MyCustomConfigurationRepository</p>
     *
     * <p>Of the other type of parameters, the following are supported by
     * the DefaultEmbeddor implementation of Embeddor. Note that some of
     * the embedded components may support other parameters.</p>
     * <ul>
     * <li><b>loom.home</b>, the home directory of loom. Defaults
     * to "..".</li>
     * <li><b>log-destination</b>, the file to save log
     * messages in. If omitted, ${loom.home}/logs/loom.log is used.</li>
     * <li><b>log-priority</b>, the priority at which log messages are filteres.
     * If omitted, then INFO will be default level used.</li>
     * <li><b>applications-directory</b>, the directory in which
     * the default applications to be loaded by the kernel are stored
     * (in .sar format). Defaults to ${loom.home}/apps</li>
     * </ul>
     *
     * @param parameters the Parameters for embeddor
     * @throws ParameterException if an error occurs
     */
    public synchronized void parameterize( final Parameters parameters )
        throws ParameterException
    {
        m_parameters = parameters;
        m_loomHome = m_parameters.getParameter( "loom.home", ".." );
        m_persistent = m_parameters.getParameterAsBoolean( "persistent", false );
        m_appDir = m_parameters.getParameter( "loom.apps.dir",
                                              m_loomHome + DEFAULT_APPS_PATH );
    }

    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        final Configuration[] children = configuration.getChildren( "component" );
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
     * Creates the core handlers - logger, deployer, Manager and
     * Kernel. Note that these are not set up properly until you have
     * called the {@link #execute()} method.
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
            final String message = REZ.getString( "embeddor.error.start.failed" );
            getLogger().fatalError( message, e );
            throw e;
        }
    }

    /**
     * This is the main method of the embeddor. It sets up the core
     * components, and then deploys the <code>Facilities</code>. These
     * are registered with the Kernel and the Manager. The same
     * happens for the {@link org.jcontainer.loom.interfaces.Application}s.
     * Now, the Kernel is taken through its lifecycle. When it is
     * finished, as well as all the applications running in it, it
     * is shut down, after which the Embeddor is as well.
     */
    public void execute()
        throws Exception
    {
        deployDefaultApplications();

        //  If the kernel is empty at this point, it is because the server was
        //  started without supplying any applications, display a message to
        //  give the user a clue as to why the server is shutting down
        //  immediately.
        if( emptyKernel() )
        {
            final String message = REZ.getString( "embeddor.error.start.no-apps" );
            getLogger().fatalError( message );
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
                            REZ.getString( "embeddor.shutdown.all-apps-disposed" );
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
        final Kernel kernel = getKernel();
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
            final String message = REZ.getString( "embeddor.error.shutdown.failed" );
            getLogger().fatalError( message, e );
        }
        for( int i = 0; i < m_entries.length; i++ )
        {
            m_entries[ i ].setObject( null );
        }
        System.gc(); // make sure resources are released
    }

    /**
     * Request the Embeddor shutsdown.
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
     * Ask the embeddor to restart itself if this operation is supported.
     *
     * @throws UnsupportedOperationException if restart not supported
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
     * Get name by which the server is know.
     * Usually this defaults to {@link ContainerConstants#SOFTWARE} but the admin
     * may assign another name. This is useful when you
     * are managing a cluster of servers.
     *
     * @return the name of server
     */
    public String getName()
    {
        return ContainerConstants.SOFTWARE;
    }

    /**
     * Get location of Phoenix installation
     *
     * @return the home directory of loom
     */
    public String getHomeDirectory()
    {
        return m_loomHome;
    }

    /**
     * Get the date at which this server started.
     *
     * @return the date at which this server started
     */
    public Date getStartTime()
    {
        return new Date( m_startTime );
    }

    /**
     * Retrieve the number of millisecond
     * the server has been up.
     *
     * @return the the number of millisecond the server has been up
     */
    public long getUpTimeInMillis()
    {
        return System.currentTimeMillis() - m_startTime;
    }

    /**
     * Retrieve a string identifying version of server.
     * Usually looks like "v4.0.1a".
     *
     * @return version string of server.
     */
    public String getVersion()
    {
        return ContainerConstants.VERSION;
    }

    /**
     * Get a string defining the build.
     * Possibly the date on which it was built, where it was built,
     * with what features it was built and so forth.
     *
     * @return the string describing build
     */
    public String getBuild()
    {
        return "(" + ContainerConstants.DATE + ")";
    }

    //////////////////////
    /// HELPER METHODS ///
    //////////////////////
    /**
     * Create the logger, deployer and kernel components.
     * Note that these components are not ready to be used
     * until setupComponents() is called.
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
            getLogger().fatalError( message, e );
            throw new LoomException( message, e );
        }
    }

    /**
     * The deployer is used to load the applications from the
     * default-apps-location specified in Parameters.
     *
     * @throws Exception if an error occurs
     */
    protected void deployDefaultApplications()
        throws Exception
    {
        //Name of optional application specified on CLI
        final String application =
            m_parameters.getParameter( "application-location", null );
        if( null != application )
        {
            final File file = new File( application );
            deployFile( file );
        }
        if( null != m_appDir )
        {
            final File directory = new File( m_appDir );
            final ExtensionFileFilter filter = new ExtensionFileFilter( ".sar" );
            final File[] files = directory.listFiles( filter );
            if( null != files )
            {
                deployFiles( files );
            }
        }
    }

    private void deployFiles( final File[] files )
        throws Exception
    {
        Arrays.sort( files );
        for( int i = 0; i < files.length; i++ )
        {
            deployFile( files[ i ] );
        }
    }

    private void deployFile( final File file )
        throws Exception
    {
        final String filename = file.getName();
        int index = filename.lastIndexOf( '.' );
        if( -1 == index )
        {
            index = filename.length();
        }
        final String name = filename.substring( 0, index );
        final File canonicalFile = file.getCanonicalFile();
        deployFile( name, canonicalFile );
    }

    protected final synchronized void deployFile( final String name, final File file )
        throws Exception
    {
        final Deployer deployer = (Deployer)getEmbeddorComponent( Deployer.class.getName() );
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
     * Setup a component and run it through al of it's
     * setup lifecycle stages.
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
        ContainerUtil.contextualize( object, m_context );
        org.jcontainer.dna.impl.ContainerUtil.compose( object, getResourceLocator() );
        ContainerUtil.parameterize( object, createChildParameters() );
        org.jcontainer.dna.impl.ContainerUtil.configure( object, config );
        org.jcontainer.dna.impl.ContainerUtil.initialize( object );
        ContainerUtil.start( object );
    }

    private Parameters createChildParameters()
    {
        final Parameters parameters = new Parameters();
        parameters.merge( m_parameters );
        parameters.setParameter( "loom.apps.dir", m_appDir );
        return parameters;
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
            ContainerUtil.stop( object );
            org.jcontainer.dna.impl.ContainerUtil.dispose( object );
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
            final String message = REZ.format( "bad-ctor.error", service.getName(), classname );
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
            final String message = REZ.format( "no-class.error", service.getName(), classname );
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
            (SystemManager)getResourceLocator().lookup( SystemManager.class.getName() );

        final SystemManager componentManager =
            systemManager.getSubContext( null, "component" );

        componentManager.register( ManagementRegistration.EMBEDDOR.getName(),
                                   this,
                                   ManagementRegistration.EMBEDDOR.getInterfaces() );

        for( int i = 0; i < m_entries.length; i++ )
        {
            final String role = m_entries[ i ].getRole();
            if( null == role )
            {
                continue;
            }
            final ManagementRegistration registration =
                ManagementRegistration.getManagementInfoForRole( role );
            if( null != registration )
            {
                componentManager.register( registration.getName(),
                                           m_entries[ i ].getObject(),
                                           registration.getInterfaces() );
            }
        }
    }

    /**
     * Unregister embeddor and it's components from
     * {@link SystemManager}.
     */
    private void unregisterComponents()
        throws Exception
    {
        final SystemManager systemManager =
            (SystemManager)getResourceLocator().lookup( SystemManager.class.getName() );

        final SystemManager componentManager = systemManager.getSubContext( null, "component" );

        componentManager.unregister( ManagementRegistration.EMBEDDOR.getName() );

        for( int i = 0; i < m_entries.length; i++ )
        {
            final String role = m_entries[ i ].getRole();
            if( null == role )
            {
                continue;
            }
            final ManagementRegistration registration =
                ManagementRegistration.getManagementInfoForRole( role );
            if( null != registration )
            {
                componentManager.unregister( registration.getName() );
            }
        }
    }

    private ResourceLocator getResourceLocator()
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
            locator.put( role, component );
        }
        return locator;
    }

    /**
     * Allow subclasses to get access to kernel.
     *
     * @return the Kernel
     */
    protected final Kernel getKernel()
    {
        return (Kernel)getEmbeddorComponent( Kernel.class.getName() );
    }

    /**
     * Allow subclasses to get access to parameters.
     *
     * @return the Parameters
     */
    protected final Parameters getParameters()
    {
        return m_parameters;
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

class EmbeddorObservable
    extends Observable
{
    public void change()
    {
        super.setChanged();
    }
}
