/* ====================================================================
 * Loom Software License, version 1.1
 *
 * Copyright (c) 2003-2005, Loom Group. All rights reserved.
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
 * Loom includes code from the Apache Software Foundation
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
package org.codehaus.loom.frontends;

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
import org.apache.log.output.io.StreamTarget;

import org.codehaus.loom.components.util.ConfigUtil;
import org.codehaus.loom.interfaces.ContainerConstants;
import org.codehaus.loom.interfaces.Embeddor;
import org.codehaus.spice.salt.i18n.ResourceManager;
import org.codehaus.spice.salt.i18n.Resources;
import org.codehaus.spice.salt.lang.ExceptionUtil;
import org.codehaus.dna.Configuration;
import org.codehaus.dna.Logger;
import org.codehaus.dna.impl.ConfigurationUtil;
import org.codehaus.dna.impl.ContainerUtil;
import org.codehaus.dna.impl.DefaultResourceLocator;
import org.codehaus.dna.impl.LogkitLogger;
import org.xml.sax.InputSource;

/**
 * The class to load the kernel and start it running.
 *
 * @author Peter Donald
 * @author <a href="mailto:mail@leosimons.com">Leo Simons</a>
 */
public final class CLIMain
    extends Observable
    implements Runnable
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( CLIMain.class );

    static final String HOME_DIR = File.class.getName() + "/home";
    static final String TEMPORARY = Boolean.class.getName() + "/temporary";
    static final String CONFIGFILE = "loom.configfile";

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

    private boolean m_shuttingDown;
    private Logger m_logger;

    private File m_home;
    private StreamTarget m_logTarget;

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
            final String command = "java " +
                getClass().getName() +
                " [options]";
            final CLISetup setup = new CLISetup( command );

            if( false == setup.parseCommandLineOptions( args ) )
            {
                return 0;
            }

            System.out.println();
            System.out.println( ContainerConstants.SOFTWARE + " " + ContainerConstants.VERSION );
            System.out.println();

            final Properties properties = setup.getParameters();
            m_home = (File)data.get( HOME_DIR );
            if( !properties.containsKey( CONFIGFILE ) )
            {
                final String filename = m_home + DEFAULT_CONF_FILE;
                final File configFile =
                    new File( filename ).getCanonicalFile();

                // setting default
                properties.setProperty( CONFIGFILE,
                                        configFile.toString() );
            }

            final Boolean temporary;
            if( !properties.containsKey( TEMPORARY ) )
            {
                temporary = Boolean.FALSE;
            }
            else
            {
                final String property =
                    properties.getProperty( TEMPORARY );
                temporary = Boolean.valueOf( property );
            }
            data.put( TEMPORARY, temporary );

            execute( properties, data, blocking );
        }
        catch( final Throwable throwable )
        {
            handleException( throwable );
        }

        m_logTarget.close();
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

        // If an Observer is present in the data object,
        // then add it as an observer for m_observable.
        final Observer observer = (Observer)data.get( Observer.class.getName() );
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
            final String configFilename = properties.getProperty( CONFIGFILE );
            final Configuration original =
              ConfigurationUtil.buildFromXML( new InputSource( configFilename ) );
            final File home = (File)data.get( HOME_DIR );
            final Properties params = new Properties();
            params.setProperty( "loom.home", home.getAbsolutePath() );
            final Configuration root = ConfigUtil.expandValues( original,
                                                                params );
            final Configuration configuration = root.getChild( "embeddor" );
            final String embeddorClassname = configuration.getAttribute( "class" );
            m_embeddor =
              (Embeddor)Class.forName( embeddorClassname ).newInstance();

            m_logger = createLogger( properties );
            ContainerUtil.enableLogging( m_embeddor, m_logger );
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
     * logger using "Loom" as its category, DEBUG as its priority
     * and the log-destination from Parameters as its destination.
     *
     * TODO: allow configurable priorities and multiple logtargets.
     */
    private Logger createLogger( final Properties properties )
        throws Exception
    {
        final String logDestination =
            properties.getProperty( "log-destination",
                                    m_home + DEFAULT_LOG_FILE );
        final String logPriority =
            properties.getProperty( "log-priority", "INFO" );
        final ExtendedPatternFormatter formatter = new ExtendedPatternFormatter( DEFAULT_FORMAT );

        if( "true".equals( properties.getProperty( "log-stdout", "false" ) ) )
        {
            m_logTarget = new StreamTarget( System.out, formatter );
        }
        else
        {
            final File file = new File( logDestination );
            m_logTarget = new FileTarget( file, false, formatter );
        }

        //Create an anonymous hierarchy so no other
        //components can get access to logging hierarchy
        final Hierarchy hierarchy = new Hierarchy();
        final org.apache.log.Logger logger = hierarchy.getLoggerFor( "Loom" );
        logger.setLogTargets( new LogTarget[]{m_logTarget} );
        logger.setPriority( Priority.getPriorityForName( logPriority ) );
        logger.info( "Logger started" );
        return new LogkitLogger( logger );
    }

    /**
     * Shut the embeddor down.
     *
     * Note must be public so that the Frontend can shut it down via reflection.
     */
    public synchronized void shutdown()
    {
        // Depending on how the shutdown process is initiated, it is possible
        //  that the shutdown() method can be recursively called from within
        //  the call to notifyObservers below.
        if( !m_shuttingDown )
        {
            m_shuttingDown = true;

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
        final String trace;
        if( null != m_logger )
        {
            trace =
                ExceptionUtil.prettyPrintStackTrace( throwable,
                                                     "org.codehaus.loom.components" );
            m_logger.error( throwable.getMessage(), throwable );
        }
        else
        {
            trace = ExceptionUtil.printStackTrace( throwable );
        }
        System.out.println( trace );
        System.out.println( REZ.getString( "main.exception.footer" ) );

        m_exitCode = 1;
    }
}
