/*
 * Copyright (C) The Loom Group. All rights reserved.
 *
 * This software is published under the terms of the Loom
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.codehaus.loom.components.util.info;

/**
 * A descriptor describing the schema to validate the components {@link
 * org.apache.avalon.framework.parameters.Parameters} or {@link
 * org.apache.avalon.framework.configuration.Configuration} object. If a
 * component is neither {@link org.apache.avalon.framework.parameters.Parameterizable}
 * nor {@link org.apache.avalon.framework.configuration.Configurable} then this
 * descriptor will hold empty values for location, category and type.
 *
 * <p>Associated with each Schema is a set of arbitrary Attributes that can be
 * used to store extra information about Schema requirements.</p>
 *
 * @author Peter Donald
 * @version $Revision: 1.2 $ $Date: 2004-05-01 12:48:34 $
 */
public class SchemaDescriptor
{
    /** The location of schema relative to component. */
    private final String m_location;

    /** The type of the schema. */
    private final String m_type;

    /**
     * Create a Schema descriptor.
     *
     * @param location the location of schema relative to component
     * @param type the type of the schema
     */
    public SchemaDescriptor( final String location,
                             final String type )
    {
        if( null == location )
        {
            throw new NullPointerException( "location" );
        }
        if( null == type )
        {
            throw new NullPointerException( "type" );
        }

        m_location = location;
        m_type = type;
    }

    /**
     * Return the location of the schema relative to the component.
     *
     * @return the location of the schema relative to the component.
     */
    public String getLocation()
    {
        return m_location;
    }

    /**
     * Return the type of the schema. Usually represented as a URI referring to
     * schema namespace declaration.
     *
     * @return the type of the schema
     */
    public String getType()
    {
        return m_type;
    }
}
