/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.realityforge.classman.builder;

import java.net.URL;
import org.realityforge.extension.Extension;

/**
 * This is the class that clients provide to implement a specific policy with
 * respect to how ClassLoader hierarchy is constructed.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.5 $ $Date: 2003-12-02 07:28:52 $
 */
public interface LoaderResolver
{
    /**
     * Resolve an Extension to a URL. The URL that locates the "Optional
     * Package" that provides specified Extension.
     *
     * @param extension the extension
     * @return the URL that locates the "Optional Package" that provides
     *         specified Extension.
     * @throws Exception if unable to locate Extension
     */
    URL resolveExtension( Extension extension )
        throws Exception;

    /**
     * Resolve a string into a URL. This allows resolvers to anchor URLs using
     * relative URL or by handling non-standard URL protocols (such as
     * "sar:/SAR-INF/lib/cornerstone.jar").
     *
     * @param location the location to transform into a URL
     * @return the URL
     * @throws Exception if unable to resolve URL
     */
    URL resolveURL( String location )
        throws Exception;

    /**
     * Resolve a fileset to a set of URLs.
     *
     * @param baseDirectory the Base directory of fileset
     * @return the set of URLs making up fileset
     * @throws Exception if unable to resolve fileset
     */
    URL[] resolveFileSet( String baseDirectory,
                          String[] includes,
                          String[] excludes )
        throws Exception;

    /**
     * Create a Join ClassLoader for specified ClassLoaders.
     *
     * @param classLoaders the ClassLoaders to "join"
     * @return the joined ClassLoader
     * @throws Exception if unable to create classloader
     */
    ClassLoader createJoinClassLoader( ClassLoader[] classLoaders )
        throws Exception;

    /**
     * Create a ClassLoader with specified parent and containing specified
     * URLs.
     *
     * @param parent the parent classloader
     * @param urls the URLs that the ClassLoader should contain
     * @return the newly created ClassLoader
     * @throws Exception if unable to create classloader
     */
    ClassLoader createClassLoader( ClassLoader parent, URL[] urls )
        throws Exception;
}