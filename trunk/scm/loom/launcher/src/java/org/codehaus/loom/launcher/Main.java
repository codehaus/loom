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
package org.codehaus.loom.launcher;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.Policy;
import java.util.HashMap;
import java.util.Map;

/**
 * LoomLoader is the class that bootstraps and sets up engine ClassLoader. It
 * also sets up a default policy that gives full permissions to engine code.
 *
 * @author Peter Donald
 */
public final class Main
{
    private static Object c_frontend;
    private static ShutdownHook c_hook;

    /**
     * Main entry point for Loom.
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
     * Method to call to startup Loom from an external (calling) application.
     * Protected to allow access from DaemonLauncher.
     *
     * @param options the command line arg array
     * @param data a set of extra parameters to pass to embeddor
     * @param blocking false if the current thread is expected to return.
     * @return the exit code which should be used to exit the JVM
     */
    protected static final int startup( final String[] options,
                                        final Map data,
                                        final boolean blocking )
    {
        int exitCode;
        try
        {
            //setup new Policy manager
            Policy.setPolicy( new FreeNEasyPolicy() );

            //Create engine ClassLoader
            final File homeDir =
                LauncherUtils.findLoomHome( "loom.home", "loom-launcher.jar" );

            final URL[] urls =
                LauncherUtils.generateClassPath( homeDir, "container/lib" );
            final URLClassLoader classLoader = new URLClassLoader( urls );

            data.put( ClassLoader.class.getName() + "/common",
                      ClassLoader.getSystemClassLoader() );
            data.put( ClassLoader.class.getName() + "/container",
                      classLoader );
            data.put( File.class.getName() + "/home", homeDir );

            //Setup context classloader
            Thread.currentThread().setContextClassLoader( classLoader );

            //Create main launcher
            final Class clazz =
                classLoader.loadClass( "org.codehaus.loom.frontends.CLIMain" );
            final Class[] paramTypes =
                new Class[]{options.getClass(), Map.class, Boolean.TYPE};
            final Method method = clazz.getMethod( "main", paramTypes );

            final Object frontend;
            synchronized( Main.class )
            {
                c_frontend = clazz.newInstance();
                frontend = c_frontend;
            }

            //By default add a shutdown hook that will invoke
            //shutdown on container when JVM shuts down
            final String enableShutdownHook =
                System.getProperty( "disable.shutdown.hook" );
            if( !"false".equals( enableShutdownHook ) )
            {
                c_hook = new ShutdownHook();
                Runtime.getRuntime().addShutdownHook( c_hook );
            }

            //kick the tires and light the fires....
            final Object[] args =
                new Object[]{options, data, new Boolean( blocking )};
            final Integer integer =
                (Integer)method.invoke( frontend, args );
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
     * Method to call to shutdown Loom from an external (calling) application.
     * Protected to allow access from DaemonLauncher.
     */
    protected static final void shutdown()
    {
        final Object frontend;
        synchronized( Main.class )
        {
            frontend = c_frontend;
            c_frontend = null;
        }
        if( null == frontend )
        {
            return;
        }
        if( null != c_hook && c_hook != Thread.currentThread() )
        {
            //Null hook so it is not tried to be removed
            //when we are shutting down. (Attempting to remove
            //hook during shutdown raises an exception).

            Runtime.getRuntime().removeShutdownHook( c_hook );
            c_hook = null;
        }

        try
        {
            final Class clazz = frontend.getClass();
            final Method method = clazz.getMethod( "shutdown",
                                                   new Class[ 0 ] );

            //Lets put this sucker to sleep
            method.invoke( frontend, new Object[ 0 ] );
        }
        catch( final Exception e )
        {
            e.printStackTrace();
        }
    }
}
