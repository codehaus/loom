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
package org.codehaus.loom.components.extensions;

import java.io.File;
import java.util.ArrayList;

import org.codehaus.loom.components.extensions.pkgmgr.ExtensionManager;
import org.codehaus.loom.components.extensions.pkgmgr.OptionalPackage;
import org.codehaus.spice.salt.i18n.ResourceManager;
import org.codehaus.spice.salt.i18n.Resources;
import org.codehaus.dna.Active;
import org.codehaus.dna.Configurable;
import org.codehaus.dna.Configuration;
import org.codehaus.dna.ConfigurationException;
import org.codehaus.dna.LogEnabled;
import org.codehaus.dna.Logger;

/**
 * @author Peter Donald
 * @version $Revision: 1.1 $ $Date: 2004-04-19 22:22:45 $
 * @dna.component
 * @mx.component
 */
public class DefaultExtensionManager
    extends org.codehaus.loom.components.extensions.pkgmgr.impl.DefaultExtensionManager
    implements LogEnabled, Configurable, Active, ExtensionManager
{
    private final static Resources REZ =
        ResourceManager.getPackageResources( DefaultExtensionManager.class );

    private Logger m_logger;
    private File m_extDir;

    public void enableLogging( final Logger logger )
    {
        m_logger = logger;
    }

    public void configure( Configuration configuration )
        throws ConfigurationException
    {
        final String extDir =
            configuration.getChild( "extensions-dir" ).getValue();
        m_extDir = new File( extDir );
    }

    public void initialize()
        throws Exception
    {
        setPath( new File[]{m_extDir} );
        rescanPath();
    }

    public void dispose()
    {
        clearCache();
    }

    /**
     * Retrieve an array of paths where each element in array represents a
     * directory in which the ExtensionManager will look for Extensions.
     *
     * @return the list of paths to search in
     * @mx.attribute description="The list of paths to search in"
     */
    public File[] getPaths()
    {
        return super.getPaths();
    }

    /**
     * Force the ExtensionManager to rescan the paths to discover new Extensions
     * that have been added or remove old Extensions that have been removed.
     *
     * @mx.operation description="Force the ExtensionManager to rescan the paths
     * to discover new Extensions that have been added or remove old Extensions
     * that have been removed."
     */
    public void rescanPath()
    {
        super.scanPath();

        //Display a list of packages once they have been added.
        if( m_logger.isDebugEnabled() )
        {
            final ArrayList list = new ArrayList();
            final OptionalPackage[] optionalPackages = getAllOptionalPackages();
            for( int i = 0; i < optionalPackages.length; i++ )
            {
                list.add( optionalPackages[ i ].getFile() );
            }

            final String message =
                REZ.format( "extension.packages.notice", list );
            m_logger.debug( message );
        }
    }

    protected void debug( final String message )
    {
        m_logger.debug( message );
    }
}
