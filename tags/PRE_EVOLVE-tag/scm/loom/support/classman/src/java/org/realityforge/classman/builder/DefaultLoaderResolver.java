/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.realityforge.classman.builder;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.apache.avalon.excalibur.packagemanager.PackageManager;
import org.realityforge.salt.io.FileUtil;
import org.realityforge.salt.io.PathMatcher;

/**
 * This resolver has all the same properties as the
 * {@link SimpleLoaderResolver} except that it also implements
 * scanning of filesets. It scans filesets based on classes
 * {@link #m_baseDirectory} value.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.6 $ $Date: 2003-06-27 03:45:05 $
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
        final String newBaseDirectory = FileUtil.normalize( baseDirectory );
        final String[] newIncludes = prefixPatterns( newBaseDirectory, includes );
        final String[] newExcludes = prefixPatterns( newBaseDirectory, excludes );
        final PathMatcher matcher = new PathMatcher( newIncludes, newExcludes );
        final File[] files = FileUtil.resolveFileSet( base, matcher );
        try
        {
            return FileUtil.toURLs( files );
        }
        catch( IOException ioe )
        {
            throw new IllegalArgumentException( ioe.getMessage() );
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
        if( 0 == prefix.length() || ".".equals( prefix ) )
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
