/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.components.assembler.data;

import java.util.Arrays;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;

/**
 * A test component.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.2 $ $Date: 2003-10-16 14:45:54 $
 * @dna.component
 */
public class Component3
    implements Serviceable
{
    /**
     * @dna.dependency type="Service1[]"
     */
    public void service( final ServiceManager manager )
        throws ServiceException
    {
        final Service1[] services =
            (Service1[])manager.lookup( Service1[].class.getName() );
        System.out.println( "Passed the following services: " +
                            Arrays.asList( services ) );
        if( 3 != services.length )
        {
            final String message =
                "Expected to get 3 services but got " + services.length;
            throw new ServiceException( Service1[].class.getName(), message );
        }

        checkEntry( services, 0 );
        checkEntry( services, 1 );
        checkEntry( services, 2 );
    }

    private void checkEntry( final Service1[] services, final int index ) throws ServiceException
    {
        if( null == services[ index ] )
        {
            final String message = "Expected non null service entry for " + index;
            throw new ServiceException( Service1[].class.getName(), message );
        }
    }
}
