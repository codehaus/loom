/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.tools.data;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.jcontainer.loom.tools.data.Service1;
import org.jcontainer.loom.tools.data.Service2;

/**
 * A test component.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.4 $ $Date: 2003-10-16 09:23:58 $
 * @dna.component
 * @dna.service type="org.jcontainer.loom.tools.data.Service1"
 */
public class Component1
    implements Serviceable, Service1
{
    /**
     * @dna.dependency type="Service2"
     */
    public void service( final ServiceManager manager )
        throws ServiceException
    {
        manager.lookup( Service2.class.getName() );
    }
}
