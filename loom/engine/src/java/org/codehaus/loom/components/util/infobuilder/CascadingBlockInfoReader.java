/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.codehaus.loom.components.util.infobuilder;

import org.codehaus.dna.Logger;
import org.codehaus.loom.components.util.info.ComponentInfo;

/**
 * @author <a href="mailto:peter.royal@pobox.com">peter royal</a>
 */
public class CascadingBlockInfoReader implements BlockInfoReader
{
    private final LegacyBlockInfoReader m_legacyBlockInfoReader;
    private final MetaClassBlockInfoReader m_metaClassBlockInfoReader = new MetaClassBlockInfoReader();

    public CascadingBlockInfoReader( final ClassLoader classLoader, final Logger logger )
    {
        if( null == classLoader )
        {
            throw new NullPointerException( "classLoader" );
        }
        else if( null == logger )
        {
            throw new NullPointerException( "logger" );
        }

        m_legacyBlockInfoReader = new LegacyBlockInfoReader( classLoader, logger );
    }

    public ComponentInfo buildComponentInfo( final Class type ) throws Exception
    {
        final ComponentInfo legacyComponentInfo = m_legacyBlockInfoReader.buildComponentInfo( type );

        if( null == legacyComponentInfo )
        {
            return m_metaClassBlockInfoReader.buildComponentInfo( type );
        }
        else
        {
            return legacyComponentInfo;
        }
    }
}