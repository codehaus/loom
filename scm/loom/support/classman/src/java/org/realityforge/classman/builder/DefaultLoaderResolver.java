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
import org.realityforge.classman.util.PathMatcher;

/**
 * This resolver has all the same properties as the
 * {@link SimpleLoaderResolver} except that it also implements
 * scanning of filesets. It scans filesets based on classes
 * {@link #m_baseDirectory} value.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003-04-04 13:52:10 $
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
        final String newBaseDirectory = normalize( baseDirectory );
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

    /**
     * Normalize a path. That means:
     * <ul>
     *   <li>changes to unix style if under windows</li>
     *   <li>eliminates "/../" and "/./"</li>
     *   <li>if path is absolute (starts with '/') and there are
     *   too many occurences of "../" (would then have some kind
     *   of 'negative' path) returns null.</li>
     *   <li>If path is relative, the exceeding ../ are kept at
     *   the begining of the path.</li>
     * </ul>
     * <br><br>
     *
     * <b>Note:</b> note that this method has been tested with unix and windows only.
     *
     * <p>Eg:</p>
     * <pre>
     * /foo//               -->     /foo/
     * /foo/./              -->     /foo/
     * /foo/../bar          -->     /bar
     * /foo/../bar/         -->     /bar/
     * /foo/../bar/../baz   -->     /baz
     * //foo//./bar         -->     /foo/bar
     * /../                 -->     null
     * </pre>
     *
     * @param path the path to be normalized.
     * @return the normalized path or null.
     * @throws NullPointerException if path is null.
     */
    private static final String normalize( String path )
    {
        if( ".".equals( path ) || "./".equals( path ) )
        {
            return "";
        }
        else if( path.length() < 2 )
        {
            return path;
        }

        StringBuffer buff = new StringBuffer( path );

        int length = path.length();

        // this whole prefix thing is for windows compatibility only.
        String prefix = null;

        if( length > 2 && buff.charAt( 1 ) == ':' )
        {
            prefix = path.substring( 0, 2 );
            buff.delete( 0, 2 );
            path = path.substring( 2 );
            length -= 2;
        }

        boolean startsWithSlash = length > 0 && ( buff.charAt( 0 ) == '/' || buff.charAt( 0 ) == '\\' );

        boolean expStart = true;
        int ptCount = 0;
        int lastSlash = length + 1;
        int upLevel = 0;

        for( int i = length - 1; i >= 0; i-- )
            switch( path.charAt( i ) )
            {
                case '\\':
                    buff.setCharAt( i, '/' );
                case '/':
                    if( lastSlash == i + 1 )
                    {
                        buff.deleteCharAt( i );
                    }

                    switch( ptCount )
                    {
                        case 1:
                            buff.delete( i, lastSlash );
                            break;

                        case 2:
                            upLevel++;
                            break;

                        default:
                            if( upLevel > 0 && lastSlash != i + 1 )
                            {
                                buff.delete( i, lastSlash + 3 );
                                upLevel--;
                            }
                            break;
                    }

                    ptCount = 0;
                    expStart = true;
                    lastSlash = i;
                    break;

                case '.':
                    if( expStart )
                    {
                        ptCount++;
                    }
                    break;

                default:
                    ptCount = 0;
                    expStart = false;
                    break;
            }

        switch( ptCount )
        {
            case 1:
                buff.delete( 0, lastSlash );
                break;

            case 2:
                break;

            default:
                if( upLevel > 0 )
                {
                    if( startsWithSlash )
                    {
                        return null;
                    }
                    else
                    {
                        upLevel = 1;
                    }
                }

                while( upLevel > 0 )
                {
                    buff.delete( 0, lastSlash + 3 );
                    upLevel--;
                }
                break;
        }

        length = buff.length();
        boolean isLengthNull = length == 0;
        char firstChar = isLengthNull?(char)0:buff.charAt( 0 );

        if( !startsWithSlash && !isLengthNull && firstChar == '/' )
        {
            buff.deleteCharAt( 0 );
        }
        else if( startsWithSlash &&
            ( isLengthNull || ( !isLengthNull && firstChar != '/' ) ) )
        {
            buff.insert( 0, '/' );
        }

        if( prefix != null )
        {
            buff.insert( 0, prefix );
        }

        return buff.toString();
    }
}
