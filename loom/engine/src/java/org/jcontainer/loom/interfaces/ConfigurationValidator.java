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

import org.jcontainer.loom.tools.profile.ComponentProfile;

/**
 * Handles parsing of configuration schema and validation against schema
 *
 * @author <a href="mailto:proyal@apache.org">Peter Royal</a>
 * @version CVS $Revision: 1.2 $ $Date: 2003-08-05 10:26:36 $
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
     * @throws Exception if expected schema is missing
     */
    boolean isValid( ComponentProfile component, ClassLoader classLoader )
        throws Exception;
}
