/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.tools.infobuilder;

import java.io.InputStream;
import java.util.ArrayList;
import org.jcontainer.dna.AbstractLogEnabled;
import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.jcontainer.loom.tools.configuration.ConfigurationBuilder;
import org.jcontainer.loom.tools.info.ComponentInfo;
import org.jcontainer.loom.tools.info.DependencyDescriptor;
import org.jcontainer.loom.tools.info.SchemaDescriptor;
import org.jcontainer.loom.tools.info.ServiceDescriptor;
import org.realityforge.metaclass.model.Attribute;
import org.realityforge.salt.i18n.ResourceManager;
import org.realityforge.salt.i18n.Resources;
import org.xml.sax.InputSource;

/**
 * A BlockInfoReader is responsible for building {@link org.jcontainer.loom.tools.info.ComponentInfo}
 * objects from <a href="http://jakarta.apache.org/avalon/phoenix">Phoenixs</a>
 * BlockInfo descriptors. The format for descriptor is specified in the
 * <a href="package-summary.html#external">package summary</a>.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.7 $ $Date: 2003-10-15 02:04:51 $
 */
public final class BlockInfoReader
    extends AbstractLogEnabled
{
    /**
     * I18n resources.
     */
    private static final Resources REZ =
        ResourceManager.getPackageResources( BlockInfoReader.class );

    /**
     * Create a {@link ComponentInfo} object for specified
     * classname, in specified ClassLoader.
     *
     * @param classname The classname of Component
     * @param classLoader the ClassLoader to load info from
     * @return the created ComponentInfo
     * @throws Exception if an error occurs
     */
    public ComponentInfo buildComponentInfo( final String classname,
                                             final ClassLoader classLoader )
        throws Exception
    {
        final String xinfo = classname.replace( '.', '/' ) + ".xinfo";
        final InputStream inputStream = classLoader.getResourceAsStream( xinfo );
        if( null == inputStream )
        {
            return null;
        }
        return createComponentInfo( classname, inputStream );
    }

    /**
     * Create a {@link ComponentInfo} object for specified
     * classname, loaded from specified {@link InputStream}.
     *
     * @param implementationKey The classname of Component
     * @param inputStream the InputStream to load ComponentInfo from
     * @return the created ComponentInfo
     * @throws Exception if an error occurs
     */
    public ComponentInfo createComponentInfo( final String implementationKey,
                                              final InputStream inputStream )
        throws Exception
    {
        final InputSource input = new InputSource( inputStream );
        final Configuration configuration =
            ConfigurationBuilder.build( input, null, getLogger() );
        return build( implementationKey, configuration );
    }

    /**
     * Create a {@link ComponentInfo} object for specified classname from
     * specified configuration data.
     *
     * @param classname The classname of Component
     * @param info the ComponentInfo configuration
     * @return the created ComponentInfo
     * @throws Exception if an error occurs
     */
    private ComponentInfo build( final String classname,
                                 final Configuration info )
        throws Exception
    {
        if( getLogger().isDebugEnabled() )
        {
            final String message =
                REZ.format( "builder.creating-info.notice",
                            classname );
            getLogger().debug( message );
        }

        final String topLevelName = info.getName();
        if( !topLevelName.equals( "blockinfo" ) )
        {
            final String message =
                REZ.format( "legacy.bad-toplevel-element.error",
                            classname,
                            topLevelName );
            throw new ConfigurationException( message, info.getPath(), info.getLocation() );
        }

        Configuration configuration = null;

        final ServiceDescriptor[] services = buildServices( info );

        configuration = info.getChild( "dependencies" );
        final DependencyDescriptor[] dependencies =
            buildDependencies( classname, configuration );

        if( getLogger().isDebugEnabled() )
        {
            final String message =
                REZ.format( "legacy.created-info.notice",
                            classname,
                            new Integer( services.length ),
                            new Integer( dependencies.length ) );
            getLogger().debug( message );
        }

        configuration = info.getChild( "block" );
        final SchemaDescriptor schema = buildConfigurationSchema( classname, configuration );

        return new ComponentInfo( classname,
                                  services,
                                  dependencies, schema );
    }

    /**
     * A utility method to build a descriptor for SchemaDescriptor,
     *
     * @return the a descriptor for the SchemaDescriptor,
     */
    private SchemaDescriptor buildConfigurationSchema( final String classname,
                                                       final Configuration configuration )
    {
        final String schemaType =
            configuration.getChild( "schema-type" ).getValue( "" );
        if( "".equals( schemaType ) )
        {
            return null;
        }
        else
        {
            final String location = LegacyUtil.getSchemaLocationFor( classname );
            return new SchemaDescriptor( location, schemaType, Attribute.EMPTY_SET );
        }

    }

    /**
     * A utility method to build an array of {@link org.jcontainer.loom.tools.info.DependencyDescriptor}
     * objects from specified configuration and classname.
     *
     * @param classname The classname of Component (used for logging purposes)
     * @param configuration the dependencies configuration
     * @return the created DependencyDescriptor
     * @throws ConfigurationException if an error occurs
     */
    private DependencyDescriptor[] buildDependencies( final String classname,
                                                      final Configuration configuration )
        throws ConfigurationException
    {
        final Configuration[] elements = configuration.getChildren( "dependency" );
        final ArrayList dependencies = new ArrayList();

        for( int i = 0; i < elements.length; i++ )
        {
            final DependencyDescriptor dependency =
                buildDependency( classname, elements[ i ] );
            dependencies.add( dependency );
        }

        return (DependencyDescriptor[])dependencies.toArray( DependencyDescriptor.EMPTY_SET );
    }

    /**
     * A utility method to build a {@link org.jcontainer.loom.tools.info.DependencyDescriptor}
     * object from specified configuraiton.
     *
     * @param classname The classname of Component (used for logging purposes)
     * @param dependency the dependency configuration
     * @return the created DependencyDescriptor
     * @throws ConfigurationException if an error occurs
     */
    private DependencyDescriptor buildDependency( final String classname,
                                                  final Configuration dependency )
        throws ConfigurationException
    {
        final String implementationKey =
            dependency.getChild( "service" ).getAttribute( "name" );
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
                final String message =
                    REZ.format( "builder.redundent-key.notice",
                                classname,
                                key );
                getLogger().warn( message );
            }
        }

        return new DependencyDescriptor( key,
                                         implementationKey,
                                         false,
                                         Attribute.EMPTY_SET );
    }

    /**
     * A utility method to build an array of {@link org.jcontainer.loom.tools.info.ServiceDescriptor}
     * objects from specified configuraiton.
     *
     * @param info the services configuration
     * @return the created ServiceDescriptor
     * @throws ConfigurationException if an error occurs
     */
    private ServiceDescriptor[] buildServices( final Configuration info )
        throws ConfigurationException
    {
        final ArrayList services = new ArrayList();

        Configuration[] elements = info.getChild( "services" ).getChildren( "service" );
        for( int i = 0; i < elements.length; i++ )
        {
            final ServiceDescriptor service = buildService( elements[ i ], false );
            services.add( service );
        }
        elements = info.getChild( "management-access-points" ).getChildren( "service" );
        for( int i = 0; i < elements.length; i++ )
        {
            final ServiceDescriptor service = buildService( elements[ i ], true );
            services.add( service );
        }

        return (ServiceDescriptor[])services.toArray( ServiceDescriptor.EMPTY_SET );
    }

    /**
     * A utility method to build a {@link org.jcontainer.loom.tools.info.ServiceDescriptor}
     * object from specified configuraiton data.
     *
     * @param service the service Configuration
     * @return the created ServiceDescriptor
     * @throws ConfigurationException if an error occurs
     */
    private ServiceDescriptor buildService( final Configuration service,
                                            final boolean isManagement )
        throws ConfigurationException
    {
        final String implementationKey = service.getAttribute( "name" );
        final ArrayList attributeSet = new ArrayList();
        if( isManagement )
        {
            attributeSet.add( LegacyUtil.MX_ATTRIBUTE );
        }

        final Attribute[] attributes = (Attribute[])attributeSet.toArray( new Attribute[ attributeSet.size() ] );
        return new ServiceDescriptor( implementationKey, attributes );
    }

}
