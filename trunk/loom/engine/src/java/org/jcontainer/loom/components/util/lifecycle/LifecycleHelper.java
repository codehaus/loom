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
package org.jcontainer.loom.components.util.lifecycle;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.excalibur.instrument.InstrumentManageable;
import org.apache.excalibur.instrument.InstrumentManager;
import org.apache.excalibur.instrument.Instrumentable;
import org.jcontainer.dna.AbstractLogEnabled;
import org.jcontainer.loom.interfaces.LoomException;
import org.realityforge.salt.i18n.ResourceManager;
import org.realityforge.salt.i18n.Resources;

/**
 * This is a class to help an Application manage the lifecycle of a component.
 * The implementation provides support for the processing of a component through
 * each lifecycle stage, and manage errors in a consistent way.
 *
 * @author Peter Donald
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 */
public class LifecycleHelper
    extends AbstractLogEnabled
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( LifecycleHelper.class );

    //Constants to designate stages
    private static final int STAGE_CREATE = 0;
    private static final int STAGE_LOGGER = 1;
    private static final int STAGE_INSTRUMENTMGR = 2;
    private static final int STAGE_CONTEXT = 3;
    private static final int STAGE_COMPOSE = 4;
    private static final int STAGE_CONFIG = 5;
    private static final int STAGE_PARAMETER = 6;
    private static final int STAGE_INIT = 7;
    private static final int STAGE_INSTRUMENTABLE = 8;
    private static final int STAGE_START = 9;
    private static final int STAGE_STOP = 10;
    private static final int STAGE_DISPOSE = 11;
    private static final int STAGE_DESTROY = 12;

    /**
     * Method to run a component through it's startup phase. Errors that occur
     * during startup will be logged appropriately and cause exceptions with
     * useful messages to be raised.
     *
     * @param name the name of the component
     * @param entry the entry representing object
     * @param provider the resource provider
     * @return the newly created component
     * @throws LoomException if an error occurs when the component passes
     * through a specific lifecycle stage
     */
    public Object startup( final String name,
                           final Object entry,
                           final ResourceProvider provider )
        throws LoomException
    {
        int stage = 0;
        try
        {
            //Creation stage
            stage = STAGE_CREATE;
            notice( name, stage );
            final Object object = provider.createObject( entry );
            final InstrumentManager instrumentManager = provider.createInstrumentManager(
                entry );

            //LogEnabled stage
            stage = STAGE_LOGGER;
            if( object instanceof LogEnabled )
            {
                notice( name, stage );
                final Logger logger = provider.createLogger( entry );
                ContainerUtil.enableLogging( object, logger );
            }

            //InstrumentManageable stage
            stage = STAGE_INSTRUMENTMGR;
            if( object instanceof InstrumentManageable )
            {
                notice( name, stage );

                ( (InstrumentManageable)object ).setInstrumentManager(
                    instrumentManager );
            }

            //Contextualize stage
            stage = STAGE_CONTEXT;
            if( object instanceof Contextualizable )
            {
                notice( name, stage );
                final Context context = provider.createContext( entry );
                ContainerUtil.contextualize( object, context );
            }

            //Composition stage
            stage = STAGE_COMPOSE;
            if( object instanceof Serviceable )
            {
                notice( name, stage );
                final ServiceManager manager =
                    provider.createServiceManager( entry );
                ContainerUtil.service( object, manager );
            }
            else if( object instanceof Composable )
            {
                notice( name, stage );
                final ComponentManager componentManager =
                    provider.createComponentManager( entry );
                ContainerUtil.compose( object, componentManager );
            }

            //Configuring stage
            stage = STAGE_CONFIG;
            if( object instanceof Configurable )
            {
                notice( name, stage );
                final Configuration configuration =
                    provider.createConfiguration( entry );
                ContainerUtil.configure( object, configuration );
            }

            //Parameterizing stage
            stage = STAGE_PARAMETER;
            if( object instanceof Parameterizable )
            {
                notice( name, stage );
                final Parameters parameters =
                    provider.createParameters( entry );
                ContainerUtil.parameterize( object, parameters );
            }

            //Initialize stage
            stage = STAGE_INIT;
            if( object instanceof Initializable )
            {
                notice( name, stage );
                ContainerUtil.initialize( object );
            }

            //InstrumentManageable stage
            stage = STAGE_INSTRUMENTABLE;
            if( object instanceof Instrumentable )
            {
                notice( name, stage );
                final String instrumentableName = provider.createInstrumentableName(
                    entry );
                final Instrumentable instrumentable = (Instrumentable)object;
                instrumentable.setInstrumentableName( instrumentableName );
                instrumentManager.registerInstrumentable( instrumentable,
                                                          instrumentableName );
            }

            //Start stage
            stage = STAGE_START;
            if( object instanceof Startable )
            {
                notice( name, stage );
                ContainerUtil.start( object );
            }

            return object;
        }
        catch( final Throwable t )
        {
            fail( name, stage, t );

            //fail() throws an exception so next
            //line will never be executed
            return null;
        }
    }

    /**
     * Method to run a component through it's shutdown phase. Errors that occur
     * during shutdown will be logged appropraitely.
     *
     * @param name the name of the component
     * @param object the component to shutdown
     * @throws LoomException if unable to process component
     */
    public void shutdown( final String name,
                          final Object object )
        throws LoomException
    {
        //Stage at which failure occured
        int stage = 0;

        //Failure exception
        Throwable failure = null;

        //Stoppable stage
        if( object instanceof Startable )
        {
            notice( name, STAGE_STOP );
            try
            {
                ContainerUtil.stop( object );
            }
            catch( final Throwable t )
            {
                safeFail( name, STAGE_STOP, t );
                failure = t;
                stage = STAGE_STOP;
            }
        }

        //Disposable stage
        if( object instanceof Disposable )
        {
            notice( name, STAGE_DISPOSE );
            try
            {
                ContainerUtil.dispose( object );
            }
            catch( final Throwable t )
            {
                safeFail( name, STAGE_DISPOSE, t );
                failure = t;
                stage = STAGE_DISPOSE;
            }
        }

        notice( name, STAGE_DESTROY );

        if( null != failure )
        {
            fail( name, stage, failure );
        }
    }

    /**
     * Utility method to report that a lifecycle stage is about to be
     * processed.
     *
     * @param name the name of component that is the subject of the notice
     * @param stage the lifecycle processing stage
     */
    private void notice( final String name, final int stage )
    {
        if( getLogger().isDebugEnabled() )
        {
            final String message =
                REZ.format( "lifecycle.stage.notice",
                            name,
                            new Integer( stage ) );
            getLogger().debug( message );
        }
    }

    /**
     * Utility method to report that there was an error processing specified
     * lifecycle stage.
     *
     * @param name the name of component that caused failure
     * @param stage the lefecycle stage
     * @param t the exception thrown
     */
    private void safeFail( final String name,
                           final int stage,
                           final Throwable t )
    {
        //final String reason = t.getMessage();
        final String reason = t.toString();
        final String message =
            REZ.format( "lifecycle.fail.error",
                        name,
                        new Integer( stage ),
                        reason );
        getLogger().error( message );
    }

    /**
     * Utility method to report that there was an error processing specified
     * lifecycle stage. It will also re-throw an exception with a better error
     * message.
     *
     * @param name the name of block that caused failure
     * @param stage the stage
     * @param t the exception thrown
     * @throws LoomException containing error
     */
    private void fail( final String name,
                       final int stage,
                       final Throwable t )
        throws LoomException
    {
        //final String reason = t.getMessage();
        final String reason = t.toString();
        final String message =
            REZ.format( "lifecycle.fail.error",
                        name,
                        new Integer( stage ), reason );
        getLogger().error( message );
        throw new LoomException( message, t );
    }
}
