/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1997-2003 The Apache Software Foundation. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *    "This product includes software developed by the
 *    Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software
 *    itself, if and wherever such third-party acknowledgments
 *    normally appear.
 *
 * 4. The names "Jakarta", "Avalon", and "Apache Software Foundation"
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
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
