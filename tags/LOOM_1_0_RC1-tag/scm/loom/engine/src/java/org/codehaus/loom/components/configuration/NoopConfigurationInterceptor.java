/*
 * Copyright (C) The Loom Group. All rights reserved.
 *
 * This software is published under the terms of the Loom
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.codehaus.loom.components.configuration;

import org.codehaus.dna.Configuration;
import org.codehaus.dna.ConfigurationException;
import org.codehaus.loom.interfaces.ConfigurationInterceptor;

/**
 * Interceptor that does not make any changes to configuration.
 *
 * @author Peter Donald
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
