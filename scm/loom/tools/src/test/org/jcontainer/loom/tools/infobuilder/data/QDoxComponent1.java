/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.tools.infobuilder.data;

import java.io.Serializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.phoenix.BlockContext;
import org.jcontainer.loom.tools.infobuilder.data.otherpkg.Service2;
import org.jcontainer.loom.tools.infobuilder.data.otherpkg.Service3;

/**
 * A simple avalon component to test QDox loading of info etc.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.3 $ $Date: 2003-10-06 12:48:52 $
 * @phoenix.component version="1.0"
 * @phoenix.service type="Service1"
 * @phoenix.service type="Service2"
 * @phoenix.service type="Service3"
 */
public class QDoxComponent1
    extends AbstractLogEnabled
    implements Serializable, Service1, Service2, Service3, Serviceable, Contextualizable, Configurable
{
    private BlockContext m_blockContext;

    /**
     * @phoenix.logger
     * @phoenix.logger name="foo"
     */
    public void enableLogging( Logger logger )
    {
        super.enableLogging( logger );
    }

    /**
     * @phoenix.context type="BlockContext"
     */
    public void contextualize( Context context )
        throws ContextException
    {
        m_blockContext = (BlockContext)context;
    }

    /**
     * @phoenix.dependency key="foo" type="Service3"
     * @phoenix.dependency type="Service3"
     */
    public void service( ServiceManager manager )
        throws ServiceException
    {
    }

    /**
     * @phoenix.configuration type="http://relaxng.org/ns/structure/1.0"
     */
    public void configure( Configuration configuration )
        throws ConfigurationException
    {
    }
}
