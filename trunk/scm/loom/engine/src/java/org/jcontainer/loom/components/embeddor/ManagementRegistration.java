/* ====================================================================
 * JContainer Software License, version 1.1
 *
 * Copyright (c) 2003, JContainer Group. All rights reserved.
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
 * 3. Neither the name of the JContainer Group nor the name "Loom" nor
 *    the names of its contributors may be used to endorse or promote
 *    products derived from this software without specific prior
 *    written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * ====================================================================
 *
 * JContainer Loom includes code from the Apache Software Foundation
 *
 * ====================================================================
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
