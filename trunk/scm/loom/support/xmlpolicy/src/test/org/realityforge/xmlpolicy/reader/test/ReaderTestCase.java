/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.realityforge.xmlpolicy.reader.test;

import org.realityforge.xmlpolicy.metadata.GrantMetaData;
import org.realityforge.xmlpolicy.metadata.KeyStoreMetaData;
import org.realityforge.xmlpolicy.metadata.PermissionMetaData;
import org.realityforge.xmlpolicy.metadata.PolicyMetaData;
import org.realityforge.xmlpolicy.test.AbstractPolicyTestCase;

/**
 * TestCase for {@link org.realityforge.xmlpolicy.reader.PolicyReader}.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 */
public class ReaderTestCase
    extends AbstractPolicyTestCase
{
    public ReaderTestCase( final String name )
    {
        super( name );
    }

    public void testConfig1()
        throws Exception
    {
        final PolicyMetaData policy = buildFromResource( "config1.xml" );

        assertEquals( "Policy KeyStore Count",
                      1,
                      policy.getKeyStores().length );

        final KeyStoreMetaData keyStore = policy.getKeyStores()[ 0 ];
        assertEquals( "KeyStore Name",
                      "myKeystore",
                      keyStore.getName() );
        assertEquals( "KeyStore Location",
                      "sar:/conf/keystore",
                      keyStore.getLocation() );
        assertEquals( "KeyStore Type",
                      "JKS",
                      keyStore.getType() );

        assertEquals( "Policy Grant Count",
                      2,
                      policy.getGrants().length );

        final GrantMetaData grant1 = policy.getGrants()[ 0 ];
        final GrantMetaData grant2 = policy.getGrants()[ 1 ];

        assertEquals( "grant1.getCodebase()",
                      "myCodeBase",
                      grant1.getCodebase() );
        assertEquals( "grant1.getKeyStore()",
                      null,
                      grant1.getKeyStore() );
        assertEquals( "grant1.getSignedBy()",
                      null,
                      grant1.getSignedBy() );

        assertEquals( "grant1.getPermissions().length",
                      1,
                      grant1.getPermissions().length );

        final PermissionMetaData permission1 = grant1.getPermissions()[ 0 ];
        assertEquals( "permission1.getClassname()",
                      "java.io.FilePermission",
                      permission1.getClassname() );
        assertEquals( "permission1.getAction()",
                      "read,write",
                      permission1.getAction() );
        assertEquals( "permission1.getTarget()",
                      "${/}tmp${/}*",
                      permission1.getTarget() );
        assertEquals( "permission1.getKeyStore()",
                      "myKeystore",
                      permission1.getKeyStore() );
        assertEquals( "permission1.getSignedBy()",
                      "Bob",
                      permission1.getSignedBy() );

        assertEquals( "grant2.getCodebase()",
                      "sar:/SAR-INF/lib/*",
                      grant2.getCodebase() );
        assertEquals( "grant2.getKeyStore()",
                      "myKeystore",
                      grant2.getKeyStore() );
        assertEquals( "grant2.getSignedBy()",
                      "Bob",
                      grant2.getSignedBy() );

        assertEquals( "grant2.getPermissions().length",
                      1,
                      grant2.getPermissions().length );

        final PermissionMetaData permission2 = grant2.getPermissions()[ 0 ];
        assertEquals( "permission2.getClassname()",
                      "java.io.FilePermission",
                      permission2.getClassname() );
        assertEquals( "permission2.getAction()",
                      null,
                      permission2.getAction() );
        assertEquals( "permission2.getTarget()",
                      null,
                      permission2.getTarget() );
        assertEquals( "permission2.getKeyStore()",
                      null,
                      permission2.getKeyStore() );
        assertEquals( "permission2.getSignedBy()",
                      null,
                      permission2.getSignedBy() );
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
              "configuration with no version" );
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
              "configuration with bad version set" );
    }

    public void testConfig4()
        throws Exception
    {
        final PolicyMetaData policy = buildFromResource( "config4.xml" );
        assertEquals( "Policy KeyStore Count",
                      0,
                      policy.getKeyStores().length );

        assertEquals( "Policy Grant Count",
                      1,
                      policy.getGrants().length );

        final GrantMetaData grant1 = policy.getGrants()[ 0 ];

        assertEquals( "grant1.getCodebase()",
                      null,
                      grant1.getCodebase() );
        assertEquals( "grant1.getKeyStore()",
                      null,
                      grant1.getKeyStore() );
        assertEquals( "grant1.getSignedBy()",
                      null,
                      grant1.getSignedBy() );

        assertEquals( "grant1.getPermissions().length",
                      1,
                      grant1.getPermissions().length );

        final PermissionMetaData permission1 = grant1.getPermissions()[ 0 ];
        assertEquals( "permission1.getClassname()",
                      "java.io.FilePermission",
                      permission1.getClassname() );
        assertEquals( "permission1.getAction()",
                      null,
                      permission1.getAction() );
        assertEquals( "permission1.getTarget()",
                      null,
                      permission1.getTarget() );
        assertEquals( "permission1.getKeyStore()",
                      null,
                      permission1.getKeyStore() );
        assertEquals( "permission1.getSignedBy()",
                      null,
                      permission1.getSignedBy() );
    }

    public void testConfig5()
        throws Exception
    {
        final PolicyMetaData policy = buildFromResource( "config5.xml" );

        assertEquals( "Policy KeyStore Count",
                      1,
                      policy.getKeyStores().length );

        final KeyStoreMetaData keyStore = policy.getKeyStores()[ 0 ];
        assertEquals( "KeyStore Name",
                      "default",
                      keyStore.getName() );
        assertEquals( "KeyStore Location",
                      "sar:/conf/keystore",
                      keyStore.getLocation() );
        assertEquals( "KeyStore Type",
                      "JKS",
                      keyStore.getType() );

        assertEquals( "Policy Grant Count",
                      2,
                      policy.getGrants().length );

        final GrantMetaData grant1 = policy.getGrants()[ 0 ];
        final GrantMetaData grant2 = policy.getGrants()[ 1 ];

        assertEquals( "grant1.getCodebase()",
                      "myCodeBase",
                      grant1.getCodebase() );
        assertEquals( "grant1.getKeyStore()",
                      null,
                      grant1.getKeyStore() );
        assertEquals( "grant1.getSignedBy()",
                      null,
                      grant1.getSignedBy() );

        assertEquals( "grant1.getPermissions().length",
                      1,
                      grant1.getPermissions().length );

        final PermissionMetaData permission1 = grant1.getPermissions()[ 0 ];
        assertEquals( "permission1.getClassname()",
                      "java.io.FilePermission",
                      permission1.getClassname() );
        assertEquals( "permission1.getAction()",
                      "read,write",
                      permission1.getAction() );
        assertEquals( "permission1.getTarget()",
                      "${/}tmp${/}*",
                      permission1.getTarget() );
        assertEquals( "permission1.getKeyStore()",
                      "default",
                      permission1.getKeyStore() );
        assertEquals( "permission1.getSignedBy()",
                      "Bob",
                      permission1.getSignedBy() );

        assertEquals( "grant2.getCodebase()",
                      "sar:/SAR-INF/lib/*",
                      grant2.getCodebase() );
        assertEquals( "grant2.getKeyStore()",
                      "default",
                      grant2.getKeyStore() );
        assertEquals( "grant2.getSignedBy()",
                      "Bob",
                      grant2.getSignedBy() );

        assertEquals( "grant2.getPermissions().length",
                      1,
                      grant2.getPermissions().length );

        final PermissionMetaData permission2 = grant2.getPermissions()[ 0 ];
        assertEquals( "permission2.getClassname()",
                      "java.io.FilePermission",
                      permission2.getClassname() );
        assertEquals( "permission2.getAction()",
                      null,
                      permission2.getAction() );
        assertEquals( "permission2.getTarget()",
                      null,
                      permission2.getTarget() );
        assertEquals( "permission2.getKeyStore()",
                      null,
                      permission2.getKeyStore() );
        assertEquals( "permission2.getSignedBy()",
                      null,
                      permission2.getSignedBy() );
    }
}
