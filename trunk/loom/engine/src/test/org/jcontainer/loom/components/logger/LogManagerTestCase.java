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

import org.apache.avalon.phoenix.BlockContext;
import org.apache.avalon.phoenix.metadata.BlockListenerMetaData;
import org.apache.avalon.phoenix.metadata.BlockMetaData;
import org.apache.avalon.phoenix.metadata.SarMetaData;

import junit.framework.TestCase;
import org.codehaus.spice.loggerstore.LoggerStore;
import org.codehaus.spice.salt.io.FileUtil;
import org.codehaus.dna.Configuration;
import org.codehaus.dna.Logger;
import org.codehaus.dna.impl.ConfigurationUtil;
import org.codehaus.dna.impl.ConsoleLogger;
import org.codehaus.dna.impl.ContainerUtil;
import org.jcontainer.loom.interfaces.LogManager;
import org.xml.sax.InputSource;

/**
 * An basic test case for the LogManager.
 *
 * @author Peter Donald
 * @version $Revision: 1.18 $ $Date: 2004-04-18 23:09:53 $
 */
public class LogManagerTestCase
    extends TestCase
{
    private static final String DEFAULT_LOGFILE = "logs" +
        File.separator +
        "default.log";
    private static final String BLOCK_LOGFILE = "logs" +
        File.separator +
        "myBlock.log";
    private File m_baseDirectory;

    protected void setUp()
        throws Exception
    {
        m_baseDirectory = generateDirectory();
        //Because log4j does not guarentee dir creation ;(
        final File logDir = new File( m_baseDirectory, "logs" );
        logDir.mkdirs();
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

    private void runtTestForConfigFile( final int index )
        throws Exception
    {
        final LoggerStore hierarchy = createHierarchy( index );
        final Logger rootLogger = hierarchy.getLogger();
        runLoggerTest( rootLogger, DEFAULT_LOGFILE );

        final Logger childLogger = hierarchy.getLogger( "myBlock" );
        runLoggerTest( childLogger, BLOCK_LOGFILE );
    }

    private void runLoggerTest( final Logger logger,
                                final String logfile )
    {
        final long before = getFileSize( logfile );
        logger.warn( "Danger Will Robinson, Danger!" );
        final long after = getFileSize( logfile );

        assertFileGrew( logfile, before, after );
    }

    private void assertFileGrew( final String logfile,
                                 final long before,
                                 final long after )
    {
        assertTrue( "Did " +
                    logfile +
                    " file grow?, Before: " +
                    before +
                    ", After: " +
                    after,
                    before < after );
    }

    private long getFileSize( final String filename )
    {
        final File file = new File( m_baseDirectory, filename );
        final StringBuffer sb = new StringBuffer();
        try
        {
            final FileReader fr = new FileReader( file );
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

    private LoggerStore createHierarchy( final int index )
        throws Exception
    {
        final Configuration logs = loadConfig( "config" + index + ".xml" );
        final LogManager logManager = createLogManager();
        final SarMetaData sarMetaData =
            new SarMetaData( "test",
                             m_baseDirectory,
                             new BlockMetaData[0],
                             new BlockListenerMetaData[0] );
        cleanHomeDirectory( sarMetaData );

        //make sure directory is created else log4j will fail.
        if( 3 == index )
        {
            final File file =
                new File( m_baseDirectory.getAbsolutePath() +
                          File.separator +
                          "logs" );
            file.mkdirs();
        }

        final HashMap context = new HashMap();
        context.put( BlockContext.APP_NAME, sarMetaData.getName() );
        context.put( BlockContext.APP_HOME_DIR,
                     sarMetaData.getHomeDirectory() );
        context.put( "classloader", getClass().getClassLoader() );

        final LoggerStore store =
            logManager.createHierarchy( logs,
                                        sarMetaData.getHomeDirectory(),
                                        sarMetaData.getHomeDirectory(),
                                        context );
        return store;
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

    private static final File generateDirectory()
        throws IOException
    {
        final File baseDirectory = getBaseDirectory();
        final File dir =
            File.createTempFile( "mgtest", ".tmp", baseDirectory )
            .getCanonicalFile();
        dir.delete();
        dir.mkdirs();
        assertTrue( "dir.exists()", dir.exists() );
        return dir;
    }

    private static final File getBaseDirectory()
    {
        final String tempDir = System.getProperty( "java.io.tmpdir" );
        final String baseDir = System.getProperty( "basedir", tempDir );

        final File base = new File( baseDir ).getAbsoluteFile();
        final String pathname =
            base + File.separator + "target" + File.separator + "test-data";
        final File dir = new File( pathname );
        dir.mkdirs();
        return dir;
    }
}
