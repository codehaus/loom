/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.components.util.factory;

import java.io.InputStream;
import org.jcontainer.loom.components.util.info.ComponentInfo;

/**
 *
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.2 $ $Date: 2003-11-03 06:11:26 $
 */
public class DefaultComponentBundle
    implements ComponentBundle
{
    private final ComponentInfo m_componentInfo;

    public DefaultComponentBundle( final ComponentInfo componentInfo )
    {
        if( null == componentInfo )
        {
            throw new NullPointerException( "componentInfo" );
        }
        m_componentInfo = componentInfo;
    }

    public ComponentInfo getComponentInfo()
    {
        return m_componentInfo;
    }
}
