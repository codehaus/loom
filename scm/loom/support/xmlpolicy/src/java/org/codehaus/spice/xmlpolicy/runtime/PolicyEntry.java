/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.codehaus.spice.xmlpolicy.runtime;

import java.security.CodeSource;
import java.security.Permissions;

/**
 * Internal Policy Entry holder class.
 * Holds information about an entry in policy file.
 */
final class PolicyEntry
{
    /**
     * The code source that entry is about.
     */
    private final CodeSource m_codeSource;

    /**
     * the set of permissions for code source.
     */
    private final Permissions m_permissions;

    public PolicyEntry( final CodeSource codeSource,
                        final Permissions permissions )
    {
        if( null == codeSource )
        {
            throw new NullPointerException( "codeSource" );
        }
        if( null == permissions )
        {
            throw new NullPointerException( "permissions" );
        }

        m_codeSource = codeSource;
        m_permissions = permissions;
    }

    public CodeSource getCodeSource()
    {
        return m_codeSource;
    }

    public Permissions getPermissions()
    {
        return m_permissions;
    }
}
