/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.realityforge.classman.runtime.test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import org.realityforge.classman.runtime.JoinClassLoader;
import org.realityforge.classman.test.AbstractLoaderTestCase;
import org.realityforge.classman.test.DataConstants;

/**
 * Unit test for join classloader.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.4 $ $Date: 2003-06-27 03:45:10 $
 */
public class JoinClassLoaderTestCase
    extends AbstractLoaderTestCase
{
    public JoinClassLoaderTestCase( String name )
    {
        super( name );
    }

    public void testJoinClassLoader()
        throws Exception
    {
        if( System.getProperty( "gump", "false" ).equals( "true" ) )
        {
            //Can't test the rest of this as it requires
            //explicit classloader setup that gump ignores.
            return;
        }

        final URLClassLoader cl1 = createClassLoader( "cl1.jar" );
        final URLClassLoader cl2 = createClassLoader( "cl2.jar" );
        final ClassLoader[] cls = new ClassLoader[]{cl1, cl2};
        final JoinClassLoader join =
            new JoinClassLoader( cls, ClassLoader.getSystemClassLoader() );
        try
        {
            join.loadClass( DataConstants.DATA1_CLASS );
        }
        catch( ClassNotFoundException e )
        {
            fail( "Unable to load Data1 from cl1 classloader" );
        }
        try
        {
            join.loadClass( DataConstants.DATA2_CLASS );
        }
        catch( ClassNotFoundException e )
        {
            fail( "Unable to load CL2Data from cl2 classloader" );
        }
        try
        {
            join.loadClass( "i.no.exist" );
            fail( "Expected that would be unable to load non-existent class" );
        }
        catch( ClassNotFoundException e )
        {
        }

        try
        {
            assertEquals( "Loading from system classloader",
                          Integer.class,
                          join.loadClass( Integer.class.getName() ) );
        }
        catch( ClassNotFoundException e )
        {
            fail( "Unable to load Integer from classloader" );
        }

        final URL url1 = join.getResource( DataConstants.DATA1_RESOURCE );
        assertNotNull( "Loading " + DataConstants.DATA1_RESOURCE + " from c1 classloader",
                       url1 );
        final URL url2 = join.getResource( "noexist.txt" );
        assertNull( "Loading noexist.txt from c1 classloader", url2 );

        final Enumeration resources1 = join.getResources( DataConstants.DATA1_RESOURCE );
        assertNotNull( "Loading set of " + DataConstants.DATA1_RESOURCE + " from c1 classloader",
                       resources1 );
        assertTrue( "Count of resource from classloader", resources1.hasMoreElements() );
        assertEquals( "Resource found from classloader", url1, resources1.nextElement() );

        assertTrue( "Second Count of resource from classloader", !resources1.hasMoreElements() );

        final Enumeration resources2 = join.getResources( "noexist.txt" );
        assertNotNull( "Loading set of noexist.txt from c1 classloader", resources2 );
        assertTrue( "Count of noexist.txt from classloader", !resources2.hasMoreElements() );

        try
        {
            ClassLoader.getSystemClassLoader().loadClass( DataConstants.DATA1_CLASS );
        }
        catch( ClassNotFoundException e )
        {
            return;
        }
        fail( "Able to load Data1 from system classloader" );
    }

    public void testCtorNullsInClassLoaderSet()
        throws Exception
    {
        try
        {
            new JoinClassLoader( null,
                                 ClassLoader.getSystemClassLoader() );
            fail( "Expected a NPE" );
        }
        catch( NullPointerException e )
        {
            assertEquals( "NPE type", "classLoaders", e.getMessage() );
        }
        try
        {
            new JoinClassLoader( new ClassLoader[]{null},
                                 ClassLoader.getSystemClassLoader() );
            fail( "Expected a NPE" );
        }
        catch( NullPointerException e )
        {
            assertEquals( "NPE type", "classLoaders[0]", e.getMessage() );
        }
    }

    private URLClassLoader createClassLoader( final String filename ) throws MalformedURLException
    {
        final File baseDirectory = getBaseDirectory();
        final File file = new File( baseDirectory, filename );
        final URL url = file.toURL();
        final URL[] urls = new URL[]{url};
        return new URLClassLoader( urls, ClassLoader.getSystemClassLoader() );
    }
}
