/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 *
 * This product includes software developed by the
 * Apache Software Foundation (http://www.apache.org/).
 */
package org.jcontainer.loom.components.configuration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.configuration.DefaultConfigurationSerializer;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.jcontainer.loom.components.configuration.merger.ConfigurationMerger;
import org.jcontainer.loom.components.util.PropertyUtil;
import org.jcontainer.loom.interfaces.ConfigurationRepository;
import org.realityforge.salt.i18n.ResourceManager;
import org.realityforge.salt.i18n.Resources;
import org.realityforge.salt.io.FileUtil;
import org.xml.sax.SAXException;

/**
 * <p>
 * A ConfigurationRepository that will store partial configurations on disk.
 * </p><p>
 * When a Configuration is retrieved from the repository, the configuration from disk is
 * <i>merged</i> with the configuration from the SAR. This merge is accompilished via
 * {@link ConfigurationMerger#merge}.
 * </p>
 * @author Peter Royal
 * @see ConfigurationMerger
 */
public class FileSystemPersistentConfigurationRepository
    extends AbstractLogEnabled
    implements ConfigurationRepository, Contextualizable, Configurable, Initializable
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( FileSystemPersistentConfigurationRepository.class );

    private final Map m_persistedConfigurations = new HashMap();

    private Context m_context;

    private File m_storageDirectory;
    private String m_debugPath;
    private DefaultConfigurationSerializer m_serializer;

    public void contextualize( final Context context )
        throws ContextException
    {
        m_context = context;
    }

    public void configure( final Configuration configuration ) throws ConfigurationException
    {
        m_storageDirectory = new File( constructStoragePath( configuration ) );

        try
        {
            FileUtil.forceMkdir( m_storageDirectory );
        }
        catch( IOException e )
        {
            final String message = REZ.format( "config.error.dir.invalid",
                                               m_storageDirectory );

            throw new ConfigurationException( message, e );
        }

        m_debugPath = configuration.getChild( "debug-output-path" ).getValue( null );
    }

    public Configuration processConfiguration( final String application,
                                               final String block,
                                               final Configuration configuration )
        throws ConfigurationException
    {
        final Configuration processedConfiguration =
            doProcessConfiguration( application, block, configuration );

        if( null != m_debugPath )
        {
            writeDebugConfiguration( application, block, processedConfiguration );
        }

        return processedConfiguration;
    }

    private Configuration doProcessConfiguration( final String application,
                                                  final String block,
                                                  final Configuration configuration )
        throws ConfigurationException
    {
        final Configuration persistedConfiguration =
            (Configuration)m_persistedConfigurations.get( genKey( application, block ) );

        if( null != persistedConfiguration )
        {
            return ConfigurationMerger.merge( persistedConfiguration, configuration );
        }
        else
        {
            return configuration;
        }
    }

    private String constructStoragePath( final Configuration configuration )
        throws ConfigurationException
    {
        final String path =
            configuration.getChild( "storage-directory" ).getValue( "${loom.home}/conf/apps" );

        try
        {
            final Object opath = PropertyUtil.resolveProperty( path, m_context, false );
            if( opath instanceof String )
            {
                return FileUtil.normalize( (String)opath );
            }
            else
            {
                final String message = REZ.format( "config.error.nonstring",
                                                   opath.getClass().getName() );

                throw new ConfigurationException( message );
            }
        }
        catch( Exception e )
        {
            final String message = REZ.format( "config.error.missingproperty",
                                               configuration.getLocation() );

            throw new ConfigurationException( message, e );
        }
    }

    public void initialize()
        throws Exception
    {
        loadConfigurations();

        if( null != m_debugPath )
        {
            FileUtil.forceMkdir( new File( m_debugPath ) );

            m_serializer = new DefaultConfigurationSerializer();
            m_serializer.setIndent( true );
        }
    }

    private void loadConfigurations()
        throws IOException, SAXException, ConfigurationException
    {
        final File[] apps = m_storageDirectory.listFiles( new ConfigurationDirectoryFilter() );
        for( int i = 0; i < apps.length; i++ )
        {
            loadConfigurations( apps[ i ] );
        }
    }

    private void loadConfigurations( final File appPath )
        throws IOException, SAXException, ConfigurationException
    {
        final DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
        final String app = appPath.getName();
        final File[] blocks = appPath.listFiles( new ConfigurationFileFilter() );

        for( int i = 0; i < blocks.length; i++ )
        {
            final String block =
                blocks[ i ].getName().substring( 0, blocks[ i ].getName().indexOf( ".xml" ) );

            m_persistedConfigurations.put( genKey( app, block ),
                                           builder.buildFromFile( blocks[ i ] ) );

            if( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "Loaded persistent configuration [app: " + app
                                   + ", block: " + block + "]" );
            }
        }
    }

    private String genKey( final String app, final String block )
    {
        return app + '-' + block;
    }

    private void writeDebugConfiguration( final String application,
                                          final String block,
                                          final Configuration configuration )
    {
        try
        {
            final File temp = File.createTempFile( application + "-" + block + "-",
                                                   ".xml",
                                                   new File( m_debugPath ) );

            m_serializer.serializeToFile( temp, configuration );

            if( getLogger().isDebugEnabled() )
            {
                final String message = "Configuration written at: " + temp;
                getLogger().debug( message );
            }
        }
        catch( final Exception e )
        {
            final String message = "Unable to write debug output";
            getLogger().error( message, e );
        }
    }
}
