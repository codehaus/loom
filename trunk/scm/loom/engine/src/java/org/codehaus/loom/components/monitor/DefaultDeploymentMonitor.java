/* ====================================================================
 * Loom Software License, version 1.1
 *
 * Copyright (c) 2003, Loom Group. All rights reserved.
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
 * 3. Neither the name of the Loom Group nor the name "Loom" nor
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
 * Loom Loom includes code from the Apache Software Foundation
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
package org.codehaus.loom.components.monitor;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.codehaus.loom.components.util.monitor.DirectoryChangeListener;
import org.codehaus.loom.components.util.monitor.DirectoryScanner;
import org.codehaus.loom.components.util.ExtensionFileFilter;
import org.codehaus.loom.interfaces.Deployer;

import org.codehaus.spice.salt.i18n.ResourceManager;
import org.codehaus.spice.salt.i18n.Resources;
import org.codehaus.spice.salt.io.FileUtil;

import org.codehaus.dna.AbstractLogEnabled;
import org.codehaus.dna.Active;
import org.codehaus.dna.Composable;
import org.codehaus.dna.Configurable;
import org.codehaus.dna.Configuration;
import org.codehaus.dna.ConfigurationException;
import org.codehaus.dna.MissingResourceException;
import org.codehaus.dna.ResourceLocator;

/**
 * This class is responsible for monitoring the deployment directory and
 * deploying, undelploying or redeploying applicatios as necessary.
 *
 * @author Peter Donald
 * @author Johan Sjoberg
 * @version $Revision: 1.4 $ $Date: 2004-07-15 08:52:55 $
 */
public class DefaultDeploymentMonitor
    extends AbstractLogEnabled
    implements Configurable, Composable, Active, DirectoryChangeListener
{
    private final static Resources REZ =
      ResourceManager.getPackageResources( DefaultDeploymentMonitor.class );

    /** The application directory */
    private File m_appsDir;

    /** Directory scanner */
    private DirectoryScanner m_scanner;

    /** Deployer that handles applications */
    private Deployer m_deployer;

    /** Frequency to poll the directory */
    private long m_frequency;

    /**
     * Configure the class
     * <br/>
     * The parameters <code>scanner-frequency</code> and
     * <code>base-application-directory</code> are used. The latter is
     * mandatory. If no <code>scanner-frequency</code> is given it defaults
     * to 5, which means five seconds. If the value is zero or negative five
     * seconds will also be used as the default.
     *
     * @param configuration The configuration object
     */
    public void configure( Configuration configuration )
        throws ConfigurationException
    {
        m_frequency = 1000L
          * configuration.getChild( "scanner-frequency" ).getValueAsLong( 5L );
        if( m_frequency <= 0 )
        {
            m_frequency = 5000L;
        }

        final String appsDir =
          configuration.getChild( "base-application-directory" ).getValue();
        m_appsDir = new File( appsDir );
    }

    /**
     * Compose
     *
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
        m_scanner = new DirectoryScanner();
        m_scanner.setDirectory( m_appsDir.getPath() );
        m_scanner.setFrequency( m_frequency );
        m_scanner.setDirectoryChangeListener( this );
        m_scanner.start();
    }

    /**
     * Stop the scanner.
     */
    public void dispose()
        throws Exception
    {
        m_scanner.stop();
    }

    /**
     * This method is called when the scanner notices changes
     * in the deployment directory.
     *
     * @param type The type of change ( addition, removal or change )
     * @param fileSet A set of <code>File</code>s that were changed
     */
    public void directoryChange( final int type, final Set fileSet )
    {
        final Set deployments = getDeployments( fileSet );
        final Iterator iterator = deployments.iterator();
        if( getLogger().isDebugEnabled() )
        {
            final String message =
              REZ.format( "monitor.directory-change.notice",
                                new Integer( type ),
                                new Integer( fileSet.size() ),
                                new Integer( deployments.size() ) );
            getLogger().debug( message );
        }
        if( deployments.isEmpty() )
        {
            return;
        }

        if( DirectoryChangeListener.ADDITION == type )
        {
            while( iterator.hasNext() )
            {
                final File file = (File)iterator.next();
                deployApplication( file );
            }
        }
        else if( DirectoryChangeListener.REMOVAL == type )
        {
            while( iterator.hasNext() )
            {
                final File file = (File)iterator.next();
                undeployApplication( file );
            }
        }
        else if( DirectoryChangeListener.MODIFICATION == type )
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
        final String name = FileUtil.removeExtension( file.getName() );
        try
        {
            final String message =
              REZ.format( "monitor.deploy.notice", name, file );
            getLogger().info( message );
            m_deployer.deploy( name, file.toURL() );
        }
        catch( final Exception e )
        {
            final String message =
              REZ.format( "monitor.no-deploy.error", file, e );
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
        final String name = FileUtil.removeExtension( file.getName() );
        try
        {
            final String message =
                REZ.format( "monitor.undeploy.notice", name );
            getLogger().info( message );
            m_deployer.undeploy( name );
        }
        catch( final Exception e )
        {
            final String message =
                REZ.format( "monitor.no-undeploy.error", file, e );
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
              REZ.format( "monitor.redeploy.notice", name, file );
            getLogger().info( message );
            m_deployer.redeploy( name, file.toURL() );
        }
        catch( final Exception e )
        {
            final String message =
              REZ.format( "monitor.no-redeploy.error", file, e );
            getLogger().warn( message, e );
        }
    }

    /**
     * Retrieve the set of files that are candidate deployments.
     *
     * @newValue The file set to check.
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
                  REZ.format( "monitor.skipping-file.notice", file );
                getLogger().info( message );
            }
        }
        return deployments;
    }

    /**
     * Check if a file represents a loom deployment.
     *
     * @param file The file to check
     * @returns true If file represents a loom deployment, else false
     */
    private boolean isDeployment( final File file )
    {
        return !file.isDirectory() && file.getName().endsWith( ".sar" );
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

    /**
     * Deploy SAR files
     *
     * @param files A list of .sar files
     * @throws Exception if an error occurs
     */
    private void deployFiles( final File[] files )
        throws Exception
    {
        Arrays.sort( files );
        for( int i = 0; i < files.length; i++ )
        {
            final File file = files[i];
            deployApplication( file );
        }
    }
}
