/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.tools.info;

import java.io.Serializable;
import java.util.Arrays;
import org.realityforge.metaclass.model.Attribute;

/**
 * This is the Abstract class for all feature descriptors.
 * Every descriptor has the capability of adding Attributes
 * of some kind. These Attributes can then be interpreted by
 * the container. The meaning of the specific Attributes will
 * be defined by future specification documents.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.3 $ $Date: 2003-10-05 01:06:31 $
 */
public abstract class FeatureDescriptor
    implements Serializable
{
    /**
     * The arbitrary set of Attributes associated with Component.
     */
    private final Attribute[] m_attributes;

    /**
     * Create a FeatureDescriptor with specific set of attributes.
     *
     * @param attributes the attributes
     */
    protected FeatureDescriptor( final Attribute[] attributes )
    {
        if( null == attributes )
        {
            throw new NullPointerException( "attributes" );
        }

        m_attributes = attributes;
    }

    /**
     * Return the attributes associated with descriptor.
     *
     * @return the attributes associated with descriptor.
     */
    public Attribute[] getAttributes()
    {
        return m_attributes;
    }

    /**
     * Return the attribute with specified name.
     *
     * @return the attribute with specified name.
     */
    public Attribute getAttribute( final String name )
    {
        for( int i = 0; i < m_attributes.length; i++ )
        {
            final Attribute attribute = m_attributes[ i ];
            if( attribute.getName().equals( name ) )
            {
                return attribute;
            }
        }
        return null;
    }

    /**
     * Helper method to convert attributes into a
     * string representation.
     *
     * @return attributes converted into a string representation
     */
    protected final String attributesToString()
    {
        if( 0 == m_attributes.length )
        {
            return "";
        }
        else
        {
            return String.valueOf( Arrays.asList( m_attributes ) );
        }
    }
}
