/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 *
 * This product includes software developed by the
�*�Apache Software Foundation (http://www.apache.org/).
 */
package org.jcontainer.loom.tools.lifecycle;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.DefaultComponentManager;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.DefaultServiceManager;
import org.apache.avalon.framework.service.ServiceManager;
import org.jcontainer.loom.tools.factory.ComponentBundle;
import org.jcontainer.loom.tools.factory.ComponentFactory;
import org.jcontainer.loom.tools.lifecycle.ResourceProvider;
import org.jcontainer.loom.tools.metadata.ComponentMetaData;
import org.jcontainer.loom.tools.metadata.DependencyMetaData;
import org.jcontainer.loom.tools.info.ComponentInfo;
import org.jcontainer.loom.tools.info.ContextDescriptor;
import org.jcontainer.loom.tools.info.EntryDescriptor;
import org.realityforge.salt.i18n.Resources;
import org.realityforge.salt.i18n.ResourceManager;

/**
 * This is a base object via which the
 * {@link org.jcontainer.loom.tools.lifecycle.LifecycleHelper}
 * aquires resources for each component. This base implementation
 * will aquire components and make sure that all required
 * components are present. It will also make sure that the types
 * of values returned from context are valid.
 *
 * <p>Note that this class assumes that the dependency graph
 * has been validated (presumably via
 * {@link org.jcontainer.loom.tools.verifier.AssemblyVerifier}</p>
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.2 $ $Date: 2003-07-03 06:35:49 $
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
     * Utility method via which the provider aquires services to place
     * in ServiceManager for a particular component.
     *
     * <p>Must be implemented in subclass.</p>
     *
     * @param name the name of service
     * @param entry the entry for component aquiring service
     * @return the service object for specified name
     */
    protected abstract Object getService( String name, Object entry );

    /**
     * Utility method via which the provider aquires a context value
     * to place in Context for a particular component.
     *
     * <p>Must be implemented in subclass.</p>
     *
     * @param name the name of service
     * @param entry the entry for component aquiring service
     * @return the context value for specified name
     */
    protected abstract Object getContextValue( String name, Object entry );

    /**
     * Create a component for a particular entry.
     * This implementation uses the associated
     * {@link org.jcontainer.loom.tools.factory.ComponentFactory} to create instance of
     * component.
     *
     * @param entry the entry
     * @return the newly created component
     * @throws java.lang.Exception if unable to create component
     */
    public Object createObject( final Object entry )
        throws Exception
    {
        final ComponentMetaData component = getMetaData( entry );
        final String implementationKey = component.getImplementationKey();
        return m_factory.createComponent( implementationKey );
    }

    /**
     * Create a Parameters object by Creating configuration object and converting that.
     *
     * @param entry the entry
     * @return a new Parameters object for component
     * @throws java.lang.Exception if unable to create resource
     */
    public Parameters createParameters( final Object entry )
        throws Exception
    {
        final ComponentMetaData component = getMetaData( entry );
        final Parameters parameters = component.getParameters();
        if( null == parameters )
        {
            final String message =
                REZ.getString( "resource.missing-parameters.error",
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
        final ComponentMetaData component = getMetaData( entry );
        final Configuration configuration = component.getConfiguration();
        if( null == configuration )
        {
            final String message =
                REZ.getString( "resource.missing-configuration.error",
                               component.getName() );
            throw new Exception( message );
        }
        return configuration;
    }

    /**
     * Create a {@link org.apache.avalon.framework.context.Context} object that contains values specified in map.
     * The default implementation creates a basic Context object but different
     * containers may choose to overide this to provide their own subclass of context.
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
     * Return the {@link org.jcontainer.loom.tools.metadata.ComponentMetaData} for specified component entry.
     * This implementation assumes that entry is instance of {@link org.jcontainer.loom.tools.metadata.ComponentMetaData}
     * but subclasses should overide this method if this assumption does not hold true.
     *
     * @param entry the component entry
     * @return the ComponentMetaData
     */
    protected ComponentMetaData getMetaData( final Object entry )
    {
        return (ComponentMetaData)entry;
    }

    /**
     * Create a context object for specified component.
     *
     * @param componentEntry the entry representing component
     * @return the created Context
     * @throws java.lang.Exception if unable to create context or entrys in context
     */
    public final Context createContext( final Object componentEntry )
        throws Exception
    {
        final ComponentMetaData component = getMetaData( componentEntry );
        final String componentName = component.getName();
        final String impl = component.getImplementationKey();
        final ComponentBundle bundle = m_factory.createBundle( impl );
        final ComponentInfo info = bundle.getComponentInfo();
        final ContextDescriptor descriptor = info.getContext();

        final Map contextData = new HashMap();

        final EntryDescriptor[] entrys = descriptor.getEntrys();
        for( int i = 0; i < entrys.length; i++ )
        {
            final EntryDescriptor entry = entrys[ i ];
            final String key = entry.getKey();
            final String type = entry.getType();
            final boolean optional = entry.isOptional();
            final Object value =
                getContextValue( key, componentEntry );

            if( null == value )
            {
                final String message =
                    REZ.format( "resource.missing-context-value.error",
                                optional ? "1" : "2",
                                key,
                                componentName );
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

            final boolean typeValid = objectImplementsType( value, type );
            if( !typeValid )
            {
                final String message =
                    REZ.format( "resource.bad-value-type.error",
                                optional ? "1" : "2",
                                key,
                                componentName,
                                type,
                                value.getClass().getName() );
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

            contextData.put( key, value );
        }

        final Context context = createContextImpl( contextData );
        final String classname = descriptor.getType();

        final boolean validContextClass = objectImplementsType( context, classname );
        if( !validContextClass )
        {
            final String message =
                REZ.format( "resource.bad-context-type.error",
                            classname,
                            context.getClass().getName(),
                            componentName );
            throw new Exception( message );
        }

        return context;
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
     * @throws java.lang.Exception if unable to create resource
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
     * Create a Map of services for specified component.
     * The map maps key name to service provider.
     *
     * @param componentEntry the component entry creating map for
     * @return the map
     * @throws java.lang.Exception if error aquiring a service to place in map
     */
    private Map createServiceMap( final Object componentEntry )
        throws Exception
    {
        final ComponentMetaData component = getMetaData( componentEntry );
        final String impl = component.getImplementationKey();
        final ComponentBundle bundle = m_factory.createBundle( impl );
        final ComponentInfo info = bundle.getComponentInfo();
        final DependencyMetaData[] dependencies = component.getDependencies();

        final HashMap services = new HashMap();

        for( int i = 0; i < dependencies.length; i++ )
        {
            final DependencyMetaData dependency = dependencies[ i ];
            final String key = dependency.getKey();
            final String providerName = dependency.getProviderName();
            final boolean optional = info.getDependency( key ).isOptional();

            final Object service =
                getService( providerName, componentEntry );
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

    /**
     * Check whether the specified value is compatible with specified type.
     *
     * @param value the value
     * @param type the desired type
     * @return true if value is compatible with type, false otherwise
     */
    private boolean objectImplementsType( final Object value, final String type )
    {
        try
        {
            final Class clazz = value.getClass();
            final ClassLoader classLoader = clazz.getClassLoader();
            final Class typeClass = classLoader.loadClass( type );
            if( typeClass.isAssignableFrom( clazz ) )
            {
                return true;
            }
        }
        catch( final ClassNotFoundException cnfe )
        {
        }
        return false;
    }
}