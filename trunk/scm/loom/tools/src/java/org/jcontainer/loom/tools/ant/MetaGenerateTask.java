/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.tools.ant;

import com.thoughtworks.qdox.ant.AbstractQdoxTask;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.tools.ant.BuildException;
import org.jcontainer.loom.tools.info.ComponentInfo;
import org.jcontainer.loom.tools.infobuilder.InfoWriter;
import org.jcontainer.loom.tools.infobuilder.LegacyBlockInfoWriter;
import org.jcontainer.loom.tools.infobuilder.SerializedInfoWriter;
import org.jcontainer.loom.tools.infobuilder.XMLInfoWriter;
import org.jcontainer.loom.tools.qdox.DefaultInfoBuilder;
import org.jcontainer.loom.tools.qdox.LegacyInfoBuilder;

/**
 * Generate MetaData for info package from the source files.
 * See XXXXXXX for a description of the format in which the
 *
 * @author Paul Hammant
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.4 $ $Date: 2003-06-29 01:07:35 $
 */
public class MetaGenerateTask
    extends AbstractQdoxTask
{
    /*
    * A set of type codes for format.
    */
    public static final int XML_TYPE = 0;
    public static final int SER_TYPE = 1;
    public static final int LEGACY_TYPE = 2;

    /**
     * A utility object that writes out info as xml files.
     */
    private static final InfoWriter c_xmlWriter = new XMLInfoWriter();

    /**
     * A utility object that writes out info as serialized object files.
     */
    private static final InfoWriter c_serWriter = new SerializedInfoWriter();

    /**
     * A utility object that writes out info as serialized object files.
     */
    private static final InfoWriter c_legacyWriter = new LegacyBlockInfoWriter();

    /**
     * The destination directory for metadata files.
     */
    private File m_destDir;

    /**
     * Variable that indicates the output type.
     */
    private int m_format;

    /**
     * Variable that indicates whether the output
     * will be generated only when the source file is
     * newer than the destination file.
     */
    private boolean m_force = false;

    /**
     * Set the desitation directory to generate output files to.
     *
     * @param destDir The destination directory
     */
    public void setDestDir( final File destDir )
    {
        m_destDir = destDir;
    }

    /**
     * Specify the output format. Must be one of xml or serialized.
     *
     * @param format the output format
     */
    public void setFormat( final FormatEnum format )
    {
        m_format = format.getTypeCode();
    }

    /**
     * Set force to be true indicating that destination
     * files should always be regenerated.
     *
     * @param force the flag for forcing output
     */
    public void setForce( boolean force )
    {
        m_force = force;
    }

    /**
     * Execute generator task.
     */
    public void execute()
        throws BuildException
    {
        validate();

        final String message =
            "Writing Info descriptors as " + getOutputDescription() + ".";
        log( message );

        super.execute();

        try
        {
            writeInfoMetaData();
        }
        catch( final Exception e )
        {
            throw new BuildException( e.toString(), e );
        }
    }

    /**
     * Validate that the parameters are valid.
     */
    private void validate()
    {
        if( null == m_destDir )
        {
            final String message =
                "DestDir (" + m_destDir + ") not specified";
            throw new BuildException( message );
        }
        if( !m_destDir.isDirectory() )
        {
            final String message =
                "DestDir (" + m_destDir + ") is not a directory.";
            throw new BuildException( message );
        }

        if( !m_destDir.exists() && !m_destDir.mkdirs() )
        {
            final String message =
                "DestDir (" + m_destDir + ") could not be created.";
            throw new BuildException( message );
        }
    }

    /**
     * Return a description of output format to print as debug message.
     *
     * @return the output formats descriptive name
     */
    private String getOutputDescription()
    {
        if( XML_TYPE == m_format )
        {
            return "xml";
        }
        else if( SER_TYPE == m_format )
        {
            return "serialized objects";
        }
        else
        {
            return "legacy xml";
        }
    }

    /**
     * Output the metadata files.
     *
     * @throws java.io.IOException If a problem writing output
     */
    private void writeInfoMetaData() throws IOException
    {
        final int size = allClasses.size();
        for( int i = 0; i < size; i++ )
        {
            final JavaClass javaClass = (JavaClass)allClasses.get( i );
            ComponentInfo info = null;
            DocletTag tag = javaClass.getTagByName( "phoenix.component" );
            if( null != tag )
            {
                final DefaultInfoBuilder infoBuilder = new DefaultInfoBuilder();
                info = infoBuilder.buildComponentInfo( javaClass );
            }
            else
            {
                tag = javaClass.getTagByName( "phoenix:block" );
                if( null != tag )
                {
                    final LegacyInfoBuilder infoBuilder = new LegacyInfoBuilder();
                    info = infoBuilder.buildComponentInfo( javaClass );
                }
            }

            //If we have built an info object
            //then write it out
            if( null != info )
            {
                final String classname = javaClass.getFullyQualifiedName();
                final File source = javaClass.getParentSource().getFile();
                final File dest = getOutputFileForClass( classname );
                if( !m_force )
                {
                    if( dest.exists() &&
                        dest.lastModified() >= source.lastModified() )
                    {
                        continue;
                    }
                }
                final File parent = dest.getParentFile();
                if( null != parent )
                {
                    if( !parent.exists() && !parent.mkdirs() )
                    {
                        final String message =
                            "Failed to create output directory: " + parent;
                        throw new BuildException( message );
                    }
                }
                writeInfo( info );
            }
        }
    }

    /**
     * Write ComponentInfo out into a file.
     *
     * @param info the ComponentInfo object
     * @throws java.io.IOException if unable to write info out
     */
    private void writeInfo( final ComponentInfo info )
        throws IOException
    {
        final String fqn = info.getDescriptor().getImplementationKey();
        final File file = getOutputFileForClass( fqn );
        final OutputStream outputStream = new FileOutputStream( file );
        try
        {
            getInfoWriter().writeComponentInfo( info, outputStream );
        }
        catch( final Exception e )
        {
            log( "Error writing " + file + ". Cause: " + e );
        }
        finally
        {
            shutdownStream( outputStream );
        }
    }

    /**
     * Return the correct info writer depending on
     * what format the info will be output as
     *
     * @return the InfoWriter to output info with
     */
    private InfoWriter getInfoWriter()
    {
        if( XML_TYPE == m_format )
        {
            return c_xmlWriter;
        }
        else if( SER_TYPE == m_format )
        {
            return c_serWriter;
        }
        else
        {
            return c_legacyWriter;
        }
    }

    /**
     * Determine the file for specified {@link org.jcontainer.loom.tools.info.ComponentInfo}.
     *
     * @param classname the fully qualified name of file to generate
     * @return the file for info
     * @throws java.io.IOException if unable to determine base file
     */
    private File getOutputFileForClass( final String classname )
        throws IOException
    {
        String filename =
            classname.replace( '.', File.separatorChar );
        if( XML_TYPE == m_format )
        {
            filename += "-info.xml";
        }
        else if( SER_TYPE == m_format )
        {
            filename += "-info.ser";
        }
        else
        {
            filename += ".xinfo";
        }
        return new File( m_destDir, filename ).getCanonicalFile();
    }

    /**
     * Close the specified output stream and swallow any exceptions.
     *
     * @param outputStream the output stream
     */
    private void shutdownStream( final OutputStream outputStream )
    {
        if( null != outputStream )
        {
            try
            {
                outputStream.close();
            }
            catch( IOException e )
            {
            }
        }
    }

    /**
     * Return the destination directory in which files are generated.
     *
     * @return the destination directory in which files are generated.
     */
    protected final File getDestDir()
    {
        return m_destDir;
    }
}