/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.interfaces;

/**
 * Exception to indicate that a problem occured.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 */
public final class LoomException
    extends Exception
{
    /**
     * the cause of the exception.
     */
    private final Throwable m_cause;

    /**
     * Construct a new <code>LoomException</code> instance.
     *
     * @param message The detail message for this exception.
     */
    public LoomException( final String message )
    {
        this( message, null );
    }

    /**
     * Construct a new <code>LoomException</code> instance.
     *
     * @param message The detail message for this exception.
     * @param throwable the root cause of the exception
     */
    public LoomException( final String message, final Throwable throwable )
    {
        super( message );
        m_cause = throwable;
    }

    /**
     * Return the cause of exception.
     *
     * @return the cause of exception. (May be null).
     */
    public Throwable getCause()
    {
        return m_cause;
    }
}
