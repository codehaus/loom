/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.codehaus.loom.components.assembler.data;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;

/**
 * @dna.component
 */
public class Component1
    implements Serviceable
{
    /**
     * @dna.dependency type="Service1"
     */
    public void service( final ServiceManager manager )
        throws ServiceException
    {
        manager.lookup( Service1.class.getName() );
    }
}
