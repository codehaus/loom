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
package org.jcontainer.loom.components.classloader;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.apache.avalon.framework.configuration.Configuration;
import org.jcontainer.loom.interfaces.ClassLoaderManager;
import org.jcontainer.loom.interfaces.ClassLoaderSet;

/**
 * Basic ClassLoaderManager that just returns current
 * ContextClassLoader.
 *
 * @author Eung-ju Park
 * @see ClassLoaderManager
 */
public class ContextClassLoaderManager
    implements ClassLoaderManager
{
    public ClassLoaderSet createClassLoaderSet( final Configuration environment,
                                                final Map data,
                                                final File baseDirectory,
                                                final File workDirectory )
        throws Exception
    {
        final ClassLoader classLoader =
            Thread.currentThread().getContextClassLoader();
        return new ClassLoaderSet( classLoader, new HashMap() );
    }
}
