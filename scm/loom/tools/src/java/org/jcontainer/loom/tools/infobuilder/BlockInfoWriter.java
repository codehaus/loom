/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.tools.infobuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import org.jcontainer.loom.tools.info.ComponentInfo;
import org.jcontainer.loom.tools.info.DependencyDescriptor;
import org.jcontainer.loom.tools.info.SchemaDescriptor;
import org.jcontainer.loom.tools.info.ServiceDescriptor;

/**
 * Write {@link org.jcontainer.loom.tools.info.ComponentInfo} objects to a stream as xml
 * documents in legacy BlockInfo format.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.2 $ $Date: 2003-10-06 13:07:38 $
 */
public class BlockInfoWriter
{
    /**
     * Write out info representation to xml.
     *
     * @param info the info object
     * @param outputStream the stream to write to
     * @throws java.io.IOException if unable to write xml
     */
    public void writeComponentInfo( final ComponentInfo info,
                                    final OutputStream outputStream )
        throws Exception
    {
        final Writer writer = new OutputStreamWriter( outputStream );
        writeHeader( writer );
        writeDoctype( writer );
        writer.write( "<blockinfo>" );
        writeBlock( writer, info );
        writeServices( writer, info.getServices() );
        writeMxServices( writer, info.getServices() );
        writeDependencies( writer, info.getDependencies() );
        writer.write( "</blockinfo>" );
        writer.flush();
    }

    private void writeHeader( final Writer writer )
        throws IOException
    {
        writer.write( "<?xml version=\"1.0\" ?>" );
    }

    /**
     * Write out DOCType delcaration.
     *
     * @param writer the writer
     * @throws java.io.IOException if unable to write xml
     */
    private void writeDoctype( final Writer writer )
        throws IOException
    {
        final String doctype =
            "<!DOCTYPE blockinfo " +
            "PUBLIC \"-//PHOENIX/Block Info DTD Version 1.0//EN\" " +
            "\"http://jakarta.apache.org/avalon/dtds/phoenix/blockinfo_1_0.dtd\">";
        writer.write( doctype );
    }

    /**
     * Write out xml representation of a component.
     *
     * @param writer the writer
     * @param info the component info
     * @throws java.io.IOException if unable to write xml
     */
    private void writeBlock( final Writer writer,
                             final ComponentInfo info )
        throws IOException
    {
        writer.write( "<block>\n" );
        writer.write( "  <version>1.0</version>" );

        final SchemaDescriptor schema = info.getConfigurationSchema();
        if( null != schema )
        {
            final String output =
                "  <schema-type>" + schema.getType() + "</schema-type>";
            writer.write( output );
        }

        writer.write( "</block>" );
    }

    /**
     * Write out xml representation of a set of services.
     *
     * @param writer the writer
     * @param services the services
     * @throws java.io.IOException if unable to write xml
     */
    private void writeServices( final Writer writer,
                                final ServiceDescriptor[] services )
        throws IOException
    {
        if( 0 == services.length )
        {
            return;
        }

        writer.write( "<services>" );
        for( int i = 0; i < services.length; i++ )
        {
            final ServiceDescriptor service = services[ i ];
            if( !LegacyUtil.isMxService( service ) )
            {
                writeService( writer, service.getType() );
            }
        }
        writer.write( "</services>" );
    }

    /**
     * Write out xml representation of a set of services.
     *
     * @param writer the writer
     * @param services the services
     * @throws java.io.IOException if unable to write xml
     */
    private void writeMxServices( final Writer writer,
                                  final ServiceDescriptor[] services )
        throws IOException
    {
        if( 0 == services.length )
        {
            return;
        }

        writer.write( "<management-access-points>" );
        for( int i = 0; i < services.length; i++ )
        {
            final ServiceDescriptor service = services[ i ];
            if( LegacyUtil.isMxService( service ) )
            {
                writeService( writer, service.getType() );
            }
        }
        writer.write( "</management-access-points>" );
    }

    /**
     * Write out xml representation of a set of dependencies.
     *
     * @param writer the writer
     * @param dependencies the dependencies
     * @throws java.io.IOException if unable to write xml
     */
    private void writeDependencies( final Writer writer,
                                    final DependencyDescriptor[] dependencies )
        throws IOException
    {
        if( 0 == dependencies.length )
        {
            return;
        }

        writer.write( "<dependencies>" );
        for( int i = 0; i < dependencies.length; i++ )
        {
            final DependencyDescriptor dependency = dependencies[ i ];
            if( dependency.isOptional() )
            {
                continue;
            }

            writer.write( "<dependency>" );
            final String key = dependency.getKey();
            final String type = dependency.getType();
            if( !key.equals( type ) )
            {
                writer.write( "<role>" );
                writer.write( key );
                writer.write( "</role>" );
            }
            writeService( writer, type );
            writer.write( "</dependency>" );
        }
        writer.write( "</dependencies>" );
    }

    /**
     * Write out xml representation of a service.
     *
     * @param writer the writer
     * @param type the type of the service
     * @throws java.io.IOException if unable to write xml
     */
    private void writeService( final Writer writer,
                               final String type )
        throws IOException
    {
        writer.write( "<service name=\"" );
        writer.write( type );
        writer.write( "\"/>" );
    }

}
