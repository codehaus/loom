/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.realityforge.xmlpolicy.builder;

import java.security.KeyStore;
import java.net.URL;
import java.util.HashMap;

/**
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.3 $ $Date: 2003-06-05 09:53:43 $
 */
class TestPolicyBuilder
    extends PolicyBuilder
{
    public static final MockCertificate JENNY_CERTIFICATE = new MockCertificate();
    public static final MockCertificate MISCHELLE_CERTIFICATE = new MockCertificate();
    public static final MockCertificate GEORGE_CERTIFICATE = new MockCertificate();

    protected KeyStore createKeyStore( String type,
                                       URL url )
        throws Exception
    {
        if( url.equals( new URL( "http://spice.sourceforge.net" ) ) )
        {
            final HashMap certs = new HashMap();
            certs.put( "jenny", JENNY_CERTIFICATE );
            certs.put( "mischelle", MISCHELLE_CERTIFICATE );
            certs.put( "george", GEORGE_CERTIFICATE );
            return new MockKeyStore( certs );
        }
        else
        {
            throw new Exception( "Unable to create keystore " + url );
        }
    }
}
