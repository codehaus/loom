/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.components;

/**
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003-10-05 08:10:19 $
 */
public interface ParameterConstants
{
    String HOME_DIR = "loom.home";

    String WORK_DIR = "loom.work.dir";
    String DEFAULT_WORK_DIR = "${loom.home}/work";

    String APPS_DIR = "loom.apps.dir";
    String DEFAULT_APPS_DIR = "${loom.home}/apps";

    String EXT_PATH = "loom.ext.path";
    String DEFAULT_EXT_PATH = "${loom.home}/ext";

    String APPLICATION_LOCATION = "application-location";

    String PERSISTENT = "persistent";
}
