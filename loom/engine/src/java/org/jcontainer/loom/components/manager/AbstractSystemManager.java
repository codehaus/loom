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
package org.jcontainer.loom.components.manager;

import java.util.HashMap;
import java.util.Map;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.jcontainer.loom.interfaces.ManagerException;
import org.jcontainer.loom.interfaces.SystemManager;
import org.realityforge.salt.i18n.Resources;
import org.realityforge.salt.i18n.ResourceManager;

/**
 * This is abstract implementation of SystemManager.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 */
public abstract class AbstractSystemManager
    extends AbstractLogEnabled
    implements SystemManager, Initializable, Disposable
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( AbstractSystemManager.class );

    private final Map m_entries = new HashMap();

    private SubContext m_subContext;

    public void initialize()
        throws Exception
    {
        m_subContext = new SubContext( this, null, null );
    }

    public void dispose()
    {
        m_subContext = null;
    }

    /**
     * @see SystemManager#register(String, Object, Class[])
     */
    public synchronized void register( final String name,
                                       final Object object,
                                       final Class[] interfaces )
        throws ManagerException, IllegalArgumentException
    {
        if( null == interfaces )
        {
            final String message =
                REZ.format( "manager.error.interfaces.null", name );
            throw new IllegalArgumentException( message );
        }
        verifyInterfaces( object, interfaces );

        doRegister( name, object, interfaces );
    }

    /**
     * @see SystemManager#register(String, Object)
     */
    public synchronized void register( final String name,
                                       final Object object )
        throws ManagerException, IllegalArgumentException
    {
        doRegister( name, object, null );
    }

    /**
     * @see SystemManager#unregister(String)
     */
    public synchronized void unregister( final String name )
        throws ManagerException
    {
        final ManagedEntry entry = (ManagedEntry)m_entries.remove( name );
        if( null == entry )
        {
            final String message =
                REZ.format( "manager.error.unregister.noentry", name );
            throw new ManagerException( message );
        }

        unexport( name, entry.getExportedObject() );
    }

    /**
     * Returns the subcontext of the specified name.  If it does not exist it
     * is created.
     *
     * @throws ManagerException if context cannot be created or retrieved
     * @return  the subcontext with the specified name
     */
    public SystemManager getSubContext( final String parent, final String type )
        throws ManagerException
    {
        return m_subContext.getSubContext( parent, type );
    }

    /**
     * Export the object to the particular management medium using
     * the supplied object and interfaces.
     * This needs to be implemented by subclasses.
     *
     * @param name the name of object
     * @param object the object
     * @param interfaces the interfaces
     * @return the exported object
     * @throws ManagerException if an error occurs
     */
    protected abstract Object export( String name, Object object, Class[] interfaces )
        throws ManagerException;

    /**
     * Stop the exported object from being managed.
     *
     * @param name the name of object
     * @param exportedObject the object return by export
     * @throws ManagerException if an error occurs
     */
    protected abstract void unexport( String name, Object exportedObject )
        throws ManagerException;

    /**
     * Verfify that name is well formed.
     *
     * @param name the name
     * @param object the object so named
     */
    protected void verifyName( final String name,
                               final Object object )
        throws ManagerException
    {
    }

    /**
     * Verify that an interface conforms to the requirements of management medium.
     *
     * @param clazz the interface class
     * @throws ManagerException if verification fails
     */
    protected abstract void verifyInterface( Class clazz )
        throws ManagerException;

    /**
     * Verify that object implements interfaces and interfaces are of "acceptable form".
     * "Acceptable form" is determined by specific management policy.
     *
     * @param object the object
     * @param interfaces the array of interfaces to check
     * @throws ManagerException if an error occurs
     */
    private void verifyInterfaces( final Object object, final Class[] interfaces )
        throws ManagerException
    {
        for( int i = 0; i < interfaces.length; i++ )
        {
            final Class clazz = interfaces[ i ];

            if( !clazz.isInterface() )
            {
                final String message =
                    REZ.format( "manager.error.verify.notinterface", clazz.getName() );
                throw new ManagerException( message );
            }

            if( !clazz.isInstance( object ) )
            {
                final String message =
                    REZ.format( "manager.error.verify.notinstance", clazz.getName() );
                throw new ManagerException( message );
            }

            verifyInterface( clazz );
        }
    }

    /**
     * Helper method to help check before an objects registration.
     * Verifies name and object are not null and verifies no entry exists using name.
     *
     * @param name the name of object
     * @param object the object to be registered
     * @throws ManagerException if name already exists
     * @throws IllegalArgumentException if name or object is null
     */
    private void checkRegister( final String name, final Object object )
        throws ManagerException, IllegalArgumentException
    {
        if( null == object )
        {
            throw new NullPointerException( "object" );
        }

        if( null == name )
        {
            throw new NullPointerException( "name" );
        }

        verifyName( name, object );

        if( null != m_entries.get( name ) )
        {
            final String message = REZ.format( "manager.error.register.exists", name );
            throw new ManagerException( message );
        }
    }

    /**
     * Utility method that actually does the registration.
     *
     * @param name the name to register under
     * @param object the object
     * @param interfaces the interfaces (may be null)
     * @throws ManagerException if error occurs
     */
    private void doRegister( final String name,
                             final Object object,
                             final Class[] interfaces )
        throws ManagerException
    {
        checkRegister( name, object );

        final Object exportedObject = export( name, object, interfaces );
        final ManagedEntry entry =
            new ManagedEntry( object, interfaces, exportedObject );
        m_entries.put( name, entry );
    }
}
