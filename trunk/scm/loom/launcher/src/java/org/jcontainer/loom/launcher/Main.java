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
import java.util.HashMap;
import java.util.Map;

/**
 * PhoenixLoader is the class that bootstraps and sets up engine ClassLoader.
 * It also sets up a default policy that gives full permissions to engine code.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 */
public final class Main
{
    private static final String MAIN_CLASS =
        "org.apache.avalon.phoenix.frontends.CLIMain";

    private static Object c_frontend;

    /**
     * Main entry point for Phoenix.
     *
     * @param args the command line arguments
     * @throws java.lang.Exception if an error occurs
     */
    public static final void main( final String[] args )
        throws Exception
    {
        final int exitCode =
            startup( args, new HashMap(), true );
        System.exit( exitCode );
    }

    /**
     * Method to call to startup Phoenix from an
     * external (calling) application. Protected to allow
     * access from DaemonLauncher.
     *
     * @param args the command line arg array
     * @param data a set of extra parameters to pass to embeddor
     * @param blocking false if the current thread is expected to return.
     *
     * @return the exit code which should be used to exit the JVM
     */
    protected static final int startup( final String[] args,
                                        final Map data,
                                        final boolean blocking )
    {
        int exitCode;
        try
        {
            //setup new Policy manager
            Policy.setPolicy( new FreeNEasyPolicy() );

            //Create engine ClassLoader
            final URL[] urls = LauncherUtils.getEngineClassPath();
            final URLClassLoader classLoader = new URLClassLoader( urls );

            data.put( "common.classloader", ClassLoader.getSystemClassLoader() );
            data.put( "container.classloader", classLoader );
            data.put( "phoenix.home", new File( LauncherUtils.findPhoenixHome() ) );

            //Setup context classloader
            Thread.currentThread().setContextClassLoader( classLoader );

            //Create main launcher
            final Class clazz = classLoader.loadClass( MAIN_CLASS );
            final Class[] paramTypes =
                new Class[]{args.getClass(), Map.class, Boolean.TYPE};
            final Method method = clazz.getMethod( "main", paramTypes );
            c_frontend = clazz.newInstance();

            //kick the tires and light the fires....
            final Integer integer = (Integer)method.invoke(
                c_frontend, new Object[]{args, data, new Boolean( blocking )} );
            exitCode = integer.intValue();
        }
        catch( final Exception e )
        {
            e.printStackTrace();
            exitCode = 1;
        }
        return exitCode;
    }

    /**
     * Method to call to shutdown Phoenix from an
     * external (calling) application. Protected to allow
     * access from DaemonLauncher.
     */
    protected static final void shutdown()
    {
        if( null == c_frontend )
        {
            return;
        }

        try
        {
            final Class clazz = c_frontend.getClass();
            final Method method = clazz.getMethod( "shutdown", new Class[ 0 ] );

            //Lets put this sucker to sleep
            method.invoke( c_frontend, new Object[ 0 ] );
        }
        catch( final Exception e )
        {
            e.printStackTrace();
        }
        finally
        {
            c_frontend = null;
        }
    }
}
