/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.realityforge.xmlpolicy.builder;

import java.io.IOException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.HashMap;

/**
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.2 $ $Date: 2003-06-05 09:42:28 $
 */
class MockKeyStore
    extends KeyStore
{
    MockKeyStore( final HashMap certs )
        throws IOException, NoSuchAlgorithmException, CertificateException
    {
        super( new MockKeyStoreSpi( certs ), null, null );
        load( null, null );
    }
}
