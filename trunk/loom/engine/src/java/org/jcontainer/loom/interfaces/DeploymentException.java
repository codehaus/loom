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
 * Exception to indicate error deploying.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 */
public final class DeploymentException
    extends CascadingException
{
    /**
     * Construct a new <code>DeploymentException</code> instance.
     *
     * @param message The detail message for this exception.
     */
    public DeploymentException( final String message )
    {
        this( message, null );
    }

    /**
     * Construct a new <code>DeploymentException</code> instance.
     *
     * @param message The detail message for this exception.
     * @param throwable the root cause of the exception
     */
    public DeploymentException( final String message, final Throwable throwable )
    {
        super( message, throwable );
    }
}
