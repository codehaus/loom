/* ====================================================================
 * Loom Software License, version 1.1
 *
 * Copyright (c) 2003-2005, Loom Group. All rights reserved.
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
 * Loom includes code from the Apache Software Foundation
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
package org.codehaus.loom.components.configuration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.transform.stream.StreamResult;

import org.codehaus.loom.components.configuration.merger.ConfigurationMerger;
import org.codehaus.loom.components.util.ExtensionFileFilter;
import org.codehaus.loom.interfaces.ConfigurationInterceptor;
import org.codehaus.spice.salt.i18n.ResourceManager;
import org.codehaus.spice.salt.i18n.Resources;
import org.codehaus.spice.salt.io.FileUtil;
import org.codehaus.dna.AbstractLogEnabled;
import org.codehaus.dna.Active;
import org.codehaus.dna.Configurable;
import org.codehaus.dna.Configuration;
import org.codehaus.dna.ConfigurationException;
import org.codehaus.dna.impl.ConfigurationUtil;
import org.xml.sax.InputSource;

/**
 * <p> A ConfigurationInterceptor that will store partial configurations on
 * disk. </p><p> When a Configuration is retrieved from the repository, the
 * configuration from disk is <i>merged</i> with the configuration from the SAR.
 * This merge is accompilished via {@link ConfigurationMerger#merge}. </p>
 *
 * @author Peter Royal
 * @see ConfigurationMerger
 */
public class FileSystemPersistentConfigurationInterceptor
    extends AbstractLogEnabled
    implements ConfigurationInterceptor, Configurable, Active
{
    private static final Resources REZ =
        ResourceManager.getPackageResources(
            FileSystemPersistentConfigurationInterceptor.class );

    private final Map m_persistedConfigurations = new HashMap();

    private File m_storageDirectory;
    private String m_debugPath;

    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        final String path =
            configuration.getChild( "storage-directory" ).getValue();
        m_storageDirectory = new File( FileUtil.normalize( path ) );

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

        m_debugPath =
        configuration.getChild( "debug-output-path" ).getValue( null );
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
            writeDebugConfiguration( application,
                                     block,
                                     processedConfiguration );
        }

        return processedConfiguration;
    }

    private Configuration doProcessConfiguration( final String application,
                                                  final String block,
                                                  final Configuration configuration )
        throws ConfigurationException
    {
        final Configuration persistedConfiguration =
            (Configuration)m_persistedConfigurations.get(
                genKey( application, block ) );

        if( null != persistedConfiguration )
        {
            return ConfigurationMerger.merge( persistedConfiguration,
                                              configuration );
        }
        else
        {
            return configuration;
        }
    }

    public void initialize()
        throws Exception
    {
        loadConfigurations();

        if( null != m_debugPath )
        {
            FileUtil.forceMkdir( new File( m_debugPath ) );
        }
    }

    public void dispose()
        throws Exception
    {
    }

    private void loadConfigurations()
        throws Exception
    {
        final File[] apps = m_storageDirectory.listFiles(
            new ConfigurationDirectoryFilter() );
        for( int i = 0; i < apps.length; i++ )
        {
            loadConfigurations( apps[ i ] );
        }
    }

    private void loadConfigurations( final File appPath )
        throws Exception
    {
        final String app = appPath.getName();
        final File[] blocks = appPath.listFiles(
            new ExtensionFileFilter( ".xml" ) );

        for( int i = 0; i < blocks.length; i++ )
        {
            final String block =
                blocks[ i ].getName().substring( 0,
                                                 blocks[ i ].getName().indexOf(
                                                     ".xml" ) );

            final InputSource input = new InputSource(
                blocks[ i ].getAbsolutePath() );
            final Configuration configuration =
                ConfigurationUtil.buildFromXML( input );
            m_persistedConfigurations.put( genKey( app, block ),
                                           configuration );

            if( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "Loaded persistent configuration [app: " +
                                   app
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
            final File temp = File.createTempFile(
                application + "-" + block + "-",
                ".xml",
                new File( m_debugPath ) );
            final StreamResult result =
                new StreamResult( new FileOutputStream( temp ) );
            ConfigurationUtil.serializeToResult( result, configuration );

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
