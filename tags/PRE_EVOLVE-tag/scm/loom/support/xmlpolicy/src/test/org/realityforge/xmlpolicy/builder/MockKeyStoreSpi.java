/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.realityforge.xmlpolicy.builder;

import java.security.KeyStoreSpi;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003-06-05 09:41:47 $
 */
class MockKeyStoreSpi
    extends KeyStoreSpi
{
    private final HashMap m_certs;

    public MockKeyStoreSpi( HashMap certs )
    {
        m_certs = certs;
    }

    public Key engineGetKey( String alias, char[] password )
        throws NoSuchAlgorithmException, UnrecoverableKeyException
    {
        return null;
    }

    public Certificate[] engineGetCertificateChain( String alias )
    {
        return new Certificate[ 0 ];
    }

    public Certificate engineGetCertificate( String alias )
    {
        return (Certificate)m_certs.get( alias );
    }

    public Date engineGetCreationDate( String alias )
    {
        return null;
    }

    public void engineSetKeyEntry( String alias, Key key,
                                   char[] password,
                                   Certificate[] chain )
        throws KeyStoreException
    {
    }

    public void engineSetKeyEntry( String alias, byte[] key,
                                   Certificate[] chain )
        throws KeyStoreException
    {
    }

    public void engineSetCertificateEntry( String alias,
                                           Certificate cert )
        throws KeyStoreException
    {
    }

    public void engineDeleteEntry( String alias )
        throws KeyStoreException
    {
    }

    public Enumeration engineAliases()
    {
        return null;
    }

    public boolean engineContainsAlias( String alias )
    {
        return false;
    }

    public int engineSize()
    {
        return 0;
    }

    public boolean engineIsKeyEntry( String alias )
    {
        return false;
    }

    public boolean engineIsCertificateEntry( String alias )
    {
        return false;
    }

    public String engineGetCertificateAlias( Certificate cert )
    {
        return null;
    }

    public void engineStore( OutputStream stream, char[] password )
        throws IOException, NoSuchAlgorithmException, CertificateException
    {
    }

    public void engineLoad( InputStream stream, char[] password )
        throws IOException, NoSuchAlgorithmException, CertificateException
    {
    }
}
