/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 *
 * This product includes software developed by the
 * Apache Software Foundation (http://www.apache.org/).
 */
package org.jcontainer.loom.demos.helloworld;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 */
public interface RMIHelloWorldServer
    extends Remote
{
    String ROLE = RMIHelloWorldServer.class.getName();

    /**
     * Just say hello.
     */
    String sayHello( final String yourName )
        throws RemoteException;
}
