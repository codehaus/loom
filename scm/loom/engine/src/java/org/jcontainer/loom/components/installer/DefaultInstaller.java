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
package org.jcontainer.loom.components.installer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.jcontainer.loom.interfaces.ContainerConstants;
import org.jcontainer.loom.interfaces.InstallationException;
import org.jcontainer.loom.interfaces.Installer;
import org.realityforge.salt.i18n.ResourceManager;
import org.realityforge.salt.i18n.Resources;
import org.realityforge.salt.io.FileUtil;
import org.realityforge.salt.io.IOUtil;

/**
 * An Installer is responsible for taking a URL for Sar
 * and installing it as appropriate.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.3 $ $Date: 2003-07-24 08:51:48 $
 */
public class DefaultInstaller
    extends AbstractLogEnabled
    implements Installer, Parameterizable, Initializable
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( DefaultInstaller.class );

    private static final String META_INF = "META-INF";
    private static final String SAR_INF = "SAR-INF";
    private static final String LIB = "SAR-INF/lib";
    private static final String CLASSES = "SAR-INF/classes/";

    //The names on the native filesystem
    private static final String FS_CONFIG_XML = "SAR-INF" + File.separator + "config.xml";
    private static final String FS_ASSEMBLY_XML = "SAR-INF" + File.separator + "assembly.xml";
    private static final String FS_ENV_XML = "SAR-INF" + File.separator + "environment.xml";
    private static final String FS_CLASSES =
        "SAR-INF" + File.separator + "classes" + File.separator;

    /**
     * The directory which is used as the base for
     * extracting all temporary files from archives. It is
     * expected that the temporary files will be deleted when
     * the .sar file is undeployed.
     */
    private File m_baseWorkDirectory;

    /**
     * The base directory in which applications are deployed.
     */
    private File m_baseDirectory;

    /**
     * Retrieve parameter that specifies work directory.
     *
     * @param parameters the parameters to read
     * @throws org.apache.avalon.framework.parameters.ParameterException if invlaid work directory
     */
    public void parameterize( final Parameters parameters )
        throws ParameterException
    {
        final String loomHome = parameters.getParameter( "loom.home" );
        final String defaultWorkDir = loomHome + File.separator + "work";
        final String defaultAppsDir = loomHome + File.separator + "apps";
        final String rawWorkDir =
            parameters.getParameter( "loom.work.dir", defaultWorkDir );
        final String rawAppsDir =
            parameters.getParameter( "loom.apps.dir", defaultAppsDir );

        final File workDir = new File( rawWorkDir );
        try
        {
            m_baseWorkDirectory = workDir.getCanonicalFile();
        }
        catch( final IOException ioe )
        {
            m_baseWorkDirectory = workDir.getAbsoluteFile();
        }

        final File appsDir = new File( rawAppsDir );
        try
        {
            m_baseDirectory = appsDir.getCanonicalFile();
        }
        catch( final IOException ioe )
        {
            m_baseDirectory = appsDir.getAbsoluteFile();
        }
    }

    public void initialize()
        throws Exception
    {
        initWorkDirectory();
        try
        {
            FileUtil.cleanDirectory( m_baseWorkDirectory );
        }
        catch( final IOException ioe )
        {
            final String message =
                REZ.format( "nodelete-workdir.error",
                            m_baseWorkDirectory,
                            ioe.getMessage() );
            getLogger().warn( message, ioe );
        }
    }

    /**
     * Uninstall the Sar designated installation.
     *
     * @param installation the installation
     * @throws InstallationException if an error occurs
     */
    public void uninstall( final Map installation )
        throws InstallationException
    {
        final File work =
            (File)installation.get( ContainerConstants.INSTALL_WORK );
        deleteWorkDir( work );
    }

    /**
     * Utility method to delete the working directory.
     *
     * @param dir the working directory
     */
    private void deleteWorkDir( final File dir )
    {
        try
        {
            FileUtil.deleteDirectory( dir );
        }
        catch( final IOException ioe )
        {
            try
            {
                //If can't delete them now (damn windows locking!)
                //then schedule them for deletion when JVM exits
                FileUtil.forceDeleteOnExit( dir );
            }
            catch( final IOException ioe2 )
            {
                //ignore
            }
            final String message =
                REZ.format( "nodelete-workdir.error",
                            dir,
                            ioe.getMessage() );
            getLogger().warn( message, ioe );
        }
    }

    /**
     * Install the Sar designated by url.
     *
     * @param url the url of instalation
     * @throws InstallationException if an error occurs
     */
    public Map install( final String name, final URL url )
        throws InstallationException
    {
        lock();
        try
        {
            final String notice = REZ.format( "installing-sar", url );
            getLogger().info( notice );

            final File file = getFileFor( url );
            if( file.isDirectory() )
            {
                final String message =
                    REZ.format( "install.sar-isa-dir.error", name, url );
                throw new InstallationException( message );
            }

            //Get Zipfile representing .sar file
            final ZipFile zipFile = new ZipFile( file );
            return installArchive( name, url, file, zipFile );
        }
        catch( final IOException ioe )
        {
            final String message = REZ.format( "bad-zip-file", url );
            throw new InstallationException( message, ioe );
        }
        finally
        {
            unlock();
        }
    }

    /**
     * Utility method to lock repository to disallow other installers to access it.
     * Currently a no-op.
     */
    private void lock()
    {
    }

    /**
     * Utility method to unlock repository to allow other installers to access it.
     * Currently a no-op.
     */
    private void unlock()
    {
    }

    /**
     * Install a new style sar.
     *
     * @param url the url designator of sar
     * @param file the file of sar
     * @param zipFile the ZipFile representing sar
     * @return the Installation object
     */
    private Map installArchive( final String name,
                                final URL url,
                                final File file,
                                final ZipFile zipFile )
        throws InstallationException
    {
        final File directory =
            new File( m_baseDirectory, name ).getAbsoluteFile();

        //Question: Should we be making sure that
        //this directory is created?
        directory.mkdirs();

        final File workDir = getRelativeWorkDir( name );
        boolean success = false;
        try
        {
            expandZipFile( zipFile, directory, workDir, url );

            //Prepare and create Installation
            final String assembly = getURLAsString( new File( directory, FS_ASSEMBLY_XML ) );
            final String config = getURLAsString( new File( directory, FS_CONFIG_XML ) );
            final String environment = getURLAsString( new File( directory, FS_ENV_XML ) );

            success = true;
            final Map install = new HashMap();
            install.put( ContainerConstants.INSTALL_SOURCE, file );
            install.put( ContainerConstants.INSTALL_HOME, directory );
            install.put( ContainerConstants.INSTALL_WORK, workDir );
            install.put( ContainerConstants.INSTALL_CONFIG, config );
            install.put( ContainerConstants.INSTALL_ASSEMBLY, assembly );
            install.put( ContainerConstants.INSTALL_ENVIRONMENT, environment );
            return install;
        }
        finally
        {
            if( !success )
            {
                deleteWorkDir( workDir );
            }
        }
    }

    /**
     * Expand the specified Zip file.
     *
     * @param zipFile the zip file
     * @param directory the directory where to extract non-jar,
     *        non-classes files
     * @param workDir the directory to extract classes/jar files
     * @param url the url of deployment (for error reporting purposes)
     * @throws InstallationException if an error occurs extracting files
     */
    private void expandZipFile( final ZipFile zipFile,
                                final File directory,
                                final File workDir,
                                final URL url )
        throws InstallationException
    {
        final Enumeration entries = zipFile.entries();
        while( entries.hasMoreElements() )
        {
            final ZipEntry entry = (ZipEntry)entries.nextElement();
            final String name = fixName( entry.getName() );

            if( name.startsWith( META_INF ) )
            {
                continue;
            }

            if( handleDirs( entry, name, directory ) )
            {
                continue;
            }

            if( handleClasses( zipFile,
                               entry,
                               name,
                               workDir ) )
            {
                continue;
            }

            if( handleJars( zipFile, entry, name, workDir ) )
            {
                continue;
            }

            //Expand the file if necesasry and issue a warning
            //if there is a file in the way
            final File destination = new File( directory, name );
            handleFile( zipFile, entry, destination, url );
        }
    }

    /**
     * Handle the extraction of normal resources
     * from zip file/
     */
    private void handleFile( final ZipFile zipFile,
                             final ZipEntry entry,
                             final File destination,
                             final URL url )
        throws InstallationException
    {
        if( !destination.exists() )
        {
            expandFile( zipFile, entry, destination );
        }
        else
        {
            final String message =
                REZ.format( "file-in-the-way",
                            url,
                            entry.getName(),
                            destination );
            getLogger().warn( message );
        }
    }

    /**
     * Handle extraction of jars.
     *
     * @param zipFile the zipFIle to exrtact from
     * @param entry the entry to extract
     * @param name the normalized name of entry
     * @param workDir the working directory to extract to
     * @return true if handled, false otherwise
     */
    private boolean handleJars( final ZipFile zipFile,
                                final ZipEntry entry,
                                final String name,
                                final File workDir )
        throws InstallationException
    {
        if( name.startsWith( LIB )
            && name.endsWith( ".jar" )
            && LIB.length() == name.lastIndexOf( "/" ) )
        {
            final File file = new File( workDir, name );
            expandFile( zipFile, entry, file );
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Handle extraction of jars.
     *
     * @param zipFile the zipFIle to exrtact from
     * @param entry the entry to extract
     * @param name the normalized name of entry
     * @param workDir the working directory to extract to
     * @return true if handled, false otherwise
     */
    private boolean handleClasses( final ZipFile zipFile,
                                   final ZipEntry entry,
                                   final String name,
                                   final File workDir )
        throws InstallationException
    {
        if( name.startsWith( CLASSES ) )
        {
            final File classDir = new File( workDir, FS_CLASSES );
            if( !classDir.exists() )
            {
                final File file = new File( workDir, name );
                expandFile( zipFile, entry, file );
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Handle expansion of dirs in the zipfile.
     *
     * @param entry the current ZipEntry
     * @param name the name of entry
     * @param directory the base directory extraacting to
     * @return true if handled, false otherwise
     */
    private boolean handleDirs( final ZipEntry entry,
                                final String name,
                                final File directory )
    {
        if( entry.isDirectory() )
        {
            if( !name.startsWith( SAR_INF ) )
            {
                final File newDir =
                    new File( directory, name );
                newDir.mkdirs();
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Create working directory inside baseWorkDir
     * for specified application.
     *
     * @param name the name of the application
     * @return the working directory for app
     */
    private File getRelativeWorkDir( final String name )
    {
        final String filename =
            name + "-" + System.currentTimeMillis();
        return new File( m_baseWorkDirectory, filename );
    }

    /**
     * Fix the specified name so that it does not start
     * with a "/" character.
     *
     * @param name the name to fix
     * @return the name stripped of initial "/" if necessary
     */
    private String fixName( final String name )
    {
        if( name.startsWith( "/" ) )
        {
            return name.substring( 1 );
        }
        else
        {
            return name;
        }
    }

    /**
     * Get File object for URL.
     * Currently it assumes that URL is a file URL but in the
     * future it will allow downloading of remote URLs thus enabling
     * a deploy from anywhere functionality.
     *
     * @param url the url of deployment
     * @return the File for deployment
     * @throws InstallationException if an error occurs
     */
    private File getFileFor( final URL url )
        throws InstallationException
    {
        if( !url.getProtocol().equals( "file" ) )
        {
            final String message = REZ.format( "install-nonlocal", url );
            throw new InstallationException( message );
        }

        File file = new File( url.getFile() );
        file = file.getAbsoluteFile();

        if( !file.exists() )
        {
            final String message = REZ.format( "install-nourl", file );
            throw new InstallationException( message );
        }

        return file;
    }

    /**
     * Expand a single zipEntry to a file.
     */
    private void expandFile( final ZipFile zipFile,
                             final ZipEntry entry,
                             final File file )
        throws InstallationException
    {
        InputStream input = null;
        OutputStream output = null;

        try
        {
            file.getParentFile().mkdirs();
            output = new FileOutputStream( file );
            input = zipFile.getInputStream( entry );
            IOUtil.copy( input, output );
        }
        catch( final IOException ioe )
        {
            final String message =
                REZ.format( "failed-to-expand",
                            entry.getName(),
                            file,
                            ioe.getMessage() );
            throw new InstallationException( message, ioe );
        }
        finally
        {
            IOUtil.shutdownStream( input );
            IOUtil.shutdownStream( output );
        }
    }

    /**
     * Utility method to extract URL from file in safe manner.
     *
     * @param file the file
     * @return the URL representation of file
     */
    private String getURLAsString( final File file )
    {
        try
        {
            return file.toURL().toExternalForm();
        }
        catch( final MalformedURLException mue )
        {
            return null;
            //should never occur
        }
    }

    /**
     * Make sure that the work directory is created and not a file.
     *
     * @throws Exception if work directory can not be created or is a file
     */
    private void initWorkDirectory()
        throws Exception
    {
        if( !m_baseWorkDirectory.exists() )
        {
            final String message =
                REZ.format( "install.create-dir.notice",
                            m_baseWorkDirectory );
            getLogger().info( message );

            if( !m_baseWorkDirectory.mkdirs() )
            {
                final String error =
                    REZ.format( "install.workdir-nocreate.error",
                                m_baseWorkDirectory );
                throw new Exception( error );
            }
        }

        if( !m_baseWorkDirectory.isDirectory() )
        {
            final String message =
                REZ.format( "install.workdir-notadir.error",
                            m_baseWorkDirectory );
            throw new Exception( message );
        }
    }
}