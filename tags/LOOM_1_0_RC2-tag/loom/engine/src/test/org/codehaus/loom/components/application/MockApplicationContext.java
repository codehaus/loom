/*
 * Copyright (C) The Loom Group. All rights reserved.
 *
 * This software is published under the terms of the Loom
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.codehaus.loom.components.application;

import java.io.File;
import java.io.InputStream;
import org.apache.avalon.framework.logger.Logger;
import org.apache.excalibur.instrument.InstrumentManager;
import org.codehaus.loom.components.instrument.NoopInstrumentManager;
import org.codehaus.loom.components.util.profile.PartitionProfile;
import org.codehaus.loom.interfaces.ApplicationContext;
import org.codehaus.spice.alchemist.logger.LoggerAlchemist;

/**
 * @author Peter Donald
 * @version $Revision: 1.3 $ $Date: 2004-06-18 10:51:11 $
 */
class MockApplicationContext
    implements ApplicationContext
{
    private final PartitionProfile m_sarMetaData;
    private final org.codehaus.dna.Logger m_logger;

    public MockApplicationContext( final PartitionProfile sarMetaData,
                                   final org.codehaus.dna.Logger logger )
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
