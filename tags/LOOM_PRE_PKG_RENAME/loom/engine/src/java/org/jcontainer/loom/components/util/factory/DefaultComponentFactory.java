/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.components.util.factory;

import java.util.Map;
import java.util.WeakHashMap;

import org.jcontainer.dna.Logger;
import org.jcontainer.loom.components.util.info.ComponentInfo;
import org.jcontainer.loom.components.util.infobuilder.BlockInfoReader;
import org.jcontainer.loom.components.util.infobuilder.CascadingBlockInfoReader;

/**
 * The default implementation of ComponentFactory that simply creates components from a ClassLoader.
 *
 * @author Peter Donald
 * @version $Revision: 1.5 $ $Date: 2004-02-20 03:49:49 $
 */
public class DefaultComponentFactory
    implements ComponentFactory
{
    /**
     * Cache of ComponentInfo objects.
     */
    private final Map m_infos = new WeakHashMap();

    /**
     * The utility class that is used when building info objects for Components.
     */
    private final BlockInfoReader m_blockInfoReader;

    /**
     * The classloader from which all resources are loaded.
     */
    private final ClassLoader m_classLoader;

    /**
     * Create a Factory that loads from specified ClassLoader.
     *
     * @param classLoader the classLoader to use in factory, must not be null
     * @param logger      the logger to use for the BlockInfoReader
     */
    public DefaultComponentFactory( final ClassLoader classLoader, final Logger logger )
    {
        if( null == classLoader )
        {
            throw new NullPointerException( "classLoader" );
        }
        else if( null == logger )
        {
            throw new NullPointerException( "logger" );
        }

        m_classLoader = classLoader;
        m_blockInfoReader = new CascadingBlockInfoReader( classLoader, logger );
    }

    /**
     * Create a component by creating info for class with specified name and loaded from factorys ClassLoader.
     *
     * @see ComponentFactory#createInfo
     */
    public ComponentInfo createInfo( final String implementationKey )
        throws Exception
    {
        ComponentInfo bundle = (ComponentInfo)m_infos.get( implementationKey );
        if( null == bundle )
        {
            bundle = createComponentInfo( implementationKey );
            m_infos.put( implementationKey, bundle );
        }

        return bundle;
    }

    /**
     * Create a component by creating instance of class with specified name and loaded from factorys ClassLoader.
     *
     * @see ComponentFactory#createComponent
     */
    public Object createComponent( final String implementationKey )
        throws Exception
    {
        final Class clazz = getClassLoader().loadClass( implementationKey );
        return clazz.newInstance();
    }

    /**
     * Create a ComponentInfo for component with specified implementationKey.
     *
     * @param implementationKey the implementationKey
     *
     * @return the created ComponentInfo
     *
     * @throws Exception if unabel to create componentInfo
     */
    protected ComponentInfo createComponentInfo( final String implementationKey )
        throws Exception
    {
        final Class type = getClassLoader().loadClass( implementationKey );
        return m_blockInfoReader.buildComponentInfo( type );
    }

    /**
     * Return the ClassLoader associated with the ComponentFactory.
     *
     * @return the ClassLoader associated with the ComponentFactory.
     */
    protected ClassLoader getClassLoader()
    {
        return m_classLoader;
    }
}
