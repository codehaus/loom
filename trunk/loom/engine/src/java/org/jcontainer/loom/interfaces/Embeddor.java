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

import org.apache.avalon.framework.activity.Executable;

/**
 * This is the object that is interacted with to create, manage and
 * dispose of the kernel and related resources.
 *
 * @author <a href="mail@leosimons.com">Leo Simons</a>
 * @author <a href="peter at realityforge.org">Peter Donald</a>
 */
public interface Embeddor
    extends Executable
{
    String ROLE = Embeddor.class.getName();

    /**
     * Request the Embeddor shutsdown.
     */
    void shutdown();
}
