/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.realityforge.classman.builder;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import org.apache.avalon.excalibur.packagemanager.PackageManager;
import org.realityforge.salt.io.PathMatcher;
import org.realityforge.salt.io.FileUtil;

/**
 * This resolver has all the same properties as the
 * {@link SimpleLoaderResolver} except that it also implements
 * scanning of filesets. It scans filesets based on classes
 * {@link #m_baseDirectory} value.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.2 $ $Date: 2003-06-04 23:47:37 $
 */
public class DefaultLoaderResolver
    extends SimpleLoaderResolver
{
    /**
     * Create a resolver that resolves all files according to specied
     * baseDirectory and using specified {@link PackageManager} to aquire
     * {@link org.apache.avalon.excalibur.extension.Extension} objects.
     *
     * @param baseDirectory the base directory
     * @param manager the {@link PackageManager}
     */
    public DefaultLoaderResolver( final File baseDirectory,
                                  final PackageManager manager )
    {
        super( baseDirectory, manager );
    }

    /**
     * Resolve a fileset.
     *
     * @param baseDirectory the base directory of fileset
     * @param includes the list of ant-style includes
     * @param excludes the list of ant style excludes
     * @return the URLs contained within fileset
     * @throws Exception if unable to resolve fileset
     */
    public URL[] resolveFileSet( final String baseDirectory,
                                 final String[] includes,
                                 final String[] excludes )
        throws Exception
    {
        final File base = getFileFor( "." );
        return resolveFileSet( base, baseDirectory, includes, excludes );
    }

    /**
     * Resolve a fileset in a particular hierarchy.
     *
     * @param base the file hierarchy to use
     * @param baseDirectory the base directory (relative to base)
     * @param includes the ant-style include patterns
     * @param excludes the ant-style exclude patterns
     * @return the resolved URLs for fileset
     */
    protected final URL[] resolveFileSet( final File base,
                                          final String baseDirectory,
                                          final String[] includes,
                                          final String[] excludes )
    {
        //woefully inefficient .. but then again - no need
        //for efficency here
        String newBaseDirectory = FileUtil.normalize( baseDirectory );
        if( ".".equals( newBaseDirectory ) )
        {
            newBaseDirectory = "";
        }
        final String[] newIncludes = prefixPatterns( newBaseDirectory, includes );
        final String[] newExcludes = prefixPatterns( newBaseDirectory, excludes );
        final PathMatcher matcher = new PathMatcher( newIncludes, newExcludes );
        final ArrayList urls = new ArrayList();

        scanDir( base, matcher, "", urls );

        return (URL[])urls.toArray( new URL[ urls.size() ] );
    }

    /**
     * Scan a directory trying to match files with matcher
     * and adding them to list of result urls if they match.
     * Recurse into sub-directorys.
     *
     * @param dir the directory to scan
     * @param matcher the matcher to use
     * @param path the virtual path to the current directory
     * @param urls the list of result URLs
     */
    private void scanDir( final File dir,
                          final PathMatcher matcher,
                          final String path,
                          final ArrayList urls )
    {
        final File[] files = dir.listFiles();
        if( null == files )
        {
            final String message = "Bad dir specified: " + dir;
            throw new IllegalArgumentException( message );
        }

        String prefix = "";
        if( 0 != path.length() )
        {
            prefix = path + "/";
        }

        for( int i = 0; i < files.length; i++ )
        {
            final File file = files[ i ];
            final String newPath = prefix + file.getName();
            if( file.isDirectory() )
            {
                scanDir( file, matcher, newPath, urls );
            }
            else
            {
                if( matcher.match( newPath ) )
                {
                    try
                    {
                        urls.add( file.toURL() );
                    }
                    catch( final MalformedURLException mue )
                    {
                        final String message = "Error converting " +
                            file + " to url. Reason: " + mue;
                        throw new IllegalArgumentException( message );
                    }
                }
            }
        }
    }

    /**
     * Return a new array with specified prefix added to start of
     * every element in supplied array.
     *
     * @param prefix the prefix
     * @param patterns the source array
     * @return a new array with all elements having prefix added
     */
    private String[] prefixPatterns( final String prefix,
                                     final String[] patterns )
    {
        if( 0 == prefix.length() )
        {
            return patterns;
        }

        final String[] newPatterns = new String[ patterns.length ];
        for( int i = 0; i < newPatterns.length; i++ )
        {
            newPatterns[ i ] = prefix + "/" + patterns[ i ];
        }
        return newPatterns;
    }
}
