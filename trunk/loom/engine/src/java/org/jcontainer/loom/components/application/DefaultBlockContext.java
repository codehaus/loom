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
package org.jcontainer.loom.components.application;

import java.io.File;
import java.io.InputStream;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.phoenix.BlockContext;
import org.jcontainer.loom.interfaces.ApplicationContext;

/**
 * Context via which Blocks communicate with container.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 */
final class DefaultBlockContext
    implements BlockContext
{
    private final String m_name;
    private final ApplicationContext m_applicationContext;

    DefaultBlockContext( final String name,
                         final ApplicationContext frame )
    {
        m_name = name;
        m_applicationContext = frame;
    }

    public Object get( final Object key )
        throws ContextException
    {
        if( BlockContext.APP_NAME.equals( key ) )
        {
            return m_applicationContext.getPartitionProfile().getMetaData().getName();
        }
        else if( BlockContext.APP_HOME_DIR.equals( key ) )
        {
            return m_applicationContext.getHomeDirectory();
        }
        else if( BlockContext.NAME.equals( key ) )
        {
            return m_name;
        }
        else
        {
            throw new ContextException( "Unknown key: " + key );
        }
    }

    /**
     * Base directory of .sar application.
     *
     * @return the base directory
     */
    public File getBaseDirectory()
    {
        return m_applicationContext.getHomeDirectory();
    }

    /**
     * Retrieve name of block.
     *
     * @return the name of block
     */
    public String getName()
    {
        return m_name;
    }

    public void requestShutdown()
    {
        m_applicationContext.requestShutdown();
    }

    public InputStream getResourceAsStream( final String name )
    {
        return m_applicationContext.getResourceAsStream( name );
    }

    public Logger getLogger( final String name )
    {
        try
        {
            return m_applicationContext.getLogger( getName() ).getChildLogger( name );
        }
        catch( Exception e )
        {
            final String message =
                "Unable to aquire logger " + name + " due to " + e;
            throw new IllegalStateException( message );
        }
    }

    public ClassLoader getClassLoader( final String name )
        throws Exception
    {
        return m_applicationContext.getClassLoader( name );
    }
}
