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
package org.jcontainer.loom.interfaces;

/**
 * This component is responsible for managing the system.
 * This includes managing the embeddor, deployer and kernel.
 *
 * @author <a href="mail@leosimons.com">Leo Simons</a>
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 */
public interface SystemManager
{
    String ROLE = SystemManager.class.getName();

    /**
     * Register an object for management.
     * The object is exported through some management scheme
     * (typically JMX) and the management is restricted
     * to the interfaces passed in as a parameter to method.
     *
     * @param name the name to register object under
     * @param object the object
     * @param interfaces the interfaces to register the component under
     * @throws ManagerException if an error occurs. An error could occur if the object doesn't
     *            implement the interfaces, the interfaces parameter contain non-instance
     *            classes, the name is already registered etc.
     * @throws IllegalArgumentException if object or interfaces is null
     */
    void register( String name, Object object, Class[] interfaces )
        throws ManagerException, IllegalArgumentException;

    /**
     * Register an object for management.
     * The object is exported through some management scheme
     * (typically JMX). Note that the particular management scheme
     * will most likely use reflection to extract manageable information.
     *
     * @param name the name to register object under
     * @param object the object
     * @throws ManagerException if an error occurs such as name already registered.
     * @throws IllegalArgumentException if object is null
     */
    void register( String name, Object object )
        throws ManagerException, IllegalArgumentException;

    /**
     * Unregister named object.
     *
     * @param name the name of object to unregister
     * @throws ManagerException if an error occurs such as when no such object registered.
     */
    void unregister( String name )
        throws ManagerException;

    /**
     * Returns the subcontext of the specified name.  If it does not exist it
     * is created.
     *
     * @param name name of the object in the parent context that will own this one
     * @param type of objects that will be managed in this context
     * @throws ManagerException if context cannot be created or retrieved
     * @return  the subcontext with the specified name
     */
    SystemManager getSubContext( String name, String type )
        throws ManagerException;
}
