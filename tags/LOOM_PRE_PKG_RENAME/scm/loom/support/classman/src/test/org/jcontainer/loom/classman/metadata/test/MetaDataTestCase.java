/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.classman.metadata.test;

import junit.framework.TestCase;
import org.jcontainer.loom.classman.metadata.ClassLoaderMetaData;
import org.jcontainer.loom.classman.metadata.ClassLoaderSetMetaData;
import org.jcontainer.loom.classman.metadata.FileSetMetaData;
import org.jcontainer.loom.classman.metadata.JoinMetaData;

/**
 * Unit test for join classloader.
 *
 * @author Peter Donald
 * @version $Revision: 1.1 $ $Date: 2004-03-31 03:18:54 $
 */
public class MetaDataTestCase
    extends TestCase
{
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
