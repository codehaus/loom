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
package org.jcontainer.loom.components.classloader;

import java.io.File;
import java.net.URL;
import java.security.Policy;
import java.util.Arrays;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.jcontainer.loom.components.extensions.pkgmgr.PackageManager;
import org.jcontainer.loom.components.util.ResourceUtil;
import org.realityforge.salt.i18n.Resources;
import org.realityforge.salt.i18n.ResourceManager;

/**
 * a LoaderResolver that knows about Phoenixs environment,
 * and the way it is split across multiple directories.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.2 $ $Date: 2003-07-13 00:15:36 $
 */
class SarLoaderResolver
    extends DefaultLoaderResolver
    implements LogEnabled
{
    private final static Resources REZ =
        ResourceManager.getPackageResources( SarLoaderResolver.class );

    /**
     * Logger to use when reporting information
     */
    private Logger m_logger;

    /**
     * The policy object to use when creating ClassLoaders
     */
    private Policy m_policy;

    /**
     * Base work directory for application.
     */
    private File m_workDirectory;

    /**
     * Create a resolver for a jar.
     * The resolver merges both the work and base directory
     * hierarchies.
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
        super( baseDirectory, manager );
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
     * Resolve a fileset. Make sure it is resolved against
     * both the work and the base directories of application.
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
            resolveFileSet( getBaseDirectory(), baseDirectory, includes, excludes );
        final URL[] workURLs =
            resolveFileSet( m_workDirectory, baseDirectory, includes, excludes );
        final URL[] urls = new URL[ baseURLs.length + workURLs.length ];
        System.arraycopy( baseURLs, 0, urls, 0, baseURLs.length );
        System.arraycopy( workURLs, 0, urls, baseURLs.length, workURLs.length );
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
}
