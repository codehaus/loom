/* ====================================================================
 * JContainer Software License, version 1.1
 *
 * Copyright (c) 2003, JContainer Group. All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. Neither the name of the JContainer Group nor the name "Loom" nor
 *    the names of its contributors may be used to endorse or promote
 *    products derived from this software without specific prior
 *    written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * ====================================================================
 *
 * JContainer Loom includes code from the Apache Software Foundation
 *
 * ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1997-2003 The Apache Software Foundation. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *    "This product includes software developed by the
 *    Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software
 *    itself, if and wherever such third-party acknowledgments
 *    normally appear.
 *
 * 4. The names "Jakarta", "Avalon", and "Apache Software Foundation"
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
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
 * @version $Revision: 1.3 $ $Date: 2003-08-17 18:27:32 $
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
