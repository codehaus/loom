/*
 * Copyright (C) The Loom Group. All rights reserved.
 *
 * This software is published under the terms of the Loom
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.codehaus.loom.interfaces;

import org.codehaus.dna.Configuration;
import org.codehaus.dna.ConfigurationException;

/**
 * Repository from which all configuration data is retrieved.
 *
 * @author Peter Donald
 */
public interface ConfigurationInterceptor
{
    /**
     * Process configuration information
     *
     * @param application Application name
     * @param block Block name to store configuration for
     * @param configuration information to store.
     * @throws ConfigurationException if configuration could not be stored
     */
    Configuration processConfiguration( String application,
                                        String block,
                                        Configuration configuration )
        throws ConfigurationException;
}
