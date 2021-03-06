/*
 * Copyright (C) The Loom Group. All rights reserved.
 *
 * This software is published under the terms of the Loom
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.codehaus.loom.components.validator;

import org.codehaus.dna.ConfigurationException;
import org.codehaus.loom.components.util.profile.ComponentProfile;
import org.codehaus.loom.interfaces.ConfigurationValidator;

/**
 * A ConfigurationValidator that always says everything is okay
 *
 * @author <a href="mailto:proyal@apache.org">Peter Royal</a>
 */
public class NoopConfigurationValidator
    implements ConfigurationValidator
{
    public boolean isValid( ComponentProfile component,
                            ClassLoader classLoader )
        throws ConfigurationException
    {
        return true;
    }
}
