/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.metainfo;

/**
 * This class contains meta-information of use to administative
 * tools and the kernel. It describes the services offered by a type
 * of block, the dependencies of the block, the management interface of
 * block (if any) and also contains information useful to presenting
 * information in administative screens (like human readable names etc).
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 */
public class BlockInfo
{
    private final BlockDescriptor m_descriptor;
    private final ServiceDescriptor[] m_services;
    private final ServiceDescriptor[] m_managementAccessPoints;
    private final DependencyDescriptor[] m_dependencies;

    /**
     * Basic constructor that takes as parameters all parts.
     */
    public BlockInfo( final BlockDescriptor descriptor,
                      final ServiceDescriptor[] services,
                      final ServiceDescriptor[] managementAccessPoints,
                      final DependencyDescriptor[] dependencies )
    {
        m_descriptor = descriptor;
        m_services = services;
        m_managementAccessPoints = managementAccessPoints;
        m_dependencies = dependencies;
    }

    /**
     * Return meta information that is generallly only required by administration tools.
     *
     * It should be loaded on demand and not always present in memory.
     *
     * @return the BlockDescriptor
     */
    public BlockDescriptor getBlockDescriptor()
    {
        return m_descriptor;
    }

    /**
     * This returns a list of Services that this block exports.
     *
     * @return an array of Services
     */
    public ServiceDescriptor[] getServices()
    {
        return m_services;
    }

    /**
     * This returns a list of Services that this block can be Managed by.
     *
     * @return an array of Management Access Points (management services)
     */
    public ServiceDescriptor[] getManagementAccessPoints()
    {
        return m_managementAccessPoints;
    }

    /**
     * Return an array of Service dependencies that this Block depends upon.
     *
     * @return an array of Service dependencies
     */
    public DependencyDescriptor[] getDependencies()
    {
        return m_dependencies;
    }

    /**
     * Retrieve a dependency with a particular role.
     *
     * @param role the role
     * @return the dependency or null if it does not exist
     */
    public DependencyDescriptor getDependency( final String role )
    {
        for( int i = 0; i < m_dependencies.length; i++ )
        {
            if( m_dependencies[ i ].getRole().equals( role ) )
            {
                return m_dependencies[ i ];
            }
        }

        return null;
    }
}
