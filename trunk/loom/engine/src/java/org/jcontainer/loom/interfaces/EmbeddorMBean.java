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

import java.util.Date;

/**
 * This is the interface via which the Management interface interacts
 * with the Embeddor.
 *
 * @phoenix:mx-topic name="Embeddor"
 *
 * @author <a href="peter at realityforge.org">Peter Donald</a>
 */
public interface EmbeddorMBean
{
    String ROLE = EmbeddorMBean.class.getName();

    /**
     * Get name by which the server is known.
     * Usually this defaults to "Phoenix" but the admin
     * may assign another name. This is useful when you
     * are managing a cluster of Phoenix servers.
     *
     * @phoenix:mx-attribute
     * @phoenix:mx-description Name by which this server is known.
     *
     * @return the name of server
     */
    String getName();

    /**
     * Get location of Phoenix installation
     *
     * @phoenix:mx-attribute
     *
     * @return the home directory of phoenix
     */
    String getHomeDirectory();

    /**
     * Get the date at which this server started.
     *
     * @phoenix:mx-attribute
     *
     * @return the date at which this server started
     */
    Date getStartTime();

    /**
     * Retrieve the number of millisecond
     * the server has been up.
     *
     * @phoenix:mx-attribute
     *
     * @return the the number of millisecond the server has been up
     */
    long getUpTimeInMillis();

    /**
     * Retrieve a string identifying version of server.
     * Usually looks like "v4.0.1a".
     *
     * @phoenix:mx-attribute
     * @phoenix:mx-description Retrieve a string identifying version of server.
     *
     * @return version string of server.
     */
    String getVersion();

    /**
     * Get a string defining the build.
     * Possibly the date on which it was built, where it was built,
     * with what features it was built and so forth.
     *
     * @phoenix:mx-attribute
     *
     * @return the string describing build
     */
    String getBuild();

    /**
     * Request the Embeddor shutsdown.
     *
     * @phoenix:mx-operation
     */
    void shutdown();

    /**
     * Request the embeddor to restart.
     *
     * @phoenix:mx-operation
     *
     * @throws UnsupportedOperationException if restart not a supported operation
     */
    void restart()
        throws UnsupportedOperationException;
}
