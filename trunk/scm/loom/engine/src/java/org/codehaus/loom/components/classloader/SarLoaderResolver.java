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
package org.codehaus.loom.components.classloader;

import java.io.File;
import java.net.URL;
import java.security.Policy;
import java.util.Arrays;
import java.util.Set;
import org.codehaus.loom.classman.builder.SimpleLoaderResolver;
import org.codehaus.loom.components.extensions.pkgmgr.OptionalPackage;
import org.codehaus.loom.components.extensions.pkgmgr.PackageManager;
import org.codehaus.loom.components.util.ResourceUtil;
import org.codehaus.loom.extension.Extension;
import org.codehaus.spice.salt.i18n.ResourceManager;
import org.codehaus.spice.salt.i18n.Resources;
import org.codehaus.dna.LogEnabled;
import org.codehaus.dna.Logger;
import org.codehaus.dna.impl.ContainerUtil;

/**
 * a LoaderResolver that knows about container environment, and the way it is
 * split across multiple directories.
 *
 * @author Peter Donald
 * @version $Revision: 1.1 $ $Date: 2004-04-19 22:22:48 $
 */
class SarLoaderResolver
    extends SimpleLoaderResolver
    implements LogEnabled
{
    private final static Resources REZ =
        ResourceManager.getPackageResources( SarLoaderResolver.class );

    /** The PackageManager to use to resolve Extensions. */
    private PackageManager m_manager;

    /** Logger to use when reporting information */
    private Logger m_logger;

    /** The policy object to use when creating ClassLoaders */
    private Policy m_policy;

    /** Base work directory for application. */
    private File m_workDirectory;

    /**
     * Create a resolver for a jar. The resolver merges both the work and base
     * directory hierarchies.
     *
     * @param manager the PackageManager
     * @param policy the policy to use when creating classloaders
     * @param baseDirectory the base directory
     * @param workDirectory the base work directory
     */
    SarLoaderResolver( final PackageManager manager,
                       final Policy policy,
                       final File baseDirectory,
                       final File workDirectory )
    {
        super( baseDirectory );
        if( null == manager )
        {
            throw new NullPointerException( "manager" );
        }
        if( null == policy )
        {
            throw new NullPointerException( "policy" );
        }
        if( null == baseDirectory )
        {
            throw new NullPointerException( "baseDirectory" );
        }
        if( null == workDirectory )
        {
            throw new NullPointerException( "workDirectory" );
        }

        m_manager = manager;
        m_policy = policy;
        m_workDirectory = workDirectory;
    }

    /**
     * Aquire an Avalon Logger.
     *
     * @param logger the avalon logger
     */
    public void enableLogging( final Logger logger )
    {
        m_logger = logger;
    }

    public URL resolveExtension( final Extension extension )
        throws Exception
    {
        if( null == m_manager )
        {
            final String message =
                REZ.getString( "missing-packagemanager" );
            throw new IllegalStateException( message );
        }
        final OptionalPackage optionalPackage =
            m_manager.getOptionalPackage( extension );
        return optionalPackage.getFile().toURL();
    }

    /**
     * Resolve a location to either the work or home hierarchys.
     *
     * @param location the location
     * @return the URL representing location
     * @throws Exception if unable to resolve location
     */
    public URL resolveURL( final String location )
        throws Exception
    {
        final File file =
            ResourceUtil.getFileForResource( location,
                                             getBaseDirectory(),
                                             m_workDirectory );
        return file.toURL();
    }

    /**
     * Resolve a fileset. Make sure it is resolved against both the work and the
     * base directories of application.
     *
     * @param baseDirectory the base directory of fileset
     * @param includes the fileset includes
     * @param excludes the ant style excludes
     * @return the URLs that are in fileset
     * @throws Exception if unable to resolve fileset
     */
    public URL[] resolveFileSet( final String baseDirectory,
                                 final String[] includes,
                                 final String[] excludes )
        throws Exception
    {
        final URL[] baseURLs =
            resolveFileSet( getBaseDirectory(),
                            baseDirectory,
                            includes,
                            excludes );
        final URL[] workURLs =
            resolveFileSet( m_workDirectory,
                            baseDirectory,
                            includes,
                            excludes );
        final URL[] urls = new URL[ baseURLs.length + workURLs.length ];
        System.arraycopy( baseURLs, 0, urls, 0, baseURLs.length );
        System.arraycopy( workURLs,
                          0,
                          urls,
                          baseURLs.length,
                          workURLs.length );
        return urls;
    }

    /**
     * Create a ClassLoader that obeys policy in environment.xml.
     *
     * @param parent the parent classloader
     * @param urls the set of URLs for classloader
     * @return the new classloader
     * @throws Exception if unable to create classloader
     */
    public ClassLoader createClassLoader( final ClassLoader parent,
                                          final URL[] urls )
        throws Exception
    {
        final URL[] classpath = determineCompleteClasspath( urls );
        if( m_logger.isDebugEnabled() )
        {
            final String message =
                REZ.format( "resolver.loader-urls.notice",
                            Arrays.asList( classpath ) );
            m_logger.debug( message );
        }
        final PolicyClassLoader loader =
            new PolicyClassLoader( classpath, parent, m_policy );
        ContainerUtil.enableLogging( loader, m_logger );
        return loader;
    }

    /**
     * Route Logging to Avalons Logger.
     *
     * @param message the debug message
     */
    protected void debug( final String message )
    {
        m_logger.debug( message );
    }

    /**
     * Defer to Avalons Logger to see if debug is enabled.
     *
     * @return true if debug is enabled
     */
    protected boolean isDebugEnabled()
    {
        return m_logger.isDebugEnabled();
    }

    /**
     * Route Logging to Avalons Logger.
     *
     * @param message the warn message
     */
    protected void warn( final String message )
    {
        m_logger.warn( message );
    }

    protected void scanDependencies( final Extension[] required,
                                     final Extension[] available,
                                     final Set dependencies,
                                     final Set unsatisfied )
    {
        m_manager.scanDependencies( required,
                                    available,
                                    dependencies,
                                    unsatisfied );
    }
}
