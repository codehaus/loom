/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.realityforge.classman.metadata;

/**
 * This class represent a set of files anchored in a base directory. The set of
 * files is determined by the include and excludes that are specified for
 * fileset. If no includes or excludes are specified then all files in base
 * directory are added to fileset.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.3 $ $Date: 2003-12-02 07:28:52 $
 */
public class FileSetMetaData
{
    /** The base directory from which to apply all the includes/excludes. */
    private final String m_baseDirectory;

    /** The set of patterns to include in fileset. */
    private final String[] m_includes;

    /** The set of patterns to exclude from fileset. */
    private final String[] m_excludes;

    public FileSetMetaData( final String baseDirectory,
                            final String[] includes,
                            final String[] excludes )
    {
        if( null == baseDirectory )
        {
            throw new NullPointerException( "baseDirectory" );
        }
        if( null == includes )
        {
            throw new NullPointerException( "includes" );
        }
        if( null == excludes )
        {
            throw new NullPointerException( "excludes" );
        }

        m_baseDirectory = baseDirectory;
        m_includes = includes;
        m_excludes = excludes;
    }

    /**
     * Return the base directory of fileset.
     *
     * @return the base directory of fileset.
     */
    public String getBaseDirectory()
    {
        return m_baseDirectory;
    }

    /**
     * Return the list of includes for fileset.
     *
     * @return the list of includes for fileset.
     */
    public String[] getIncludes()
    {
        return m_includes;
    }

    /**
     * Return the list of excludes for fileset.
     *
     * @return the list of excludes for fileset.
     */
    public String[] getExcludes()
    {
        return m_excludes;
    }
}
