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
package org.jcontainer.loom.components.extensions.pkgmgr;

import java.io.File;
import org.realityforge.extension.Extension;

/**
 * This contains the required meta-data for an "Optional Package"
 * (formerly known as "Standard Extension") as described in the manifest
 * of a JAR file.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 */
public final class OptionalPackage
{
    private final File m_file;
    private final Extension[] m_available;
    private final Extension[] m_required;

    /**
     * Convert a list of OptionalPackages into a list of Files.
     *
     * @param packages the list of packages
     * @return the list of files
     */
    public static final File[] toFiles( final OptionalPackage[] packages )
    {
        final File[] results = new File[ packages.length ];

        for( int i = 0; i < packages.length; i++ )
        {
            results[ i ] = packages[ i ].getFile();
        }

        return results;
    }

    /**
     * Constructor for OptionalPackage.
     * No parameter is allowed to be null.
     *
     * @param file absolute location of file
     * @param available the list of Extensions Optional Package provides
     * @param required the list of Extensions Optional Package requires
     */
    public OptionalPackage( final File file,
                            final Extension[] available,
                            final Extension[] required )
    {
        if( null == file )
        {
            throw new NullPointerException( "file" );
        }

        if( null == available )
        {
            throw new NullPointerException( "available" );
        }

        if( null == required )
        {
            throw new NullPointerException( "required" );
        }

        m_file = file;
        m_available = available;
        m_required = required;
    }

    /**
     * Return <code>File</code> object in which OptionalPackage
     * is contained.
     *
     * @return the file object for OptionalPackage
     */
    public File getFile()
    {
        return m_file;
    }

    /**
     * Return <code>Extension</code>s which OptionalPackage
     * requires to operate.
     *
     * @return the extensions required by OptionalPackage
     */
    public Extension[] getRequiredExtensions()
    {
        return m_required;
    }

    /**
     * Return <code>Extension</code>s which OptionalPackage
     * makes available.
     *
     * @return the extensions made available by OptionalPackage
     */
    public Extension[] getAvailableExtensions()
    {
        return m_available;
    }

    /**
     * Return <code>true</code> if any of the available <code>Extension</code>s
     * are compatible with specified extension. Otherwise return <code>false</code>.
     *
     * @param extension the extension
     * @return true if compatible, false otherwise
     */
    public boolean isCompatible( final Extension extension )
    {
        for( int i = 0; i < m_available.length; i++ )
        {
            if( m_available[ i ].isCompatibleWith( extension ) )
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Return a String representation of this object.
     *
     * @return the string representation of object
     */
    public String toString()
    {
        final StringBuffer sb = new StringBuffer( "OptionalPackage[" );
        sb.append( m_file );

        sb.append( ", Available[" );
        for( int i = 0; i < m_available.length; i++ )
        {
            if( 0 != i )
            {
                sb.append( " " );
            }
            sb.append( m_available[ i ].getExtensionName() );
        }

        sb.append( "], Required[" );
        for( int i = 0; i < m_required.length; i++ )
        {
            if( 0 != i )
            {
                sb.append( " " );
            }
            sb.append( m_required[ i ].getExtensionName() );
        }

        sb.append( "] ]" );

        return sb.toString();
    }
}
