/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.realityforge.classman.verifier.test;

import org.realityforge.classman.metadata.ClassLoaderSetMetaData;
import org.realityforge.classman.test.AbstractLoaderTestCase;
import org.realityforge.classman.verifier.ClassLoaderVerifier;

/**
 * TestCase for {@link org.realityforge.classman.reader.ClassLoaderSetReader}.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 */
public class VerifierTestCase
    extends AbstractLoaderTestCase
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
              "bad name for join" );
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

        fail( "Expected verify to fail as specified " +
              "bad name for classloader" );
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

        fail( "Expected verify to fail as specified " +
              "bad parent for classloader" );
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
              "non existing ClassLoader in Join " +
              "classloader-ref" );
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
            return;
        }

        fail( "Expected verify to fail as specified " +
              "default classloader does not exist." );
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
              "join classloader links against same " +
              "classloader multiple times." );
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
              "classloader has entry multiple times." );
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
            return;
        }

        fail( "Expected verify to fail as a " +
              "join had same name as another join." );
    }

    public void testConfig9()
        throws Exception
    {
        try
        {
            verifyResource( "config9.xml" );
        }
        catch( final Throwable t )
        {
            return;
        }

        fail( "Expected verify to fail as a " +
              "join had same name as a " +
              "classloader." );
    }

    public void testConfig10()
        throws Exception
    {
        try
        {
            verifyResource( "config10.xml" );
        }
        catch( final Throwable t )
        {
            return;
        }

        fail( "Expected verify to fail as a " +
              "join had same name as another " +
              "predefined classloader." );
    }

    public void testConfig11()
        throws Exception
    {
        try
        {
            verifyResource( "config11.xml" );
        }
        catch( final Throwable t )
        {
            return;
        }

        fail( "Expected verify to fail as a " +
              "classloader had same name as another " +
              "classloader." );
    }

    public void testConfig12()
        throws Exception
    {
        try
        {
            verifyResource( "config12.xml" );
        }
        catch( final Throwable t )
        {
            return;
        }

        fail( "Expected verify to fail as a " +
              "classloader had same name as a " +
              "predefined classloader." );
    }

    public void testConfig13()
        throws Exception
    {
        try
        {
            final ClassLoaderSetMetaData defs =
                buildFromResource( "config13.xml" );
            verify( defs );
        }
        catch( final Throwable t )
        {
            return;
        }

        fail( "Expected verify to fail as a " +
              "predefined had same name as another " +
              "predefined." );
    }

    public void testConfig14()
        throws Exception
    {
        try
        {
            final ClassLoaderSetMetaData defs =
                buildFromResource( "config14.xml" );
            verify( defs );
        }
        catch( final Throwable t )
        {
            return;
        }

        fail( "Expected verify to fail as a " +
              "predefined an empty name." );
    }

    public void testConfig15()
        throws Exception
    {
        try
        {
            final ClassLoaderSetMetaData defs =
                buildFromResource( "config15.xml" );
            verify( defs );
        }
        catch( final Throwable t )
        {
            return;
        }

        fail( "Expected verify to fail as a " +
              "predefined has same name as join." );
    }

    public void testConfig16()
        throws Exception
    {
        try
        {
            final ClassLoaderSetMetaData defs =
                buildFromResource( "config16.xml" );
            verify( defs );
        }
        catch( final Throwable t )
        {
            fail( "Should be able to have a join as default classloader." );
        }
    }

    public void testConfig17()
        throws Exception
    {
        try
        {
            verifyResource( "config17.xml" );
        }
        catch( final Throwable t )
        {
            fail( "Expected verify to pass" );
        }
    }

    private void verifyResource( final String resource )
        throws Exception
    {
        final ClassLoaderSetMetaData defs = buildFromResource( resource );
        verify( defs );
    }

    private void verify( final ClassLoaderSetMetaData defs )
        throws Exception
    {
        final ClassLoaderVerifier verifier = new ClassLoaderVerifier();
        verifier.verifyClassLoaderSet( defs );
    }
}
