/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.tools.creator;

import java.util.Map;

/**
 * This defines the interface via which containers are created.
 * The notion of container is suitable vague to allow arbitary
 * objects to be created as containers - not necessarily related
 * to Avalon containers in anyway.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003-06-29 01:09:03 $
 */
public interface ContainerFactory
{
    /**
     * Create an instance of container using specified input data.
     *
     * @param data the initial config data for container
     * @return the newly created container
     * @throws java.lang.Exception if unable to create a container
     */
    Object create( Map data )
        throws Exception;

    /**
     * Destroy a container created with this factory.
     *
     * @param container the container
     * @throws java.lang.Exception if unable to destroy container
     */
    void destroy( Object container )
        throws Exception;
}
