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

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

/**
 * Repository from which all configuration data is retrieved.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 */
public interface ConfigurationRepository
{
    String ROLE = ConfigurationRepository.class.getName();

    /**
     * Process configuration information
     *
     * @param application Application name
     * @param block Block name to store configuration for
     * @param configuration information to store.
     *
     * @throws ConfigurationException if configuration could not be stored
     */
    Configuration processConfiguration( String application,
                                        String block,
                                        Configuration configuration )
        throws ConfigurationException;
}
