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

import java.rmi.RemoteException;

import org.apache.avalon.cornerstone.services.rmification.RMIfication;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;

/**
 * @phoenix:block
 * @phoenix:service name="org.jcontainer.loom.demos.helloworld.RMIHelloWorldServer"
 *
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 */
public class RMIHelloWorldServerImpl
    extends AbstractLogEnabled
    implements Serviceable, Configurable, Initializable, RMIHelloWorldServer
{
    private RMIfication m_rmification;
    private String m_publicationName;

    /**
     *
     * @phoenix:dependency name="org.apache.avalon.cornerstone.services.rmification.RMIfication"
     *
     */
    public void service( final ServiceManager serviceManager )
        throws ServiceException
    {
        m_rmification = (RMIfication)serviceManager.lookup( RMIfication.ROLE );
    }

    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        m_publicationName = configuration.getChild( "pub-name" ).getValue();
    }

    public void initialize()
        throws Exception
    {
        m_rmification.publish( this, m_publicationName );
    }

    public void dispose()
    {
        try
        {
            m_rmification.unpublish( m_publicationName );
        }
        catch( final Exception e )
        {
            getLogger().error( "Fail to unpublish service", e );
        }
    }

    public String sayHello( final String yourName )
        throws RemoteException
    {
        return "Hello " + yourName + ".";
    }
}
