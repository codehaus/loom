/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.realityforge.xmlpolicy.metadata;

/**
 * This class defines a keystore that is used when locating
 * signers of a codebase.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003-04-16 11:45:59 $
 */
public class KeyStoreMetaData
{
    /**
     * The name of the keystore. Used by Grants to
     * refer to particular key stores.
     */
    private final String m_name;

    /**
     * The location of the keystore (usually a URL).
     */
    private final String m_location;

    /**
     * The type of the keystore.
     */
    private final String m_type;

    /**
     * Construct a keysotre.
     *
     * @param name the name of the key store
     * @param location the location of keystore
     * @param type the keystore type
     */
    public KeyStoreMetaData( final String name,
                             final String location,
                             final String type )
    {
        if( null == name )
        {
            throw new NullPointerException( "name" );
        }
        if( null == location )
        {
            throw new NullPointerException( "location" );
        }
        if( null == type )
        {
            throw new NullPointerException( "type" );
        }

        m_name = name;
        m_location = location;
        m_type = type;
    }

    /**
     * Return the name of keystore.
     *
     * @return the name of keystore.
     */
    public String getName()
    {
        return m_name;
    }

    /**
     * Return the location of the KeyStore (usually a URL).
     *
     * @return the location of the KeyStore (usually a URL).
     */
    public String getLocation()
    {
        return m_location;
    }

    /**
     * Return the type of the key store (ie JKS).
     *
     * @return the type of the key store (ie JKS).
     */
    public String getType()
    {
        return m_type;
    }
}
