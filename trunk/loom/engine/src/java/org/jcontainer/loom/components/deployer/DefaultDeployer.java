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
package org.jcontainer.loom.components.deployer;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.phoenix.BlockContext;
import org.jcontainer.loom.interfaces.ClassLoaderManager;
import org.jcontainer.loom.interfaces.ClassLoaderSet;
import org.jcontainer.loom.interfaces.ConfigurationRepository;
import org.jcontainer.loom.interfaces.ConfigurationValidator;
import org.jcontainer.loom.interfaces.ContainerConstants;
import org.jcontainer.loom.interfaces.Deployer;
import org.jcontainer.loom.interfaces.DeployerMBean;
import org.jcontainer.loom.interfaces.DeploymentException;
import org.jcontainer.loom.interfaces.InstallationException;
import org.jcontainer.loom.interfaces.Installer;
import org.jcontainer.loom.interfaces.Kernel;
import org.jcontainer.loom.interfaces.LogManager;
import org.jcontainer.loom.tools.configuration.ConfigurationBuilder;
import org.jcontainer.loom.tools.verifier.SarVerifier;
import org.realityforge.loggerstore.LoggerStore;
import org.xml.sax.InputSource;

/**
 * Deploy .sar files into a kernel using this class.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 */
public class DefaultDeployer
    extends AbstractLogEnabled
    implements Deployer, Serviceable, Initializable, Disposable, DeployerMBean
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( DefaultDeployer.class );

    private final SarVerifier m_verifier = new SarVerifier();
    private final org.jcontainer.loom.tools.profile.ProfileBuilder m_builder = new PhoenixProfileBuilder();
    private final Map m_installations = new Hashtable();
    private LogManager m_logManager;
    private Kernel m_kernel;
    private Installer m_installer;
    private ConfigurationRepository m_repository;
    private ClassLoaderManager m_classLoaderManager;
    private ConfigurationValidator m_validator;

    /**
     * Retrieve relevant services needed to deploy.
     *
     * @param serviceManager the ComponentManager
     * @throws ServiceException if an error occurs
     */
    public void service( final ServiceManager serviceManager )
        throws ServiceException
    {
        m_kernel = (Kernel)serviceManager.lookup( Kernel.ROLE );
        m_repository = (ConfigurationRepository)serviceManager.
            lookup( ConfigurationRepository.ROLE );
        m_classLoaderManager = (ClassLoaderManager)serviceManager.
            lookup( ClassLoaderManager.ROLE );
        m_logManager = (LogManager)serviceManager.lookup( LogManager.ROLE );
        m_validator = (ConfigurationValidator)serviceManager.lookup( ConfigurationValidator.ROLE );
        m_installer = (Installer)serviceManager.lookup( Installer.ROLE );
    }

    public void initialize()
        throws Exception
    {
        setupLogger( m_builder );
        setupLogger( m_verifier );
    }

    /**
     * Dispose the dpeloyer which effectively means undeploying
     * all the currently deployed apps.
     */
    public void dispose()
    {
        final Set set = m_installations.keySet();
        final String[] applications =
            (String[])set.toArray( new String[ set.size() ] );
        for( int i = 0; i < applications.length; i++ )
        {
            final String name = applications[ i ];
            try
            {
                undeploy( name );
            }
            catch( final DeploymentException de )
            {
                final String message =
                    REZ.getString( "deploy.undeploy-indispose.error",
                                   name,
                                   de.getMessage() );
                getLogger().error( message, de );
            }
        }
    }

    /**
     * Redeploy an application.
     *
     * @param name the name of deployment
     * @throws DeploymentException if an error occurs
     */
    public void redeploy( final String name )
        throws DeploymentException
    {
        final Map installation =
            (Map)m_installations.get( name );
        if( null == installation )
        {
            final String message =
                REZ.getString( "deploy.no-deployment.error", name );
            throw new DeploymentException( message );
        }
        try
        {
            final File source = (File)installation.get( ContainerConstants.INSTALL_SOURCE );
            redeploy( name, source.toURL() );
        }
        catch( final DeploymentException e )
        {
            throw e;
        }
        catch( final Exception e )
        {
            throw new DeploymentException( e.getMessage(), e );
        }
    }

    /**
     * Undeploy and deploy an installation.
     *
     * @param name the name of deployment
     * @param location the installation to redeploy
     * @throws DeploymentException if an error occurs
     */
    public void redeploy( String name, URL location )
        throws DeploymentException
    {
        m_kernel.lock();
        try
        {
            undeploy( name );
            deploy( name, location );
        }
        catch( final Exception e )
        {
            throw new DeploymentException( e.getMessage(), e );
        }
        finally
        {
            m_kernel.unlock();
        }
    }

    /**
     * Undeploy an application.
     *
     * @param name the name of deployment
     * @throws DeploymentException if an error occurs
     */
    public void undeploy( final String name )
        throws DeploymentException
    {
        final Map installation =
            (Map)m_installations.remove( name );
        if( null == installation )
        {
            final String message =
                REZ.getString( "deploy.no-deployment.error", name );
            throw new DeploymentException( message );
        }
        try
        {
            m_kernel.removeApplication( name );
            m_installer.uninstall( installation );
        }
        catch( final Exception e )
        {
            throw new DeploymentException( e.getMessage(), e );
        }
    }

    /**
     * Deploy an application from an installation.
     *
     * @param name the name of application
     * @param sarURL the location to deploy from represented as String
     * @throws DeploymentException if an error occurs
     */
    public void deploy( final String name, final String sarURL )
        throws DeploymentException
    {
        try
        {
            deploy( name, new URL( sarURL ) );
        }
        catch( MalformedURLException mue )
        {
            throw new DeploymentException( mue.getMessage(), mue );
        }
    }

    /**
     * Deploy an application from an installation.
     *
     * @param name the name of application
     * @param location the location to deploy from
     * @throws DeploymentException if an error occurs
     */
    public void deploy( final String name, final URL location )
        throws DeploymentException
    {
        if( m_installations.containsKey( name ) )
        {
            final String message =
                REZ.getString( "deploy.already-deployed.error",
                               name );
            throw new DeploymentException( message );
        }

        /*
         * Clear all the reosurces out of ResourceManager cache
         * so that reloaded applications will have their i18n bundles
         * reloaded.
         */
        ResourceManager.clearResourceCache();

        Map installation = null;
        boolean success = false;
        try
        {
            //m_baseWorkDirectory
            installation = m_installer.install( name, location );

            final Configuration config =
                getConfigurationFor( installation, ContainerConstants.INSTALL_CONFIG, null );
            final Configuration environment =
                getConfigurationFor( installation, ContainerConstants.INSTALL_ENVIRONMENT, null );
            final Configuration assembly =
                getConfigurationFor( installation,
                                     ContainerConstants.INSTALL_ASSEMBLY,
                                     ConfigurationBuilder.ASSEMBLY_SCHEMA );

            final File homeDirectory =
                (File)installation.get( ContainerConstants.INSTALL_HOME );
            final File workDirectory =
                (File)installation.get( ContainerConstants.INSTALL_WORK );

            final Map data = new HashMap();
            data.put( BlockContext.APP_NAME, name );
            data.put( BlockContext.APP_HOME_DIR, homeDirectory );

            final Configuration logs = environment.getChild( "logs", false );
            //Load hierarchy before classloader placed in context as
            //that way the logFactory will not try to use the application
            //specific classloader to load the targets which will cause
            //CastClassExceptions
            final LoggerStore store =
                m_logManager.createHierarchy( logs, homeDirectory, workDirectory, data );

            final ClassLoaderSet classLoaderSet =
                m_classLoaderManager.createClassLoaderSet( environment,
                                                           data,
                                                           homeDirectory,
                                                           workDirectory );
            final ClassLoader classLoader = classLoaderSet.getDefaultClassLoader();

            final Configuration newConfig = processConfiguration( name, config );

            final Map parameters = new HashMap();
            parameters.put( ContainerConstants.ASSEMBLY_NAME, name );
            parameters.put( ContainerConstants.ASSEMBLY_DESCRIPTOR, assembly );
            parameters.put( ContainerConstants.CONFIG_DESCRIPTOR, newConfig );
            parameters.put( ContainerConstants.ASSEMBLY_CLASSLOADER, classLoader );

            //assemble all the blocks for application
            final org.jcontainer.loom.tools.profile.PartitionProfile profile = m_builder.buildProfile( parameters );

            m_verifier.verifySar( profile, classLoader );

            //Setup configuration for all the applications blocks
            verifyConfiguration( profile, newConfig );

            validateConfiguration( profile, classLoader );

            //Finally add application to kernel
            m_kernel.addApplication( profile,
                                     homeDirectory,
                                     workDirectory,
                                     classLoader,
                                     store,
                                     classLoaderSet.getClassLoaders() );

            m_installations.put( name, installation );

            final String message =
                REZ.getString( "deploy.notice.sar.add",
                               name );
            getLogger().debug( message );
            success = true;
        }
        catch( final DeploymentException de )
        {
            throw de;
        }
        catch( final Exception e )
        {
            //From classloaderManager/kernel
            throw new DeploymentException( e.getMessage(), e );
        }
        finally
        {
            if( !success && null != installation )
            {
                try
                {
                    m_installer.uninstall( installation );
                }
                catch( final InstallationException ie )
                {
                    getLogger().error( ie.getMessage(), ie );
                }
            }
        }
    }

    /**
     * Helper method to load configuration data.
     *
     * @param install the install data
     * @param key the key under which config data is stored in install data
     * @return the Configuration
     * @throws DeploymentException if an error occurs
     */
    private Configuration getConfigurationFor( final Map install, final String key, final String schema )
        throws DeploymentException
    {
        final String location = (String)install.get( key );
        try
        {
            return ConfigurationBuilder.build( new InputSource( location ), schema, getLogger() );
        }
        catch( final Exception e )
        {
            final String message = REZ.getString( "deploy.error.config.create", location );
            getLogger().error( message, e );
            throw new DeploymentException( message, e );
        }
    }

    /**
     * Pass the configuration to the configurationManager
     * and give it a chance to process the configuration in
     * one form or another.
     *
     * @param configuration the block configurations.
     * @throws DeploymentException if an error occurs
     */
    private Configuration processConfiguration( final String application,
                                                final Configuration configuration )
        throws DeploymentException
    {
        final DefaultConfiguration newConfiguration = new DefaultConfiguration( "config" );
        final Configuration[] configurations = configuration.getChildren();
        for( int i = 0; i < configurations.length; i++ )
        {
            final Configuration config = configurations[ i ];
            try
            {
                final Configuration newConfig =
                    m_repository.processConfiguration( application,
                                                       config.getName(),
                                                       config );
                newConfiguration.addChild( newConfig );
            }
            catch( final ConfigurationException ce )
            {
                throw new DeploymentException( ce.getMessage(), ce );
            }
        }
        return newConfiguration;
    }

    /**
     * Verify that configuration present in config file is valid for this assembly.
     *
     * @param profile the PartitionProfile
     * @param config the block configurations.
     * @throws DeploymentException if an error occurs
     */
    private void verifyConfiguration( final org.jcontainer.loom.tools.profile.PartitionProfile profile,
                                      final Configuration config )
        throws DeploymentException
    {
        final Configuration[] configurations = config.getChildren();
        final org.jcontainer.loom.tools.profile.PartitionProfile listenerPartition =
            profile.getPartition( ContainerConstants.LISTENER_PARTITION );
        final org.jcontainer.loom.tools.profile.PartitionProfile blockPartition =
            profile.getPartition( ContainerConstants.BLOCK_PARTITION );
        for( int i = 0; i < configurations.length; i++ )
        {
            final Configuration configuration = configurations[ i ];
            final String name = configuration.getName();
            org.jcontainer.loom.tools.profile.ComponentProfile component = listenerPartition.getComponent( name );
            if( null == component )
            {
                component = blockPartition.getComponent( name );
            }
            if( null == component )
            {
                final String message =
                    REZ.getString( "deploy.error.extra.config",
                                   name );
                throw new DeploymentException( message );
            }
        }
    }

    /**
     * Verify that configuration conforms to schema for all components in this assembly.
     *
     * @param profile the PartitionProfile
     * @param classLoader the classloader application is loaded in
     * @throws DeploymentException if an error occurs
     */
    private void validateConfiguration( final org.jcontainer.loom.tools.profile.PartitionProfile profile,
                                        final ClassLoader classLoader )
        throws DeploymentException
    {
        final org.jcontainer.loom.tools.profile.PartitionProfile[] partitions = profile.getPartitions();
        for( int i = 0; i < partitions.length; i++ )
        {
            validateConfiguration( partitions[ i ], classLoader );
        }
        final org.jcontainer.loom.tools.profile.ComponentProfile[] components = profile.getComponents();
        for( int i = 0; i < components.length; i++ )
        {
            final org.jcontainer.loom.tools.profile.ComponentProfile component = components[ i ];
            boolean isValid = false;
            try
            {
                isValid = m_validator.isValid( component, classLoader );
            }
            catch( ConfigurationException e )
            {
                getLogger().warn( e.getMessage(), e );
            }
            if( !isValid )
            {
                final String message =
                    "Unable to validate configuration of component " +
                    component.getMetaData().getName() + " of type " +
                    component.getInfo().getDescriptor().getImplementationKey();
                throw new DeploymentException( message );
            }
        }
    }

}
