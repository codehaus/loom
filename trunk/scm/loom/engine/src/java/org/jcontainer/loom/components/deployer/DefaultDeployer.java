/* ====================================================================
 * JContainer Software License, version 1.1
 *
 * Copyright (c) 2003, JContainer Group. All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. Neither the name of the JContainer Group nor the name "Loom" nor
 *    the names of its contributors may be used to endorse or promote
 *    products derived from this software without specific prior
 *    written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * ====================================================================
 *
 * JContainer Loom includes code from the Apache Software Foundation
 *
 * ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1997-2003 The Apache Software Foundation. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *    "This product includes software developed by the
 *    Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software
 *    itself, if and wherever such third-party acknowledgments
 *    normally appear.
 *
 * 4. The names "Jakarta", "Avalon", and "Apache Software Foundation"
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */
package org.jcontainer.loom.components.deployer;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.phoenix.BlockContext;
import org.jcomponent.loggerstore.LoggerStore;
import org.jcontainer.dna.Active;
import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.jcontainer.dna.Composable;
import org.jcontainer.dna.ResourceLocator;
import org.jcontainer.dna.MissingResourceException;
import org.jcontainer.dna.impl.DefaultConfiguration;
import org.jcontainer.loom.interfaces.ClassLoaderManager;
import org.jcontainer.loom.interfaces.ClassLoaderSet;
import org.jcontainer.loom.interfaces.ConfigurationInterceptor;
import org.jcontainer.loom.interfaces.ConfigurationValidator;
import org.jcontainer.loom.interfaces.ContainerConstants;
import org.jcontainer.loom.interfaces.Deployer;
import org.jcontainer.loom.interfaces.DeployerMBean;
import org.jcontainer.loom.interfaces.Installer;
import org.jcontainer.loom.interfaces.Kernel;
import org.jcontainer.loom.interfaces.LogManager;
import org.jcontainer.loom.interfaces.LoomException;
import org.jcontainer.loom.tools.LoomToolConstants;
import org.jcontainer.loom.tools.configuration.ConfigurationBuilder;
import org.jcontainer.loom.tools.profile.ComponentProfile;
import org.jcontainer.loom.tools.profile.PartitionProfile;
import org.jcontainer.loom.tools.profile.ProfileBuilder;
import org.jcontainer.loom.tools.verifier.SarVerifier;
import org.realityforge.salt.i18n.ResourceManager;
import org.realityforge.salt.i18n.Resources;
import org.xml.sax.InputSource;

/**
 * Deploy .sar files into a kernel using this class.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 */
public class DefaultDeployer
    extends AbstractLogEnabled
    implements Deployer, Composable, Active, DeployerMBean
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( DefaultDeployer.class );

    private final SarVerifier m_verifier = new SarVerifier();
    private final ProfileBuilder m_builder = new PhoenixProfileBuilder();
    private final Map m_installations = new Hashtable();
    private LogManager m_logManager;
    private Kernel m_kernel;
    private Installer m_installer;
    private ConfigurationInterceptor m_repository;
    private ClassLoaderManager m_classLoaderManager;
    private ConfigurationValidator m_validator;

    /**
     * @dna.dependency type="Kernel"
     * @dna.dependency type="ConfigurationInterceptor"
     * @dna.dependency type="ClassLoaderManager"
     * @dna.dependency type="LogManager"
     * @dna.dependency type="ConfigurationValidator"
     * @dna.dependency type="Installer"
     */
    public void compose( final ResourceLocator locator )
        throws MissingResourceException
    {
        m_kernel = (Kernel)locator.lookup( Kernel.class.getName() );
        m_repository = (ConfigurationInterceptor)locator.
            lookup( ConfigurationInterceptor.class.getName() );
        m_classLoaderManager = (ClassLoaderManager)locator.
            lookup( ClassLoaderManager.class.getName() );
        m_logManager = (LogManager)locator.lookup( LogManager.class.getName() );
        m_validator = (ConfigurationValidator)locator.lookup( ConfigurationValidator.class.getName() );
        m_installer = (Installer)locator.lookup( Installer.class.getName() );
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
            catch( final LoomException de )
            {
                final String message =
                    REZ.format( "deploy.undeploy-indispose.error",
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
     * @throws LoomException if an error occurs
     */
    public void redeploy( final String name )
        throws LoomException
    {
        final Map installation =
            (Map)m_installations.get( name );
        if( null == installation )
        {
            final String message =
                REZ.format( "deploy.no-deployment.error", name );
            throw new LoomException( message );
        }
        try
        {
            final File source = (File)installation.get( ContainerConstants.INSTALL_SOURCE );
            redeploy( name, source.toURL() );
        }
        catch( final LoomException e )
        {
            throw e;
        }
        catch( final Exception e )
        {
            throw new LoomException( e.getMessage(), e );
        }
    }

    /**
     * Undeploy and deploy an installation.
     *
     * @param name the name of deployment
     * @param location the installation to redeploy
     * @throws LoomException if an error occurs
     */
    public void redeploy( String name, URL location )
        throws LoomException
    {
        m_kernel.lock();
        try
        {
            undeploy( name );
            deploy( name, location );
        }
        catch( final Exception e )
        {
            throw new LoomException( e.getMessage(), e );
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
     * @throws LoomException if an error occurs
     */
    public void undeploy( final String name )
        throws LoomException
    {
        final Map installation =
            (Map)m_installations.remove( name );
        if( null == installation )
        {
            final String message =
                REZ.format( "deploy.no-deployment.error", name );
            throw new LoomException( message );
        }
        try
        {
            m_kernel.removeApplication( name );
            m_installer.uninstall( installation );
        }
        catch( final Exception e )
        {
            throw new LoomException( e.getMessage(), e );
        }
    }

    /**
     * Deploy an application from an installation.
     *
     * @param name the name of application
     * @param sarURL the location to deploy from represented as String
     * @throws LoomException if an error occurs
     */
    public void deploy( final String name, final String sarURL )
        throws LoomException
    {
        try
        {
            deploy( name, new URL( sarURL ) );
        }
        catch( MalformedURLException mue )
        {
            throw new LoomException( mue.getMessage(), mue );
        }
    }

    /**
     * Deploy an application from an installation.
     *
     * @param name the name of application
     * @param location the location to deploy from
     * @throws LoomException if an error occurs
     */
    public void deploy( final String name, final URL location )
        throws LoomException
    {
        if( m_installations.containsKey( name ) )
        {
            final String message =
                REZ.format( "deploy.already-deployed.error",
                            name );
            throw new LoomException( message );
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
            final PartitionProfile profile = m_builder.buildProfile( parameters );

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
                REZ.format( "deploy.notice.sar.add",
                            name );
            getLogger().debug( message );
            success = true;
        }
        catch( final LoomException de )
        {
            throw de;
        }
        catch( final Exception e )
        {
            //From classloaderManager/kernel
            throw new LoomException( e.getMessage(), e );
        }
        finally
        {
            if( !success && null != installation )
            {
                try
                {
                    m_installer.uninstall( installation );
                }
                catch( final LoomException ie )
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
     * @throws LoomException if an error occurs
     */
    private Configuration getConfigurationFor( final Map install, final String key, final String schema )
        throws LoomException
    {
        final String location = (String)install.get( key );
        try
        {
            return ConfigurationBuilder.build( new InputSource( location ), schema, getLogger() );
        }
        catch( final Exception e )
        {
            final String message = REZ.format( "deploy.error.config.create", location );
            getLogger().error( message, e );
            throw new LoomException( message, e );
        }
    }

    /**
     * Pass the configuration to the configurationManager
     * and give it a chance to process the configuration in
     * one form or another.
     *
     * @param configuration the block configurations.
     * @throws LoomException if an error occurs
     */
    private Configuration processConfiguration( final String application,
                                                final Configuration configuration )
        throws LoomException
    {
        final DefaultConfiguration newConfiguration =
            new DefaultConfiguration( "config",
                                      configuration.getPath(),
                                      configuration.getLocation() );
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
                throw new LoomException( ce.getMessage(), ce );
            }
        }
        return newConfiguration;
    }

    /**
     * Verify that configuration present in config file is valid for this assembly.
     *
     * @param profile the PartitionProfile
     * @param config the block configurations.
     * @throws LoomException if an error occurs
     */
    private void verifyConfiguration( final PartitionProfile profile,
                                      final Configuration config )
        throws LoomException
    {
        final Configuration[] configurations = config.getChildren();
        final PartitionProfile listenerPartition =
            profile.getPartition( LoomToolConstants.LISTENER_PARTITION );
        final PartitionProfile blockPartition =
            profile.getPartition( LoomToolConstants.BLOCK_PARTITION );
        for( int i = 0; i < configurations.length; i++ )
        {
            final Configuration configuration = configurations[ i ];
            final String name = configuration.getName();
            ComponentProfile component = listenerPartition.getComponent( name );
            if( null == component )
            {
                component = blockPartition.getComponent( name );
            }
            if( null == component )
            {
                final String message =
                    REZ.format( "deploy.error.extra.config",
                                name );
                throw new LoomException( message );
            }
        }
    }

    /**
     * Verify that configuration conforms to schema for all components in this assembly.
     *
     * @param profile the PartitionProfile
     * @param classLoader the classloader application is loaded in
     * @throws LoomException if an error occurs
     */
    private void validateConfiguration( final PartitionProfile profile,
                                        final ClassLoader classLoader )
        throws LoomException
    {
        final PartitionProfile[] partitions = profile.getPartitions();
        for( int i = 0; i < partitions.length; i++ )
        {
            validateConfiguration( partitions[ i ], classLoader );
        }
        final ComponentProfile[] components = profile.getComponents();
        for( int i = 0; i < components.length; i++ )
        {
            final ComponentProfile component = components[ i ];
            boolean isValid = false;
            try
            {
                isValid = m_validator.isValid( component, classLoader );
            }
            catch( final Exception e )
            {
                getLogger().warn( e.getMessage(), e );
            }
            if( !isValid )
            {
                final String message =
                    "Unable to validate configuration of component " +
                    component.getMetaData().getName() + " of type " +
                    component.getInfo().getDescriptor().getImplementationKey();
                throw new LoomException( message );
            }
        }
    }
}
