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
package org.jcontainer.loom.launcher;

/**
 * A simple JMX interface to launcher.
 * Used to launch Phoenix from within Phoenix.
 *
 * @author <a href="mailto:sshort at postx.com">Steve Short</a>
 */
public interface JMXLauncherMBean
{
    String OBJECT_NAME = "Phoenix:service=Launcher";

    // Constants -----------------------------------------------------

    String[] states = new String[]
    {
        "Stopped", "Stopping", "Starting", "Started", "Failed"
    };

    int STOPPED = 0;
    int STOPPING = 1;
    int STARTING = 2;
    int STARTED = 3;
    int FAILED = 4;

    /**
     * Return MBean state as an int
     */
    int getState();

    /**
     * Return MBean state as a String
     */
    String getStateString();

    /**
     * create the service, do expensive operations etc
     */
    void create() throws Exception;

    /**
     * start the service, create is already called
     */
    void start() throws Exception;

    /**
     * stop the service
     */
    void stop();

    /**
     * destroy the service, tear down
     */
    void destroy();

    /**
     * Accessors for phoenix home directory
     */
    void setPhoenixHome( String value );

    String getPhoenixHome();

    /**
     * Accessors for phoenix config file
     */
    void setPhoenixConfigFile( String value );

    String getPhoenixConfigFile();

    /**
     * Accessors for phoenix applications directory
     */
    void setAppsPath( String value );

    String getAppsPath();

    /**
     * Accessors for phoenix log file name
     */
    void setLogFilename( String value );

    String getLogFilename();

    /**
     * Accessors for phoenix debug flag
     */
    void setPhoenixDebug( String value );

    String getPhoenixDebug();
}
