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

import java.io.File;
import java.io.FileFilter;

class ConfigurationDirectoryFilter implements FileFilter
{
    public boolean accept( final File pathname )
    {
        return pathname.isDirectory();
    }
}
