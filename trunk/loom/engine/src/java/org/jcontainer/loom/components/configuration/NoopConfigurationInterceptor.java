/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.components.configuration;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.jcontainer.loom.interfaces.ConfigurationInterceptor;

/**
 * Interceptor that does not make any changes to configuration.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 */
public class NoopConfigurationInterceptor
    implements ConfigurationInterceptor
{
    public Configuration processConfiguration( final String application,
                                               final String block,
                                               final Configuration configuration )
        throws ConfigurationException
    {
        return configuration;
    }
}
