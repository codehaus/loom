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
package org.jcontainer.loom.components.extensions.pkgmgr.impl;

import java.util.ArrayList;
import java.util.Collections;
import org.realityforge.extension.Extension;
import org.jcontainer.loom.components.extensions.pkgmgr.ExtensionManager;
import org.jcontainer.loom.components.extensions.pkgmgr.OptionalPackage;

/**
 * A {@link ExtensionManager} that can delegate to multiple
 * different package repositories.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.2 $ $Date: 2003-08-02 12:26:32 $
 */
public class DelegatingExtensionManager
    implements ExtensionManager
{
    /**
     * The list containing the {@link ExtensionManager} objects.
     */
    private final ArrayList m_extensionManagers = new ArrayList();

    /**
     * Default constructor that does not add any repositories.
     */
    public DelegatingExtensionManager()
    {
    }

    /**
     * Default constructor that delegates to specified extensionManagers.
     */
    public DelegatingExtensionManager( final ExtensionManager[] extensionManagers )
    {
        for( int i = 0; i < extensionManagers.length; i++ )
        {
            addExtensionManager( extensionManagers[ i ] );
        }
    }

    /**
     * Add a extensionManager to list of repositories delegated to
     * to find Optional Packages.
     *
     * @param extensionManager the extensionManager to add
     */
    protected synchronized void addExtensionManager( final ExtensionManager extensionManager )
    {
        if( !m_extensionManagers.contains( extensionManager ) )
        {
            m_extensionManagers.add( extensionManager );
        }
    }

    /**
     * Add a extensionManager to list of repositories delegated to
     * to find Optional Packages.
     *
     * @param extensionManager the extensionManager to add
     * @deprecated Use addExtensionManager instead
     */
    protected void addPackageRepository( final ExtensionManager extensionManager )
    {
        addExtensionManager( extensionManager );
    }

    /**
     * Remove a repository from list of repositories delegated to
     * to find Optional Packages.
     *
     * @param repository the repository to remove
     */
    protected synchronized void removeExtensionManager( final ExtensionManager repository )
    {
        m_extensionManagers.remove( repository );
    }

    /**
     * Remove a extensionManager from list of repositories delegated to
     * to find Optional Packages.
     *
     * @param extensionManager the extensionManager to remove
     * @deprecated Use removeExtensionManager instead.
     */
    protected void removePackageRepository( final ExtensionManager extensionManager )
    {
        removeExtensionManager( extensionManager );
    }

    /**
     * Scan through list of respositories and return all the matching {@link OptionalPackage}
     * objects that match in any repository.
     *
     * @param extension the extension to search for
     * @return the matching {@link OptionalPackage} objects.
     */
    public synchronized OptionalPackage[] getOptionalPackages( final Extension extension )
    {
        final ArrayList resultPackages = new ArrayList();

        final int size = m_extensionManagers.size();
        for( int i = 0; i < size; i++ )
        {
            final ExtensionManager repository =
                (ExtensionManager)m_extensionManagers.get( i );
            final OptionalPackage[] packages =
                repository.getOptionalPackages( extension );
            if( null == packages || 0 == packages.length )
            {
                continue;
            }

            for( int j = 0; j < packages.length; j++ )
            {
                resultPackages.add( packages[ j ] );
            }
        }

        final OptionalPackageComparator comparator =
            new OptionalPackageComparator( extension.getExtensionName() );
        Collections.sort( resultPackages, comparator );
        final OptionalPackage[] resultData =
            new OptionalPackage[ resultPackages.size() ];
        return (OptionalPackage[])resultPackages.toArray( resultData );
    }
}
