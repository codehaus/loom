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
package org.jcontainer.loom.components.monitor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.avalon.excalibur.monitor.DirectoryResource;
import org.apache.avalon.excalibur.monitor.impl.ActiveMonitor;
import org.jcontainer.dna.AbstractLogEnabled;
import org.jcontainer.dna.Active;
import org.jcontainer.dna.Composable;
import org.jcontainer.dna.Configurable;
import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.jcontainer.dna.MissingResourceException;
import org.jcontainer.dna.ResourceLocator;
import org.jcontainer.loom.components.util.ExtensionFileFilter;
import org.jcontainer.loom.interfaces.Deployer;
import org.realityforge.salt.i18n.ResourceManager;
import org.realityforge.salt.i18n.Resources;
import org.realityforge.salt.io.FileUtil;

/**
 * This class is responsible for monitoring the deployment directory and
 * deploying, undelploying or redeploying an application as necessary.
 *
 * @author Peter Donald
 * @version $Revision: 1.15 $ $Date: 2003-11-29 13:44:21 $
 */
public class DefaultDeploymentMonitor
    extends AbstractLogEnabled
    implements Configurable, Composable, Active, PropertyChangeListener
{
    private final static Resources REZ =
        ResourceManager.getPackageResources( DefaultDeploymentMonitor.class );

    private File m_appsDir;
    private ActiveMonitor m_monitor;
    private Deployer m_deployer;
    private long m_frequency;

    public void configure( Configuration configuration )
        throws ConfigurationException
    {
        m_frequency =
        configuration.getChild( "scanner-frequency" ).getValueAsLong( 1000L );
        final String appsDir =
            configuration.getChild( "base-application-directory" ).getValue();
        m_appsDir = new File( appsDir );
    }

    /**
     * @dna.dependency type="Deployer"
     */
    public void compose( final ResourceLocator locator )
        throws MissingResourceException
    {
        m_deployer = (Deployer)locator.lookup( Deployer.class.getName() );
    }

    /**
     * Start the scanner.
     */
    public void initialize()
        throws Exception
    {
        deployDefaultApplications();
        final DirectoryResource resource =
            new DirectoryResource( m_appsDir.getPath() );
        resource.addPropertyChangeListener( this );
        m_monitor = new ActiveMonitor();
        m_monitor.setFrequency( m_frequency );
        m_monitor.addResource( resource );
        m_monitor.start();
    }

    /**
     * Stop the scanner.
     */
    public void dispose()
        throws Exception
    {
        m_monitor.stop();
        m_monitor.stop();
    }

    /**
     * This method is called when the scanner detects that the contents of
     * deployment directory has changed.
     */
    public void propertyChange( final PropertyChangeEvent event )
    {
        final String name = event.getPropertyName();
        final Set newValue = (Set)event.getNewValue();
        final Set deployments = getDeployments( newValue );
        final Iterator iterator = deployments.iterator();

        if( name.equals( DirectoryResource.ADDED ) )
        {
            while( iterator.hasNext() )
            {
                final File file = (File)iterator.next();
                deployApplication( file );
            }
        }
        else if( name.equals( DirectoryResource.REMOVED ) )
        {
            while( iterator.hasNext() )
            {
                final File file = (File)iterator.next();
                undeployApplication( file );
            }
        }
        else
        {
            while( iterator.hasNext() )
            {
                final File file = (File)iterator.next();
                redeployApplication( file );
            }
        }
    }

    /**
     * Deploy application for specified file.
     *
     * @param file the application archive
     */
    private void deployApplication( final File file )
    {
        final String name =
            FileUtil.removeExtension( file.getName() );
        try
        {
            final String message =
                REZ.format( "scanner.deploy.notice",
                            name,
                            file );
            getLogger().info( message );

            m_deployer.deploy( name, file.toURL() );
        }
        catch( final Exception e )
        {
            final String message =
                REZ.format( "scanner.no-deploy.error", file, e );
            getLogger().warn( message, e );
        }
    }

    /**
     * Undeploy application for specified file.
     *
     * @param file the application archive
     */
    private void undeployApplication( final File file )
    {
        final String name =
            FileUtil.removeExtension( file.getName() );
        try
        {
            final String message =
                REZ.format( "scanner.undeploy.notice",
                            name );
            getLogger().info( message );
            m_deployer.undeploy( name );
        }
        catch( final Exception e )
        {
            final String message =
                REZ.format( "scanner.no-undeploy.error", file, e );
            getLogger().warn( message, e );
        }
    }

    /**
     * Redeploy application for specified file.
     *
     * @param file the application archive
     */
    private void redeployApplication( final File file )
    {
        final String name =
            FileUtil.removeExtension( file.getName() );
        try
        {
            final String message =
                REZ.format( "scanner.redeploy.notice",
                            name,
                            file );
            getLogger().info( message );
            m_deployer.redeploy( name, file.toURL() );
        }
        catch( final Exception e )
        {
            final String message =
                REZ.format( "scanner.no-redeploy.error", file, e );
            getLogger().warn( message, e );
        }
    }

    /**
     * Retrieve the set of files that are candidate deployments.
     */
    private Set getDeployments( final Set newValue )
    {
        final Set deployments = new HashSet();
        final Iterator iterator = newValue.iterator();
        while( iterator.hasNext() )
        {
            final File file = (File)iterator.next();
            if( isDeployment( file ) )
            {
                deployments.add( file );
            }
            else
            {
                final String message =
                    REZ.format( "scanner.skipping-file.notice", file );
                getLogger().info( message );
            }
        }
        return deployments;
    }

    /**
     * Return true if file represents a loom deployment.
     *
     * @param file the file
     */
    private boolean isDeployment( final File file )
    {
        return
            !file.isDirectory() &&
            file.getName().endsWith( ".sar" );
    }

    /**
     * The deployer is used to load the applications from the
     * default-apps-location specified in Parameters.
     *
     * @throws Exception if an error occurs
     */
    protected void deployDefaultApplications()
        throws Exception
    {
        final ExtensionFileFilter filter = new ExtensionFileFilter( ".sar" );
        final File[] files = m_appsDir.listFiles( filter );
        if( null != files )
        {
            deployFiles( files );
        }
    }

    private void deployFiles( final File[] files )
        throws Exception
    {
        Arrays.sort( files );
        for( int i = 0; i < files.length; i++ )
        {
            final File file = files[ i ];
            deployApplication( file );
        }
    }
}
