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
package org.jcontainer.loom.components.manager;

import java.util.ArrayList;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;

/**
 * This is a hosted version of System Manager. It assumes
 * a MBeanServer is already running.
 *
 * @author <a href="mailto:sshort at postx.com">Steve Short</a>
 */
public class HostedSystemManager
    extends AbstractJMXManager
{
    protected MBeanServer createMBeanServer()
        throws Exception
    {
        final ArrayList serverList = MBeanServerFactory.findMBeanServer( null );

        if( serverList.size() == 0 )
        {
            getLogger().debug( "HostedSystemManager createMBeanServer no MBeanServer could be found" );
            return null;
        }

        MBeanServer ms = (MBeanServer)serverList.get( 0 );
        getLogger().debug( "HostedSystemManager createMBeanServer \"" + ms + "\"" );
        return ms;
    }
}
