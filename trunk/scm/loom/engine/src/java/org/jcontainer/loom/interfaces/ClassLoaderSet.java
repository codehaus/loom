/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 *
 * This product includes software developed by the
 * Apache Software Foundation (http://www.apache.org/).
 */
package org.jcontainer.loom.interfaces;

import java.util.Map;

/**
 * A dodgy class to hold all apps classloaders.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003-06-29 04:38:22 $
 * @todo Remove me when kernel is reworked!
 */
public final class ClassLoaderSet
{
    private final ClassLoader m_defaultClassLoader;
    private final Map m_classLoaders;

    public ClassLoaderSet( final ClassLoader defaultClassLoader,
                           final Map classLoaders )
    {
        if( null == defaultClassLoader )
        {
            throw new NullPointerException( "defaultClassLoader" );
        }
        if( null == classLoaders )
        {
            throw new NullPointerException( "classLoaders" );
        }

        m_defaultClassLoader = defaultClassLoader;
        m_classLoaders = classLoaders;
    }

    public ClassLoader getDefaultClassLoader()
    {
        return m_defaultClassLoader;
    }

    public Map getClassLoaders()
    {
        return m_classLoaders;
    }
}
