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

import org.apache.avalon.framework.configuration.ConfigurationException;

/**
 * Handles parsing of configuration schema and validation against schema
 *
 * @author <a href="mailto:proyal@apache.org">Peter Royal</a>
 * @version CVS $Revision: 1.1 $ $Date: 2003-06-29 04:38:22 $
 */
public interface ConfigurationValidator
{
    String ROLE = ConfigurationValidator.class.getName();

    /**
     * Check to see if configuration is valid for specified component.
     *
     * @param component the ComponentProfile
     * @param classLoader the ClassLoader (to load schema from if necessary)
     * @return true if configuration is valid
     * @throws ConfigurationException if expected schema is missing
     */
    boolean isValid( org.jcontainer.loom.tools.profile.ComponentProfile component, ClassLoader classLoader )
        throws ConfigurationException;
}
