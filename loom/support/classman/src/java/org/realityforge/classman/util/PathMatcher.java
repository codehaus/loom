/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.realityforge.classman.util;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;

/**
 * Utility class for scanning a filesystem and matching a
 * particular set of include and exclude patterns ala ant.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003-04-04 13:52:13 $
 */
public class PathMatcher
{
    /**
     * The set of patterns that indicate paths to include.
     */
    private final Pattern[] m_includes;

    /**
     * The set of patterns that indicate paths to exclude.
     * Takes precedence over includes.
     */
    private final Pattern[] m_excludes;

    /**
     * The object that is capable of performing the matching.
     */
    private final Perl5Matcher m_matcher = new Perl5Matcher();

    /**
     * Construct a matcher from ant style includes/excludes.
     *
     * @param includes the set of includes
     * @param excludes the set of excludes
     */
    public PathMatcher( final String[] includes,
                        final String[] excludes )
    {
        if( null == includes )
        {
            throw new NullPointerException( "includes" );
        }
        if( null == excludes )
        {
            throw new NullPointerException( "excludes" );
        }

        m_includes = toPatterns( includes );
        m_excludes = toPatterns( excludes );
    }

    /**
     * Test if a virtual path matches any of the ant style
     * patterns specified in this objects constructor.
     *
     * @param vPath the virtual path string
     * @return true if matches, false otherwise
     */
    public boolean match( final String vPath )
    {
        if( isExcluded( vPath ) )
        {
            return false;
        }
        else if( isIncluded( vPath ) )
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Determine if specified path is excludes by patterns.
     *
     * @param vPath the virtual path
     * @return true if excluded
     */
    private boolean isExcluded( final String vPath )
    {
        return testMatch( m_excludes, vPath );
    }

    /**
     * Determine if specified path is includes by patterns.
     *
     * @param vPath the virtual path
     * @return true if included
     */
    private boolean isIncluded( final String vPath )
    {
        return testMatch( m_includes, vPath );
    }

    /**
     * Determine whether the virtual path matches
     * specified patterns.
     *
     * @param patterns the patterns
     * @param vPath the virtual path
     * @return true if matches, false otherwise
     */
    private boolean testMatch( final Pattern[] patterns,
                               final String vPath )
    {
        for( int i = 0; i < patterns.length; i++ )
        {
            if( m_matcher.matches( vPath, patterns[ i ] ) )
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Convert a set of ant patterns into ORO Pattern objects.
     *
     * @param strs the ant style patterns
     * @return the ORO Pattern objects
     */
    private Pattern[] toPatterns( final String[] strs )
    {
        final Perl5Compiler compiler = new Perl5Compiler();
        final Pattern[] patterns = new Pattern[ strs.length ];
        for( int i = 0; i < patterns.length; i++ )
        {
            final String perlPatternStr = toPerlPatternStr( strs[ i ] );
            try
            {
                patterns[ i ] = compiler.compile( perlPatternStr );
            }
            catch( final MalformedPatternException mpe )
            {
                throw new IllegalArgumentException( mpe.toString() );
            }
        }
        return patterns;
    }

    /**
     * Convert an ant style fileset pattern to a Perl pattern.
     *
     * @param str the ant style pattern
     * @return the perl pattern
     */
    private String toPerlPatternStr( String str )
    {
        final StringBuffer sb = new StringBuffer();
        final int size = str.length();

        for( int i = 0; i < size; i++ )
        {
            final char ch = str.charAt( i );
            if( '.' == ch || '/' == ch || '\\' == ch )
            {
                sb.append( '\\' );
                sb.append( ch );
            }
            else if( '*' == ch )
            {
                if( ( i + 2 ) < size &&
                    '*' == str.charAt( i + 1 ) &&
                    '/' == str.charAt( i + 2 )  )
                {
                    sb.append( "([^/]*/)*" );
                    i += 2;
                }
                else
                {
                    sb.append( "[^/]*" );
                }
            }
            else
            {
                sb.append( ch );
            }
        }

        return sb.toString();
    }
}
