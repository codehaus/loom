/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.codehaus.loom.classman.test;

import java.io.File;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import junit.framework.TestCase;

import org.codehaus.loom.classman.metadata.ClassLoaderSetMetaData;
import org.codehaus.loom.classman.reader.ClassLoaderSetReader;
import org.w3c.dom.Document;

/**
 * An abstract base class for all the tests that performed on loaders.
 *
 * @author Peter Donald
 * @version $Revision: 1.1 $ $Date: 2004-04-19 22:19:24 $
 */
public abstract class AbstractLoaderTestCase
    extends TestCase
{
    protected ClassLoaderSetMetaData buildFromStream( final InputStream stream )
        throws Exception
    {
        try
        {
            final ClassLoaderSetReader builder = new ClassLoaderSetReader();
            final Document config = load( stream );
            return builder.build( config.getDocumentElement() );
        }
        catch( final Exception e )
        {
            fail( "Error building ClassLoaderSet: " + e );
            return null;
        }
    }

    protected Document load( final InputStream stream )
        throws Exception
    {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        //factory.setValidating(true);
        //factory.setNamespaceAware(true);
        final DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse( stream );
    }

    protected ClassLoaderSetMetaData buildFromResource( final String resource )
        throws Exception
    {
        final InputStream stream = getClass().getResourceAsStream( resource );
        if( null == stream )
        {
            fail( "Missing resource " + resource );
        }
        return buildFromStream( stream );
    }

    protected File getBaseDirectory()
    {
        final String basedir = System.getProperty( "basedir", "." );
        final File root = new File( basedir );
        File baseDirectory = new File( root, "target/test-classes" );
        if( !baseDirectory.exists() )
        {
            baseDirectory = new File( root, "target/classes" );
        }

        if( !baseDirectory.exists() )
        {
            fail( "Unable to locate base test directory" );
        }
        return baseDirectory;
    }
}
