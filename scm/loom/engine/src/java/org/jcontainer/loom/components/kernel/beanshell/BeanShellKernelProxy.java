/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 *
 * This product includes software developed by the
�*�Apache Software Foundation (http://www.apache.org/).
 */
package org.jcontainer.loom.components.kernel.beanshell;

import java.io.File;
import java.util.Map;
import org.jcontainer.loom.interfaces.Application;
import org.jcontainer.loom.interfaces.Kernel;
import org.jcomponent.loggerstore.LoggerStore;

/**
 * @author Paul Hammant
 * @author David Gray
 */
public class BeanShellKernelProxy
    implements Kernel
{
    private final transient Kernel m_kernel;

    /**
     * Construct a Proxy to the Kernel that does not implement all methods.
     */
    public BeanShellKernelProxy( final Kernel kernel )
    {
        m_kernel = kernel;
    }

    public void addApplication( final org.jcontainer.loom.tools.profile.PartitionProfile profile,
                                final File homeDirectory, final File workDirectory,
                                final ClassLoader classLoader,
                                final LoggerStore store,
                                final Map classloaders )
        throws Exception
    {
        throw new UnsupportedOperationException( "This is not supported for non-kernel visitors" );
    }

    public void removeApplication( final String name )
        throws Exception
    {
        m_kernel.removeApplication( name );
    }

    public Application getApplication( final String name )
    {
        return m_kernel.getApplication( name );
    }

    public String[] getApplicationNames()
    {
        return m_kernel.getApplicationNames();
    }

    public void lock()
    {
        m_kernel.lock();
    }

    public void unlock()
    {
        m_kernel.unlock();
    }

}
