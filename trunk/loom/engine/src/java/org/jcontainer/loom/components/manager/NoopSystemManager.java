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
package org.jcontainer.loom.components.manager;

import org.jcontainer.loom.interfaces.ManagerException;

/**
 * Null SystemManager.
 *
 * @author <a href="mailto:colus@isoft.co.kr">Eung-ju Park</a>
 */
public class NoopSystemManager
    extends AbstractSystemManager
{
    protected Object export( final String name,
                             final Object object,
                             final Class[] interfaces )
        throws ManagerException
    {
        return object;
    }

    protected void unexport( final String name,
                             final Object exportedObject )
        throws ManagerException
    {
    }

    protected void verifyInterface( final Class clazz )
        throws ManagerException
    {
    }
}
