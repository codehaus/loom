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
import org.jcontainer.loom.tools.infobuilder.data.otherpkg.Service2;
import org.jcontainer.loom.tools.infobuilder.data.otherpkg.Service3;

/**
 * A simple avalon component to test QDox loading of info etc.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003-06-26 09:00:53 $
 * @phoenix.component
 * @phoenix.service type="Service1"
 * @phoenix.service type="Service2"
 * @phoenix.service type="Service3"
 */
public class QDoxComponent1
    extends AbstractLogEnabled
    implements Serializable, Service1, Service2, Service3, Serviceable, Contextualizable, Configurable
{
    /**
     * @phoenix.logger
     * @phoenix.logger name="foo"
     */
    public void enableLogging( Logger logger )
    {
        super.enableLogging( logger );
    }

    /**
     * @phoenix.context type="Context"
     * @phoenix.entry key="foo" type="ClassLoader"
     * @phoenix.entry key="bar" type="Logger"
     * @phoenix.entry key="baz" type="java.io.File"
     */
    public void contextualize( Context context )
        throws ContextException
    {
    }

    /**
     * @phoenix.dependency key="foo" type="Service3"
     * @phoenix.dependency type="Service3"
     * @phoenix.dependency type="Service2" optional="true"
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
