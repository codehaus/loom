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

import java.net.URL;
import java.util.Map;

/**
 * A basic service to Install an application.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 */
public interface Installer
{
    String ROLE = Installer.class.getName();

    /**
     * Install the Sar designated by url.
     *
     * @param url the url of instalation
     * @throws InstallationException if an error occurs
     */
    Map install( String name, URL url )
        throws InstallationException;

    /**
     * Uninstall the Sar designated installation.
     *
     * @param installation the installation
     * @throws InstallationException if an error occurs
     */
    void uninstall( Map installation )
        throws InstallationException;
}
