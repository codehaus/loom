/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.realityforge.classman.builder.test;

import java.net.URL;
import org.realityforge.classman.builder.SimpleLoaderResolver;
import org.realityforge.classman.test.AbstractLoaderTestCase;
import org.realityforge.classman.test.DataConstants;

/**
 * Unit test for join classloader.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.3 $ $Date: 2003-08-01 02:51:54 $
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
        final SimpleLoaderResolver resolver = new SimpleLoaderResolver( null );
        final URL url = resolver.resolveURL( "." );
        assertTrue( "URL is a dir", url.toString().endsWith( "/" ) );
    }

    public void testNullManager()
        throws Exception
    {
        final SimpleLoaderResolver resolver = new SimpleLoaderResolver( null );
        try
        {
            resolver.resolveExtension( DataConstants.EXTENSION );
            fail( "Expected resolve extension as resolver unable to implement" );
        }
        catch( UnsupportedOperationException e )
        {
        }
    }
}
