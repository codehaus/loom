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

import java.io.File;
import java.util.Map;
import org.apache.avalon.framework.configuration.Configuration;
import org.realityforge.loggerstore.LoggerStore;

/**
 * Interface that is used to manage Log objects for a Sar.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 */
public interface LogManager
{
    String ROLE = LogManager.class.getName();

    /**
     * Create a Logger hierarchy for an applicaiton.
     *
     * @param logs the configuration data for logs
     * @param context the context in which to build hierarchy
     * @return the configured Logger hierarchy
     * @throws Exception if an error occurs
     */
    LoggerStore createHierarchy( Configuration logs,
                                 File homeDirectory,
                                 File workDirectory,
                                 Map context )
        throws Exception;
}
