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
package org.jcontainer.loom.components.embeddor;

import java.util.HashMap;
import java.util.Map;
import org.jcontainer.loom.components.extensions.pkgmgr.ExtensionManager;
import org.jcontainer.loom.interfaces.Deployer;
import org.jcontainer.loom.interfaces.DeployerMBean;
import org.jcontainer.loom.interfaces.Embeddor;
import org.jcontainer.loom.interfaces.EmbeddorMBean;
import org.jcontainer.loom.interfaces.ExtensionManagerMBean;
import org.jcontainer.loom.interfaces.Kernel;
import org.jcontainer.loom.interfaces.KernelMBean;
import org.jcontainer.loom.interfaces.LogManager;

/**
 *
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 */
final class ManagementRegistration
{
    private static final Map c_map = new HashMap();
    public static final ManagementRegistration KERNEL =
        new ManagementRegistration( Kernel.ROLE,
                                    "Kernel",
                                    new Class[]{KernelMBean.class} );
    public static final ManagementRegistration EXTENSION_MANAGER =
        new ManagementRegistration( ExtensionManager.ROLE,
                                    "ExtensionManager",
                                    new Class[]{ExtensionManagerMBean.class} );
    public static final ManagementRegistration EMBEDDOR =
        new ManagementRegistration( Embeddor.ROLE, "Embeddor", new Class[]{EmbeddorMBean.class} );
    public static final ManagementRegistration DEPLOYER =
        new ManagementRegistration( Deployer.ROLE, "Deployer", new Class[]{DeployerMBean.class} );
    public static final ManagementRegistration LOG_MANAGER =
        new ManagementRegistration( LogManager.ROLE, "LogManager", new Class[]{} );
    //TODO: Need information for SystemManager?

    private final String m_role;
    private final String m_name;
    private final Class[] m_interfaces;

    private ManagementRegistration( final String role, final String name, final Class[] interfaces )
    {
        m_role = role;
        m_name = name;
        m_interfaces = interfaces;

        c_map.put( m_role, this );
    }

    public String getRole()
    {
        return m_role;
    }

    public String getName()
    {
        return m_name;
    }

    public Class[] getInterfaces()
    {
        return m_interfaces;
    }

    public static ManagementRegistration getManagementInfoForRole( final String role )
    {
        return (ManagementRegistration)c_map.get( role );
    }
}
