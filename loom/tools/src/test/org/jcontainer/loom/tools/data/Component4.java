/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.tools.data;

import java.util.Map;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;

/**
 * A test component.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.3 $ $Date: 2003-10-16 05:11:33 $
 * @phoenix:block
 */
public class Component4
    implements Serviceable
{
    private static final String KEY = Service2.class.getName() + "{}";

    /**
     * @dna.dependency key="org.jcontainer.loom.tools.data.Service2{}"
     *                 type="org.jcontainer.loom.tools.data.Service2{}"
     *                 optional="false"
     */
    public void service( final ServiceManager manager )
        throws ServiceException
    {
        final Map services =
            (Map)manager.lookup( KEY );
        System.out.println( "Passed the following services: " +
                            services );

        final int size = services.size();
        if( 3 != size )
        {
            final String message =
                "Expected to get 3 services but got " + size;
            throw new ServiceException( KEY, message );
        }

        checkService( "c2a", services );
        checkService( "c2b", services );
        checkService( "fred", services );

        checkReadOnly( services );
    }

    private void checkReadOnly( final Map services )
        throws ServiceException
    {
        try
        {
            services.put( "s", services.get( "fred" ) );
        }
        catch( Exception e )
        {
            return;
        }

        throw new ServiceException( KEY,
                                    "Was able to modify map " +
                                    "retrieved from ServiceManager" );
    }

    private void checkService( final String name,
                               final Map services )
        throws ServiceException
    {
        final Object service1 = services.get( name );
        if( null == service1 )
        {
            final String message =
                "Expected to get service " + name;
            throw new ServiceException( name, message );
        }
        else if( !( service1 instanceof Service2 ) )
        {
            final String message =
                "Expected to service " + name +
                " to be of type Service2 but was " +
                "of type: " + service1.getClass().getName();
            throw new ServiceException( KEY, message );
        }
    }
}
