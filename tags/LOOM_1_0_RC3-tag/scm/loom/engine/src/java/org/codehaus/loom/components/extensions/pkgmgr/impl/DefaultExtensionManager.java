/* ====================================================================
 * Loom Software License, version 1.1
 *
 * Copyright (c) 2003, Loom Group. All rights reserved.
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
 * 3. Neither the name of the Loom Group nor the name "Loom" nor
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
 * Loom includes code from the Apache Software Foundation
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
package org.codehaus.loom.components.extensions.pkgmgr.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.codehaus.loom.components.extensions.pkgmgr.ExtensionManager;
import org.codehaus.loom.components.extensions.pkgmgr.OptionalPackage;
import org.codehaus.loom.extension.Extension;

/**
 * <p>Interface used to contain "Optional Packages" (formerly known as "Standard
 * Extensions"). It is assumed that each "Optional Package" is represented by a
 * single file on the file system. This Repository searches a path to find the
 * Optional Packages.</p>
 *
 * @author Peter Donald
 * @version $Revision: 1.3 $ $Date: 2004-08-17 23:14:32 $
 * @see OptionalPackage
 * @see ExtensionManager
 */
public class DefaultExtensionManager
    implements ExtensionManager
{
    private static final boolean DEBUG = false;

    /** separator used to separate path elements in a string. */
    private static final String SEPARATOR = "|";

    /** Map between files and {@link OptionalPackage} objects. */
    private final Map m_packages = new HashMap();

    /** The set of directories in which to look for Optional Packages */
    private File[] m_path;

    /**
     * Flag set when it is necessary to scan paths to build "Optional Package"
     * list
     */
    private boolean m_needToScan;

    /**
     * Construct a package repository with no path specified.
     */
    public DefaultExtensionManager()
    {
        this( new File[ 0 ] );
    }

    /**
     * Construct a package repository with path.
     *
     * @param path The set of directories in which to look for Optional
     * Packages
     */
    public DefaultExtensionManager( final File[] path )
    {
        setPath( path );
    }

    /**
     * Return an array of path elements where each element in array represents a
     * directory in which the ExtensionManager will look for Extensions.
     *
     * @return the list of paths to search in
     */
    public File[] getPaths()
    {
        return m_path;
    }

    /**
     * Return all the {@link OptionalPackage}s that satisfy specified {@link
     * Extension}. It is expected that this {@link Extension} object will be one
     * retrieved via getLocalExtension() method. If the specified {@link
     * Extension} is not local then <code>null</code> is returned.
     *
     * @param extension the extension to search for
     * @return an array of optional packages that satisfy the extension (and the
     *         extensions dependencies)
     */
    public synchronized OptionalPackage[] getOptionalPackages(
        final Extension extension )
    {
        if( m_needToScan )
        {
            scanPath();
        }

        final ArrayList results = new ArrayList();
        final ArrayList candidates = (ArrayList)m_packages.get(
            extension.getExtensionName() );
        if( null != candidates )
        {
            final int size = candidates.size();
            for( int i = 0; i < size; i++ )
            {
                final OptionalPackage optionalPackage = (OptionalPackage)candidates.get(
                    i );
                final Extension[] extensions = optionalPackage.getAvailableExtensions();

                for( int j = 0; j < extensions.length; j++ )
                {
                    if( extensions[ j ].isCompatibleWith( extension ) )
                    {
                        results.add( optionalPackage );
                    }
                }
            }
        }

        final OptionalPackageComparator comparator =
            new OptionalPackageComparator( extension.getExtensionName() );
        Collections.sort( results, comparator );
        return (OptionalPackage[])results.toArray( new OptionalPackage[ 0 ] );
    }

    /**
     * Return all the OptionalPackages stored in ExtensionManager.
     *
     * @return all the OptionalPackages stored in ExtensionManager.
     */
    protected synchronized OptionalPackage[] getAllOptionalPackages()
    {
        //This is woefully inefficient - should rewrite it somehow
        final ArrayList packages = new ArrayList();
        final Iterator iterator = m_packages.values().iterator();
        while( iterator.hasNext() )
        {
            final ArrayList list = (ArrayList)iterator.next();
            final int size = list.size();
            for( int i = 0; i < size; i++ )
            {
                final OptionalPackage optionalPackage = (OptionalPackage)list.get(
                    i );
                if( !packages.contains( optionalPackage ) )
                {
                    packages.add( optionalPackage );
                }
            }
        }

        return (OptionalPackage[])packages.toArray(
            new OptionalPackage[ packages.size() ] );
    }

    /**
     * Add path elements to repository search path
     *
     * @param pathElements the path elements to add to repository search path
     */
    protected void addPathElements( final String[] pathElements )
    {
        final File[] path = toFiles( pathElements );
        addPathElements( path );
    }

    /**
     * Add path elements to repository search path
     *
     * @param path the path elements to add to repository search path
     */
    protected synchronized void addPathElements( final File[] path )
    {
        validatePath( path );
        final File[] files = resolvePath( path );
        m_path = mergePaths( files );
        m_needToScan = true;
    }

    /**
     * Add path elements to repository search path. Note that each path element
     * is separated by a '|' character.
     *
     * @param pathString the path elements to add to repository search path
     */
    protected void addPathElements( final String pathString )
    {
        final String[] pathElements = split( pathString, SEPARATOR );
        addPathElements( pathElements );
    }

    /**
     * Set the path for the Repository. Note thart each path element is
     * separated by a '|' character.
     *
     * @param pathString the list of directories in which to search
     */
    protected synchronized void setPath( final String pathString )
    {
        final String[] pathElements = split( pathString, SEPARATOR );
        setPath( pathElements );
    }

    /**
     * Set the path for the Repository.
     *
     * @param pathElements the list of directories in which to search
     */
    protected synchronized void setPath( final String[] pathElements )
    {
        final File[] path = toFiles( pathElements );
        setPath( path );
    }

    /**
     * Set the path for the Repository.
     *
     * @param path the list of directories in which to search
     */
    protected synchronized void setPath( final File[] path )
    {
        validatePath( path );
        m_path = resolvePath( path );
        m_needToScan = true;
    }

    /**
     * Scan the path for this repository and reload all the "Optional Packages"
     * found in the path. All of the old Extensions/Optional Packages will be
     * removed.
     */
    protected final synchronized void scanPath()
    {
        clearCache();

        for( int i = 0; i < m_path.length; i++ )
        {
            scanDirectory( m_path[ i ] );
        }
    }

    /**
     * Utility method to scan a directory for all jar fi;les in directory and
     * add them as OptionalPackages.
     *
     * @param directory the directory to scan
     */
    private synchronized void scanDirectory( final File directory )
    {
        final File[] files = directory.listFiles();
        for( int i = 0; i < files.length; i++ )
        {
            final File file = files[ i ];
            final String name = file.getName();

            if( !name.endsWith( ".jar" ) )
            {
                final String message =
                    "Skipping " + file + " as it does not end with '.jar'";
                debug( message );
                continue;
            }

            if( !file.isFile() )
            {
                final String message =
                    "Skipping " + file + " as it is not a file.";
                debug( message );
                continue;
            }

            if( !file.canRead() )
            {
                final String message =
                    "Skipping " + file + " as it is not readable.";
                debug( message );
                continue;
            }

            try
            {
                final OptionalPackage optionalPackage = getOptionalPackage(
                    file );
                cacheOptionalPackage( optionalPackage );
            }
            catch( final IOException ioe )
            {
                final String message =
                    "Skipping " +
                    file +
                    " as it could not be loaded " +
                    "due to " + ioe;
                debug( message );
            }
        }
    }

    /**
     * Clear internal cache of optional packages.
     */
    protected final synchronized void clearCache()
    {
        m_packages.clear();
        m_needToScan = true;
    }

    /**
     * Add OptionalPackage to internal cache of Optional Packages. Note that
     * this method is only protected so that unit tests can sub-class and add
     * entries to PackageRepository by calling this method.
     *
     * @param optionalPackage the OptionalPackage to be added to repository
     */
    protected final synchronized void cacheOptionalPackage(
        final OptionalPackage optionalPackage )
    {
        m_needToScan = false;

        // added to avoid out of bounds exception
        if( optionalPackage.getAvailableExtensions().length == 0 )
        {
            return;
        }

        final Extension extension = optionalPackage.getAvailableExtensions()[ 0 ];
        ArrayList candidates = (ArrayList)m_packages.get(
            extension.getExtensionName() );
        if( null == candidates )
        {
            candidates = new ArrayList();
            m_packages.put( extension.getExtensionName(), candidates );
        }

        candidates.add( optionalPackage );
    }

    /**
     * Construct an OptionalPackage out of the specified jar archive.
     *
     * @param archive the file object for Jar archive
     * @return the OptionalPackage constructed
     * @throws IOException if an error occurs
     */
    private OptionalPackage getOptionalPackage( final File archive )
        throws IOException
    {
        final File file = archive.getCanonicalFile();
        final JarFile jarFile = new JarFile( file );
        try
        {
            final Manifest manifest = jarFile.getManifest();
            if( null == manifest )
            {
                return null;
            }
            final Extension[] available = Extension.getAvailable( manifest );
            final Extension[] required = Extension.getRequired( manifest );

            return new OptionalPackage( file, available, required );
        }
        finally
        {
            jarFile.close();
        }
    }

    /**
     * Output a debug message for repository.
     *
     * @param message the debug message
     */
    protected void debug( final String message )
    {
        if( DEBUG )
        {
            System.out.println( message );
        }
    }

    /**
     * Get Canonical or failing that the absolute file for every specified
     * file.
     *
     * @param path the files that make up path
     * @return the resolved path
     */
    private File[] resolvePath( final File[] path )
    {
        final File[] resultPath = new File[ path.length ];
        for( int i = 0; i < path.length; i++ )
        {
            resultPath[ i ] = resolveFile( path[ i ] );
        }
        return resultPath;
    }

    /**
     * Get Canonical or failing that the absolute file for specified file.
     *
     * @param file the file
     * @return the resolved file
     */
    private File resolveFile( final File file )
    {
        try
        {
            return file.getCanonicalFile();
        }
        catch( IOException e )
        {
            return file.getAbsoluteFile();
        }
    }

    /**
     * Validate each element in path to make sure they are valid.
     *
     * @param path the path
     */
    private void validatePath( final File[] path )
    {
        if( null == path )
        {
            throw new NullPointerException( "path" );
        }

        for( int i = 0; i < path.length; i++ )
        {
            validatePathElement( path[ i ] );
        }
    }

    /**
     * Make sure specified path element is valid. The elements should exist and
     * should be a directory.
     *
     * @param file the path element
     */
    private void validatePathElement( final File file )
    {
        if( !file.exists() || !file.isDirectory() )
        {
            final String message = "path element " +
                file +
                " must exist and must be a directory";
            throw new IllegalArgumentException( message );
        }
    }

    /**
     * Merge the specified file list with existing path.
     *
     * @param files the files to merge
     * @return the merged path
     */
    private File[] mergePaths( final File[] files )
    {
        final File[] resultPath =
            new File[ m_path.length + files.length ];
        System.arraycopy( m_path, 0, resultPath, 0, m_path.length );
        System.arraycopy( files, m_path.length, resultPath, 0, files.length );
        return resultPath;
    }

    /**
     * Convert set of string elements into file objects
     *
     * @param pathElements the string path elements
     * @return the file array representing each element
     */
    private File[] toFiles( final String[] pathElements )
    {
        final File[] path = new File[ pathElements.length ];
        for( int i = 0; i < path.length; i++ )
        {
            path[ i ] = new File( pathElements[ i ] );
        }
        return path;
    }

    /**
     * Splits the string on every token into an array of strings.
     *
     * @param string the string
     * @param onToken the token
     * @return the resultant array
     */
    private static String[] split( final String string, final String onToken )
    {
        final StringTokenizer tokenizer = new StringTokenizer( string,
                                                               onToken );
        final String[] result = new String[ tokenizer.countTokens() ];

        for( int i = 0; i < result.length; i++ )
        {
            result[ i ] = tokenizer.nextToken();
        }

        return result;
    }
}
