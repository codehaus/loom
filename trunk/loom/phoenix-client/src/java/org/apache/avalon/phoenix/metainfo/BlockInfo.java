/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1997-2003 The Apache Software Foundation. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *    "This product includes software developed by the
 *    Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software
 *    itself, if and wherever such third-party acknowledgments
 *    normally appear.
 *
 * 4. The names "Jakarta", "Avalon", and "Apache Software Foundation"
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */
package org.apache.avalon.phoenix.metainfo;

/**
 * This class contains meta-information of use to administative
 * tools and the kernel. It describes the services offered by a type
 * of block, the dependencies of the block, the management interface of
 * block (if any) and also contains information useful to presenting
 * information in administative screens (like human readable names etc).
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
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
