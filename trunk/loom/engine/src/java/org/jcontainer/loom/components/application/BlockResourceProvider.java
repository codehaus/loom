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
package org.jcontainer.loom.components.application;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.WrapperComponentManager;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.DefaultServiceManager;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.logger.Logger;
import org.apache.excalibur.instrument.InstrumentManager;
import org.jcontainer.loom.interfaces.Application;
import org.jcontainer.loom.interfaces.ApplicationContext;
import org.jcontainer.loom.tools.info.DependencyDescriptor;
import org.jcontainer.loom.components.util.lifecycle.ResourceProvider;
import org.jcontainer.loom.tools.metadata.DependencyMetaData;
import org.jcontainer.loom.tools.profile.ComponentProfile;
import org.jcontainer.loom.components.util.ConfigurationConverter;
import org.jcontainer.dna.AbstractLogEnabled;

/**
 * The accessor used to access resources for a particular
 * Block or Listener.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.9 $ $Date: 2003-10-16 05:48:07 $
 */
class BlockResourceProvider
    extends AbstractLogEnabled
    implements ResourceProvider
{
    /**
     * Context in which Blocks/Listeners operate.
     */
    private final ApplicationContext m_context;

    /**
     * The Application which this phase is associated with.
     * Required to build a ComponentManager.
     */
    private final Application m_application;

    public BlockResourceProvider( final ApplicationContext context,
                                  final Application application )
    {
        if( null == context )
        {
            throw new NullPointerException( "context" );
        }

        if( null == application )
        {
            throw new NullPointerException( "application" );
        }

        m_context = context;
        m_application = application;
    }

    /**
     * Create Block for specified entry.
     *
     * @param entry the entry
     * @return a new object
     * @throws Exception
     */
    public Object createObject( final Object entry )
        throws Exception
    {
        final ComponentProfile profile = getProfileFor( entry );
        final Class clazz = profile.getInfo().getType();
        return clazz.newInstance();
    }

    /**
     * Retrieve Logger for specified block.
     *
     * @param entry the entry representing block
     * @return the new Logger object
     * @throws Exception if an error occurs
     */
    public Logger createLogger( final Object entry )
        throws Exception
    {
        final ComponentProfile profile = getProfileFor( entry );
        final String name = profile.getMetaData().getName();
        return m_context.getLogger( name );
    }

    /**
     * Create a new InstrumentMaanger object for component.
     *
     * @param entry the entry
     * @return a new InstrumentManager object for component
     * @throws Exception if unable to create resource
     */
    public InstrumentManager createInstrumentManager( Object entry )
        throws Exception
    {
        return m_context.getInstrumentManager();
    }

    /**
     * Create a name for this components instrumentables.
     *
     * @param entry the entry
     * @return the String to use as the instrumentable name
     * @throws Exception if unable to create resource
     */
    public String createInstrumentableName( Object entry )
        throws Exception
    {
        final ComponentProfile profile = getProfileFor( entry );
        final String name = profile.getMetaData().getName();
        return m_context.getInstrumentableName( name );
    }

    /**
     * Create a BlockContext object for Block.
     *
     * @param entry the entry representing block
     * @return the created BlockContext
     */
    public Context createContext( final Object entry )
        throws Exception
    {
        final ComponentProfile profile = getProfileFor( entry );
        return new DefaultBlockContext( profile.getMetaData().getName(),
                                        m_context );
    }

    /**
     * Create a {@link ComponentManager} object for a
     * specific Block. This requires that for
     * each dependency a reference to providing Block
     * is aaqiured from the Application and placing it in
     * {@link ComponentManager} under the correct name.
     *
     * @param entry the entry representing block
     * @return the created ComponentManager
     */
    public ComponentManager createComponentManager( final Object entry )
        throws Exception
    {
        final ServiceManager serviceManager = createServiceManager( entry );
        return new WrapperComponentManager( serviceManager );
    }

    /**
     * Create a {@link ServiceManager} object for a
     * specific Block. This requires that for
     * each dependency a reference to providing Block
     * is aaqiured from the Application and placing it in
     * {@link ServiceManager} under the correct name.
     *
     * @param entry the entry representing block
     * @return the created ServiceManager
     */
    public ServiceManager createServiceManager( final Object entry )
        throws Exception
    {
        final Map serviceMap = createServiceMap( entry );
        final DefaultServiceManager manager = new DefaultServiceManager();

        final Iterator iterator = serviceMap.keySet().iterator();
        while( iterator.hasNext() )
        {
            final String key = (String)iterator.next();
            final Object value = serviceMap.get( key );
            manager.put( key, value );
        }

        return manager;
    }

    private Map createServiceMap( final Object entry )
        throws Exception
    {
        final ComponentProfile metaData = getProfileFor( entry );
        final HashMap map = new HashMap();
        final HashMap sets = new HashMap();

        final DependencyMetaData[] roles = metaData.getMetaData().getDependencies();

        for( int i = 0; i < roles.length; i++ )
        {
            final DependencyMetaData role = roles[ i ];
            final Object dependency = m_application.getBlock( role.getProviderName() );

            final String key = role.getKey();
            final DependencyDescriptor candidate =
                metaData.getInfo().getDependency( key );

            if( candidate.isArray() )
            {
                ArrayList list = (ArrayList)sets.get( key );
                if( null == list )
                {
                    list = new ArrayList();
                    sets.put( key, list );
                }

                list.add( dependency );
            }
            else if( candidate.isMap() )
            {
                HashMap smap = (HashMap)sets.get( key );
                if( null == smap )
                {
                    smap = new HashMap();
                    sets.put( key, smap );
                }

                smap.put( role.getAlias(), dependency );
            }
            else
            {
                map.put( key, dependency );
            }
        }

        final Iterator iterator = sets.keySet().iterator();
        while( iterator.hasNext() )
        {
            final String key = (String)iterator.next();
            final Object value = sets.get( key );
            if( value instanceof List )
            {
                final List list = (List)value;
                final DependencyDescriptor dependency = metaData.getInfo().getDependency( key );

                final Object[] result = toArray( list, dependency.getComponentType() );
                map.put( key, result );

                if( key.equals( dependency.getType() ) )
                {
                    final String classname =
                        "[L" + dependency.getComponentType() + ";";
                    map.put( classname, result );
                }
            }
            else
            {
                final Map smap =
                    Collections.unmodifiableMap( (Map)value );
                map.put( key, smap );
            }
        }

        return map;
    }

    /**
     * Convert specified list into array of specified type.
     * Note that the class for the type must be loaded from same
     * classloader as the elements in the list are loaded from.
     *
     * @param list the list
     * @param type the classname of type
     * @return array of objects that are in list
     * @throws ClassNotFoundException if unable to find correct type
     */
    private Object[] toArray( final List list, final String type )
        throws ClassNotFoundException
    {
        final ClassLoader classLoader =
            list.get( 0 ).getClass().getClassLoader();
        final Class clazz = classLoader.loadClass( type );
        final Object[] elements =
            (Object[])Array.newInstance( clazz, list.size() );
        return list.toArray( elements );
    }

    public Configuration createConfiguration( final Object entry )
        throws Exception
    {
        final ComponentProfile metaData = getProfileFor( entry );
        return ConfigurationConverter.toConfiguration( metaData.getMetaData().getConfiguration() );
    }

    public Parameters createParameters( final Object entry )
        throws Exception
    {
        final Configuration configuration =
            createConfiguration( entry );
        final Parameters parameters =
            Parameters.fromConfiguration( configuration );
        parameters.makeReadOnly();
        return parameters;
    }

    /**
     * Retrieve metadata for entry.
     *
     * @param entry the entry
     * @return the MetaData for entry
     */
    private ComponentProfile getProfileFor( final Object entry )
    {
        return (ComponentProfile)entry;
    }
}
