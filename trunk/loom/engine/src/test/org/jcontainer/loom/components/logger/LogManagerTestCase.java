/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.components.logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import junit.framework.TestCase;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.phoenix.BlockContext;
import org.apache.avalon.phoenix.metadata.BlockListenerMetaData;
import org.apache.avalon.phoenix.metadata.BlockMetaData;
import org.apache.avalon.phoenix.metadata.SarMetaData;
import org.realityforge.loggerstore.LoggerStore;
import org.realityforge.salt.io.FileUtil;
import org.jcontainer.loom.components.logger.DefaultLogManager;
import org.jcontainer.loom.interfaces.LogManager;

/**
 *  An basic test case for the LogManager.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.3 $ $Date: 2003-07-19 01:23:38 $
 */
public class LogManagerTestCase
    extends TestCase
{
    public static final String DEFAULT_LOGFILE = "logs/default.log";
    public static final String BLOCK_LOGFILE = "logs/myBlock.log";

    private File m_baseDirectory;

    public LogManagerTestCase( final String name )
    {
        super( name );
    }

    protected void setUp() throws Exception
    {
        m_baseDirectory = new File( "target" + File.separator + "testdata" );
        m_baseDirectory.mkdirs();

        //Because log4j does not guarentee dir creation ;(
        final File logDir = new File( m_baseDirectory, "logs" );
        logDir.mkdirs();
    }

    private SarMetaData createSarMetaData( final String subdir )
    {
        final BlockMetaData[] blocks = new BlockMetaData[ 0 ];
        final BlockListenerMetaData[] listeners = new BlockListenerMetaData[ 0 ];
        final File homeDirectory = new File( m_baseDirectory, subdir );
        return new SarMetaData( "test",
                                homeDirectory,
                                blocks,
                                listeners );
    }

    private LogManager createLogManager()
    {
        final DefaultLogManager logManager = new DefaultLogManager();
        ContainerUtil.enableLogging( logManager, new ConsoleLogger() );
        return logManager;
    }

    public void testBasic()
        throws Exception
    {
        runtTestForConfigFile( 1 );
    }

    public void testExcaliburLogger()
        throws Exception
    {
        runtTestForConfigFile( 2 );
    }

    public void testLog4jVersion()
        throws Exception
    {
        runtTestForConfigFile( 3 );
    }

    private void runtTestForConfigFile( final int index ) throws Exception
    {
        final Logger hierarchy = createHierarchy( index );
        runLoggerTest( hierarchy, DEFAULT_LOGFILE, index );

        final Logger childLogger = hierarchy.getChildLogger( "myBlock" );
        runLoggerTest( childLogger, BLOCK_LOGFILE, index );
    }

    private void runLoggerTest( final Logger logger,
                                final String logfile,
                                final int index )
    {
        final long before = getFileSize( index, logfile );
        logger.warn( "Danger Will Robinson, Danger!" );
        final long after = getFileSize( index, logfile );

        assertFileGrew( logfile, before, after );
    }

    private void assertFileGrew( final String logfile, long before, long after )
    {
        assertTrue( "Did " + logfile + " file grow?, Before: " + before + ", After: " + after,
                    before < after );
    }

    private long getFileSize( final int index, final String filename )
    {
        final File base = getBaseDir( index );
        final File file = new File( base, filename );
        return file.length();
    }

    private File getBaseDir( final int index )
    {
        final String baseDir = getBaseDirName( index );
        return new File( m_baseDirectory, baseDir );
    }

    private Logger createHierarchy( final int index )
        throws Exception
    {
        final Configuration logs = loadConfig( "config" + index + ".xml" );
        final LogManager logManager = createLogManager();
        final SarMetaData sarMetaData = createSarMetaData( getBaseDirName( index ) );

        cleanHomeDirectory( sarMetaData );

        //make sure directory is created else log4j will fail.
        if( 3 == index )
        {
            final File file =
                new File( getBaseDir( index ).getAbsolutePath() + "/logs" );
            file.mkdirs();
        }

        final HashMap context = new HashMap();
        context.put( BlockContext.APP_NAME, sarMetaData.getName() );
        context.put( BlockContext.APP_HOME_DIR, sarMetaData.getHomeDirectory() );
        context.put( "classloader", getClass().getClassLoader() );

        final LoggerStore store =
            logManager.createHierarchy( logs,
                                        sarMetaData.getHomeDirectory(),
                                        sarMetaData.getHomeDirectory(),
                                        context );
        return store.getLogger();
    }

    private String getBaseDirName( final int index )
    {
        return "test" + index;
    }

    private void cleanHomeDirectory( final SarMetaData sarMetaData )
        throws IOException
    {
        final File homeDirectory = sarMetaData.getHomeDirectory();
        FileUtil.deleteDirectory( homeDirectory );
        homeDirectory.mkdirs();
    }

    private Configuration loadConfig( final String config )
        throws Exception
    {
        final DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
        final InputStream resource =
            getClass().getResourceAsStream( config );
        return builder.build( resource );
    }
}
