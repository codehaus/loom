/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.realityforge.extension;
/**
 * Enum used in {@link Extension} to indicate the compatability
 * of one extension to another. See {@link Extension} for instances
 * of object.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.2 $ $Date: 2003-08-04 01:52:52 $
 * @see Extension
 */
public final class Compatability
{
    /**
     * A string representaiton of compatability level.
     */
    private final String m_name;
    /**
     * Create a compatability enum with specified name.
     *
     * @param name the name of compatability level
     */
    Compatability( final String name )
    {
        if( null == name )
        {
            throw new NullPointerException( "name" );
        }
        m_name = name;
    }
    /**
     * Return name of compatability level.
     *
     * @return the name of compatability level
     */
    public String getName()
    {
        return m_name;
    }
    /**
     * Return name of compatability level.
     *
     * @return the name of compatability level
     */
    public String toString()
    {
        return m_name;
    }
}
