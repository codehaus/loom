/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.metainfo;

/**
 * A descriptor that describes dependency information for Block.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 */
public final class DependencyDescriptor
{
    private final String m_role;
    private final ServiceDescriptor m_service;

    /**
     * Constructor that has all parts as parameters.
     */
    public DependencyDescriptor( final String role, final ServiceDescriptor service )
    {
        m_role = role;
        m_service = service;
    }

    /**
     * Return role of dependency.
     *
     * The role is what is used by block implementor to
     * aquire dependency in ComponentManager.
     *
     * @return the name of the dependency
     */
    public String getRole()
    {
        return m_role;
    }

    /**
     * Return Service dependency provides.
     *
     * @return the service dependency provides
     */
    public ServiceDescriptor getService()
    {
        return m_service;
    }

    public String toString()
    {
        return "Dependency[" + getRole() + "::" + getService() + "]";
    }
}
