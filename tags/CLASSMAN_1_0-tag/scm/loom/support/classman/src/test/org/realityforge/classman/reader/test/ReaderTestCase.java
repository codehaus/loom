/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.realityforge.classman.reader.test;

import org.realityforge.classman.metadata.ClassLoaderSetMetaData;
import org.realityforge.classman.test.AbstractLoaderTestCase;

/**
 * TestCase for {@link org.realityforge.classman.reader.ClassLoaderSetReader}.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 */
public class ReaderTestCase
    extends AbstractLoaderTestCase
{
    public ReaderTestCase( final String name )
    {
        super( name );
    }

    public void testConfig1()
        throws Exception
    {
        final ClassLoaderSetMetaData defs =
            buildFromResource( "config1.xml" );

        assertEquals( "ClassLoader Predefined Count",
                      1,
                      defs.getPredefined().length );
        assertEquals( "ClassLoader Predefined",
                      "*system*",
                      defs.getPredefined()[ 0 ] );
        assertEquals( "ClassLoader Default",
                      "join2",
                      defs.getDefault() );
        assertEquals( "ClassLoader Count",
                      2,
                      defs.getClassLoaders().length );
        assertEquals( "Join Count",
                      2,
                      defs.getJoins().length );
        assertNotNull( "ClassLoader cl1",
                       defs.getClassLoader( "cl1" ) );
        assertNotNull( "ClassLoader cl2",
                       defs.getClassLoader( "cl2" ) );
        assertNotNull( "Join join1",
                       defs.getJoin( "join1" ) );
        assertNotNull( "Join join2",
                       defs.getJoin( "join2" ) );

        assertEquals( "ClassLoader cl1.entrys Name",
                      defs.getClassLoader( "cl1" ).getName(),
                      "cl1" );
        assertEquals( "ClassLoader cl1.entrys Count",
                      defs.getClassLoader( "cl1" ).getEntrys().length,
                      2 );
        assertEquals( "ClassLoader cl1.filesets Count",
                      defs.getClassLoader( "cl1" ).getFilesets().length,
                      1 );
        assertEquals( "ClassLoader cl1.extensions Count",
                      defs.getClassLoader( "cl1" ).getExtensions().length,
                      1 );
        assertEquals( "ClassLoader cl1.fileset[0] Dir",
                      defs.getClassLoader( "cl1" ).getFilesets()[ 0 ].getBaseDirectory(),
                      "someDir" );
        assertEquals( "ClassLoader cl1.fileset[0] Include count",
                      defs.getClassLoader( "cl1" ).getFilesets()[ 0 ].getIncludes().length,
                      2 );
        assertEquals( "ClassLoader cl1.fileset[0] Exclude count",
                      defs.getClassLoader( "cl1" ).getFilesets()[ 0 ].getExcludes().length,
                      1 );
        assertEquals( "ClassLoader cl1.fileset[0].include[0] name",
                      defs.getClassLoader( "cl1" ).getFilesets()[ 0 ].getIncludes()[ 0 ],
                      "**/*.jar" );
        assertEquals( "ClassLoader cl1.fileset[0].include[1] name",
                      defs.getClassLoader( "cl1" ).getFilesets()[ 0 ].getIncludes()[ 1 ],
                      "**/*.bar" );
        assertEquals( "ClassLoader cl1.fileset[0].exclude[0] name",
                      defs.getClassLoader( "cl1" ).getFilesets()[ 0 ].getExcludes()[ 0 ],
                      "**/unwanted/*" );
        assertEquals( "ClassLoader cl1.entrys[0] Location",
                      defs.getClassLoader( "cl1" ).getEntrys()[ 0 ],
                      "someFile.jar" );
        assertEquals( "ClassLoader cl1.entrys[1] Location",
                      defs.getClassLoader( "cl1" ).getEntrys()[ 1 ],
                      "someOtherFile.jar" );

        assertEquals( "ClassLoader cl2.entrys Name",
                      defs.getClassLoader( "cl2" ).getName(),
                      "cl2" );
        assertEquals( "ClassLoader cl2.entrys Count",
                      defs.getClassLoader( "cl2" ).getEntrys().length,
                      1 );
        assertEquals( "ClassLoader cl2.filesets Count",
                      defs.getClassLoader( "cl2" ).getFilesets().length,
                      0 );
        assertEquals( "ClassLoader cl2.extensions Count",
                      defs.getClassLoader( "cl2" ).getExtensions().length,
                      0 );
        assertEquals( "ClassLoader cl2.entrys[0] Location",
                      defs.getClassLoader( "cl2" ).getEntrys()[ 0 ],
                      "aFile.jar" );

        assertEquals( "Join join1.refs Name",
                      defs.getJoin( "join1" ).getName(),
                      "join1" );
        assertEquals( "Join join1.refs Count",
                      defs.getJoin( "join1" ).getClassloaders().length,
                      1 );
        assertEquals( "Join join1.refs[0] Name",
                      defs.getJoin( "join1" ).getClassloaders()[ 0 ],
                      "cl1" );

        assertEquals( "Join join2.refs Name",
                      defs.getJoin( "join2" ).getName(),
                      "join2" );
        assertEquals( "Join join2.refs Count",
                      defs.getJoin( "join2" ).getClassloaders().length,
                      2 );
        assertEquals( "Join join2.refs[0] Name",
                      defs.getJoin( "join2" ).getClassloaders()[ 0 ],
                      "cl1" );
        assertEquals( "Join join2.refs[1] Name",
                      defs.getJoin( "join2" ).getClassloaders()[ 1 ],
                      "cl2" );
    }

    public void testConfig2()
        throws Exception
    {
        try
        {
            buildFromResource( "config2.xml" );
        }
        catch( final Throwable t )
        {
            return;
        }
        fail( "Should have failed as loaded a " +
              "configuration with no default set" );
    }

    public void testConfig3()
        throws Exception
    {
        try
        {
            buildFromResource( "config3.xml" );
        }
        catch( final Throwable t )
        {
            return;
        }
        fail( "Should have failed as loaded a " +
              "configuration with no version set" );
    }

    public void testConfig4()
        throws Exception
    {
        try
        {
            buildFromResource( "config4.xml" );
        }
        catch( final Throwable t )
        {
            return;
        }
        fail( "Should have failed as loaded a " +
              "configuration with bad version" );
    }

    public void testConfig5()
        throws Exception
    {
        try
        {
            buildFromResource( "config5.xml" );
        }
        catch( final Throwable t )
        {
            return;
        }
        fail( "Should have failed as loaded a " +
              "configuration with extension with null name" );
    }
}
