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
package org.jcontainer.loom.components.kernel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.excalibur.instrument.InstrumentManager;
import org.jcomponent.loggerstore.LoggerStore;
import org.jcontainer.dna.Active;
import org.jcontainer.dna.Composable;
import org.jcontainer.dna.ResourceLocator;
import org.jcontainer.dna.MissingResourceException;
import org.jcontainer.loom.components.util.ResourceUtil;
import org.jcontainer.loom.interfaces.ApplicationContext;
import org.jcontainer.loom.interfaces.ContainerConstants;
import org.jcontainer.loom.interfaces.Kernel;
import org.jcontainer.loom.interfaces.LoomException;
import org.jcontainer.loom.interfaces.SystemManager;
import org.realityforge.salt.i18n.ResourceManager;
import org.realityforge.salt.i18n.Resources;

/**
 * Manage the "frame" in which Applications operate.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @author <a href="mailto:proyal@apache.org">Peter Royal</a>
 */
class DefaultApplicationContext
    extends AbstractLogEnabled
    implements ApplicationContext, Composable, Active
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( DefaultApplicationContext.class );

    //Log Hierarchy for application
    private final LoggerStore m_store;

    ///ClassLoader for application
    private final ClassLoader m_classLoader;

    //InstrumentManager to register instruments with
    private InstrumentManager m_instrumentManager;

    ///Place to expose Management beans
    private SystemManager m_systemManager;

    private SystemManager m_blockManager;

    private final org.jcontainer.loom.tools.profile.PartitionProfile m_profile;
    private final File m_workDirectory;
    private final File m_homeDirectory;

    /**
     * The map containing all the named loaders.
     */
    private final Map m_loaders;

    /**
     * The kernel associate with context
     */
    private Kernel m_kernel;

    protected DefaultApplicationContext( final org.jcontainer.loom.tools.profile.PartitionProfile profile,
                                         final File homeDirectory,
                                         final File workDirectory,
                                         final ClassLoader classLoader,
                                         final LoggerStore store,
                                         final Map loaders )
    {
        if( null == profile )
        {
            throw new NullPointerException( "profile" );
        }
        if( null == classLoader )
        {
            throw new NullPointerException( "classLoader" );
        }
        if( null == store )
        {
            throw new NullPointerException( "store" );
        }
        if( null == workDirectory )
        {
            throw new NullPointerException( "workDirectory" );
        }
        if( null == homeDirectory )
        {
            throw new NullPointerException( "homeDirectory" );
        }
        m_profile = profile;
        m_classLoader = classLoader;
        m_store = store;
        m_workDirectory = workDirectory;
        m_homeDirectory = homeDirectory;
        m_loaders = loaders;
    }

    /**
     * @dna.dependency type="Kernel"
     * @dna.dependency type="InstrumentManager"
     * @dna.dependency type="SystemManager"
     */
    public void compose( final ResourceLocator locator )
        throws MissingResourceException
    {
        m_systemManager = (SystemManager)locator.
            lookup( SystemManager.class.getName() );
        m_kernel = (Kernel)locator.lookup( Kernel.class.getName() );
        m_instrumentManager = (InstrumentManager)locator.lookup( InstrumentManager.class.getName() );
    }

    public void initialize()
        throws Exception
    {
        m_blockManager = getManagementContext();
    }

    public void dispose()
        throws Exception
    {
    }

    public InputStream getResourceAsStream( final String name )
    {
        final File file =
            ResourceUtil.getFileForResource( name,
                                             getHomeDirectory(),
                                             m_workDirectory );
        if( !file.exists() )
        {
            return null;
        }
        else
        {
            try
            {
                return new FileInputStream( file );
            }
            catch( FileNotFoundException e )
            {
                //Should never happen
                return null;
            }
        }
    }

    public org.jcontainer.loom.tools.profile.PartitionProfile getPartitionProfile()
    {
        return m_profile;
    }

    public void requestShutdown()
    {
        final Thread thread = new Thread( "AppShutdown" )
        {
            public void run()
            {
                scheduleShutdown();
            }
        };
        thread.start();
    }

    private void scheduleShutdown()
    {
        try
        {
            //Sleep for a little bit so that the
            //thread that requested this method can
            //return and do whatever it needs to be
            //done
            Thread.sleep( 2 );
            m_kernel.removeApplication( getName() );
        }
        catch( Exception e )
        {
            final String message =
                REZ.format( "applicationcontext.error.noremove",
                            getName() );
            getLogger().error( message, e );
        }
    }

    public File getHomeDirectory()
    {
        return m_homeDirectory;
    }

    /**
     * Get ClassLoader for the current application.
     *
     * @return the ClassLoader
     */
    public ClassLoader getClassLoader()
    {
        return m_classLoader;
    }

    /**
     * Get logger with category for application.
     * Note that this name may not be the absolute category.
     *
     * @param category the logger category
     * @return the Logger
     */
    public Logger getLogger( final String category )
        throws Exception
    {
        return m_store.getLogger( category );
    }

    /**
     * Export specified object into management system.
     * The object is exported using specifed interface
     * and using the specified name.
     *
     * @param name the name of object to export
     * @param services the interface of object with which to export
     * @param object the actual object to export
     */
    public void exportObject( final String name,
                              final Class[] services,
                              final Object object )
        throws Exception
    {
        m_blockManager.register( name, object, services );
    }

    /**
     * Unexport specified object from management system.
     *
     * @param name the name of object to unexport
     */
    public void unexportObject( final String name )
        throws Exception
    {
        m_blockManager.unregister( name );
    }

    public ClassLoader getClassLoader( final String name )
        throws Exception
    {
        final ClassLoader classLoader = (ClassLoader)m_loaders.get( name );
        if( null == classLoader )
        {
            final String message =
                REZ.format( "applicationcontext.error.noloader",
                            name );
            throw new Exception( message );
        }
        return classLoader;
    }

    /**
     *  Returns the local SystemManager where the blocks should be registered
     *  for management.
     *
     *  TODO: context should probably be passed in by reference from the kernel
     */
    private SystemManager getManagementContext()
        throws LoomException
    {
        final SystemManager appContext =
            m_systemManager.getSubContext( null, "application" );
        return appContext.getSubContext( getName(), "block" );
    }

    /**
     * Get the instrument manager to use for this application
     *
     * @return the InstrumentManager
     */
    public InstrumentManager getInstrumentManager()
    {
        return m_instrumentManager;
    }

    /**
     * Get the name to use for the instrumentables for the specified component
     *
     * @param component the component
     * @return the name to use for Instrumentables
     */
    public String getInstrumentableName( String component )
    {
        return ContainerConstants.ROOT_INSTRUMENT_CATEGORY + "." + getName() + "." + component;
    }

    private String getName()
    {
        return m_profile.getMetaData().getName();
    }
}
