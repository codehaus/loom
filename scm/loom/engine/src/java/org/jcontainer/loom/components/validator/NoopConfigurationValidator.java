/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.components.validator;

import org.jcontainer.dna.ConfigurationException;
import org.jcontainer.loom.components.util.profile.ComponentProfile;
import org.jcontainer.loom.interfaces.ConfigurationValidator;

/**
 * A ConfigurationValidator that always says everything is okay
 *
 * @author <a href="mailto:proyal@apache.org">Peter Royal</a>
 */
public class NoopConfigurationValidator
    implements ConfigurationValidator
{
    public boolean isValid( ComponentProfile component, ClassLoader classLoader )
        throws ConfigurationException
    {
        return true;
    }
}
