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
import org.apache.avalon.phoenix.BlockEvent;

/**
 * Implementations of this interface receive notifications about
 * changes to the state of Blocks in the Server Application
 * they are a part of. The implementation <em>must</em> have a zero argument
 * constructor and is instantiated before any other component of the Server
 * Application. To receive notification events, the implementation class
 * should be specified in the <tt>assembly.xml</tt> descriptor.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @deprecated This interface has been replaced by ApplicationListener
 */
public interface BlockListener
    extends EventListener
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
}
