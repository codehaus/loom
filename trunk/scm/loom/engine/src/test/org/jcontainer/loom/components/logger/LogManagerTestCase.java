/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.components.logger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import junit.framework.TestCase;
import org.apache.avalon.phoenix.BlockContext;
import org.apache.avalon.phoenix.metadata.BlockListenerMetaData;
import org.apache.avalon.phoenix.metadata.BlockMetaData;
import org.apache.avalon.phoenix.metadata.SarMetaData;
import org.apache.avalon.framework.logger.Logger;
import org.jcomponent.loggerstore.LoggerStore;
import org.jcontainer.loom.interfaces.LogManager;
import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.impl.ConfigurationUtil;
import org.jcontainer.dna.impl.ContainerUtil;
import org.jcontainer.dna.impl.ConsoleLogger;
import org.realityforge.salt.io.FileUtil;
import org.xml.sax.InputSource;

/**
 *  An basic test case for the LogManager.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.9 $ $Date: 2003-10-05 10:07:04 $
 */
public class LogManagerTestCase
    extends TestCase
{
    private static final String DEFAULT_LOGFILE = "logs" + File.separator + "default.log";
    private static final String BLOCK_LOGFILE = "logs" + File.separator + "myBlock.log";

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
        final File homeDirectory = new File( m_baseDirectory, subdir ).getAbsoluteFile();
        System.out.println( "homeDirectory = " + homeDirectory );
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
        StringBuffer sb = new StringBuffer();
        try
        {
            FileReader fr = new FileReader( file );
            int c = 0;
            while( c != -1 )
            {
                c = fr.read();
                sb.append( Character.forDigit( c, 10 ) );
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
        return sb.length();
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
                new File( getBaseDir( index ).getAbsolutePath() + File.separator + "logs" );
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
        final InputStream resource =
            getClass().getResourceAsStream( config );
        return ConfigurationUtil.buildFromXML( new InputSource( resource ) );
    }
}
