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
package org.apache.avalon.phoenix;

import java.util.EventListener;

/**
 * Implementations of this interface receive notifications about
 * changes to the state of Application.
 * The implementation <em>must</em> have a zero argument
 * constructor and is instantiated before any other component of the Server
 * Application. To receive notification events, the implementation class
 * should be specified in the <tt>assembly.xml</tt> descriptor.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 */
public interface ApplicationListener
    extends EventListener, BlockListener
{
    /**
     * Notification that a block has just been added
     * to Server Application.
     *
     * @param event the BlockEvent
     */
    void blockAdded( BlockEvent event );

    /**
     * Notification that a block is just about to be
     * removed from Server Application.
     *
     * @param event the BlockEvent
     */
    void blockRemoved( BlockEvent event );

    /**
     * Notification that an application is being started.
     *
     * @param applicationEvent the ApplicationEvent
     *
     */
    void applicationStarting( ApplicationEvent applicationEvent ) throws Exception;

    /**
     * Notification that an application has now started.
     */
    void applicationStarted();

    /**
     * Notification that an application is being stopped.
     */
    void applicationStopping();

    /**
     * Notification that an application has stopped.
     */
    void applicationStopped();

    /**
     * Notification that an application has failed at some moment.
     * This is for information only as Phoenix will do the right
     * thing for correct shutdown both before and after this method
     * is called.  The user of this method should NOT call System.exit()
     *
     * @param causeOfFailure
     */
    void applicationFailure( Exception causeOfFailure );
}
