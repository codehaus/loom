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
package org.jcontainer.loom.interfaces;

import java.io.File;
import java.util.Map;
import org.jcomponent.loggerstore.LoggerStore;

/**
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 */
public interface Kernel
{
    String ROLE = Kernel.class.getName();

    /**
     * Adds an application to the container
     */
    void addApplication( org.jcontainer.loom.tools.profile.PartitionProfile profile,
                         File homeDirectory, File workDirectory,
                         ClassLoader classLoader,
                         LoggerStore store,
                         Map classloaders )
        throws Exception;

    /**
     * Removes the application from the container
     *
     * @param name the name of application to remove
     */
    void removeApplication( String name )
        throws Exception;

    /**
     * Gets the named application
     *
     * @param name the name of application
     */
    Application getApplication( String name );

    /**
     * Gets the list of applications running in the container
     *
     * @return applicationNames The array of application names
     */
    String[] getApplicationNames();

    /**
     * Lock the kernel, temporarily preserving the list of applications running in the container
     */
    void lock();

    /**
     * Unlock the kernel, restoring the list of applications to be the current active list
     */
    void unlock();
}
