/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.realityforge.xmlpolicy.verifier.test;

import org.realityforge.xmlpolicy.metadata.PolicyMetaData;
import org.realityforge.xmlpolicy.test.AbstractPolicyTestCase;
import org.realityforge.xmlpolicy.verifier.PolicyVerifier;

/**
 * TestCase for {@link org.realityforge.xmlpolicy.reader.PolicyReader}.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 */
public class VerifierTestCase
    extends AbstractPolicyTestCase
{
    public VerifierTestCase( final String name )
    {
        super( name );
    }

    public void testConfig1()
        throws Exception
    {
        try
        {
            verifyResource( "config1.xml" );
        }
        catch( final Throwable t )
        {
            return;
        }

        fail( "Expected verify to fail as specified " +
              "bad name for keyStore" );
    }

    public void testConfig2()
        throws Exception
    {
        try
        {
            verifyResource( "config2.xml" );
        }
        catch( final Throwable t )
        {
            return;
        }

        fail( "Expected verify to fail as permission " +
              "references non existent keystore" );
    }

    public void testConfig3()
        throws Exception
    {
        try
        {
            verifyResource( "config3.xml" );
        }
        catch( final Throwable t )
        {
            return;
        }

        fail( "Expected verify to fail as grant " +
              "references non existent keystore" );
    }

    public void testConfig4()
        throws Exception
    {
        try
        {
            verifyResource( "config4.xml" );
        }
        catch( final Throwable t )
        {
            return;
        }

        fail( "Expected verify to fail as specified " +
              "action with null target" );
    }

    public void testConfig5()
        throws Exception
    {
        try
        {
            verifyResource( "config5.xml" );
        }
        catch( final Throwable t )
        {
            fail( "Expected to pass when not specifying keystore" );
        }
    }

    public void testConfig6()
        throws Exception
    {
        try
        {
            verifyResource( "config6.xml" );
        }
        catch( final Throwable t )
        {
            return;
        }

        fail( "Expected verify to fail as specified " +
              "empty name for keyStore" );
    }

    public void testConfig7()
        throws Exception
    {
        try
        {
            verifyResource( "config7.xml" );
        }
        catch( final Throwable t )
        {
            return;
        }

        fail( "Expected verify to fail as specified " +
              "bad character in center of name for keyStore" );
    }

    public void testConfig8()
        throws Exception
    {
        try
        {
            verifyResource( "config8.xml" );
        }
        catch( final Throwable t )
        {
            fail( "Expected verify to pass as specified " +
                  "good name for keyStore" );
        }        
    }

    private void verifyResource( final String resource )
        throws Exception
    {
        final PolicyMetaData defs = buildFromResource( resource );
        final PolicyVerifier verifier = new PolicyVerifier();
        verifier.verifyPolicy( defs );
    }
}
