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
package org.jcontainer.loom.demos.distributed.client;


import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author <a href="mailto:Paul_Hammant@yahoo.com">Paul Hammant</a>
 */
public class Client
{
    public static void main( final String[] args )
        throws IOException
    {
        if( args.length < 3 )
        {
            System.out.println( "Usage java -jar client.jar <hostname> <port> <message>" );
            System.exit( 10 );
        }

        final String host = args[ 0 ];
        final int port = Integer.parseInt( args[ 1 ] );
        final String message = args[ 2 ];
        final Socket socket = new Socket( host, port );
        final ObjectOutputStream oos = new ObjectOutputStream( socket.getOutputStream() );

        oos.writeObject( message );
        oos.close();
        socket.close();
    }
}
