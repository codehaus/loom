/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.realityforge.xmlpolicy.builder;

import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Policy;
import java.security.cert.Certificate;
import junit.framework.TestCase;
import org.realityforge.xmlpolicy.metadata.GrantMetaData;
import org.realityforge.xmlpolicy.metadata.KeyStoreMetaData;
import org.realityforge.xmlpolicy.metadata.PolicyMetaData;

/**
 * TestCase for Builder package.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 */
public class BuilderTestCase
    extends TestCase
{
    public BuilderTestCase( final String name )
    {
        super( name );
    }

    public void testEmptyMetaData()
        throws Exception
    {
        final PolicyBuilder builder = new PolicyBuilder();
        final PolicyMetaData metaData =
            new PolicyMetaData( new KeyStoreMetaData[ 0 ], new GrantMetaData[ 0 ] );
        final TestResolver resolver = new TestResolver();
        try
        {
            final Policy policy = builder.buildPolicy( metaData, resolver );
            final PermissionCollection permissions =
                policy.getPermissions( new CodeSource( null, new Certificate[ 0 ] ) );
            assertEquals( "Expect no permissions for empty metaData",
                          false,
                          permissions.elements().hasMoreElements() );
        }
        catch( final Exception e )
        {
            fail( "Expected to be able to build Policy with empty metadata" );
        }
    }

    public void testNullResolverInBuildPolicy()
        throws Exception
    {
        final PolicyBuilder builder = new PolicyBuilder();
        final PolicyMetaData metaData =
            new PolicyMetaData( new KeyStoreMetaData[ 0 ], new GrantMetaData[ 0 ] );
        try
        {
            builder.buildPolicy( metaData, null );
            fail( "Expected to fail due to null pointer in buildPolicy" );
        }
        catch( final NullPointerException npe )
        {
            assertEquals( "NPE message",
                          "resolver",
                          npe.getMessage() );
        }
    }

    public void testNullMetaDataInBuildPolicy()
        throws Exception
    {
        final PolicyBuilder builder = new PolicyBuilder();
        try
        {
            builder.buildPolicy( null, new TestResolver() );
            fail( "Expected to fail due to null pointer in buildPolicy" );
        }
        catch( final NullPointerException npe )
        {
            assertEquals( "NPE message",
                          "policy",
                          npe.getMessage() );
        }

    }
}
