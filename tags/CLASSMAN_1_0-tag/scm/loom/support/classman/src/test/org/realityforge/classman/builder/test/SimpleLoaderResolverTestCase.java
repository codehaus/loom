/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.realityforge.classman.builder.test;

import org.realityforge.classman.builder.SimpleLoaderResolver;
import org.realityforge.classman.test.AbstractLoaderTestCase;
import org.realityforge.classman.test.DummyExtensionManager;
import java.net.URL;

/**
 * Unit test for join classloader.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003-04-04 13:52:13 $
 */
public class SimpleLoaderResolverTestCase
    extends AbstractLoaderTestCase
{
    public SimpleLoaderResolverTestCase( String name )
    {
        super( name );
    }

    public void testNullBaseDirectory()
        throws Exception
    {
        final SimpleLoaderResolver resolver = new SimpleLoaderResolver( null, null );
        final URL url = resolver.resolveURL( "." );
        assertTrue( "URL is a dir", url.toString().endsWith( "/" ) );
    }

    public void testNullManager()
        throws Exception
    {
        final SimpleLoaderResolver resolver = new SimpleLoaderResolver( null, null );
        try
        {
            resolver.resolveExtension( DummyExtensionManager.EXTENSION );
            fail( "Expected resolve extension to fail as no manager set" );
        }
        catch( IllegalStateException e )
        {
        }
    }

    public void testFilesetNotSupported()
        throws Exception
    {
        final SimpleLoaderResolver resolver = new SimpleLoaderResolver( null, null );
        try
        {
            resolver.resolveFileSet( null, null, null );
            fail( "Expected resolve fileset to be an unsupported operation" );
        }
        catch( UnsupportedOperationException e )
        {
        }
    }
}
