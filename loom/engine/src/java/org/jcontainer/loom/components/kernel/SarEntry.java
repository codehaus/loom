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
package org.jcontainer.loom.components.kernel;

import java.io.File;
import java.util.Map;
import org.jcontainer.loom.interfaces.Application;
import org.jcomponent.loggerstore.LoggerStore;

/**
 * This is the structure describing each server application before it is loaded.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 */
final class SarEntry
{
    private final org.jcontainer.loom.tools.profile.PartitionProfile m_profile;
    private final ClassLoader m_classLoader;
    private final LoggerStore m_store;
    private final File m_homeDirectory;
    private final File m_workDirectory;
    private final Map m_classLoaders;
    private Application m_application;

    protected SarEntry( final org.jcontainer.loom.tools.profile.PartitionProfile profile,
                        final File homeDirectory,
                        final File workDirectory,
                        final ClassLoader classLoader,
                        final LoggerStore store,
                        final Map classLoaders )
    {
        if( null == profile )
        {
            throw new NullPointerException( "profile" );
        }
        if( null == classLoader )
        {
            throw new NullPointerException( "classLoader" );
        }
        if( null == store )
        {
            throw new NullPointerException( "store" );
        }
        if( null == workDirectory )
        {
            throw new NullPointerException( "workDirectory" );
        }
        if( null == homeDirectory )
        {
            throw new NullPointerException( "homeDirectory" );
        }
        if( null == classLoaders )
        {
            throw new NullPointerException( "classLoaders" );
        }

        m_profile = profile;
        m_classLoader = classLoader;
        m_store = store;
        m_homeDirectory = homeDirectory;
        m_workDirectory = workDirectory;
        m_classLoaders = classLoaders;
    }

    public File getHomeDirectory()
    {
        return m_homeDirectory;
    }

    public File getWorkDirectory()
    {
        return m_workDirectory;
    }

    public Application getApplication()
    {
        return m_application;
    }

    public void setApplication( final Application application )
    {
        m_application = application;
    }

    public org.jcontainer.loom.tools.profile.PartitionProfile getProfile()
    {
        return m_profile;
    }

    public LoggerStore getLoggerStore()
    {
        return m_store;
    }

    public ClassLoader getClassLoader()
    {
        return m_classLoader;
    }

    public Map getClassLoaders()
    {
        return m_classLoaders;
    }
}
