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
package org.jcontainer.loom.demos.distributed.block;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.jcontainer.loom.demos.distributed.api.Server;

/**
 * @author <a href="mailto:Paul_Hammant@yahoo.com">Paul Hammant</a>
 */
public class ServerImpl
    implements Server
{
    public void processSocket( final Socket socket )
    {
        try
        {
            final ObjectInputStream ois =
                new ObjectInputStream( socket.getInputStream() );

            String string = null;

            try
            {
                string = (String)ois.readObject();
            }
            catch( final ClassNotFoundException cnfe )
            {
            }

            message( string );
            ois.close();
            socket.close();
        }
        catch( final IOException ioe )
        {
            System.out.println( "Unexpected IO Exception" );
        }
    }

    public void message( String string )
    {
        System.out.println( "Message string passed = " + string );
    }

    public static void main( final String[] args )
        throws IOException
    {
        final ServerImpl svr = new ServerImpl();
        final ServerSocket serverSocket = new ServerSocket( 7654 );

        System.out.println( "Server listening on port " + 7654 );
        System.out.println( "Ctrl-C to exit" );

        while( true )
        {
            svr.processSocket( serverSocket.accept() );
        }
    }
}
