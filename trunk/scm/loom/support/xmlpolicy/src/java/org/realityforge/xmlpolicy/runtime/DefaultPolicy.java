/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.realityforge.xmlpolicy.runtime;

import java.security.CodeSource;
import java.security.Permission;
import java.security.Permissions;
import java.util.Iterator;
import java.util.Map;

/**
 * A policy implementation that accepts policys details from a map.
 * The map is between a codebase and a array of permissions.
 * Note that it was a deliberate decision to limit the time at which you can
 * specify policy data for security reasons.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 */
public class DefaultPolicy
    extends AbstractPolicy
{
    /**
     * Create a Policy that applies specified grants.
     * Each entry in map maps a codeSOurce to an array
     * of Permissions.
     *
     * @param grants the grant map
     * @throws Exception if unable to construct Policy
     */
    public DefaultPolicy( final Map grants )
        throws Exception
    {
        processGrants( grants );
    }

    /**
     * Create a policy with zero entrys.
     * Sub-classes usually use this constructor then
     * invoke processGrants separately.
     */
    public DefaultPolicy()
    {
    }

    /**
     * Process map of grants and configure Policy appropriately.
     *
     * @param grants the grants map
     * @throws Exception if unable to perform configuration
     */
    protected final void processGrants( final Map grants )
        throws Exception
    {
        final Iterator iterator = grants.keySet().iterator();
        while( iterator.hasNext() )
        {
            final CodeSource codeSource = (CodeSource)iterator.next();
            final Permission[] permissions = (Permission[])grants.get( codeSource );
            final Permissions permissionSet = createPermissionSetFor( codeSource );

            for( int i = 0; i < permissions.length; i++ )
            {
                final Permission permission = permissions[ i ];
                permissionSet.add( permission );
            }
        }
    }
}
