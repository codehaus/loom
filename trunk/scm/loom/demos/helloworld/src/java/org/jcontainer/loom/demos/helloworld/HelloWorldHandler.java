/* ====================================================================
 * JContainer Software License, version 1.1
 *
 * Copyright (c) 2003, JContainer Group. All rights reserved.
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
 * 3. Neither the name of the JContainer Group nor the name "Loom" nor
 *    the names of its contributors may be used to endorse or promote
 *    products derived from this software without specific prior
 *    written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * ====================================================================
 *
 * JContainer Loom includes code from the Apache Software Foundation
 *
 * ====================================================================
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
package org.jcontainer.loom.demos.helloworld;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.phoenix.BlockContext;
import org.codehaus.spice.netserve.connection.RequestHandler;

/**
 * This handles an individual incoming request.  It outputs a greeting as html.
 *
 * @author Paul Hammant <Paul_Hammant@yahoo.com>
 * @author Federico Barbieri <scoobie@systemy.it>
 * @version 1.0
 */
final class HelloWorldHandler
    extends AbstractLogEnabled
    implements RequestHandler
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
     * Handle a connection. This handler is responsible for processing
     * connections as they occur.
     *
     * @param socket the connection
     * @throws IOException if an error reading from socket occurs
     */
    public void handleConnection( final Socket socket )
    {
        final String remoteHost = socket.getInetAddress().getHostName();
        final String remoteIP = socket.getInetAddress().getHostAddress();

        try
        {
            final PrintWriter out = new PrintWriter( socket.getOutputStream(),
                                                     true );
            out.println( "<html><body><b>" +
                         m_greeting +
                         "!</b><br> Requests so far = " +
                         ++c_counter + "<br>" );
            out.println( "you are " + remoteHost + " at " + remoteIP + "<br>" );
            out.println( "<p>The application will shutdown after 10 requests" );
            out.println( "</body></html>" );

            socket.close();
        }
        catch( final SocketException se )
        {
            getLogger().debug(
                "Socket to " + remoteHost + " closed remotely in HelloWorld",
                se );
        }
        catch( final InterruptedIOException iioe )
        {
            getLogger().debug(
                "Socket to " + remoteHost + " timeout in HelloWorld", iioe );
        }
        catch( final IOException ioe )
        {
            getLogger().debug(
                "Exception in HelloWorld handling socket to " + remoteHost,
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

        getLogger().info(
            "Connection from " + remoteHost + " (" + remoteIP + ")" );


        // A test of shutting down a block & app programatically.
        if( c_counter >= 10 )
        {
            System.out.println( "Testing Auto-Shutdown after 10 requests.." );
            m_context.requestShutdown();
        }
    }

    /**
     * Shutdown the handler and any requests currently being handled.
     */
    public void shutdown( long timeout )
    {
    }
}
