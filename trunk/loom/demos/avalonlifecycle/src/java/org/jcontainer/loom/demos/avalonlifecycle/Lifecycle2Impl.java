/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 *
 * This product includes software developed by the
 * Apache Software Foundation (http://www.apache.org/).
 */
package org.jcontainer.loom.demos.avalonlifecycle;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;

/**
 * A demo of the lifecycle methods.  Mount the SAR fle contaning there blocks in Loom, go
 * to the JMX console ..
 *   http://localhost:8082/mbean?objectname=Loom%3Aapplication%3Ddemo-avalonlifecycle%2Ctopic%3DApplication
 * .. and try stopt/starting the blocks.
 * @phoenix:block
 * @author  Paul Hammant <Paul_Hammant@yahoo.com>
 * @version 1.0
 */
public class Lifecycle2Impl implements LogEnabled, Startable, Initializable, Contextualizable,
    Serviceable, Configurable, Disposable
{

    private Lifecycle1 m_lifecycle1;

    public Lifecycle2Impl()
    {
        System.out.println( "Lifecycle2Impl.constructor() called. "
                            + "(You should never do too much in here)" );
        System.out.flush();
    }

    // Lifecycle methods themselves.


    /**
     * Enable Logging
     * @param logger The logger to use
     */
    public void enableLogging( Logger logger )
    {
        System.out.println( "Lifecycle2Impl.enableLogging() called." );
        System.out.flush();
    }

    /**
     * Start
     * @throws Exception If a problem
     */
    public void start() throws Exception
    {
        System.out.println( "Lifecycle2Impl.start() called." );
        System.out.flush();
    }

    /**
     * Stop
     * @throws Exception If a problem
     */
    public void stop() throws Exception
    {
        System.out.println( "Lifecycle2Impl.stop() called." );
        System.out.flush();
    }

    /**
     * Initialize
     * @throws Exception If a problem
     */
    public void initialize() throws Exception
    {
        System.out.println( "Lifecycle2Impl.initialize() called." );
        System.out.flush();

        System.out.println( "Lifecycle2Impl.initialize(), Lifecycle1.myServiceMethod() "
                            + "method called result = " + m_lifecycle1.myServiceMethod() );
        System.out.flush();

    }

    /**
     * Contextualize
     * @throws ContextException If a problem
     */
    public void contextualize( Context context ) throws ContextException
    {
        System.out.println( "Lifecycle2Impl.contextualize() called (things like base directory passed in here)." );
        System.out.flush();
    }

    /**
     *
     * @phoenix:dependency name="org.jcontainer.loom.demos.avalonlifecycle.Lifecycle1"
     *
     * @param serviceManager
     * @throws ServiceException
     */
    public void service( ServiceManager serviceManager ) throws ServiceException
    {
        System.out.println( "Lifecycle2Impl.service() called "
                            + "(lookup on other services possible now)." );
        System.out.println( "Lifecycle2Impl.service(), Lifecycle1 service looked up" );
        m_lifecycle1 = (Lifecycle1)
            serviceManager.lookup( Lifecycle1.class.getName() );
    }

    /**
     * Configure
     * @throws ConfigurationException If a problem
     */
    public void configure( Configuration configuration ) throws ConfigurationException
    {
        System.out.println( "Lifecycle2Impl.configure() called (configuration from config.xml passed here)." );
        System.out.flush();
    }

    /**
     * Dispose
     */
    public void dispose()
    {
        System.out.println( "Lifecycle2Impl.dispose() Called" );
        System.out.flush();
    }

}
