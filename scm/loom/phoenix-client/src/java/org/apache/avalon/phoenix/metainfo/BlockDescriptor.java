/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.metainfo;

import org.apache.avalon.framework.Version;

/**
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 */
public class BlockDescriptor
{
    /**
     * The short name of the Block. Useful for displaying
     * human readable strings describing the type in
     * assembly tools or generators.
     */
    private final String m_name;
    private final String m_implementationKey;
    private final Version m_version;
    private final String m_schemaType;

    public BlockDescriptor( final String name,
                            final String implementationKey,
                            final String schemaType,
                            final Version version )
    {
        m_name = name;
        m_implementationKey = implementationKey;
        m_version = version;
        m_schemaType = schemaType;
    }

    /**
     * Retrieve the name of Block type.
     *
     * @return the name of Block type.
     */
    public String getName()
    {
        return m_name;
    }

    /**
     * Retrieve the Class Name of Block.
     *
     * @return the Class Name of block
     * @see #getImplementationKey
     * @deprecated Deprecated and replaced by {@link #getImplementationKey}
     */
    public String getClassname()
    {
        return getImplementationKey();
    }

    /**
     * Retrieve the implementation key for the Block.
     * Usually the keys is a classname.
     *
     * @return the implementation key for the Block
     */
    public String getImplementationKey()
    {
        return m_implementationKey;
    }

    /**
     * Retrieve Version of current Block.
     *
     * @return the version of block
     */
    public Version getVersion()
    {
        return m_version;
    }

    /**
     * Retrieve the Schema Type of Block
     *
     * @return the Schema Type of block
     */
    public String getSchemaType()
    {
        return m_schemaType;
    }
}

