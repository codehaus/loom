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
import java.security.AllPermission;
import java.security.Policy;
import java.security.PermissionCollection;
import java.security.Permission;
import java.security.cert.Certificate;
import java.util.HashMap;
import java.util.Enumeration;
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
        final URL url = new URL( "http://spice.sourveforge.net" );
        final CodeSource codeSource = new CodeSource( url, new Certificate[ 0 ] );
        final Permissions permissions = new Permissions();
        try
        {
            final PolicyEntry entry = new PolicyEntry( codeSource, permissions );
            assertEquals( "Entry.getCodeSource", codeSource, entry.getCodeSource() );
            assertEquals( "Entry.getPermissions", permissions, entry.getPermissions() );
        }
        catch( final Throwable t )
        {
            fail( "Expected ctor not to except" );
        }
    }

    public void testPolicyWithNull()
        throws Exception
    {
        final URL url = new URL( "file:/-" );
        final CodeSource codeSource = new CodeSource( url, new Certificate[ 0 ] );
        final AllPermission allPermission = new AllPermission();
        final HashMap grants = new HashMap();
        grants.put( codeSource, new Permission[]{allPermission} );

        final Policy policy = new DefaultPolicy( grants );
        policy.refresh();
        final PermissionCollection resultPermissions = policy.getPermissions( codeSource );
        final Enumeration enumeration = resultPermissions.elements();
        while( enumeration.hasMoreElements() )
        {
            final Permission permission = (Permission)enumeration.nextElement();
            assertEquals( "Permissions for codeSource" + codeSource,
                          allPermission,
                          permission );
            return;
        }
        fail( "Expected to find AllPermission in set" );
    }
}
