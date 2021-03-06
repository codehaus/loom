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
package org.codehaus.loom.components.util.lifecycle;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.DefaultComponentManager;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.DefaultServiceManager;
import org.apache.avalon.framework.service.ServiceManager;
import org.codehaus.loom.components.util.factory.ComponentFactory;
import org.codehaus.loom.components.util.info.ComponentInfo;
import org.codehaus.loom.components.util.metadata.ComponentTemplate;
import org.codehaus.loom.components.util.metadata.DependencyDirective;
import org.codehaus.spice.alchemist.configuration.ConfigurationAlchemist;
import org.codehaus.spice.salt.i18n.ResourceManager;
import org.codehaus.spice.salt.i18n.Resources;
import org.codehaus.dna.AbstractLogEnabled;

/**
 * This is a base object via which the {@link org.codehaus.loom.components.util.lifecycle.LifecycleHelper}
 * aquires resources for each component. This base implementation will aquire
 * components and make sure that all required components are present. It will
 * also make sure that the types of values returned from context are valid.
 *
 * <p>Note that this class assumes that the dependency graph has been validated
 * (presumably via {@link org.codehaus.loom.components.util.verifier.AssemblyVerifier}</p>
 *
 * @author Peter Donald
 * @version $Revision: 1.5 $ $Date: 2005-02-22 08:57:36 $
 */
public abstract class AbstractResourceProvider
    extends AbstractLogEnabled
    implements ResourceProvider
{
    private final static Resources REZ =
        ResourceManager.getPackageResources( AbstractResourceProvider.class );

    private final ComponentFactory m_factory;

    /**
     * Create an abstract provider which the specified ComponentFactory.
     *
     * @param factory the ComponentFactory
     */
    protected AbstractResourceProvider( final ComponentFactory factory )
    {
        if( null == factory )
        {
            throw new NullPointerException( "factory" );
        }
        m_factory = factory;
    }

    /**
     * Utility method via which the provider aquires services to place in
     * ServiceManager for a particular component.
     *
     * <p>Must be implemented in subclass.</p>
     *
     * @param name the name of service
     * @param entry the entry for component aquiring service
     * @return the service object for specified name
     */
    protected abstract Object getService( String name, Object entry );

    /**
     * Utility method via which the provider aquires a context value to place in
     * Context for a particular component.
     *
     * <p>Must be implemented in subclass.</p>
     *
     * @param name the name of service
     * @param entry the entry for component aquiring service
     * @return the context value for specified name
     */
    protected abstract Object getContextValue( String name, Object entry );

    /**
     * Create a component for a particular entry. This implementation uses the
     * associated ComponentFactory to create instance of component.
     *
     * @param entry the entry
     * @return the newly created component
     * @throws java.lang.Exception if unable to create component
     */
    public Object createObject( final Object entry )
        throws Exception
    {
        final ComponentTemplate component = getMetaData( entry );
        final String implementationKey = component.getImplementationKey();
        return m_factory.createComponent( implementationKey );
    }

    /**
     * Create a Parameters object by Creating configuration object and
     * converting that.
     *
     * @param entry the entry
     * @return a new Parameters object for component
     * @throws java.lang.Exception if unable to create resource
     */
    public Parameters createParameters( final Object entry )
        throws Exception
    {
        final ComponentTemplate component = getMetaData( entry );
        final Parameters parameters = component.getParameters();
        if( null == parameters )
        {
            final String message =
                REZ.format( "resource.missing-parameters.error",
                            component.getName() );
            throw new Exception( message );
        }
        parameters.makeReadOnly();
        return parameters;
    }

    /**
     * Create a new Configuration object for component.
     *
     * @param entry the entry
     * @return a new Configuration object for component
     * @throws java.lang.Exception if unable to create resource
     */
    public Configuration createConfiguration( final Object entry )
        throws Exception
    {
        final ComponentTemplate component = getMetaData( entry );
        final Configuration configuration =
            ConfigurationAlchemist.toAvalonConfiguration(
                component.getConfiguration() );
        if( null == configuration )
        {
            final String message =
                REZ.format( "resource.missing-configuration.error",
                            component.getName() );
            throw new Exception( message );
        }
        return configuration;
    }

    /**
     * Create a Context object that contains values specified in map. The
     * default implementation creates a basic Context object but different
     * containers may choose to overide this to provide their own subclass of
     * context.
     *
     * @param contextData the data to place in context
     * @return the Context object
     */
    protected Context createContextImpl( final Map contextData )
    {
        final DefaultContext context = new DefaultContext( contextData );
        context.makeReadOnly();
        return context;
    }

    /**
     * Return the ComponentTemplate for specified component entry. This
     * implementation assumes that entry is instance of ComponentTemplate but
     * subclasses should overide this method if this assumption does not hold
     * true.
     *
     * @param entry the component entry
     * @return the ComponentTemplate
     */
    protected ComponentTemplate getMetaData( final Object entry )
    {
        return (ComponentTemplate)entry;
    }

    /**
     * Create a context object for specified component.
     *
     * @param componentEntry the entry representing component
     * @return the created Context
     * @throws java.lang.Exception if unable to create context or entrys in
     * context
     */
    public final Context createContext( final Object componentEntry )
        throws Exception
    {
        return createContextImpl( new HashMap() );
    }

    /**
     * Create a new ComponentManager for component.
     *
     * @param entry the entry
     * @return a new ComponentManager for component
     * @throws java.lang.Exception if unable to create resource
     */
    public final ComponentManager createComponentManager( final Object entry )
        throws Exception
    {
        final Map services = createServiceMap( entry );

        final DefaultComponentManager componentManager = new DefaultComponentManager();

        final Iterator keys = services.keySet().iterator();
        while( keys.hasNext() )
        {
            final String key = (String)keys.next();
            final Object service = services.get( key );
            if( !Component.class.isInstance( service ) )
            {
                final String message =
                    REZ.format( "resource.service-not-a-component.error",
                                key,
                                service.getClass().getName() );
                throw new Exception( message );
            }
            componentManager.put( key, (Component)service );
        }

        componentManager.makeReadOnly();
        return componentManager;
    }

    /**
     * Create a new ServiceManager for component.
     *
     * @param entry the entry
     * @return a new ServiceManager for component
     * @throws Exception if unable to create resource
     */
    public final ServiceManager createServiceManager( final Object entry )
        throws Exception
    {
        final Map services = createServiceMap( entry );

        final DefaultServiceManager serviceManager = new DefaultServiceManager();

        final Iterator keys = services.keySet().iterator();
        while( keys.hasNext() )
        {
            final String key = (String)keys.next();
            final Object service = services.get( key );
            serviceManager.put( key, service );
        }

        serviceManager.makeReadOnly();
        return serviceManager;
    }

    /**
     * Accessor for component factory for sub-classes.
     *
     * @return the componentFactory associated with ResourceProvider.
     */
    protected final ComponentFactory getComponentFactory()
    {
        return m_factory;
    }

    /**
     * Create a Map of services for specified component. The map maps key name
     * to service provider.
     *
     * @param entry the component entry creating map for
     * @return the map
     * @throws java.lang.Exception if error aquiring a service to place in map
     */
    private Map createServiceMap( final Object entry )
        throws Exception
    {
        final ComponentTemplate component = getMetaData( entry );
        final String impl = component.getImplementationKey();
        final ComponentInfo info = m_factory.createInfo( impl );
        final DependencyDirective[] dependencies = component.getDependencies();

        final HashMap services = new HashMap();

        for( int i = 0; i < dependencies.length; i++ )
        {
            final DependencyDirective dependency = dependencies[ i ];
            final String key = dependency.getKey();
            final String providerName = dependency.getProviderName();
            final boolean optional = info.getDependency( key ).isOptional();

            final Object service =
                getService( providerName, entry );
            if( null == service )
            {
                final String message =
                    REZ.format( "resource.missing-dependency.error",
                                optional ? "1" : "2",
                                key,
                                component.getName() );
                if( !optional )
                {
                    throw new Exception( message );
                }
                else
                {
                    getLogger().warn( message );
                    continue;
                }
            }

            services.put( key, service );
        }

        return services;
    }
}
