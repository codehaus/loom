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
import java.net.ServerSocket;
import java.net.Socket;

/**
 * SocketThread accepts socket connections 
 *
 * @author <a href="mailto:Paul_Hammant@yahoo.com">Paul Hammant</a>
 */
public class SocketThread
    extends Thread
{
    private ServerImpl m_ServerImpl;
    private ServerSocket m_serverSocket;

    protected SocketThread( final ServerImpl ServerImpl,
                            final int port )
    {

        m_ServerImpl = ServerImpl;

        try
        {
            m_serverSocket = new ServerSocket( port );
        }
        catch( final IOException ioe )
        {
            final String message = "Unable to open listening port. " +
                "It is probably already being listened to.";
            throw new RuntimeException( message );
        }
    }

    /**
     * Method run
     *
     *
     */
    public void run()
    {

        while( true )
        {
            try
            {
                ConnectionThread ct = new ConnectionThread( m_serverSocket.accept() );
                ct.start();
            }
            catch( IOException ioe )
            {
                System.out.println( "Some problem with getting a socket for the connection." );
                ioe.printStackTrace();
            }
        }
    }

    /**
     * Class ConnectionThread
     */
    class ConnectionThread extends Thread
    {
        private Socket m_socket;

        private ConnectionThread( final Socket socket )
        {
            m_socket = socket;
        }

        public void run()
        {
            m_ServerImpl.processSocket( m_socket );
        }
    }
}
