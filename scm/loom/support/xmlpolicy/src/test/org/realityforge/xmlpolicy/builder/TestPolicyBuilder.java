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
 * @version $Revision: 1.1 $ $Date: 2003-06-05 09:41:31 $
 */
class TestPolicyBuilder
    extends PolicyBuilder
{
    protected KeyStore createKeyStore( String type,
                                       URL url )
        throws Exception
    {
        final HashMap certs = new HashMap();
        certs.put( "jenny", new MockCertificate() );
        certs.put( "mischelle", new MockCertificate() );
        certs.put( "george", new MockCertificate() );
        return new MockKeyStore( certs );
    }
}
