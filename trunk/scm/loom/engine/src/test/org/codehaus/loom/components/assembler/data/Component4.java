/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.codehaus.loom.components.assembler.data;

import java.util.Iterator;
import java.util.Map;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;

/**
 * A test component.
 *
 * @author Peter Donald
 * @version $Revision: 1.1 $ $Date: 2004-04-19 22:24:07 $
 * @dna.component
 */
public class Component4
    implements Serviceable
{
    /**
     * @dna.dependency type="Service1{}"
     */
    public void service( final ServiceManager manager )
        throws ServiceException
    {
        final Map services =
            (Map)manager.lookup( Service1.class.getName() + "{}" );
        System.out.println( "Passed the following services: " +
                            services.keySet() );
        if( 3 != services.size() )
        {
            final String message =
                "Expected to get 3 services but got " + services.size();
            throw new ServiceException( Service1.class.getName() + "{}",
                                        message );
        }

        final Iterator iterator = services.keySet().iterator();
        while( iterator.hasNext() )
        {
            final String key = (String)iterator.next();
            final Service1 service1 = (Service1)services.get( key );
            if( null == service1 )
            {
                final String message = "Expected non null service entry for " +
                    key;
                throw new ServiceException( Service1[].class.getName(),
                                            message );
            }
        }
    }
}
