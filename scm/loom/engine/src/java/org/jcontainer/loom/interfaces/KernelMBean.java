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
 * This is the interface via which you can manager
 * the root container of Applications.
 *
 * @phoenix:mx-topic name="Kernel"
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 */
public interface KernelMBean
{
    String ROLE = KernelMBean.class.getName();

    /**
     * Gets the list of applications running in the container
     *
     * @phoenix:mx-attribute
     *
     * @return applicationNames The array of application names
     */
    String[] getApplicationNames();

    /**
     * Removes the application from the container
     *
     * @phoenix:mx-operation
     *
     * @param name the name of application to remove
     */
    void removeApplication( String name )
        throws Exception;
}
