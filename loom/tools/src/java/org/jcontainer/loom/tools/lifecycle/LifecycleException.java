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
 package org.jcontainer.loom.tools.lifecycle;

import org.apache.avalon.framework.CascadingException;

/**
 * Exception to indicate error processing a component through its lifecycle.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version $Revision: 1.1 $ $Date: 2003-06-29 01:06:23 $
 */
public final class LifecycleException
    extends CascadingException
{
    /**
     * Construct a new <code>VerifyException</code> instance.
     *
     * @param message The detail message for this exception.
     */
    public LifecycleException( final String message )
    {
        this( message, null );
    }

    /**
     * Construct a new <code>VerifyException</code> instance.
     *
     * @param message The detail message for this exception.
     * @param throwable the root cause of the exception
     */
    public LifecycleException( final String message, final Throwable throwable )
    {
        super( message, throwable );
    }
}
