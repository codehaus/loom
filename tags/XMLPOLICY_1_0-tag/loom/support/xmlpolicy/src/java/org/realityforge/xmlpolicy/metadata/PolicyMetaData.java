/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.realityforge.xmlpolicy.metadata;

/**
 * This class defines the set of KeyStores and Grants
 * in a policy declaration.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003-04-16 11:45:59 $
 */
public class PolicyMetaData
{
    /**
     * The KeyStores associated with policy.
     */
    private final KeyStoreMetaData[] m_keyStores;

    /**
     * The grants that make up the policy.
     */
    private final GrantMetaData[] m_grants;

    /**
     * Create a policy with specific keystores and grants.
     *
     * @param keyStores the key stores
     * @param grants the grants
     */
    public PolicyMetaData( final KeyStoreMetaData[] keyStores,
                           final GrantMetaData[] grants )
    {
        m_keyStores = keyStores;
        m_grants = grants;
    }

    /**
     * Return the KeyStores associated with policy.
     *
     * @return the KeyStores associated with policy.
     */
    public KeyStoreMetaData[] getKeyStores()
    {
        return m_keyStores;
    }

    /**
     * Return the grants that make up policy.
     *
     * @return the grants that make up policy.
     */
    public GrantMetaData[] getGrants()
    {
        return m_grants;
    }
}
