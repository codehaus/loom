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
package org.jcontainer.loom.launcher;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.Policy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:sshort at postx.com">Steve Short</a>
 */
public class JMXLauncher
    implements JMXLauncherMBean
{
    private static final String MAIN_CLASS =
        "org.apache.avalon.phoenix.frontends.CLIMain";

    private int m_state;                  // Service lifecycle m_state

    private String m_phoenixHome = "../../phoenix/";
    private String m_phoenixConfigFile = "../../phoenix/conf/kernel.xml";
    private String m_appsPath = "../../phoenix/apps/";
    private String m_logFilename = "../../phoenix/logs/phoenix.log";
    private boolean m_debugPhoenix = false;

    private Object m_frontend;
    private Method m_shutdownMethod;

    /**
     * MBean accessor to set the MBean m_state as an int
     */
    public int getState()
    {
        return m_state;
    }

    /**
     * MBean accessor to set the MBean m_state as a String
     */
    public String getStateString()
    {
        return states[ m_state ];
    }

    /**
     * MBean accessor to set the Phoenix home directory
     */
    public void setPhoenixHome( final String value )
    {
        m_phoenixHome = value;
    }

    /**
     * MBean accessor to return the Phoenix home directory
     */
    public String getPhoenixHome()
    {
        return m_phoenixHome;
    }

    /**
     * MBean accessor to set the Phoenix ConfigFile directory
     */
    public void setPhoenixConfigFile( final String value )
    {
        m_phoenixConfigFile = value;
    }

    /**
     * MBean accessor to return the Phoenix ConfigFile directory
     */
    public String getPhoenixConfigFile()
    {
        return m_phoenixConfigFile;
    }

    /**
     * MBean accessor to set the Phoenix applications directory
     */
    public void setAppsPath( final String value )
    {
        m_appsPath = value;
    }

    /**
     * MBean accessor to return the Phoenix applications directory
     */
    public String getAppsPath()
    {
        return m_appsPath;
    }

    /**
     * MBean accessor to set the name of the Phoenix log file
     */
    public void setLogFilename( final String value )
    {
        m_logFilename = value;
    }

    /**
     * MBean accessor to return the name of the Phoenix log file
     */
    public String getLogFilename()
    {
        return m_logFilename;
    }

    /**
     * MBean accessor to set the Phoenix debug flag
     */
    public void setPhoenixDebug( final String value )
    {
        m_debugPhoenix = new Boolean( value ).booleanValue();
    }

    /**
     * MBean accessor to return the Phoenix debug flag
     */
    public String getPhoenixDebug()
    {
        return new Boolean( m_debugPhoenix ).toString();
    }

    /**
     * Method to create this MBean
     */
    public void create() throws Exception
    {
        m_state = STOPPED;
    }

    /**
     * Method to start this MBean
     */
    public void start() throws Exception
    {
        m_state = STARTING;

        final HashMap data = new HashMap();

        if( m_debugPhoenix )
        {
            System.out.println( "JMXLauncher: Starting up Phoenix" );

            File f = new File( m_phoenixHome );
            System.out.println( "JMXLauncher setting phoenix home to " + f.getCanonicalPath() );
            f = new File( m_phoenixConfigFile );
            System.out.println( "JMXLauncher setting phoenix config file to " + f.getCanonicalPath() );
            f = new File( m_appsPath );
            System.out.println( "JMXLauncher setting phoenix apps path to " + f.getCanonicalPath() );
            f = new File( m_logFilename );
            System.out.println( "JMXLauncher setting phoenix log file to " + f.getCanonicalPath() );
        }

        try
        {
            String[] args;

            if( m_debugPhoenix )
                args = new String[ 8 ];
            else
                args = new String[ 7 ];

            int idx = 0;

            args[ idx++ ] = "-f";
            args[ idx++ ] = m_phoenixConfigFile;
            args[ idx++ ] = "-a";
            args[ idx++ ] = m_appsPath;
            args[ idx++ ] = "-l";
            args[ idx++ ] = m_logFilename;
            args[ idx++ ] = "--disable-hook";

            if( m_debugPhoenix )
            {
                args[ idx ] = "-d";
            }

            System.setProperty( "phoenix.home", m_phoenixHome );

            startPhoenix( args, data );

            m_state = STARTED;

            if( m_debugPhoenix )
            {
                System.out.println( "JMXLauncher: Phoenix startup completed" );
            }
        }
        catch( final Exception e )
        {
            m_state = FAILED;
            System.out.println( "JMXLauncher: Failed to start Phoenix launcher thread" );
            e.printStackTrace();
        }
    }

    /**
     * Method to stop this MBean
     */
    public void stop()
    {
        if( m_state != STOPPED && m_state != STOPPING )
        {
            m_state = STOPPING;
            if( m_debugPhoenix )
            {
                System.out.println( "JMXLauncher: Stopping Phoenix" );
            }

            try
            {
                shutdownPhoenix();
            }
            catch( final Exception e )
            {
                System.out.println( "JMXLauncher: Failed to stop Phoenix" );
                e.printStackTrace();
            }

            m_state = STOPPED;
        }
    }

    /**
     * Method to destroy this MBean
     */
    public void destroy()
    {
    }

    /**
     * Runs the thread which launches James
     */
    private void startPhoenix( final String[] args, final Map data )
    {
        int exitCode;

        try
        {
            //setup new Policy manager
            Policy.setPolicy( new FreeNEasyPolicy() );

            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

            //Create common ClassLoader
            final URL[] libUrls = getLibClassPath();
            final URLClassLoader libClassLoader = new URLClassLoader( libUrls, contextClassLoader );

            //Create engine ClassLoader
            final URL[] engineUrls = LauncherUtils.getEngineClassPath();
            final URLClassLoader engineClassLoader = new URLClassLoader( engineUrls, libClassLoader );

            data.put( "common.classloader", libClassLoader );
            data.put( "container.classloader", engineClassLoader );
            data.put( "phoenix.home", new File( LauncherUtils.findPhoenixHome() ) );

            //Setup context classloader
            Thread.currentThread().setContextClassLoader( libClassLoader );

            //Create main launcher
            final Class clazz = engineClassLoader.loadClass( MAIN_CLASS );
            final Class[] paramTypes =
                new Class[]{args.getClass(), Map.class, Boolean.TYPE};
            final Method startupMethod = clazz.getMethod( "main", paramTypes );
            m_shutdownMethod = clazz.getMethod( "shutdown", new Class[]{} );
            m_frontend = clazz.newInstance();

            //kick the tires and light the fires....
            final Integer integer = (Integer)startupMethod.invoke(
                m_frontend, new Object[]{args, data, new Boolean( false )} );
            exitCode = integer.intValue();

            if( exitCode != 0 )
            {
                throw new Exception( "Result " + exitCode + " from main()" );
            }
        }
        catch( final Exception e )
        {
            System.out.println( "JMXLauncher: Failed to start Phoenix" );
            e.printStackTrace();
        }
    }

    /**
     * Shutdown Phoenix
     */
    private void shutdownPhoenix() throws Exception
    {
        if( m_frontend != null && m_shutdownMethod != null )
        {
            m_shutdownMethod.invoke( m_frontend, new Object[]{} );
        }

        m_shutdownMethod = null;
        m_frontend = null;
    }

    /**
     * Create a ClassPath for the engine.
     *
     * @return the set of URLs that engine uses to load
     * @throws java.lang.Exception if unable to aquire classpath
     */
    private static URL[] getLibClassPath()
        throws Exception
    {
        final ArrayList urls = new ArrayList();

        final File dir = findLibDir();
        final File[] files = dir.listFiles();
        for( int i = 0; i < files.length; i++ )
        {
            final File file = files[ i ];
            if( file.getName().endsWith( ".jar" ) )
            {
                urls.add( file.toURL() );
            }
        }

        return (URL[])urls.toArray( new URL[ urls.size() ] );
    }

    /**
     * Find directory to load common libraries from.
     *
     * @return the lib dir
     * @throws java.lang.Exception if unable to aquire directory
     */
    private static File findLibDir()
        throws Exception
    {
        final String phoenixHome = LauncherUtils.findPhoenixHome();
        final String libDir =
            phoenixHome + File.separator + "lib";
        final File dir = new File( libDir ).getCanonicalFile();
        if( !dir.exists() )
        {
            throw new Exception( "Unable to locate engine lib directory at " + dir.getCanonicalPath() );
        }
        return dir;
    }
}
