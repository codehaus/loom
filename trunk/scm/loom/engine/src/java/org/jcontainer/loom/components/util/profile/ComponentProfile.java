/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.components.util.profile;

import org.jcontainer.loom.components.util.info.ComponentInfo;
import org.jcontainer.loom.components.util.metadata.ComponentTemplate;

/**
 * The ComponentProfile defines a component as a conjunction
 * of the ComponentInfo and ComponentTemplate.
 * The ComponentInfo defines the type of the component
 * and the ComponentTemplate defines the data required to
 * construct a specific instance of the component.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.3 $ $Date: 2003-11-03 06:43:15 $
 */
public class ComponentProfile
{
    /**
     * An empty array of component profiles.
     */
    public static final ComponentProfile[] EMPTY_SET = new ComponentProfile[ 0 ];

    /**
     * The ComponentInfo that describes
     * the type of this component.
     */
    private final ComponentInfo m_info;

    /**
     * The ComponentTemplate that describes
     * this component.
     */
    private final ComponentTemplate m_metaData;

    /**
     * Creation of a new <code>ComponentProfile</code> instance.
     *
     * @param metaData the ComponentTemplate instance defining the component.
     */
    public ComponentProfile( final ComponentInfo info,
                             final ComponentTemplate metaData )
    {
        if( null == info )
        {
            throw new NullPointerException( "info" );
        }
        if( null == metaData )
        {
            throw new NullPointerException( "metaData" );
        }
        m_info = info;
        m_metaData = metaData;
    }

    /**
     * Returns the underlying ComponentInfo instance.
     *
     * @return the component info instance
     */
    public ComponentInfo getInfo()
    {
        return m_info;
    }

    /**
     * Returns the underlying ComponentTemplate instance.
     * @return the component meta data instance
     */
    public ComponentTemplate getTemplate()
    {
        return m_metaData;
    }
}
