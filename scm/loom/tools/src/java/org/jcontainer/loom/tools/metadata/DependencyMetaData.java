/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
 package org.jcontainer.loom.tools.metadata;

import org.jcontainer.loom.tools.info.Attribute;
import org.jcontainer.loom.tools.info.FeatureDescriptor;

/**
 * The {@link org.jcontainer.loom.tools.metadata.DependencyMetaData} is the mapping of a component as a dependency
 * of another component. Each component declares dependencies (via
 * {@link org.jcontainer.loom.tools.info.ComponentInfo})
 * and for each dependency there must be a coressponding DependencyMetaData which
 * has a matching key. The name value in {@link org.jcontainer.loom.tools.metadata.DependencyMetaData} object must refer
 * to another Component that implements a service as specified in DependencyInfo.
 *
 * <p>Note that it is invalid to have circular dependencies.</p>
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version $Revision: 1.2 $ $Date: 2003-06-29 01:07:36 $
 */
public final class DependencyMetaData
    extends FeatureDescriptor
{
    /**
     * The key that the client component will use to access a dependency.
     */
    private final String m_key;

    /**
     * the name of the component profile that represents a component
     * type that is capable of fullfilling the dependency.
     */
    private final String m_providerName;

    /**
     * The key that is used when the dependency is a map dependency.
     * Usually this defaults to the same value as the key.
     */
    private final String m_alias;

    /**
     * Create Association between key and provider.
     *
     * @param key the key the client uses to access component
     * @param providerName the name of {@link org.jcontainer.loom.tools.metadata.ComponentMetaData}
     *   that is associated as a service provider
     */
    public DependencyMetaData( final String key,
                               final String providerName,
                               final String alias,
                               final Attribute[] attributes )
    {
        super( attributes );

        if( null == key )
        {
            throw new NullPointerException( "key" );
        }
        if( null == providerName )
        {
            throw new NullPointerException( "providerName" );
        }
        if( null == alias )
        {
            throw new NullPointerException( "alias" );
        }
        m_key = key;
        m_providerName = providerName;
        m_alias = alias;
    }

    /**
     * Return the key that will be used by a component instance to access a
     * dependent service.
     *
     * @return the name that the client component will use to access dependency.
     * @see org.apache.avalon.framework.service.ServiceManager#lookup( java.lang.String )
     */
    public String getKey()
    {
        return m_key;
    }

    /**
     * Return the name of a {@link org.jcontainer.loom.tools.metadata.ComponentMetaData} instance that will used to
     * fulfill the dependency.
     *
     * @return the name of the Component that will provide the dependency.
     */
    public String getProviderName()
    {
        return m_providerName;
    }

    /**
     * The key under which the dependency is placed in map if dependency is
     * a Map dependency.
     *
     * @return the key under which the dependency is placed in map if dependency is
     *         a Map dependency.
     */
    public String getAlias()
    {
        return m_alias;
    }
}
