/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.components.util.infobuilder;

import java.io.InputStream;
import java.util.ArrayList;
import org.codehaus.spice.salt.i18n.ResourceManager;
import org.codehaus.spice.salt.i18n.Resources;
import org.jcontainer.dna.AbstractLogEnabled;
import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.jcontainer.dna.Logger;
import org.jcontainer.loom.components.util.ConfigurationBuilder;
import org.jcontainer.loom.components.util.info.ComponentInfo;
import org.jcontainer.loom.components.util.info.DependencyDescriptor;
import org.jcontainer.loom.components.util.info.SchemaDescriptor;
import org.jcontainer.loom.components.util.info.ServiceDescriptor;
import org.xml.sax.InputSource;

/**
 * A LegacyBlockInfoReader is responsible for building {@link
 * org.jcontainer.loom.components.util.info.ComponentInfo} objects from <a
 * href="http://jakarta.apache.org/avalon/phoenix">Phoenixs</a> BlockInfo
 * descriptors. The format for descriptor is specified in the <a
 * href="package-summary.html#external">package summary</a>.
 *
 * @author Peter Donald
 * @version $Revision: 1.2 $ $Date: 2004-03-22 00:22:42 $
 */
public final class LegacyBlockInfoReader
    extends AbstractLogEnabled
    implements BlockInfoReader
{
    /**
     * I18n resources.
     */
    private static final Resources REZ = ResourceManager.getPackageResources(
        LegacyBlockInfoReader.class );
    private static final String BLOCKINFO_SCHEMA = "-//PHOENIX/Block Info DTD Version 1.0//EN";
    private final ClassLoader m_classLoader;

    public LegacyBlockInfoReader( final ClassLoader classLoader,
                                  final Logger logger )
    {
        if( null == classLoader )
        {
            throw new NullPointerException( "classLoader" );
        }
        else if( null == logger )
        {
            throw new NullPointerException( "logger" );
        }

        enableLogging( logger );

        m_classLoader = classLoader;
    }

    public ComponentInfo buildComponentInfo( final Class type )
        throws Exception
    {
        final String xinfo = type.getName().replace( '.', '/' ) + ".xinfo";
        final InputStream inputStream = m_classLoader.getResourceAsStream(
            xinfo );

        if( null == inputStream )
        {
            return null;
        }
        else
        {
            final InputSource input = new InputSource( inputStream );
            final Configuration configuration = ConfigurationBuilder.build(
                input, BLOCKINFO_SCHEMA, getLogger() );

            return build( type, configuration );
        }
    }

    /**
     * Create a {@link org.jcontainer.loom.components.util.info.ComponentInfo}
     * object for specified classname from specified configuration data.
     *
     * @param type The Components type
     * @param info the ComponentInfo configuration
     * @return the created ComponentInfo
     * @throws ConfigurationException if an error occurs
     */
    private ComponentInfo build( final Class type, final Configuration info )
        throws Exception
    {
        if( getLogger().isDebugEnabled() )
        {
            final String message = REZ.format(
                "legacy.builder.creating-info.notice", type.getName() );
            getLogger().debug( message );
        }

        final String topLevelName = info.getName();
        if( !topLevelName.equals( "blockinfo" ) )
        {
            final String message = REZ.format(
                "legacy.bad-toplevel-element.error",
                type.getName(),
                topLevelName );
            throw new ConfigurationException( message,
                                              info.getPath(),
                                              info.getLocation() );
        }

        Configuration configuration;

        final ServiceDescriptor[] services = buildServices( info );

        configuration = info.getChild( "dependencies" );
        final DependencyDescriptor[] dependencies = buildDependencies(
            type.getName(), configuration );

        if( getLogger().isDebugEnabled() )
        {
            final String message = REZ.format( "legacy.created-info.notice",
                                               type.getName(),
                                               new Integer( services.length ),
                                               new Integer(
                                                   dependencies.length ) );
            getLogger().debug( message );
        }

        configuration = info.getChild( "block" );
        final SchemaDescriptor schema = buildConfigurationSchema(
            type.getName(), configuration );

        return new ComponentInfo( type, services, dependencies, schema );
    }

    /**
     * A utility method to build a descriptor for SchemaDescriptor,
     *
     * @return the a descriptor for the SchemaDescriptor,
     */
    private SchemaDescriptor buildConfigurationSchema( final String classname,
                                                       final Configuration configuration )
    {
        final String schemaType = configuration.getChild( "schema-type" ).getValue(
            "" );
        if( "".equals( schemaType ) )
        {
            return null;
        }
        else
        {
            final String location = LegacyUtil.getSchemaLocationFor( classname );
            return new SchemaDescriptor( location, schemaType );
        }

    }

    /**
     * A utility method to build an array of {@link DependencyDescriptor}
     * objects from specified configuration and classname.
     *
     * @param classname     The classname of Component (used for logging
     *                      purposes)
     * @param configuration the dependencies configuration
     * @return the created DependencyDescriptor
     * @throws ConfigurationException if an error occurs
     */
    private DependencyDescriptor[] buildDependencies( final String classname,
                                                      final Configuration configuration )
        throws ConfigurationException
    {
        final Configuration[] elements = configuration.getChildren(
            "dependency" );
        final ArrayList dependencies = new ArrayList();

        for( int i = 0; i < elements.length; i++ )
        {
            final DependencyDescriptor dependency = buildDependency( classname,
                                                                     elements[ i ] );
            dependencies.add( dependency );
        }

        return (DependencyDescriptor[]) dependencies.toArray(
            DependencyDescriptor.EMPTY_SET );
    }

    /**
     * A utility method to build a {@link DependencyDescriptor} object from
     * specified configuraiton.
     *
     * @param classname  The classname of Component (used for logging purposes)
     * @param dependency the dependency configuration
     * @return the created DependencyDescriptor
     * @throws ConfigurationException if an error occurs
     */
    private DependencyDescriptor buildDependency( final String classname,
                                                  final Configuration dependency )
        throws ConfigurationException
    {
        final String implementationKey = dependency.getChild( "service" ).getAttribute(
            "name" );
        String key = dependency.getChild( "role" ).getValue( null );

        //default to name of service if key unspecified
        if( null == key )
        {
            key = implementationKey;
        }
        else
        {
            //If key is specified and it is the same as
            //service name then warn that it is redundent.
            if( key.equals( implementationKey ) )
            {
                final String message = REZ.format(
                    "legacy.builder.redundent-key.notice", classname, key );
                getLogger().warn( message );
            }
        }

        return new DependencyDescriptor( key, implementationKey, false );
    }

    /**
     * A utility method to build an array of {@link ServiceDescriptor} objects
     * from specified configuraiton.
     *
     * @param info the services configuration
     * @return the created ServiceDescriptor
     * @throws ConfigurationException if an error occurs
     */
    private ServiceDescriptor[] buildServices( final Configuration info )
        throws ConfigurationException
    {
        final ArrayList services = new ArrayList();

        Configuration[] elements = info.getChild( "services" ).getChildren(
            "service" );
        for( int i = 0; i < elements.length; i++ )
        {
            final ServiceDescriptor service = buildService( elements[ i ],
                                                            false );
            services.add( service );
        }
        elements =
        info.getChild( "management-access-points" ).getChildren( "service" );
        for( int i = 0; i < elements.length; i++ )
        {
            final ServiceDescriptor service = buildService( elements[ i ],
                                                            true );
            services.add( service );
        }

        return (ServiceDescriptor[]) services.toArray(
            ServiceDescriptor.EMPTY_SET );
    }

    /**
     * A utility method to build a {@link ServiceDescriptor} object from
     * specified configuraiton data.
     *
     * @param service the service Configuration
     * @return the created ServiceDescriptor
     * @throws ConfigurationException if an error occurs
     */
    private ServiceDescriptor buildService( final Configuration service,
                                            final boolean isManagement )
        throws ConfigurationException
    {
        //TODO need to do something if its a management service. Ideally this is what gets looked at later on
        //when plugging everything back in
        return new ServiceDescriptor( service.getAttribute( "name" ) );
    }
}
