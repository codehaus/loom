/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 *
 * This product includes software developed by the
 * Apache Software Foundation (http://www.apache.org/).
 */
package org.jcontainer.loom.launcher;

import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;

/**
 * Default polic class to give every code base all permssions.
 * Will be replaced once the kernel loads.
 */
class FreeNEasyPolicy
    extends Policy
{
    public PermissionCollection getPermissions( final CodeSource codeSource )
    {
        final Permissions permissions = new Permissions();
        permissions.add( new java.security.AllPermission() );
        return permissions;
    }

    public void refresh()
    {
    }
}
