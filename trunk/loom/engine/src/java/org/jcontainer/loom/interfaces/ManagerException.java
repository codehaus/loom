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

import org.apache.avalon.framework.CascadingException;

/**
 * The ManagerException used to indicate problems with managers.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 */
public class ManagerException
    extends CascadingException
{
    public ManagerException( final String message )
    {
        this( message, null );
    }

    public ManagerException( final String message, final Throwable throwable )
    {
        super( message, throwable );
    }
}
