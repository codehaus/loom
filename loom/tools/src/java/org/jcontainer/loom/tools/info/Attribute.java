/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.tools.info;

import java.io.Serializable;
import java.util.Properties;

/**
 * Attributes are the mechanism via which the Component model
 * is extended. Each Attribute is made up of
 * <ul>
 *   <li>name: the name of the Attribute</li>
 *   <li>parameters: a set of key-value pairs specifying parameters for Attribute</li>
 * </ul>
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003-06-26 08:37:43 $
 */
public final class Attribute
    implements Serializable
{
    /**
     * An empty array of attributes.
     */
    public static final Attribute[] EMPTY_SET = new Attribute[ 0 ];

    /**
     * To save memory always return same emtpy array of names
     */
    private static final String[] EMPTY_NAME_SET = new String[ 0 ];

    /**
     * The name of the Attribute.
     */
    private final String m_name;

    /**
     * The arbitrary set of parameters associated with the Attribute.
     */
    private final Properties m_parameters;

    /**
     * Create a Attribute with specified name and parameters.
     *
     * @param name the Attribute name
     * @param parameters the Attribute parameters
     */
    public Attribute( final String name,
                      final Properties parameters )
    {
        if( null == name )
        {
            throw new NullPointerException( "name" );
        }

        m_name = name;
        m_parameters = parameters;
    }

    /**
     * Return the name of the Attribute.
     *
     * @return the name of the Attribute.
     */
    public String getName()
    {
        return m_name;
    }

    /**
     * Return the parameter for specified key.
     *
     * @return the parameter for specified key.
     */
    public String getParameter( final String key )
    {
        if( null == m_parameters )
        {
            return null;
        }
        else
        {
            return m_parameters.getProperty( key );
        }
    }

    /**
     * Return the parameter for specified key, or defaultValue if unspecified.
     *
     * @return the parameter for specified key, or defaultValue if unspecified.
     */
    public String getParameter( final String key,
                                final String defaultValue )
    {
        if( null == m_parameters )
        {
            return defaultValue;
        }
        else
        {
            return m_parameters.getProperty( key, defaultValue );
        }
    }

    /**
     * Returns an array of parameter names available under this Attribute.
     *
     * @return an array of parameter names available under this Attribute.
     */
    public String[] getParameterNames()
    {
        if( null == m_parameters )
        {
            return EMPTY_NAME_SET;
        }
        else
        {
            return (String[])m_parameters.keySet().toArray( EMPTY_NAME_SET );
        }
    }

    public String toString()
    {
        if( null != m_parameters )
        {
            return getName() + m_parameters;
        }
        else
        {
            return getName();
        }
    }
}
