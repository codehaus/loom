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
 * @version $Revision: 1.1 $ $Date: 2003-06-05 22:38:41 $
 */
class TestNoInitPolicyBuilder
    extends PolicyBuilder
{
    protected KeyStore createKeyStore( String type,
                                       URL url )
        throws Exception
    {
        if( url.equals( new URL( "http://spice.sourceforge.net" ) ) )
        {
            final HashMap certs = new HashMap();
            certs.put( "jenny", MockCertificate.JENNY_CERTIFICATE );
            certs.put( "mischelle", MockCertificate.MISCHELLE_CERTIFICATE );
            certs.put( "george", MockCertificate.GEORGE_CERTIFICATE );
            return new MockNoInitKeyStore( certs );
        }
        else
        {
            throw new Exception( "Unable to create keystore " + url );
        }
    }
}
