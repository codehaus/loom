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

/**
 * A set of constants that are used internally in the container to communicate
 * about different artefacts. They usually act as keys into maps.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 */
public interface ContainerConstants
{
    /**
     * The name of the software. (Usually Loom but different
     * users may overide this).
     */
    String SOFTWARE = "@@NAME@@";

    /**
     * The version of the software.
     */
    String VERSION = "@@VERSION@@";

    /**
     * The date on which software was built.
     */
    String DATE = "@@DATE@@";

    /**
     * The name of the attribute used to determine whether
     * a block is not proxied.
     */
    String DISABLE_PROXY_ATTR = "loom:disable-proxy";

    /**
     * The name which the assembly is registered into phoenix
     * using.
     */
    String ASSEMBLY_NAME = "loom:assembly-name";

    /**
     * The name of the config file which is used
     * to load config data.
     */
    String CONFIG_DESCRIPTOR = "loom:config";

    /**
     * The name of the config file which is used
     * to load assembly data.
     */
    String ASSEMBLY_DESCRIPTOR = "loom:assembly";

    /**
     * The default classloader to use to load components.
     */
    String ASSEMBLY_CLASSLOADER = "loom:classloader";

    /**
     * The name of the partition in which blocks are contained.
     */
    String BLOCK_PARTITION = "block";

    /**
     * The name of the partition in which listeners are contained.
     */
    String LISTENER_PARTITION = "listener";

    /**
     * The root instrumentation category for all applications.
     */
    String ROOT_INSTRUMENT_CATEGORY = "applications";

    /**
     * The source of installation (usually a directory in .sar format or a .sar file).
     * Type: {@link java.io.File}
     */
    String INSTALL_SOURCE = "install:source";

    /**
     * The Directory in which application is installed.
     * Type: {@link java.io.File}
     */
    String INSTALL_HOME = "install:home";

    /**
     * The Directory in which application temporary/work data is stored.
     * Type: {@link java.io.File}
     */
    String INSTALL_WORK = "install:work";

    /**
     * The URL to block configuration data.
     * Type: {@link java.lang.String}
     */
    String INSTALL_CONFIG = "install:config";

    /**
     * The URL to assembly data.
     * Type: {@link java.lang.String}
     */
    String INSTALL_ASSEMBLY = "install:assembly";

    /**
     * The URL to application configuration data.
     * Type: {@link java.lang.String}
     */
    String INSTALL_ENVIRONMENT = "install:environment";
}
