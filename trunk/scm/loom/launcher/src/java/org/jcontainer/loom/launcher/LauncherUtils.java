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
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * A set of utilities that help when writing
 * Launchers.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 */
public class LauncherUtils
{
    private static final String LOADER_JAR = "phoenix-loader.jar";

    /**
     * Create a ClassPath for the engine.
     *
     * @return the set of URLs that engine uses to load
     * @throws java.lang.Exception if unable to aquire classpath
     */
    static URL[] getEngineClassPath()
        throws Exception
    {
        final ArrayList urls = new ArrayList();

        final File dir = findEngineLibDir();
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
     * Find directory to load engine specific libraries from.
     *
     * @return the lib dir
     * @throws java.lang.Exception if unable to aquire directory
     */
    private static File findEngineLibDir()
        throws Exception
    {
        final String phoenixHome = findPhoenixHome();
        final String engineLibDir =
            phoenixHome + File.separator + "bin" + File.separator + "lib";
        final File dir = new File( engineLibDir ).getCanonicalFile();
        if( !dir.exists() )
        {
            throw new Exception( "Unable to locate engine lib directory at " + engineLibDir );
        }
        return dir;
    }

    /**
     * Utility method to find the home directory
     * of Phoenix and make sure system property is
     * set to it.
     *
     * @return the location of phoenix directory
     * @throws java.lang.Exception if unable to locate directory
     */
    static String findPhoenixHome()
        throws Exception
    {
        String phoenixHome = System.getProperty( "phoenix.home", null );
        if( null == phoenixHome )
        {
            final File loaderDir = findLoaderDir();
            phoenixHome = loaderDir.getAbsoluteFile().getParentFile() + File.separator;
        }

        phoenixHome = ( new File( phoenixHome ) ).getCanonicalFile().toString();
        System.setProperty( "phoenix.home", phoenixHome );
        return phoenixHome;
    }

    /**
     *  Finds the LOADER_JAR file in the classpath.
     */
    private static final File findLoaderDir()
        throws Exception
    {
        final String classpath = System.getProperty( "java.class.path" );
        final String pathSeparator = System.getProperty( "path.separator" );
        final StringTokenizer tokenizer = new StringTokenizer( classpath, pathSeparator );

        while( tokenizer.hasMoreTokens() )
        {
            final String element = tokenizer.nextToken();

            if( element.endsWith( LOADER_JAR ) )
            {
                File file = ( new File( element ) ).getCanonicalFile();
                file = file.getParentFile();
                return file;
            }
        }

        throw new Exception( "Unable to locate " + LOADER_JAR +
                             " in classpath. User must specify " +
                             "phoenix.home system property." );
    }
}
