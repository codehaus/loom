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
import org.codehaus.spice.alchemist.LoggerAlchemist;
import org.jcontainer.loom.components.instrument.NoopInstrumentManager;
import org.jcontainer.loom.components.util.profile.PartitionProfile;
import org.jcontainer.loom.interfaces.ApplicationContext;

/**
 * @author Peter Donald
 * @version $Revision: 1.10 $ $Date: 2003-12-15 17:32:46 $
 */
class MockApplicationContext
    implements ApplicationContext
{
    private final PartitionProfile m_sarMetaData;
    private final org.jcontainer.dna.Logger m_logger;

    public MockApplicationContext( final PartitionProfile sarMetaData,
                                   final org.jcontainer.dna.Logger logger )
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

    public void exportObject( String name, Object object )
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
        return getClass().getClassLoader().getResourceAsStream( name );
    }

    public File getHomeDirectory()
    {
        return new File( "." );
    }

    public Logger getLogger( String name )
    {
        return LoggerAlchemist.toAvalonLogger( m_logger );
    }

    public InstrumentManager getInstrumentManager()
    {
        return new NoopInstrumentManager();
    }

    public String getInstrumentableName( String component )
    {
        return component;
    }
}
