/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.realityforge.classman.reader;

import java.util.ArrayList;
import org.apache.avalon.excalibur.extension.Extension;
import org.realityforge.classman.metadata.ClassLoaderMetaData;
import org.realityforge.classman.metadata.ClassLoaderSetMetaData;
import org.realityforge.classman.metadata.FileSetMetaData;
import org.realityforge.classman.metadata.JoinMetaData;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This class builds a {@link ClassLoaderSetMetaData} object from
 * specified configuration.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.2 $ $Date: 2003-06-27 03:45:08 $
 */
public class ClassLoaderSetReader
{
    /**
     * Build ClassLoader MetaData from a DOM tree.
     *
     * @param config the root element
     * @return the meta data
     * @throws Exception if malformed DOM
     */
    public ClassLoaderSetMetaData build( final Element config )
        throws Exception
    {
        final String defaultClassLoader =
            config.getAttribute( "default" );
        if( isUnspecified( defaultClassLoader ) )
        {
            final String message = "Default classloader not specified.";
            throw new Exception( message );
        }

        final String version =
            config.getAttribute( "version" );
        if( !"1.0".equals( version ) )
        {
            final String message = "Bad version:" + version;
            throw new Exception( message );
        }

        final NodeList joinConfigs =
            config.getElementsByTagName( "join" );
        final JoinMetaData[] joins = buildJoins( joinConfigs );

        final NodeList clConfigs =
            config.getElementsByTagName( "classloader" );

        final ClassLoaderMetaData[] classloaders =
            buildClassLoaders( clConfigs );

        final NodeList predefinedConfigs =
            config.getElementsByTagName( "predefined" );

        final String[] predefined =
            buildPredefined( predefinedConfigs );

        return new ClassLoaderSetMetaData( defaultClassLoader,
                                           predefined,
                                           classloaders,
                                           joins );
    }

    /**
     * Parse out a set of predefined classloaders from
     * specified nodes.
     *
     * @param configs the nodes to process
     * @return the predefined classloaders
     */
    private String[] buildPredefined( final NodeList configs )
    {
        final ArrayList predefines = new ArrayList();

        final int length = configs.getLength();
        for( int i = 0; i < length; i++ )
        {
            final Element element = (Element)configs.item( i );
            final String predefined = element.getAttribute( "name" );
            predefines.add( predefined );
        }

        return (String[])predefines.toArray( new String[ predefines.size() ] );
    }

    /**
     * Build an array of ClassLoader meta datas from node list.
     *
     * @param configs the nodes to process
     * @return the classloaders
     */
    private ClassLoaderMetaData[] buildClassLoaders( final NodeList configs )
        throws Exception
    {
        final ArrayList loaders = new ArrayList();
        final int length = configs.getLength();

        for( int i = 0; i < length; i++ )
        {
            final Element item = (Element)configs.item( i );
            final ClassLoaderMetaData loader = buildLoader( item );
            loaders.add( loader );
        }

        return (ClassLoaderMetaData[])loaders.toArray( new ClassLoaderMetaData[ loaders.size() ] );
    }

    /**
     * Build a ClassLoader meta datas from element.
     *
     * @param config the nodes to process
     * @return the classloader
     */
    private ClassLoaderMetaData buildLoader( final Element config )
        throws Exception
    {
        final String name = config.getAttribute( "name" );
        final String parent = config.getAttribute( "parent" );

        final String[] entrys =
            buildEntrys( config.getElementsByTagName( "entry" ) );
        final Extension[] extensions =
            buildExtensions( config.getElementsByTagName( "extension" ) );
        final FileSetMetaData[] fileSets =
            buildFileSets( config.getElementsByTagName( "fileset" ) );
        return new ClassLoaderMetaData( name, parent, entrys,
                                        extensions, fileSets );
    }

    /**
     * Build and array of extensions
     *
     * @param configs the nodes to process
     * @return an array of extensions
     */
    private Extension[] buildExtensions( final NodeList configs )
        throws Exception
    {
        final ArrayList extensions = new ArrayList();

        final int length = configs.getLength();
        for( int i = 0; i < length; i++ )
        {
            final Extension extension =
                buildExtension( (Element)configs.item( i ) );
            extensions.add( extension );
        }

        return (Extension[])extensions.toArray( new Extension[ extensions.size() ] );
    }

    /**
     * Build an extension from a DOM element.
     *
     * @param config the node to process
     * @return an extension
     */
    private Extension buildExtension( final Element config )
        throws Exception
    {
        final String name = config.getAttribute( "name" );
        if( isUnspecified( name ) )
        {
            final String message = "Missing name from extension";
            throw new Exception( message );
        }
        final String specVersion =
            config.getAttribute( "specification-version" );
        final String specVendor =
            config.getAttribute( "specification-vendor" );
        final String implVersion =
            config.getAttribute( "implementation-version" );
        final String implVendor =
            config.getAttribute( "implementation-vendor" );
        final String implVendorID =
            config.getAttribute( "implementation-vendor-id" );
        final String implURL =
            config.getAttribute( "implementation-url" );

        return new Extension( name, specVersion, specVendor,
                              implVersion, implVendor, implVendorID,
                              implURL );
    }

    /**
     * Build and array of file sets.
     *
     * @param configs the nodes to process
     * @return an array of file sets
     */
    private FileSetMetaData[] buildFileSets( final NodeList configs )
    {
        final ArrayList fileSets = new ArrayList();

        final int length = configs.getLength();
        for( int i = 0; i < length; i++ )
        {
            final FileSetMetaData fileSet =
                buildFileSet( (Element)configs.item( i ) );
            fileSets.add( fileSet );
        }

        return (FileSetMetaData[])fileSets.toArray( new FileSetMetaData[ fileSets.size() ] );
    }

    /**
     * Build a fileset from a DOM element.
     *
     * @param config the node to process
     * @return a fileset
     */
    private FileSetMetaData buildFileSet( final Element config )
    {
        final String dir = config.getAttribute( "dir" );
        final String[] includes =
            buildSelectors( config.getElementsByTagName( "include" ) );
        final String[] excludes =
            buildSelectors( config.getElementsByTagName( "exclude" ) );
        return new FileSetMetaData( dir, includes, excludes );
    }

    /**
     * Build and array of selectors (ie includes/excludes).
     *
     * @param configs the nodes to process
     * @return an array of selectors
     */
    private String[] buildSelectors( final NodeList configs )
    {
        final ArrayList selectors = new ArrayList();

        final int length = configs.getLength();
        for( int i = 0; i < length; i++ )
        {
            final Element element = (Element)configs.item( i );
            final String name = element.getAttribute( "name" );
            selectors.add( name );
        }

        return (String[])selectors.toArray( new String[ selectors.size() ] );
    }

    /**
     * Build file entrys from a nodelist.
     *
     * @param configs the nodes to procexx
     * @return an array of file entrys
     */
    private String[] buildEntrys( final NodeList configs )
    {
        final ArrayList entrys = new ArrayList();

        final int length = configs.getLength();
        for( int i = 0; i < length; i++ )
        {
            final Element config = (Element)configs.item( i );
            final String entry = config.getAttribute( "location" );
            entrys.add( entry );
        }

        return (String[])entrys.toArray( new String[ entrys.size() ] );
    }

    /**
     * Build an array of Join classloaders out of supplied nodes.
     *
     * @param configs the nodes to process
     * @return the join meta data array
     */
    private JoinMetaData[] buildJoins( final NodeList configs )
    {
        final ArrayList joins = new ArrayList();

        final int length = configs.getLength();
        for( int i = 0; i < length; i++ )
        {
            final Element config = (Element)configs.item( i );
            final JoinMetaData join = buildJoin( config );
            joins.add( join );
        }

        return (JoinMetaData[])joins.toArray( new JoinMetaData[ joins.size() ] );
    }

    /**
     * Build a Join classloader out of supplied Element.
     *
     * @param config the config element
     * @return the join meta data
     */
    private JoinMetaData buildJoin( final Element config )
    {
        final String name = config.getAttribute( "name" );
        final NodeList children =
            config.getElementsByTagName( "classloader-ref" );
        final String[] classloaders =
            buildClassLoaderRefs( children );
        return new JoinMetaData( name, classloaders );
    }

    /**
     * Create a set of strings for ClassLoader references
     * in a Join classloader.
     *
     * @param configs the nodes to process
     * @return the strings for class loader refs
     */
    private String[] buildClassLoaderRefs( final NodeList configs )
    {
        final ArrayList refs = new ArrayList();

        final int length = configs.getLength();
        for( int i = 0; i < length; i++ )
        {
            final Element element = (Element)configs.item( i );
            final String ref = element.getAttribute( "name" );
            refs.add( ref );
        }

        return (String[])refs.toArray( new String[ refs.size() ] );
    }

    /**
     * Utility class to test if attribute is unspecified (by
     * testing if it is equal to empty string).
     *
     * @param attribute the attribute
     * @return true if equal to empty string
     */
    private boolean isUnspecified( final String attribute )
    {
        return "".equals( attribute );
    }
}
