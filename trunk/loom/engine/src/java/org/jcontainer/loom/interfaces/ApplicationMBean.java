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
 * @phoenix:mx-topic name="Application"
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @author <a href="mailto:leosimons@apache.org">Leo Simons</a>
 */
public interface ApplicationMBean
{
    String ROLE = ApplicationMBean.class.getName();

    /**
     * Get the name of the application.
     *
     * @phoenix:mx-attribute
     *
     * @return the name of the application
     */
    String getName();

    /**
     * Get the name to display in Management UI.
     *
     * @phoenix:mx-attribute
     *
     * @return the name of the application to display in UI
     */
    String getDisplayName();

    /**
     * Get the string used to describe the application in the UI.
     *
     * @phoenix:mx-attribute
     *
     * @return a short description of the application
     */
    String getDescription();

    /**
     * Get location of Application installation
     *
     * @phoenix:mx-attribute
     *
     * @return the home directory of application
     */
    String getHomeDirectory();

    /**
     * Get the names of the blocks that compose this Application
     *
     * @phoenix:mx-attribute
     *
     * @return list of block names
     */
    String[] getBlockNames();

    /**
     * Return true if the application is
     * running or false otherwise.
     *
     * @phoenix:mx-attribute
     *
     * @return true if application is running, false otherwise
     */
    boolean isRunning();

    /**
     * Start the application running.
     * This is only valid when isRunning() returns false,
     * otherwise it will generate an IllegalStateException.
     *
     * @phoenix:mx-operation
     *
     * @throws IllegalStateException if application is already running
     * @throws ApplicationException if the application failed to start.
     *            the message part of exception will contain more information
     *            pertaining to why the application failed to startup
     */
    void start()
        throws IllegalStateException, ApplicationException;

    /**
     * Shutdown and restart the application running.
     * This is only valid when isRunning() returns true,
     * otherwise it will generate an IllegalStateException.
     * This is equivelent to  calling stop() and then start()
     * in succession.
     *
     * @phoenix:mx-operation
     *
     * @throws IllegalStateException if application is not already running
     * @throws ApplicationException if the application failed to stop or start.
     *            the message part of exception will contain more information
     *            pertaining to why the application failed to startup/shutdown
     */
    void restart()
        throws IllegalStateException, ApplicationException;

    /**
     * Stop the application running.
     * This is only valid when isRunning() returns true,
     * otherwise it will generate an IllegalStateException.
     *
     * @phoenix:mx-operation
     *
     * @throws IllegalStateException if application is not already running
     * @throws ApplicationException if the application failed to shutdown.
     *            the message part of exception will contain more information
     *            pertaining to why the application failed to shutodwn
     */
    void stop()
        throws IllegalStateException, ApplicationException;
}
