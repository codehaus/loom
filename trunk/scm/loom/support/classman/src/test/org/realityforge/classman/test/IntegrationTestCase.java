/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.realityforge.classman.test;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import org.realityforge.classman.builder.DefaultLoaderResolver;
import org.realityforge.classman.builder.LoaderBuilder;
import org.realityforge.classman.metadata.ClassLoaderSetMetaData;
import org.realityforge.classman.verifier.ClassLoaderVerifier;

/**
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.2 $ $Date: 2003-06-27 03:45:17 $
 */
public class IntegrationTestCase
    extends AbstractLoaderTestCase
{
    public IntegrationTestCase( final String name )
    {
        super( name );
    }

    public void testPresence()
        throws Exception
    {
        final Map map = buildClassLoaders( "config1.xml" );

        assertPresent( map, "LOADER_1" );
        assertPresent( map, "LOADER_2" );
        assertPresent( map, "LOADER_3" );
        assertPresent( map, "LOADER_4" );
        assertPresent( map, "LOADER_5" );
        assertPresent( map, "LOADER_6" );
        assertPresent( map, "JOIN_1" );

        final String[] files1 = new String[]{"cl1.jar"};
        final String[] files2 = new String[]{"cl2.jar"};
        final String[] files3 = new String[]{"cl1.jar", "cl2.jar"};
        final String[] files4 =
            new String[]{"SAR-INF/lib/cl3.jar", "SAR-INF/lib/cl4.jar"};
        final String[] files5 = new String[]{"SAR-INF/lib/cl4.jar"};
        final String[] files6 = new String[]{"SAR-INF/lib/cl4.jar"};

        assertURLsPresent( map, "LOADER_1", files1 );
        assertURLsPresent( map, "LOADER_2", files2 );
        assertURLsPresent( map, "LOADER_3", files3 );
        assertURLsPresent( map, "LOADER_4", files4 );
        assertURLsPresent( map, "LOADER_5", files5 );
        assertURLsPresent( map, "LOADER_6", files6 );

        assertClassPresent( map, "LOADER_1", DataConstants.DATA1_CLASS );
        assertClassPresent( map, "LOADER_1", DataConstants.DATA1_CLASS2 );
        assertClassPresent( map, "LOADER_2", DataConstants.DATA2_CLASS );
        assertClassPresent( map, "LOADER_3", DataConstants.DATA1_CLASS );
        assertClassPresent( map, "LOADER_3", DataConstants.DATA1_CLASS2 );
        assertClassPresent( map, "LOADER_3", DataConstants.DATA2_CLASS );
        assertClassPresent( map, "LOADER_4", DataConstants.DATA1_CLASS );
        assertClassPresent( map, "LOADER_4", DataConstants.DATA1_CLASS2 );
        assertClassPresent( map, "LOADER_4", DataConstants.DATA3_CLASS );
        assertClassPresent( map, "LOADER_4", DataConstants.DATA4_CLASS );
        assertClassPresent( map, "LOADER_5", DataConstants.DATA1_CLASS );
        assertClassPresent( map, "LOADER_5", DataConstants.DATA1_CLASS2 );
        assertClassPresent( map, "LOADER_5", DataConstants.DATA4_CLASS );
        assertClassPresent( map, "LOADER_6", DataConstants.DATA1_CLASS );
        assertClassPresent( map, "LOADER_6", DataConstants.DATA1_CLASS2 );
        assertClassPresent( map, "LOADER_6", DataConstants.DATA4_CLASS );
        assertClassPresent( map, "JOIN_1", DataConstants.DATA1_CLASS );
        assertClassPresent( map, "JOIN_1", DataConstants.DATA1_CLASS2 );
        assertClassPresent( map, "JOIN_1", DataConstants.DATA2_CLASS );
    }

    private void assertClassPresent( final Map map,
                                     final String name,
                                     final String classname )
    {
        final ClassLoader classLoader = (ClassLoader)map.get( name );
        try
        {
            classLoader.loadClass( classname );
        }
        catch( ClassNotFoundException e )
        {
            fail( "Unable to load " + classname + " from " +
                  name + " classloader" );
        }
    }

    private void assertURLsPresent( final Map map,
                                    final String name,
                                    final String[] files )
    {
        final URLClassLoader classLoader = (URLClassLoader)map.get( name );
        final URL[] urls = classLoader.getURLs();

        assertEquals( "URL count:", files.length, urls.length );
        for( int i = 0; i < urls.length; i++ )
        {
            final URL url = urls[ i ];
            final String file = files[ i ];
            assertTrue( "url=" + url + " ends with " + file,
                        url.getFile().endsWith( file ) );
        }
    }

    private void assertPresent( final Map map,
                                final String name )
    {
        assertTrue( "map.containsKey( '" + name + "' )",
                    map.containsKey( name ) );
        /*
        final Object object = map.get( name );
        if( object instanceof URLClassLoader )
        {
            final URLClassLoader classLoader = (URLClassLoader)object;
            final URL[] urls = classLoader.getURLs();
            System.out.println( name + ": " + Arrays.asList( urls ) );
        }
        */
    }

    private Map buildClassLoaders( final String resource )
        throws Exception
    {
        final ClassLoaderSetMetaData metaData =
            buildFromResource( resource );
        final ClassLoaderVerifier verifier = new ClassLoaderVerifier();
        verifier.verifyClassLoaderSet( metaData );
        final LoaderBuilder builder = new LoaderBuilder();
        final HashMap predefined = new HashMap();
        predefined.put( "*system*", ClassLoader.getSystemClassLoader() );
        final File baseDirectory = getBaseDirectory();
        final DefaultLoaderResolver resolver =
            new DefaultLoaderResolver( baseDirectory, null );
        return builder.buildClassLoaders( metaData, resolver, predefined );
    }
}
