/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.tools.tasks;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.types.ZipFileSet;

/**
 * Creates a Sar archive.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @author <a href="mailto:stefan.bodewig@epost.de">Stefan Bodewig</a>
 */
public class Sar
    extends Jar
{
    private File m_config;
    private File m_assembly;
    private File m_environment;

    public Sar()
    {
        archiveType = "sar";
        emptyBehavior = "fail";
    }

    public void setSarfile( final File file )
    {
        setDestFile( file );
    }

    public void setConfig( final File config )
    {
        m_config = config;

        if( !m_config.exists() )
        {
            final String message =
                "Config descriptor: " + m_config + " does not exist.";
            throw new BuildException( message, getLocation() );
        }

        if( !m_config.isFile() )
        {
            final String message =
                "Config descriptor: " + m_config + " is not a file.";
            throw new BuildException( message, getLocation() );
        }
    }

    public void setAssembly( final File assembly )
    {
        m_assembly = assembly;

        if( !m_assembly.exists() )
        {
            final String message =
                "Assembly descriptor: " + m_assembly + " does not exist.";
            throw new BuildException( message, getLocation() );
        }

        if( !m_assembly.isFile() )
        {
            final String message =
                "Assembly descriptor: " + m_assembly + " is not a file.";
            throw new BuildException( message, getLocation() );
        }
    }

    public void setServer( final File server )
    {
        System.err.println( "DEPRECATED: Server attribute of sar task is deprecated" );
        setEnvironment( server );
    }

    public void setEnvironment( final File environment )
    {
        m_environment = environment;

        if( !m_environment.exists() )
        {
            final String message = "Environment descriptor: " +
                m_environment + " does not exist.";
            throw new BuildException( message, getLocation() );
        }

        if( !m_environment.isFile() )
        {
            final String message = "Environment descriptor: " +
                m_environment + " is not a file.";
            throw new BuildException( message, getLocation() );
        }
    }

    public void addLib( final ZipFileSet zipFileSet )
    {
        zipFileSet.setPrefix( "SAR-INF/lib" );
        super.addFileset( zipFileSet );
    }

    public void addClasses( final ZipFileSet zipFileSet )
    {
        zipFileSet.setPrefix( "SAR-INF/classes" );
        super.addFileset( zipFileSet );
    }

    public void execute()
        throws BuildException
    {
        if( null == m_config )
        {
            final String message = "config attribute is required";
            throw new BuildException( message, getLocation() );
        }
        if( null == m_assembly )
        {
            final String message = "assembly attribute is required";
            throw new BuildException( message, getLocation() );
        }
        if( null == m_environment )
        {
            final String message = "environment attribute is required";
            throw new BuildException( message, getLocation() );
        }

        pushFile( "SAR-INF/config.xml", m_config );
        pushFile( "SAR-INF/assembly.xml", m_assembly );
        pushFile( "SAR-INF/environment.xml", m_environment );

        super.execute();
    }

    private void pushFile( final String path, final File file )
    {
        final ZipFileSet zipFileSet = new ZipFileSet();
        zipFileSet.setDir( new File( file.getParent() ) );
        zipFileSet.setIncludes( file.getName() );
        zipFileSet.setFullpath( path );
        super.addFileset( zipFileSet );
    }

    protected void cleanUp()
    {
        super.cleanUp();

        m_config = null;
        m_assembly = null;
        m_environment = null;
    }
}
