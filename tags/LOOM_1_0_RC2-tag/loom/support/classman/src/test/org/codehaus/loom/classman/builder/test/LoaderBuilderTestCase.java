/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.codehaus.loom.classman.builder.test;

import java.io.File;
import java.util.HashMap;

import org.codehaus.loom.classman.builder.LoaderBuilder;
import org.codehaus.loom.classman.builder.SimpleLoaderResolver;
import org.codehaus.loom.classman.metadata.ClassLoaderMetaData;
import org.codehaus.loom.classman.metadata.ClassLoaderSetMetaData;
import org.codehaus.loom.classman.metadata.FileSetMetaData;
import org.codehaus.loom.classman.metadata.JoinMetaData;
import org.codehaus.loom.classman.test.AbstractLoaderTestCase;
import org.codehaus.loom.classman.test.DataConstants;
import org.codehaus.loom.extension.Extension;

/**
 * Unit test for join classloader.
 *
 * @author Peter Donald
 * @version $Revision: 1.1 $ $Date: 2004-04-19 22:19:25 $
 */
public class LoaderBuilderTestCase
    extends AbstractLoaderTestCase
{
    public void testMissingClassLoader()
        throws Exception
    {
        final LoaderBuilder builder = new LoaderBuilder();
        final ClassLoaderMetaData classLoader =
            new ClassLoaderMetaData( "blah",
                                     "bad-parent",
                                     new String[ 0 ],
                                     new Extension[ 0 ],
                                     new FileSetMetaData[ 0 ] );
        final ClassLoaderSetMetaData metadata =
            new ClassLoaderSetMetaData( "blah",
                                        new String[ 0 ],
                                        new ClassLoaderMetaData[]{classLoader},
                                        new JoinMetaData[ 0 ] );
        final SimpleLoaderResolver resolver = new SimpleLoaderResolver( null );
        final HashMap predefined = new HashMap();
        try
        {
            builder.buildClassLoaders( metadata, resolver, predefined );
            fail( "Expected to fail as unable to find classloader 'bad-parent'" );
        }
        catch( Exception e )
        {
            assertTrue( "Exception message", -1 != e.getMessage().indexOf( "bad-parent" ) );
            return;
        }
    }

    public void testMissingPredefined()
        throws Exception
    {
        final LoaderBuilder builder = new LoaderBuilder();
        final ClassLoaderSetMetaData metadata =
            new ClassLoaderSetMetaData( "*system*",
                                        new String[]{"*system*"},
                                        new ClassLoaderMetaData[ 0 ],
                                        new JoinMetaData[ 0 ] );
        final SimpleLoaderResolver resolver = new SimpleLoaderResolver( null );
        final HashMap predefined = new HashMap();
        try
        {
            builder.buildClassLoaders( metadata, resolver, predefined );
            fail( "Expected to fail as predefined list did " +
                  "not container predefined specified in metadata" );
        }
        catch( Exception e )
        {
            assertTrue( "Exception message", -1 != e.getMessage().indexOf( "*system*" ) );
            return;
        }
    }

    public void testExtensions()
        throws Exception
    {
        final LoaderBuilder builder = new LoaderBuilder();
        final ClassLoaderMetaData classLoader =
            new ClassLoaderMetaData( "ext",
                                     "*system*",
                                     new String[ 0 ],
                                     new Extension[]{DataConstants.EXTENSION},
                                     new FileSetMetaData[ 0 ] );
        final ClassLoaderSetMetaData metadata =
            new ClassLoaderSetMetaData( "ext",
                                        new String[]{"*system*"},
                                        new ClassLoaderMetaData[]{classLoader},
                                        new JoinMetaData[ 0 ] );
        final SimpleLoaderResolver resolver = new TestLoaderResolver( new File( "." ) );
        final HashMap predefined = new HashMap();
        predefined.put( "*system*", ClassLoader.getSystemClassLoader() );
        try
        {
            builder.buildClassLoaders( metadata, resolver, predefined );
        }
        catch( Exception e )
        {
            fail( "Unexpected failure to build classloaders with extension: " + e );
        }
    }
}
