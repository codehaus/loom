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

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.jcontainer.loom.demos.distributed.api.Server;

/**
 * DefaultServer is the block implementing the <code>Server</code> server interface. 
 * It delegates implentation of interface to <code>ServerImpl</code> class, which 
 * is placed in this package for simplicity but may also be implemented elsewhere.
 * 
 * @author  <a href="mailto:Paul_Hammant@yahoo.com">Paul Hammant</a>
 * @author  <a href="mailto:mauro.talevi at aquilonia.org">Mauro Talevi</a>
 */
public class DefaultServer
    extends AbstractLogEnabled
    implements Server, Configurable, Startable, Initializable
{
    private int m_port;
    private ServerImpl m_server;
    private SocketThread m_socketThread;

    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        m_port = configuration.getChild( "port" ).getValueAsInteger( 7777 );
    }

    public void initialize()
        throws Exception
    {
        m_server = new ServerImpl();
    }

    public void start()
        throws Exception
    {
        m_socketThread = new SocketThread( m_server, m_port );
        m_socketThread.start();

        System.out.println( "Server started on port " + m_port );
    }

    public void stop()
        throws Exception
    {
        m_socketThread.interrupt();
        m_socketThread = null;

        System.out.println( "Server stopped on port " + m_port );
    }

    public void message( String string )
    {
        m_server.message( string );
    }


}
