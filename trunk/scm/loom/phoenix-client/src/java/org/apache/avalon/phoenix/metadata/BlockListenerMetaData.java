/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.metadata;

/**
 * This describs a BlockListener.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 */
public final class BlockListenerMetaData
{
    private final String m_name;
    private final String m_implementationKey;

    public BlockListenerMetaData( final String name,
                                  final String implementationKey )
    {
        m_name = name;
        m_implementationKey = implementationKey;
    }

    public String getImplementationKey()
    {
        return m_implementationKey;
    }

    /**
     * @deprecated Use getImplementationKey() instead.
     */
    public String getClassname()
    {
        return getImplementationKey();
    }

    public String getName()
    {
        return m_name;
    }
}
