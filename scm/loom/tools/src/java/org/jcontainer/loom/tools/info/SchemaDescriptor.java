/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.tools.info;

/**
 * A descriptor describing the schema to validate the components
 * {@link org.apache.avalon.framework.parameters.Parameters} or
 * {@link org.apache.avalon.framework.configuration.Configuration}
 * object. If a component is neither
 * {@link org.apache.avalon.framework.parameters.Parameterizable}
 * nor {@link org.apache.avalon.framework.configuration.Configurable}
 * then this descriptor will hold empty values for location, category
 * and type.
 *
 * <p>Associated with each Schema is a set of arbitrary
 * Attributes that can be used to store extra information
 * about Schema requirements.</p>
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.3 $ $Date: 2003-06-29 01:07:35 $
 */
public class SchemaDescriptor
    extends FeatureDescriptor
{
    /**
     * The location of schema relative to component.
     */
    private final String m_location;

    /**
     * The type of the schema.
     */
    private final String m_type;

    /**
     * Create a Schema descriptor.
     *
     * @param location the location of schema relative to component
     * @param type the type of the schema
     * @param attributes the attributes associated with schema
     */
    public SchemaDescriptor( final String location,
                             final String type,
                             final Attribute[] attributes )
    {
        super( attributes );
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
     * Return the type of the schema.
     * Usually represented as a URI referring to schema
     * namespace declaration.
     *
     * @return the type of the schema
     */
    public String getType()
    {
        return m_type;
    }
}
