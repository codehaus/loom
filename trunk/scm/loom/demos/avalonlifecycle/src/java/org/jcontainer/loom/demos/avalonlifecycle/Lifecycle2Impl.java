/* ====================================================================
 * JContainer Software License, version 1.1
 *
 * Copyright (c) 2003, JContainer Group. All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. Neither the name of the JContainer Group nor the name "Loom" nor
 *    the names of its contributors may be used to endorse or promote
 *    products derived from this software without specific prior
 *    written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * ====================================================================
 *
 * JContainer Loom includes code from the Apache Software Foundation
 *
 * ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1997-2003 The Apache Software Foundation. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *    "This product includes software developed by the
 *    Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software
 *    itself, if and wherever such third-party acknowledgments
 *    normally appear.
 *
 * 4. The names "Jakarta", "Avalon", and "Apache Software Foundation"
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
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
 * @dna.component
 * @author  Paul Hammant <Paul_Hammant@yahoo.com>
 * @version 1.0
 */
public class Lifecycle2Impl
    implements LogEnabled, Startable, Initializable, Contextualizable,
    Serviceable, Configurable, Disposable
{
    private Lifecycle1 m_lifecycle1;

    public Lifecycle2Impl()
    {
        System.out.println( "Lifecycle2Impl.constructor() called. "
                            + "(You should never do too much in here)" );
        System.out.flush();
    }

    public void enableLogging( Logger logger )
    {
        System.out.println( "Lifecycle2Impl.enableLogging() called." );
        System.out.flush();
    }

    public void contextualize( Context context )
        throws ContextException
    {
        System.out.println( "Lifecycle2Impl.contextualize() called (things like base directory passed in here)." );
        System.out.flush();
    }

    /**
     * @dna.dependency key="org.jcontainer.loom.demos.avalonlifecycle.Lifecycle1"
     *                 type="org.jcontainer.loom.demos.avalonlifecycle.Lifecycle1"
     *                 optional="false"
     */
    public void service( ServiceManager serviceManager ) throws ServiceException
    {
        System.out.println( "Lifecycle2Impl.service() called "
                            + "(lookup on other services possible now)." );
        System.out.println( "Lifecycle2Impl.service(), Lifecycle1 service looked up" );
        m_lifecycle1 = (Lifecycle1)
            serviceManager.lookup( Lifecycle1.class.getName() );
    }

    public void configure( Configuration configuration )
        throws ConfigurationException
    {
        System.out.println( "Lifecycle2Impl.configure() called (configuration from config.xml passed here)." );
        System.out.flush();
    }

    public void initialize()
        throws Exception
    {
        System.out.println( "Lifecycle2Impl.initialize() called." );
        System.out.flush();
        System.out.println( "Lifecycle2Impl.initialize(), Lifecycle1.myServiceMethod() "
                            + "method called result = " + m_lifecycle1.myServiceMethod() );
        System.out.flush();
    }

    public void start()
        throws Exception
    {
        System.out.println( "Lifecycle2Impl.start() called." );
        System.out.flush();
    }

    public void stop()
        throws Exception
    {
        System.out.println( "Lifecycle2Impl.stop() called." );
        System.out.flush();
    }

    public void dispose()
    {
        System.out.println( "Lifecycle2Impl.dispose() Called" );
        System.out.flush();
    }
}
