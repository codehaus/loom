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

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.phoenix.BlockContext;

import org.apache.avalon.cornerstone.services.connection.ConnectionHandler;

/**
 * This handles an individual incoming request.  It outputs a greeting as html.
 * @author  Paul Hammant <Paul_Hammant@yahoo.com>
 * @author Federico Barbieri <scoobie@systemy.it>
 * @version 1.0
 */
final class HelloWorldHandler
    extends AbstractLogEnabled
    implements ConnectionHandler
{
    private static int c_counter;
    private String m_greeting;
    private BlockContext m_context;

    HelloWorldHandler( final String greeting,
                       final BlockContext context )
    {
        m_greeting = greeting;
        m_context = context;
    }

    /**
     * Handle a connection.
     * This handler is responsible for processing connections as they occur.
     *
     * @param socket the connection
     * @exception IOException if an error reading from socket occurs
     */
    public void handleConnection( final Socket socket )
        throws IOException
    {
        final String remoteHost = socket.getInetAddress().getHostName();
        final String remoteIP = socket.getInetAddress().getHostAddress();
        final PrintWriter out = new PrintWriter( socket.getOutputStream(), true );

        try
        {
            out.println( "<html><body><b>" + m_greeting + "!</b><br> Requests so far = " +
                         ++c_counter + "<br>" );
            out.println( "you are " + remoteHost + " at " + remoteIP + "<br>" );
            out.println( "<p>The application will shutdown after 10 requests" );
            out.println( "</body></html>" );

            socket.close();
        }
        catch( final SocketException se )
        {
            getLogger().debug( "Socket to " + remoteHost + " closed remotely in HelloWorld", se );
        }
        catch( final InterruptedIOException iioe )
        {
            getLogger().debug( "Socket to " + remoteHost + " timeout in HelloWorld", iioe );
        }
        catch( final IOException ioe )
        {
            getLogger().debug( "Exception in HelloWorld handling socket to " + remoteHost,
                               ioe );
        }
        catch( final Exception e )
        {
            getLogger().debug( "Exception in HelloWorld opening socket", e );
        }
        finally
        {
            try
            {
                socket.close();
            }
            catch( final IOException ioe )
            {
                getLogger().error( "Exception closing socket ", ioe );
            }
        }

        getLogger().info( "Connection from " + remoteHost + " (" + remoteIP + ")" );


        // A test of shutting down a block & app programatically.
        if( c_counter >= 10 )
        {
            System.out.println( "Testing Auto-Shutdown after 10 requests.." );
            m_context.requestShutdown();
        }
    }
}
