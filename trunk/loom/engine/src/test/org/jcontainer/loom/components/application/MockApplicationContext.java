/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.components.application;

import java.io.File;
import java.io.InputStream;
import org.apache.avalon.framework.logger.Logger;
import org.apache.excalibur.instrument.InstrumentManager;
import org.jcontainer.loom.interfaces.ApplicationContext;
import org.jcontainer.loom.tools.profile.PartitionProfile;

/**
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003-07-19 02:25:23 $
 */
class MockApplicationContext
    implements ApplicationContext
{
    private final PartitionProfile m_sarMetaData;
    private final Logger m_logger;

    public MockApplicationContext( final PartitionProfile sarMetaData,
                                   final Logger logger )
    {
        m_sarMetaData = sarMetaData;
        m_logger = logger;
    }

    public PartitionProfile getPartitionProfile()
    {
        return m_sarMetaData;
    }

    public void requestShutdown()
    {
        //ignore
    }

    public void exportObject( String name, Class[] interfaceClasses, Object object )
        throws Exception
    {
        //ignore
    }

    public void unexportObject( String name )
        throws Exception
    {
        //ignore
    }

    public ClassLoader getClassLoader()
    {
        return getClass().getClassLoader();
    }

    public InputStream getResourceAsStream( final String name )
    {
        return getClassLoader().getResourceAsStream( name );
    }

    public File getHomeDirectory()
    {
        return new File( "." );
    }

    public ClassLoader getClassLoader( String name )
        throws Exception
    {
        throw new Exception( "I can't do that dave!" );
    }

    public Logger getLogger( String name )
    {
        return m_logger;
    }

    public InstrumentManager getInstrumentManager()
    {
        return new MockInstrumentManager();
    }

    public String getInstrumentableName( String component )
    {
        return component;
    }
}
