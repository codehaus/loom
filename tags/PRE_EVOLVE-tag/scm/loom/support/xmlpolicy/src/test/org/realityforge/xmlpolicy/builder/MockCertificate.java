/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.realityforge.xmlpolicy.builder;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;

/**
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.4 $ $Date: 2003-06-05 10:16:46 $
 */
class MockCertificate
    extends Certificate
{
    public static final MockCertificate JENNY_CERTIFICATE = new MockCertificate("jenny");
    public static final MockCertificate MISCHELLE_CERTIFICATE = new MockCertificate("mischelle");
    public static final MockCertificate GEORGE_CERTIFICATE = new MockCertificate("george");

    private final String m_name;

    public MockCertificate(final String name )
    {
        super( "name" );
        m_name = name;
    }

    public byte[] getEncoded()
        throws CertificateEncodingException
    {
        return new byte[ 0 ];
    }

    public void verify( PublicKey key )
        throws CertificateException, NoSuchAlgorithmException,
        InvalidKeyException, NoSuchProviderException,
        SignatureException
    {
    }

    public void verify( PublicKey key, String sigProvider )
        throws CertificateException, NoSuchAlgorithmException,
        InvalidKeyException, NoSuchProviderException,
        SignatureException
    {
    }

    public String toString()
    {
        return m_name;
    }

    public PublicKey getPublicKey()
    {
        return null;
    }
}
