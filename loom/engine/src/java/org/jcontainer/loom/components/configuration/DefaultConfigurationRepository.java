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
package org.jcontainer.loom.components.configuration;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.jcontainer.loom.interfaces.ConfigurationRepository;

/**
 * Repository from which all configuration data is retrieved.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 */
public class DefaultConfigurationRepository
    implements ConfigurationRepository
{
    public Configuration processConfiguration( final String application,
                                               final String block,
                                               final Configuration configuration )
        throws ConfigurationException
    {
        return configuration;
    }
}
