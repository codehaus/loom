/* ====================================================================
 * Loom Software License, version 1.1
 *
 * Copyright (c) 2003, Loom Group. All rights reserved.
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
 * 3. Neither the name of the Loom Group nor the name "Loom" nor
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
 * Loom includes code from the Apache Software Foundation
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
package org.codehaus.loom.demos.helloworld;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.phoenix.BlockContext;
import org.codehaus.spice.netserve.connection.RequestHandler;
import org.codehaus.spice.netserve.connection.SocketAcceptorManager;
import org.codehaus.spice.netserve.sockets.ServerSocketFactory;

/**
 * @author Paul Hammant <Paul_Hammant@yahoo.com>
 * @author Federico Barbieri <scoobie@pop.systemy.it>
 * @version 1.0
 * @dna.component
 * @dna.service type="HelloWorldServer"
 * @mx.component
 */
public final class HelloWorldServerImpl
    extends AbstractLogEnabled
    implements HelloWorldServer, Contextualizable, Serviceable,
      Configurable, Initializable, Disposable
{

    private ServerSocketFactory m_socketManager;
    private SocketAcceptorManager m_socketAcceptorManager;
    private BlockContext m_context;
    private String m_greeting = "Hello World!";
    private InetAddress m_bindTo;
    private int m_port;
    private String m_connectionName = "HelloWorldListener";
    private ServerSocket m_serverSocket;
    private HelloWorldHandler m_helloWorldHandler;

    /**
     * Set the greeting message
     *
     * @mx.operation description="Set the greeting."
     * @mx.parameter name="greeting" description="The new greeting message"
     * @param greeting The new greeting message
     */
    public void setGreeting( final String greeting )
    {
        m_greeting = greeting;
        m_helloWorldHandler.setGreeting( greeting );
    }

    /**
     * Fetch the greeting message
     *
     * @mx.attribute description="The current greeting"
     * @return The current greeting message.
     */
    public String getGreeting()
    {
        return m_greeting;
    }

    /**
     * Give the component its context
     */
    public void contextualize( final Context context )
    {
        m_context = (BlockContext)context;
    }

    /**
     * Configure the component
     */
    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        m_port = configuration.getChild( "port" ).getValueAsInteger( 8000 );

        try
        {
            final String bindAddress =
              configuration.getChild( "bind" ).getValue();
            m_bindTo = InetAddress.getByName( bindAddress );
        }
        catch( final UnknownHostException unhe )
        {
            throw new ConfigurationException(
              "Malformed bind parameter", unhe );
        }
    }

    /**
     * Give the component its <code>ServiceManager</code>.
     *
     * @dna.dependency type="org.codehaus.spice.netserve.connection.SocketAcceptorManager"
     * @dna.dependency type="org.codehaus.spice.netserve.sockets.ServerSocketFactory"
     */
    public void service( final ServiceManager serviceManager )
        throws ServiceException
    {
        getLogger().info( "HelloWorldServer.compose()" );

        m_socketManager =
          (ServerSocketFactory)serviceManager.lookup(
            ServerSocketFactory.class.getName() );
        m_socketAcceptorManager =
          (SocketAcceptorManager)serviceManager.lookup(
            SocketAcceptorManager.class.getName() );
    }

    /**
     * Initialize the component
     */
    public void initialize()
        throws Exception
    {
        m_serverSocket =  m_socketManager.createServerSocket( m_port,
                                                              5,
                                                              m_bindTo );
        m_helloWorldHandler = createHandler();
        m_socketAcceptorManager.connect( m_connectionName,
                                         m_serverSocket,
                                         (RequestHandler)m_helloWorldHandler );
        // This is only to help newbies...
        System.out.println(
          "HelloWorld server running with a greeting of '" + m_greeting
          + "'.  Point your browser to http://localhost:" + m_port
          + " to see its page" );
    }

    /**
     * Dispose off the component
     */
    public void dispose()
    {
        try
        {
            m_socketAcceptorManager.disconnect( m_connectionName );
        }
        catch( final Exception e )
        {
            getLogger().warn( "Error while disconnecting.", e );
        }

        try
        {
            m_serverSocket.close();
        }
        catch( final IOException ioe )
        {
            getLogger().warn( "Error while closing server socket.", ioe );
        }
    }

    /**
     * Create a request handler
     *
     * @return A new request handler
     */
    public HelloWorldHandler createHandler()
        throws Exception
    {
        final HelloWorldHandler handler =
          new HelloWorldHandler( m_greeting, m_context );
        setupLogger( handler );
        return handler;
    }

}
