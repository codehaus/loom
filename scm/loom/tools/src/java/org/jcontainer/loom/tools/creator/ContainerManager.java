/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.tools.creator;

import java.util.Map;
import java.util.WeakHashMap;
import org.jcontainer.loom.tools.creator.ContainerFactory;

/**
 * This defines the interface via which containers are created.
 * The notion of container is suitable vague to allow arbitary
 * objects to be created as containers - not necessarily related
 * to Avalon containers in anyway.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003-06-29 01:09:03 $
 */
public final class ContainerManager
{
    /**
     * The key used to specify the name of the Container factory.
     */
    public static final String INITIAL_CONTAINER_FACTORY =
        ContainerFactory.class.getName();

    /**
     * Cache of created container->factory map.
     */
    private static final Map c_containers = new WeakHashMap();

    /**
     * Create an instance of container using specified input data.
     *
     * @param data the initial config data for container
     * @return the newly created container
     * @throws java.lang.Exception if unable to create a container
     */
    public static Object create( Map data )
        throws Exception
    {
        final String classname = (String)data.remove( INITIAL_CONTAINER_FACTORY );
        if( null == classname )
        {
            final String message = "No INITIAL_CONTAINER_FACTORY specified.";
            throw new Exception( message );
        }
        final Class clazz = Class.forName( classname );
        final ContainerFactory factory = (ContainerFactory)clazz.newInstance();
        final Object container = factory.create( data );
        c_containers.put( container, factory );
        return container;
    }

    /**
     * Destroy a container created with this factory.
     *
     * @param container the container
     * @throws java.lang.Exception if unable to destroy container
     */
    public static void destroy( final Object container )
        throws Exception
    {
        final ContainerFactory factory = (ContainerFactory)c_containers.remove( container );
        if( null == factory )
        {
            final String message = "Container was not created by " +
                "ContainerManager or has already been destroyed.";
            throw new Exception( message );
        }
        factory.destroy( container );
    }
}
