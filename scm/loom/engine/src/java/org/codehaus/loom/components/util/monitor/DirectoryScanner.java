/*
 * Copyright (C) The Loom Group. All rights reserved.
 *
 * This software is published under the terms of the Loom
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.codehaus.loom.components.util.monitor;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Utility class that scans for changes in a directory. If changes are
 * detected it will notify its <code>DirectoryChangeListener</code>.
 *
 * @author Johan Sjoberg
 * @version $Revision: 1.1 $ $Date: 2004-07-12 14:46:23 $
 */
public class DirectoryScanner implements Runnable
{
    /** The monitor thread */
    private final Thread m_monitorThread = new Thread( this );

    /** Flag indicating if we are running or not */
    private volatile boolean m_keepRunning = false;

    /** Poll frequency */
    private long m_frequency = 1000L * 60L;

    /** Priority of the monitor thread */
    private int m_priority = Thread.NORM_PRIORITY;

    /** Class listening for changes in the directory */
    private DirectoryChangeListener m_directoryChangeListener;

    /** Last modification time */
    private long m_lastModified;

    /** The directory to monitor */
    private File m_directory;

    /** Files in the monitored directory */
    private Set m_files;

    /** File modification times */
    private Map m_times;

    /**
     * Set the directory to be scanned.
     *
     * @param path The directory's path
     * @throws IllegalArgumentException if the directory doesn't exist.
     */
    public void setDirectory( final String path )
    {
        m_directory = new File( path );
        if( !m_directory.isDirectory() )
        {
            final String message =
              "Argument [" + path + "] doesn't seem to be a directory.";
            throw new IllegalArgumentException( message );
        }
        m_files = new HashSet();
        m_times = new HashMap();
        final File[] files = m_directory.listFiles();
        for( int i = 0; i < files.length; i++ )
        {
            final File file = files[i];
            m_files.add( file );
            m_times.put( file, new Long( file.lastModified() ) );
        }
        m_lastModified = System.currentTimeMillis();
    }

    /**
     * Set the <code>DirectoryChangeListener</code> to notify about
     * changes in the directory.
     *
     * @param directoryChangeListener The listener
     */
    public void setDirectoryChangeListener( DirectoryChangeListener directoryChangeListener )
    {
        m_directoryChangeListener = directoryChangeListener;
    }

    /**
     * Set scan frequency for the background thread.
     *
     * @param frequency The scan frequency in milliseconds
     */
    public void setFrequency( long frequency )
    {
        m_frequency = frequency;
    }

    /**
     * Start the background thread
     */
    public void start() throws Exception
    {
        m_keepRunning = true;
        m_monitorThread.setDaemon( true );
        m_monitorThread.setPriority( m_priority );
        m_monitorThread.start();
    }

    /**
     * Stop the background thread
     */
    public void stop() throws Exception
    {
        m_keepRunning = false;
        m_monitorThread.interrupt();
        m_monitorThread.join();
    }

    /**
     * Kick the scanner thread in motion.
     */
    public final void run()
    {
        try
        {
            while( m_keepRunning )
            {
                Thread.sleep( m_frequency );
                testModifiedAfter( System.currentTimeMillis() );
            }
        }
        catch( InterruptedException e )
        {
            Thread.interrupted();
        }
    }

    /**
     * Test if the directory has been modified after a given time
     *
     * @param time The time to use when polling for changes.
     */
    public void testModifiedAfter( final long time )
    {
        if( m_lastModified > time )
        {
            return;
        }

        final HashSet existingFiles = new HashSet();
        final HashSet modifiedFiles = new HashSet();
        final HashSet addedFiles = new HashSet();

        final File[] files = m_directory.listFiles();
        int fileCount = 0;
        if( null != files )
        {
            fileCount = files.length;
            for( int i = 0; i < files.length; i++ )
            {
                final File file = files[i];
                final long newModTime = file.lastModified();
                if( m_files.contains( file ) )
                {
                    existingFiles.add( file );
                    final Long oldModTime = (Long)m_times.get( file );
                    if( oldModTime.longValue() != newModTime )
                    {
                        modifiedFiles.add( file );
                    }
                }
                else
                {
                    addedFiles.add( file );
                }
                m_times.put( file, new Long( newModTime ) );
            }
        }

        final int lastCount = m_files.size();
        final int addedCount = addedFiles.size();
        final int modifiedCount = modifiedFiles.size();

        // If no new files have been added and
        // none deleted then nothing to do
        if( fileCount == lastCount && 0 == addedCount && 0 == modifiedCount )
        {
            return;
        }

        final HashSet deletedFiles = new HashSet();

        // If only new files were added and none were changed then
        // we don't have to scan for deleted files
        if( fileCount != lastCount + addedCount )
        {
            final Iterator iterator = m_files.iterator();
            while( iterator.hasNext() )
            {
                final File file = (File)iterator.next();
                if( !existingFiles.contains( file ) )
                {
                    deletedFiles.add( file );
                    m_times.remove( file );
                }
            }
        }

        final int deletedCount = deletedFiles.size();

        if( 0 != addedCount )
        {
            m_directoryChangeListener.directoryChange(
              DirectoryChangeListener.ADDITION, addedFiles );
        }
        if( 0 != deletedCount )
        {
            m_directoryChangeListener.directoryChange(
              DirectoryChangeListener.REMOVAL, deletedFiles );
        }
        if( 0 != modifiedCount )
        {
            m_directoryChangeListener.directoryChange(
              DirectoryChangeListener.MODIFICATION, modifiedFiles );
        }

        existingFiles.addAll( addedFiles );
        m_files = existingFiles;
    }
}
