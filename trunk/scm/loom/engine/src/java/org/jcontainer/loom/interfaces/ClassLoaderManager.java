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

/**
 * Interface for component that creates and manages the
 * {@link ClassLoader} for an Application. The specific
 * mechanism by which the {@link ClassLoader} is created
 * is dependent on the type of {@link Embeddor} and the
 * deployment format.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 */
public interface ClassLoaderManager
{
    String ROLE = ClassLoaderManager.class.getName();

    /**
     * Create a {@link ClassLoaderSet} for a specific application.
     *
     * @param environment the configuration "environment.xml" for the application
     * @param data the context data used when expanding config files
     * @param baseDirectory the base directory of application
     * @param workDirectory the work directory of application
     * @return the ClassLoaderSet created
     * @throws Exception if an error occurs
     */
    ClassLoaderSet createClassLoaderSet( Configuration environment,
                                         Map data,
                                         File baseDirectory,
                                         File workDirectory )
        throws Exception;
}
