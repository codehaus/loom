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
package org.jcontainer.loom.frontends;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import org.apache.log.Hierarchy;
import org.apache.log.LogTarget;
import org.apache.log.Priority;
import org.apache.log.format.ExtendedPatternFormatter;
import org.apache.log.output.io.FileTarget;
import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.Logger;
import org.jcontainer.dna.impl.ConfigurationUtil;
import org.jcontainer.dna.impl.ContainerUtil;
import org.jcontainer.dna.impl.DefaultParameters;
import org.jcontainer.dna.impl.DefaultResourceLocator;
import org.jcontainer.dna.impl.LogkitLogger;
import org.jcontainer.loom.components.ParameterConstants;
import org.jcontainer.loom.components.util.ConfigUtil;
import org.jcontainer.loom.interfaces.ContainerConstants;
import org.jcontainer.loom.interfaces.Embeddor;
import org.realityforge.salt.i18n.ResourceManager;
import org.realityforge.salt.i18n.Resources;
import org.realityforge.salt.lang.ExceptionUtil;
import org.xml.sax.InputSource;

/**
 * The class to load the kernel and start it running.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @author <a href="mailto:mail@leosimons.com">Leo Simons</a>
 */
public final class CLIMain
    extends Observable
    implements Runnable
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( CLIMain.class );

    private static final String DEFAULT_LOG_FILE =
        File.separator + "logs" + File.separator + "loom.log";

    private static final String DEFAULT_CONF_FILE =
        File.separator + "conf" + File.separator + "kernel.xml";

    private static final String DEFAULT_FORMAT =
        "%7.7{priority} %23.23{time:yyyy-MM-dd' 'HH:mm:ss.SSS} [%8.8{category}] (%{context}): "
        + "%{message}\n%{throwable}";

    ///The embeddor attached to frontend
    private Embeddor m_embeddor;

    ///The code to return to system using exit code
    private int m_exitCode;

    private ShutdownHook m_hook;

    private boolean m_shuttingDown;
    private Logger m_logger;

    /**
     * Main entry point.
     *
     * @param args the command line arguments
     */
    public int main( final String[] args,
                     final Map data,
                     final boolean blocking )
    {
        try
        {
            final String command = "java " + getClass().getName() + " [options]";
            final CLISetup setup = new CLISetup( command );

            if( false == setup.parseCommandLineOptions( args ) )
            {
                return 0;
            }

            System.out.println();
            System.out.println( ContainerConstants.SOFTWARE + " " + ContainerConstants.VERSION );
            System.out.println();

            final Properties properties = setup.getParameters();
            final String loomHome = System.getProperty( ParameterConstants.HOME_DIR, ".." );
            properties.setProperty( ParameterConstants.HOME_DIR, loomHome );
            if( !properties.containsKey( "loom.configfile" ) )
            {
                final String filename = loomHome + DEFAULT_CONF_FILE;
                final File configFile =
                    new File( filename ).getCanonicalFile();

                // setting default
                properties.setProperty( "loom.configfile",
                                        configFile.toString() );
            }

            execute( properties, data, blocking );
        }
        catch( final Throwable throwable )
        {
            handleException( throwable );
        }

        return m_exitCode;
    }

    /**
     * Actually create and execute the main component of embeddor.
     */
    private void execute( final Properties properties,
                          final Map data,
                          final boolean blocking )
    {
        if( !startup( properties, data ) )
        {
            return;
        }

        final boolean disableHook =
            properties.getProperty( "disable-hook", "false" ).equals( "true" );
        if( false == disableHook )
        {
            m_hook = new ShutdownHook( this );
            Runtime.getRuntime().addShutdownHook( m_hook );
        }

        // If an Observer is present in the data object, then add it as an observer for
        //  m_observable
        Observer observer = (Observer)data.get( Observer.class.getName() );
        if( null != observer )
        {
            addObserver( observer );
        }

        if( blocking )
        {
            run();
        }
        else
        {
            final Thread thread = new Thread( this, "Loom-Monitor" );
            thread.setDaemon( false );
            thread.start();
        }
    }

    public void run()
    {
        try
        {
            m_embeddor.execute();
        }
        catch( final Throwable throwable )
        {
            handleException( throwable );
        }
        finally
        {
            if( null != m_hook )
            {
                Runtime.getRuntime().removeShutdownHook( m_hook );
            }
            shutdown();
        }
    }

    /**
     * Startup the embeddor.
     */
    private synchronized boolean startup( final Properties properties,
                                          final Map data )
    {
        try
        {
            final String configFilename = properties.getProperty( "loom.configfile" );
            final Configuration original = ConfigurationUtil.buildFromXML( new InputSource( configFilename ) );
            final Configuration root = ConfigUtil.expandValues( original, properties );
            final Configuration configuration = root.getChild( "embeddor" );
            final String embeddorClassname = configuration.getAttribute( "class" );
            m_embeddor = (Embeddor)Class.forName( embeddorClassname ).newInstance();

            m_logger = createLogger( properties );
            ContainerUtil.enableLogging( m_embeddor, m_logger );
            ContainerUtil.parameterize( m_embeddor, reateParameters( properties ) );
            ContainerUtil.compose( m_embeddor, createLocator( data ) );
            ContainerUtil.configure( m_embeddor, configuration );
            ContainerUtil.initialize( m_embeddor );
        }
        catch( final Throwable throwable )
        {
            handleException( throwable );
            return false;
        }

        return true;
    }

    private DefaultParameters reateParameters( final Properties properties )
    {
        final DefaultParameters parameters = new DefaultParameters();
        final Iterator iterator = properties.keySet().iterator();
        while( iterator.hasNext() )
        {
            final String name = (String)iterator.next();
            final String value = properties.getProperty( name );
            parameters.setParameter( name, value );
        }
        return parameters;
    }

    private DefaultResourceLocator createLocator( final Map data )
    {
        final DefaultResourceLocator locator = new DefaultResourceLocator();
        final Iterator iterator = data.keySet().iterator();
        while( iterator.hasNext() )
        {
            final String key = (String)iterator.next();
            final Object value = data.get( key );
            locator.put( key, value );
        }
        return locator;
    }

    /**
     * Uses {@link org.apache.log.Hierarchy} to create a new
     * logger using "Loom" as its category, DEBUG as its
     * priority and the log-destination from Parameters as its
     * destination.
     * TODO: allow configurable priorities and multiple
     * logtargets.
     */
    private Logger createLogger( final Properties properties )
        throws Exception
    {
        final String loomHome = properties.getProperty( "loom.home" );
        final String logDestination =
            properties.getProperty( "log-destination", loomHome + DEFAULT_LOG_FILE );
        final String logPriority =
            properties.getProperty( "log-priority", "INFO" );
        final ExtendedPatternFormatter formatter = new ExtendedPatternFormatter( DEFAULT_FORMAT );
        final File file = new File( logDestination );
        final FileTarget logTarget = new FileTarget( file, false, formatter );

        //Create an anonymous hierarchy so no other
        //components can get access to logging hierarchy
        final Hierarchy hierarchy = new Hierarchy();
        final org.apache.log.Logger logger = hierarchy.getLoggerFor( "Loom" );
        logger.setLogTargets( new LogTarget[]{logTarget} );
        logger.setPriority( Priority.getPriorityForName( logPriority ) );
        logger.info( "Logger started" );
        return new LogkitLogger( logger );
    }

    /**
     * Shut the embeddor down.
     * This method is designed to only be called from within the ShutdownHook.
     * To shutdown Pheonix, call shutdown() below.
     */
    protected void forceShutdown()
    {
        if( null == m_hook || null == m_embeddor )
        {
            //We were shutdown gracefully but the shutdown hook
            //thread was not removed. This can occur when an earlier
            //shutdown hook caused a shutdown() request to be processed
            return;
        }

        final String message = REZ.getString( "main.abnormal-exit.notice" );
        if( null != m_logger )
        {
            m_logger.info( message );
        }
        System.out.print( message );
        System.out.print( " " );
        System.out.flush();

        shutdown();
    }

    /**
     * Shut the embeddor down.
     *
     * Note must be public so that the Frontend can
     * shut it down via reflection.
     */
    public synchronized void shutdown()
    {
        // Depending on how the shutdown process is initiated, it is possible
        //  that the shutdown() method can be recursively called from within
        //  the call to notifyObservers below.
        if( !m_shuttingDown )
        {
            m_shuttingDown = true;

            //Null hook so it is not tried to be removed
            //when we are shutting down. (Attempting to remove
            //hook during shutdown raises an exception).
            m_hook = null;

            if( null != m_embeddor )
            {
                final String message = REZ.getString( "main.exit.notice" );
                System.out.println( message );
                System.out.flush();

                try
                {
                    ContainerUtil.dispose( m_embeddor );
                }
                catch( final Throwable throwable )
                {
                    handleException( throwable );
                }
                finally
                {
                    m_embeddor = null;
                }
            }

            // Notify any observers of shutdown
            setChanged();
            notifyObservers( "shutdown" );
        }
    }

    /**
     * Print out exception and details to standard out.
     *
     * @param throwable the exception that caused failure
     */
    private void handleException( final Throwable throwable )
    {
        System.out.println( REZ.getString( "main.exception.header" ) );
        final String trace =
            ExceptionUtil.prettyPrintStackTrace( throwable,
                                                 "org.jcontainer.loom.components" );
        System.out.println( trace );
        System.out.println( REZ.getString( "main.exception.footer" ) );

        if( null != m_logger )
        {
            m_logger.error( throwable.getMessage(), throwable );
        }
        m_exitCode = 1;
    }
}

final class ShutdownHook
    extends Thread
{
    private final CLIMain m_main;

    protected ShutdownHook( final CLIMain main )
    {
        m_main = main;
    }

    /**
     * Run the shutdown hook.
     */
    public void run()
    {
        m_main.forceShutdown();
    }
}
