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

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Client for <code>RMIHelloWorldServer</code>.
 *
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 * @author <a href="mailto:jefft@apache.org">Jeff Turner</a>
 */
public class RMIHelloWorldClient
{
    public static void main( final String[] args )
        throws Exception
    {
        if( args.length != 3 )
        {
            System.err.println( "Usage: " + RMIHelloWorldClient.class.getName() + " <host> <port> <text>" );
            return;
        }
        final String host = args[ 0 ];
        final int port = Integer.parseInt( args[ 1 ] );
        final String yourName = args[ 2 ];

        final Registry registry = LocateRegistry.getRegistry( host, port );
        final RMIHelloWorldServer server = (RMIHelloWorldServer)registry.lookup( "rmi-helloworldserver" );
        System.out.println( "sayHello( " + yourName + " ): " + server.sayHello( yourName ) );
    }
}
