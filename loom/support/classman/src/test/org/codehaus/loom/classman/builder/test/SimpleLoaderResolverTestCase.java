/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.codehaus.loom.classman.builder.test;

import java.net.URL;

import org.codehaus.loom.classman.builder.SimpleLoaderResolver;
import org.codehaus.loom.classman.test.AbstractLoaderTestCase;
import org.codehaus.loom.classman.test.DataConstants;

/**
 * Unit test for join classloader.
 *
 * @author Peter Donald
 * @version $Revision: 1.1 $ $Date: 2004-04-19 22:19:25 $
 */
public class SimpleLoaderResolverTestCase
    extends AbstractLoaderTestCase
{
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
