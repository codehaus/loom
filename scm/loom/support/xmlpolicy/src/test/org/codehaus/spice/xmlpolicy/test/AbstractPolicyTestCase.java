/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.codehaus.spice.xmlpolicy.test;

import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import junit.framework.TestCase;
import org.codehaus.spice.xmlpolicy.metadata.PolicyMetaData;
import org.codehaus.spice.xmlpolicy.reader.PolicyReader;
import org.w3c.dom.Document;

/**
 *  An abstract testcase to test policys.
 *
 * @author Peter Donald
 * @version $Revision: 1.1 $ $Date: 2003-12-02 09:16:08 $
 */
public class AbstractPolicyTestCase
    extends TestCase
{
    protected PolicyMetaData buildFromStream( final InputStream stream )
        throws Exception
    {
        try
        {
            final PolicyReader builder = new PolicyReader();
            final Document config = load( stream );
            return builder.readPolicy( config.getDocumentElement() );
        }
        catch( final Exception e )
        {
            fail( "Error building Policy: " + e );
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

    protected PolicyMetaData buildFromResource( final String resource )
        throws Exception
    {
        final InputStream stream = getClass().getResourceAsStream( resource );
        if( null == stream )
        {
            fail( "Missing resource " + resource );
        }
        return buildFromStream( stream );
    }

    public AbstractPolicyTestCase( String name )
    {
        super( name );
    }
}
