/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.realityforge.extension;

import java.util.StringTokenizer;

/**
 * Utility class to contain version numbers in "Dewey Decimal"
 * syntax.  Numbers in the "Dewey Decimal" syntax consist of positive
 * decimal integers separated by periods ".".  For example, "2.0" or
 * "1.2.3.4.5.6.7".  This allows an extensible number to be used to
 * represent major, minor, micro, etc versions.  The version number
 * must begin with a number.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003-07-28 13:30:46 $
 */
public final class DeweyDecimal
{
    ///Array of components that make up DeweyDecimal
    private int[] m_components;

    /**
     * Construct a DeweyDecimal from an array of integer components.
     *
     * @param components an array of integer components.
     */
    public DeweyDecimal( final int[] components )
    {
        m_components = new int[ components.length ];

        for( int i = 0; i < m_components.length; i++ )
        {
            m_components[ i ] = components[ i ];
        }
    }

    /**
     * Construct a DeweyDecimal from string in DeweyDecimal format.
     *
     * @param string the string in dewey decimal format
     * @throws java.lang.NumberFormatException if string is malformed
     */
    public DeweyDecimal( final String string )
        throws NumberFormatException
    {
        final StringTokenizer tokenizer = new StringTokenizer( string, ".", true );
        final int size = tokenizer.countTokens();

        m_components = new int[ ( size + 1 ) / 2 ];

        for( int i = 0; i < m_components.length; i++ )
        {
            final String component = tokenizer.nextToken();
            if( component.equals( "" ) )
            {
                throw new NumberFormatException( "Empty component in string" );
            }

            m_components[ i ] = Integer.parseInt( component );

            //Strip '.' token
            if( tokenizer.hasMoreTokens() )
            {
                tokenizer.nextToken();

                //If it ended in a dot, throw an exception
                if( !tokenizer.hasMoreTokens() )
                {
                    final String message = "DeweyDecimal ended in a '.'";
                    throw new NumberFormatException( message );
                }
            }
        }
    }

    /**
     * Return number of components in <code>DeweyDecimal</code>.
     *
     * @return the number of components in dewey decimal
     */
    public int getSize()
    {
        return m_components.length;
    }

    /**
     * Return the component at specified index.
     *
     * @param index the index of components
     * @return the value of component at index
     */
    public int get( final int index )
    {
        return m_components[ index ];
    }

    /**
     * Return <code>true</code> if this <code>DeweyDecimal</code> is
     * equal to the other <code>DeweyDecimal</code>.
     *
     * @param other the other DeweyDecimal
     * @return true if equal to other DeweyDecimal, false otherwise
     */
    public boolean isEqual( final DeweyDecimal other )
    {
        final int max = Math.max( other.m_components.length, m_components.length );

        for( int i = 0; i < max; i++ )
        {
            final int component1 = ( i < m_components.length ) ? m_components[ i ] : 0;
            final int component2 = ( i < other.m_components.length ) ? other.m_components[ i ] : 0;

            if( component2 != component1 )
            {
                return false;
            }
        }

        return true; // Exact match
    }

    /**
     * Return <code>true</code> if this <code>DeweyDecimal</code> is
     * less than the other <code>DeweyDecimal</code>.
     *
     * @param other the other DeweyDecimal
     * @return true if less than other DeweyDecimal, false otherwise
     */
    public boolean isLessThan( final DeweyDecimal other )
    {
        return !isGreaterThanOrEqual( other );
    }

    /**
     * Return <code>true</code> if this <code>DeweyDecimal</code> is
     * less than or equal to the other <code>DeweyDecimal</code>.
     *
     * @param other the other DeweyDecimal
     * @return true if less than or equal to other DeweyDecimal, false otherwise
     */
    public boolean isLessThanOrEqual( final DeweyDecimal other )
    {
        return !isGreaterThan( other );
    }

    /**
     * Return <code>true</code> if this <code>DeweyDecimal</code> is
     * greater than the other <code>DeweyDecimal</code>.
     *
     * @param other the other DeweyDecimal
     * @return true if greater than other DeweyDecimal, false otherwise
     */
    public boolean isGreaterThan( final DeweyDecimal other )
    {
        final int max = Math.max( other.m_components.length, m_components.length );

        for( int i = 0; i < max; i++ )
        {
            final int component1 = ( i < m_components.length ) ? m_components[ i ] : 0;
            final int component2 = ( i < other.m_components.length ) ? other.m_components[ i ] : 0;

            if( component2 > component1 )
            {
                return false;
            }
            if( component2 < component1 )
            {
                return true;
            }
        }

        return false; // Exact match
    }

    /**
     * Return <code>true</code> if this <code>DeweyDecimal</code> is
     * greater than or equal to the other <code>DeweyDecimal</code>.
     *
     * @param other the other DeweyDecimal
     * @return true if greater than or equal to other DeweyDecimal, false otherwise
     */
    public boolean isGreaterThanOrEqual( final DeweyDecimal other )
    {
        final int max = Math.max( other.m_components.length, m_components.length );

        for( int i = 0; i < max; i++ )
        {
            final int component1 = ( i < m_components.length ) ? m_components[ i ] : 0;
            final int component2 = ( i < other.m_components.length ) ? other.m_components[ i ] : 0;

            if( component2 > component1 )
            {
                return false;
            }
            if( component2 < component1 )
            {
                return true;
            }
        }

        return true; // Exact match
    }

    /**
     * Return string representation of <code>DeweyDecimal</code>.
     *
     * @return the string representation of DeweyDecimal.
     */
    public String toString()
    {
        final StringBuffer sb = new StringBuffer();

        for( int i = 0; i < m_components.length; i++ )
        {
            if( i != 0 )
            {
                sb.append( '.' );
            }
            sb.append( m_components[ i ] );
        }

        return sb.toString();
    }
}
