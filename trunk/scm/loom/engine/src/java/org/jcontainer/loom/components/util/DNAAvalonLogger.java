/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.components.util;

import org.apache.avalon.framework.logger.Logger;

/**
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003-10-05 09:35:20 $
 */
public class DNAAvalonLogger
    implements Logger
{
    private final org.jcontainer.dna.Logger m_logger;

    public DNAAvalonLogger( org.jcontainer.dna.Logger logger )
    {
        m_logger = logger;
    }

    public void debug( String message )
    {
        m_logger.debug( message );
    }

    public void debug( String message, Throwable throwable )
    {
        m_logger.debug( message, throwable );
    }

    public boolean isDebugEnabled()
    {
        return m_logger.isDebugEnabled();
    }

    public void info( String message )
    {
        m_logger.info( message );
    }

    public void info( String message, Throwable throwable )
    {
        m_logger.info( message, throwable );
    }

    public boolean isInfoEnabled()
    {
        return m_logger.isInfoEnabled();
    }

    public void warn( String message )
    {
        m_logger.warn( message );
    }

    public void warn( String message, Throwable throwable )
    {
        m_logger.info( message, throwable );
    }

    public boolean isWarnEnabled()
    {
        return m_logger.isWarnEnabled();
    }

    public void error( String message )
    {
        m_logger.error( message );
    }

    public void error( String message, Throwable throwable )
    {
        m_logger.error( message, throwable );
    }

    public boolean isErrorEnabled()
    {
        return m_logger.isErrorEnabled();
    }

    public void fatalError( String message )
    {
        m_logger.error( message );
    }

    public void fatalError( String message, Throwable throwable )
    {
        m_logger.error( message, throwable );
    }

    public boolean isFatalErrorEnabled()
    {
        return m_logger.isErrorEnabled();
    }

    public Logger getChildLogger( String name )
    {
        return new DNAAvalonLogger( m_logger.getChildLogger( name ) );
    }
}
