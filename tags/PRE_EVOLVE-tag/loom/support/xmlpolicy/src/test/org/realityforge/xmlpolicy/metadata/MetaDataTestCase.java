/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.realityforge.xmlpolicy.metadata;

import junit.framework.TestCase;

/**
 * TestCase for {@link org.realityforge.xmlpolicy.reader.PolicyReader}.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 */
public class MetaDataTestCase
    extends TestCase
{
    public MetaDataTestCase( final String name )
    {
        super( name );
    }

    public void testNullClassNameInPermissionCtor()
        throws Exception
    {
        try
        {
            new PermissionMetaData( null, null, null, null, null );
            fail( "Expected to fail due to null pointer in ctor" );
        }
        catch( final NullPointerException npe )
        {
            assertEquals( "NPE message",
                          "classname",
                          npe.getMessage() );
        }
    }

    public void testNullPermissionsInGrantCtor()
        throws Exception
    {
        try
        {
            new GrantMetaData( null, null, null, null );
            fail( "Expected to fail due to null pointer in ctor" );
        }
        catch( final NullPointerException npe )
        {
            assertEquals( "NPE message",
                          "permissions",
                          npe.getMessage() );
        }
    }

    public void testNullPermissionsElementInGrantCtor()
        throws Exception
    {
        try
        {
            new GrantMetaData( null, null, null, new PermissionMetaData[]{null} );
            fail( "Expected to fail due to null pointer in ctor" );
        }
        catch( final NullPointerException npe )
        {
            assertEquals( "NPE message",
                          "permissions[0]",
                          npe.getMessage() );
        }
    }


    public void testNullSignedByNonNullKeyStoreInGrantCtor()
        throws Exception
    {
        try
        {
            new GrantMetaData( null, null, "default", new PermissionMetaData[0] );
            fail( "Expected to fail due to null pointer in ctor" );
        }
        catch( final NullPointerException npe )
        {
            assertEquals( "NPE message",
                          "signedBy",
                          npe.getMessage() );
        }
    }

    public void testNullKeyStoreNonNullSignedByInGrantCtor()
        throws Exception
    {
        try
        {
            new GrantMetaData( null, "default", null, new PermissionMetaData[ 0 ] );
            fail( "Expected to fail due to null pointer in ctor" );
        }
        catch( final NullPointerException npe )
        {
            assertEquals( "NPE message",
                          "keyStore",
                          npe.getMessage() );
        }
    }

    public void testNullNameInKeyStoreCtor()
        throws Exception
    {
        try
        {
            new KeyStoreMetaData( null, "", "" );
            fail( "Expected to fail due to null pointer in ctor" );
        }
        catch( final NullPointerException npe )
        {
            assertEquals( "NPE message",
                          "name",
                          npe.getMessage() );
        }
    }

    public void testNullLocationInKeyStoreCtor()
        throws Exception
    {
        try
        {
            new KeyStoreMetaData( "", null, "" );
            fail( "Expected to fail due to null pointer in ctor" );
        }
        catch( final NullPointerException npe )
        {
            assertEquals( "NPE message",
                          "location",
                          npe.getMessage() );
        }
    }

    public void testNullTypeInKeyStoreCtor()
        throws Exception
    {
        try
        {
            new KeyStoreMetaData( "", "", null );
            fail( "Expected to fail due to null pointer in ctor" );
        }
        catch( final NullPointerException npe )
        {
            assertEquals( "NPE message",
                          "type",
                          npe.getMessage() );
        }
    }

    public void testNullKeyStoreNonNullSignedByInPermissionCtor()
        throws Exception
    {
        try
        {
            new PermissionMetaData( "", "", "", "", null );
            fail( "Expected to fail due to null pointer in ctor" );
        }
        catch( final NullPointerException npe )
        {
            assertEquals( "NPE message",
                          "keyStore",
                          npe.getMessage() );
        }
    }

    public void testNullSignedByNonNullKeyStoreInPermissionCtor()
        throws Exception
    {
        try
        {
            new PermissionMetaData( "", "","", null, "default" );
            fail( "Expected to fail due to null pointer in ctor" );
        }
        catch( final NullPointerException npe )
        {
            assertEquals( "NPE message",
                          "signedBy",
                          npe.getMessage() );
        }
    }

}
