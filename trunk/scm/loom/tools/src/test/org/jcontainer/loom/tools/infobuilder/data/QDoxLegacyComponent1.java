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
package org.jcontainer.loom.tools.infobuilder.data;

import java.io.Serializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.jcontainer.loom.tools.infobuilder.data.otherpkg.Service2;
import org.jcontainer.loom.tools.infobuilder.data.otherpkg.Service3;

/**
 * A simple avalon component to test QDox loading of info etc.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.2 $ $Date: 2003-06-29 01:25:30 $
 * @phoenix:block
 * @phoenix:service name="org.jcontainer.loom.tools.infobuilder.data.Service1"
 * @phoenix:service name="org.jcontainer.loom.tools.infobuilder.data.otherpkg.Service2"
 * @phoenix:service name="org.jcontainer.loom.tools.infobuilder.data.otherpkg.Service3"
 * @phoenix:mx name="org.jcontainer.loom.tools.infobuilder.data.otherpkg.ServiceMBean"
 */
public class QDoxLegacyComponent1
    extends AbstractLogEnabled
    implements Serializable, Service1, Service2, Service3, Serviceable, Configurable
{
    /**
     * @phoenix:dependency role="foo" name="org.jcontainer.loom.tools.infobuilder.data.otherpkg.Service3"
     * @phoenix:dependency name="org.jcontainer.loom.tools.infobuilder.data.otherpkg.Service3"
     * @phoenix:dependency name="org.jcontainer.loom.tools.infobuilder.data.otherpkg.Service2"
     */
    public void service( ServiceManager manager )
        throws ServiceException
    {
    }

    /**
     * @phoenix:configuration-schema type="http://relaxng.org/ns/structure/1.0"
     */
    public void configure( Configuration configuration )
        throws ConfigurationException
    {
    }
}
