/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 *
 * This product includes software developed by the
 * Apache Software Foundation (http://www.apache.org/).
 */
package org.jcontainer.loom.components.embeddor;

import java.io.File;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.jcontainer.loom.interfaces.Application;

/**
 * WARNING: DO NOT USE THIS SERVLET FOR PRODUCTION SERVICE. THIS IS EXPERIMENTAL.
 * Embeddor to host only a single application.
 * It is required that the user set parameter
 * <ul>
 *   <li>application-location = location of application directory or file
 * </ul>
 * Other parameters are inherited from Embeddor.
 *
 * @author <a href="colus@isoft.co.kr">Eung-ju Park</a>
 * @author <a href="peter at realityforge.org">Peter Donald</a>
 * @deprecated
 */
public class SingleAppEmbeddor
    extends DefaultEmbeddor
    implements ComponentManager
{
    ///Sole application hosted in kernel
    private Application m_application;

    /**
     * Deploy a single application.
     *
     * @throws Exception if an error occurs
     */
    protected void deployDefaultApplications()
        throws Exception
    {
        final String applicationName =
            getParameters().getParameter( "application-name", "default" );
        final String applicationLocation = getParameters().getParameter( "application-location" );
        final File directory = new File( applicationLocation );
        deployFile( applicationName, directory );
        m_application = getKernel().getApplication( applicationName );
    }

    /**
     * List all block names in application.
     *
     * @return the list of all block names
     */
    public String[] list()
    {
        return m_application.getBlockNames();
    }

    /**
     * Get a Block by name.
     *
     * @return the block in application
     */
    public Component lookup( final String role )
        throws ComponentException
    {
        final Object component = m_application.getBlock( role );
        if( null == component )
        {
            throw new ComponentException( role, "Could not find component" );
        }
        return (Component)component;
    }

    public boolean hasComponent( final String role )
    {
        return ( null != m_application.getBlock( role ) );
    }

    /**
     * Release block back to application.
     *
     * @param component the block
     */
    public void release( final Component component )
    {
    }
}
