/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.realityforge.xmlpolicy.runtime;

import java.net.URL;
import java.security.CodeSource;
import java.security.Permissions;
import java.security.cert.Certificate;
import junit.framework.TestCase;

/**
 * TestCase for Runtime package.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 */
public class RuntimeTestCase
    extends TestCase
{
    public RuntimeTestCase( final String name )
    {
        super( name );
    }

    public void testNullCodeSourceInEntryCtor()
        throws Exception
    {
        try
        {
            new PolicyEntry( null, new Permissions() );
            fail( "Expected to fail due to null pointer in ctor" );
        }
        catch( final NullPointerException npe )
        {
            assertEquals( "NPE message",
                          "codeSource",
                          npe.getMessage() );
        }
    }

    public void testNullPermissionsInEntryCtor()
        throws Exception
    {
        try
        {
            new PolicyEntry( new CodeSource( new URL( "http://spice.sourveforge.net" ),
                                             new Certificate[ 0 ] ),
                             null );
            fail( "Expected to fail due to null pointer in ctor" );
        }
        catch( final NullPointerException npe )
        {
            assertEquals( "NPE message",
                          "permissions",
                          npe.getMessage() );
        }
    }

    public void testEntryCtor()
        throws Exception
    {
        try
        {
            new PolicyEntry( new CodeSource( new URL( "http://spice.sourveforge.net" ),
                                             new Certificate[ 0 ] ),
                             new Permissions() );
        }
        catch( final Throwable t )
        {
            fail( "Expected ctor not to except" );
        }
    }
}
