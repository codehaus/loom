/*
 * Copyright (C) The Loom Group. All rights reserved.
 *
 * This software is published under the terms of the Loom
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.codehaus.loom.components.util.monitor;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Iterator;

import junit.framework.TestCase;

/**
 * Testcase for DirectoryScanner
 *
 * @author Johan Sjoberg
 * @version $Revision: 1.3 $ $Date: 2004-10-11 20:12:28 $
 */
public class DirectoryScannerTestCase extends TestCase
{
    /**
     * Test a completely empty directory with no changes done to it.
     */
    public void testNoChanges_1() throws Exception
    {
        File testDirectory = createTestDirectory();
        MockDirectoryChangeListener changeListener =
          new MockDirectoryChangeListener();

        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setDirectory( testDirectory.getAbsolutePath() );
        scanner.setDirectoryChangeListener( changeListener );
        scanner.testModifiedAfter( System.currentTimeMillis() );

        deleteDirectory( testDirectory );
        if( !changeListener.m_fileSet.isEmpty() &&
          !( 0 == changeListener.m_changeType ) )
        {
            fail( "Changes in were logged even if none suspected." );
        }
    }

    /**
     * Test a directory with some files in it, but with no changes
     * done to it.
     */
    public void testNoChanges_2() throws Exception
    {
        File testDirectory = createTestDirectory();
        createFile( testDirectory, "a.test" );
        modifyFile( testDirectory, "a.test" );
        createFile( testDirectory, "b.tst" );
        createFile( testDirectory, "b.test" );
        createFile( testDirectory, "c.test" );
        createFile( testDirectory, ".test" );

        MockDirectoryChangeListener changeListener =
          new MockDirectoryChangeListener();
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setDirectory( testDirectory.getAbsolutePath() );
        scanner.setDirectoryChangeListener( changeListener );
        scanner.testModifiedAfter( System.currentTimeMillis() );

        deleteDirectory( testDirectory );
        if( !changeListener.m_fileSet.isEmpty() &&
          !( 0 == changeListener.m_changeType ) )
        {
            fail( "Changes in were logged even if none suspected." );
        }
    }

    /**
     * Test addition of one file to an empty directory.
     */
    public void testAddition() throws Exception
    {
         File testDirectory = createTestDirectory();

        MockDirectoryChangeListener changeListener =
          new MockDirectoryChangeListener();
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setDirectory( testDirectory.getAbsolutePath() );
        scanner.setDirectoryChangeListener( changeListener );

        createFile( testDirectory, "a.txt" );
        modifyFile( testDirectory, "a.txt" );

        scanner.testModifiedAfter( System.currentTimeMillis() );

        deleteDirectory( testDirectory );
        if( !( 1 == changeListener.m_fileSet.size() ) &&
          !( DirectoryChangeListener.ADDITION == changeListener.m_changeType ) )
        {
            fail( "Wrong type or number of changes." );
        }
        File changedFile = (File)changeListener.m_fileSet.iterator().next();
        if( !( changedFile.getPath().endsWith( "a.txt" ) ) )
        {
            fail( "Wrong filename." );
        }
    }

    /**
     * Test removal of a file, leaving the directory empty.
     */
    public void testRemoval() throws Exception
    {
         File testDirectory = createTestDirectory();
        createFile( testDirectory, "r.txt" );
        modifyFile( testDirectory, "r.txt" );

        MockDirectoryChangeListener changeListener =
          new MockDirectoryChangeListener();
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setDirectory( testDirectory.getAbsolutePath() );
        scanner.setDirectoryChangeListener( changeListener );

        deleteFile( testDirectory, "r.txt" );
        scanner.testModifiedAfter( System.currentTimeMillis() );

        deleteDirectory( testDirectory );
        if( !( 1 == changeListener.m_fileSet.size() ) &&
          !( DirectoryChangeListener.REMOVAL == changeListener.m_changeType ) )
        {
            fail( "Wrong type or number of changes." );
        }
        File changedFile = (File)changeListener.m_fileSet.iterator().next();
        if( !( changedFile.getPath().endsWith( "r.txt" ) ) )
        {
            fail( "Wrong filename." );
        }
    }

    /**
     * Test modification of a single file.
     * <br/>
     * To keep the tested <code>DirectoryScanner</code> from getting
     * the same initial time as the change done to the file a pause of the
     * running thread is needed. 500ms should do it, but here we go to
     * sleep fo a whole second.
     */
    public void testModification() throws Exception
    {
        File testDirectory = createTestDirectory();
        createFile( testDirectory, "m.txt" );
        modifyFile( testDirectory, "m.txt" );

        MockDirectoryChangeListener changeListener =
          new MockDirectoryChangeListener();
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setDirectory( testDirectory.getAbsolutePath() );
        scanner.setDirectoryChangeListener( changeListener );

        Thread.sleep( 1000 );

        modifyFile( testDirectory, "m.txt" );
        scanner.testModifiedAfter( System.currentTimeMillis() );

        deleteDirectory( testDirectory );
        if( 1 != changeListener.m_fileSet.size() &&
          DirectoryChangeListener.MODIFICATION != changeListener.m_changeType )
        {
            fail( "Wrong type or number of changes." );
        }
        File changedFile = (File)changeListener.m_fileSet.iterator().next();
        if( !( changedFile.getPath().endsWith( "m.txt" ) ) )
        {
            fail( "Wrong filename." );
        }
    }

    /**
     * Test all operations on an already populated directory.
     * <br/>
     * The test consists of six parts; 1) creation and population
     * of a test directory, 2) creation of the DirectoryScanner to
     * test, 3) a file creation test, 4) a file modification test, 5)
     * a file removal test and 6) removal of the test directory.
     *
     * @throws Exception an any kind of error or problem.
     */
    public void testAll() throws Exception
    {
        /** Iterator used holding changed files */
        Iterator fileIterator;

        File testDirectory = createTestDirectory();
        createFile( testDirectory, "a.jar" );
        createFile( testDirectory, "a.sar" );
        createFile( testDirectory, "a.war" );
        createFile( testDirectory, "a.bar" );

        MockDirectoryChangeListener changeListener =
          new MockDirectoryChangeListener();
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setDirectory( testDirectory.getAbsolutePath() );
        scanner.setDirectoryChangeListener( changeListener );

        // Test file creation
        createFile( testDirectory, "a.txt" );
        createFile( testDirectory, "b.txt" );
        scanner.testModifiedAfter( System.currentTimeMillis() );
        if( 2 != changeListener.m_fileSet.size() &&
          DirectoryChangeListener.ADDITION != changeListener.m_changeType )
        {
            fail( "Wrong type or number of changes. Two additions expected." );
        }
        fileIterator = changeListener.m_fileSet.iterator();
        File changedFile = (File)fileIterator.next();
        if( ( changedFile.getPath().endsWith( "b.txt" ) ) )
        {
            File changedFile2 = (File)fileIterator.next();
            if( !( changedFile2.getPath().endsWith( "a.txt" ) ) )
            {
                fail( "Wrong filename in addition test, [a.txt] expected." );
            }
        }
        else if( ( changedFile.getPath().endsWith( "a.txt" ) ) )
        {
            File changedFile2 = (File)fileIterator.next();
            if( !( changedFile2.getPath().endsWith( "b.txt" ) ) )
            {
                fail( "Wrong filename in addition test, [b.txt] expected." );
            }
        }
        else
        {
            fail( "Wrong filename addition test, [a.txt] or [b.txt] expected." );
        }

        // Test file modification
        modifyFile( testDirectory, "a.txt" );
        modifyFile( testDirectory, "b.txt" );
        scanner.testModifiedAfter( System.currentTimeMillis() );
        if( 2 != changeListener.m_fileSet.size() &&
          DirectoryChangeListener.MODIFICATION != changeListener.m_changeType )
        {
            fail( "Wrong type or number of changes. Two modifications expected." );
        }
        fileIterator = changeListener.m_fileSet.iterator();
        changedFile = (File)fileIterator.next();
        if( ( changedFile.getPath().endsWith( "b.txt" ) ) )
        {
            File changedFile2 = (File)fileIterator.next();
            if( !( changedFile2.getPath().endsWith( "a.txt" ) ) )
            {
                fail( "Wrong filename in modification test, [a.txt] expected." );
            }
        }
        else if( ( changedFile.getPath().endsWith( "a.txt" ) ) )
        {
            File changedFile2 = (File)fileIterator.next();
            if( !( changedFile2.getPath().endsWith( "b.txt" ) ) )
            {
                fail( "Wrong filename in modification test, [b.txt] expected." );
            }
        }
        else
        {
            fail( "Wrong filename modification test, [a.txt] or [b.txt] expected." );
        }

        // Test file removal
        deleteFile( testDirectory, "a.txt" );
        deleteFile( testDirectory, "b.txt" );
        scanner.testModifiedAfter( System.currentTimeMillis() );
        if( 2 != changeListener.m_fileSet.size() &&
          DirectoryChangeListener.REMOVAL != changeListener.m_changeType )
        {
            fail( "Wrong type or number of changes. Two removals expected." );
        }
        fileIterator = changeListener.m_fileSet.iterator();
        changedFile = (File)fileIterator.next();
        if( ( changedFile.getPath().endsWith( "b.txt" ) ) )
        {
            File changedFile2 = (File)fileIterator.next();
            if( !( changedFile2.getPath().endsWith( "a.txt" ) ) )
            {
                fail( "Wrong filename in removal test, [a.txt] expected." );
            }
        }
        else if( ( changedFile.getPath().endsWith( "a.txt" ) ) )
        {
            File changedFile2 = (File)fileIterator.next();
            if( !( changedFile2.getPath().endsWith( "b.txt" ) ) )
            {
                fail( "Wrong filename in removal test, [b.txt] expected." );
            }
        }
        else
        {
            fail( "Wrong filename removal test, [a.txt] or [b.txt] expected." );
        }

        deleteDirectory( testDirectory );
    }


    /**
     * Create a directory for testing
     *
     * @return A newly created test directory
     */
    private File createTestDirectory() throws Exception
    {
        String tempDirectoryName = System.getProperty( "java.io.tmpdir" );
        String testDirectoryName = "loom_directory_scanner_testcase";
        if( null != tempDirectoryName )
        {
            testDirectoryName = tempDirectoryName + "/" + testDirectoryName;
        }
        int counter = 0;
        File testDirectory = new File( testDirectoryName );
        while( testDirectory.exists() )
        {
            testDirectory = new File( testDirectoryName + "-" + counter++ );
        }
        testDirectory.deleteOnExit();
        testDirectory.mkdirs();
        return testDirectory;
    }

    /**
     * Remove a test directory and all test files in it
     */
    private void deleteDirectory( File directory ) throws Exception
    {
        final File[] files = directory.listFiles();
        for( int i = 0; i < files.length; i++ )
        {
            File file = files[i];
            file.delete();
        }
        directory.delete();
    }

    /**
     * Create a test file;
     */
    private void createFile( final File directory, final String name )
        throws Exception
    {
        File newFile = new File( directory, name );
        newFile.createNewFile();
    }

    /**
     * Delete a test file
     */
    private void deleteFile( final File directory, final String name )
        throws Exception
    {
        File file = new File( directory, name );
        file.delete();
    }

    /**
     * Modify a test file
     */
    private void modifyFile( final File directory, final String name )
        throws Exception
    {
        File file = new File( directory, name );
        FileOutputStream outStream = new FileOutputStream( file, true );
        outStream.write( (byte)48 );
        outStream.close();
    }
}
