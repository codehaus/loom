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

import mx4j.log.Logger;

/**
 * A class to pipe MX4J's own logger to the one Phoenix wants to use.
 */
public class MX4JLoggerAdapter extends Logger
{
    private static org.apache.avalon.framework.logger.Logger avalonLogger;

    /**
     * This is really bad.  A static way of introducing a logger to a tool.
     * @param logger the Avalon logger.
     */
    public static void setLogger( final org.apache.avalon.framework.logger.Logger logger )
    {
        avalonLogger = logger;
    }

    /**
     * This overides the method in the super class to actually deliver Avalon
     * Logging to MX4J
     *
     * @param level the debug/warn/error level.
     * @param message the message to log.
     * @param throwable a message that may be sent.
     */
    protected void log( final int level, final Object message, final Throwable throwable )
    {
        switch( level )
        {
            case mx4j.log.Logger.DEBUG:
                avalonLogger.debug( message.toString(), throwable );
                break;
            case mx4j.log.Logger.ERROR:
                avalonLogger.error( message.toString(), throwable );
                break;
            case mx4j.log.Logger.FATAL:
                avalonLogger.fatalError( message.toString(), throwable );
                break;
            case mx4j.log.Logger.INFO:
                avalonLogger.info( message.toString(), throwable );
                break;
            case mx4j.log.Logger.TRACE:
                avalonLogger.debug( message.toString(), throwable );
                break;
            case mx4j.log.Logger.WARN:
                avalonLogger.warn( message.toString(), throwable );
                break;
        }
    }
}
