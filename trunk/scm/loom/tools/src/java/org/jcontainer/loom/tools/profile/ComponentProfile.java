/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.tools.profile;

import org.apache.avalon.phoenix.containerkit.metadata.ComponentMetaData;
import org.jcontainer.loom.tools.info.ComponentInfo;

/**
 * The ComponentProfile defines a component as a conjunction
 * of the {@link org.jcontainer.loom.tools.info.ComponentInfo} and {@link org.apache.avalon.phoenix.containerkit.metadata.ComponentMetaData}.
 * The {@link org.jcontainer.loom.tools.info.ComponentInfo} defines the type of the component
 * and the {@link org.apache.avalon.phoenix.containerkit.metadata.ComponentMetaData} defines the data required to
 * construct a specific instance of the component.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003-06-29 00:44:44 $
 */
public class ComponentProfile
{
    /**
     * The {@link org.jcontainer.loom.tools.info.ComponentInfo} that describes
     * the type of this component.
     */
    private final ComponentInfo m_info;

    /**
     * The {@link org.apache.avalon.phoenix.containerkit.metadata.ComponentMetaData} that describes
     * this component.
     */
    private final ComponentMetaData m_metaData;

    /**
     * Creation of a new <code>ComponentProfile</code> instance.
     *
     * @param metaData the {@link org.apache.avalon.phoenix.containerkit.metadata.ComponentMetaData} instance defining the component.
     */
    public ComponentProfile( final ComponentInfo info,
                             final ComponentMetaData metaData )
    {
        m_info = info;
        m_metaData = metaData;
    }

    /**
     * Returns the underlying {@link org.jcontainer.loom.tools.info.ComponentInfo} instance.
     *
     * @return the component info instance
     */
    public ComponentInfo getInfo()
    {
        return m_info;
    }

    /**
     * Returns the underlying {@link org.apache.avalon.phoenix.containerkit.metadata.ComponentMetaData} instance.
     * @return the component meta data instance
     */
    public ComponentMetaData getMetaData()
    {
        return m_metaData;
    }
}
