/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.xmlpolicy.builder;

import java.io.IOException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.HashMap;

/**
 *
 * @author Peter Donald
 * @version $Revision: 1.1 $ $Date: 2004-03-31 03:27:58 $
 */
class MockNoInitKeyStore
    extends KeyStore
{
    MockNoInitKeyStore( final HashMap certs )
        throws IOException, NoSuchAlgorithmException, CertificateException
    {
        super( new MockKeyStoreSpi( certs ), null, null );
    }
}