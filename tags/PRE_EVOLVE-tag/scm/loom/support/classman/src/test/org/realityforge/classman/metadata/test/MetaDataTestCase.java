/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.realityforge.classman.metadata.test;

import junit.framework.TestCase;
import org.realityforge.classman.metadata.ClassLoaderMetaData;
import org.realityforge.classman.metadata.ClassLoaderSetMetaData;
import org.realityforge.classman.metadata.FileSetMetaData;
import org.realityforge.classman.metadata.JoinMetaData;

/**
 * Unit test for join classloader.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.2 $ $Date: 2003-06-27 03:45:09 $
 */
public class MetaDataTestCase
    extends TestCase
{
    public MetaDataTestCase( String name )
    {
        super( name );
    }

    public void testCtorNullsInClassLoaderSet()
        throws Exception
    {
        try
        {
            new ClassLoaderSetMetaData( null,
                                        new String[ 0 ],
                                        new ClassLoaderMetaData[ 0 ],
                                        new JoinMetaData[ 0 ] );
            fail( "Expected a NPE" );
        }
        catch( NullPointerException e )
        {
            assertEquals( "NPE type", "aDefault", e.getMessage() );
        }

        try
        {
            new ClassLoaderSetMetaData( "",
                                        null,
                                        new ClassLoaderMetaData[ 0 ],
                                        new JoinMetaData[ 0 ] );
            fail( "Expected a NPE" );
        }
        catch( NullPointerException e )
        {
            assertEquals( "NPE type", "predefined", e.getMessage() );
        }

        try
        {
            new ClassLoaderSetMetaData( "",
                                        new String[ 0 ],
                                        new ClassLoaderMetaData[ 0 ],
                                        null );
            fail( "Expected a NPE" );
        }
        catch( NullPointerException e )
        {
            assertEquals( "NPE type", "joins", e.getMessage() );
        }

        try
        {
            new ClassLoaderSetMetaData( "",
                                        new String[ 0 ],
                                        null,
                                        new JoinMetaData[ 0 ] );
            fail( "Expected a NPE" );
        }
        catch( NullPointerException e )
        {
            assertEquals( "NPE type", "classLoaders", e.getMessage() );
        }
    }

    public void testCtorNullsInFileSet()
        throws Exception
    {
        try
        {
            new FileSetMetaData( null,
                                 new String[ 0 ],
                                 new String[ 0 ] );
            fail( "Expected a NPE" );
        }
        catch( NullPointerException e )
        {
            assertEquals( "NPE type", "baseDirectory", e.getMessage() );
        }

        try
        {
            new FileSetMetaData( ".",
                                 null,
                                 new String[ 0 ] );
            fail( "Expected a NPE" );
        }
        catch( NullPointerException e )
        {
            assertEquals( "NPE type", "includes", e.getMessage() );
        }

        try
        {
            new FileSetMetaData( ".",
                                 new String[ 0 ],
                                 null );
            fail( "Expected a NPE" );
        }
        catch( NullPointerException e )
        {
            assertEquals( "NPE type", "excludes", e.getMessage() );
        }
    }

}
