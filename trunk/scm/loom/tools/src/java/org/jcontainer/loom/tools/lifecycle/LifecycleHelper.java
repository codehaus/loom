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
package org.jcontainer.loom.tools.lifecycle;

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
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.excalibur.instrument.InstrumentManageable;
import org.apache.excalibur.instrument.InstrumentManager;
import org.apache.excalibur.instrument.Instrumentable;
import org.jcontainer.loom.tools.lifecycle.LifecycleException;
import org.realityforge.salt.i18n.Resources;
import org.realityforge.salt.i18n.ResourceManager;

/**
 * This is a class to help an Application manage the lifecycle of a component.
 * The implementation provides support for the processing of a component through
 * each lifecycle stage, and manage errors in a consistent way.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
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
     * Method to run a component through it's startup phase.
     * Errors that occur during startup will be logged appropriately and
     * cause exceptions with useful messages to be raised.
     *
     * @param name the name of the component
     * @param entry the entry representing object
     * @param provider the resource provider
     * @return the newly created component
     * @throws LifecycleException if an error occurs when the component passes
     *     through a specific lifecycle stage
     */
    public Object startup( final String name,
                           final Object entry,
                           final ResourceProvider provider )
        throws LifecycleException
    {
        int stage = 0;
        try
        {
            //Creation stage
            stage = STAGE_CREATE;
            notice( name, stage );
            final Object object = provider.createObject( entry );
            final InstrumentManager instrumentManager = provider.createInstrumentManager( entry );

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

                ( (InstrumentManageable)object ).setInstrumentManager( instrumentManager );
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
                final String instrumentableName = provider.createInstrumentableName( entry );
                final Instrumentable instrumentable = (Instrumentable)object;
                instrumentable.setInstrumentableName( instrumentableName );
                instrumentManager.registerInstrumentable( instrumentable, instrumentableName );
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
     * Method to run a component through it's shutdown phase.
     * Errors that occur during shutdown will be logged appropraitely.
     *
     * @param name the name of the component
     * @param object the component to shutdown
     * @throws LifecycleException if unable to process component
     */
    public void shutdown( final String name,
                          final Object object )
        throws LifecycleException
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
     * Utility method to report that a lifecycle stage is about to be processed.
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
     * Utility method to report that there was an error processing
     * specified lifecycle stage.
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
     * Utility method to report that there was an error processing
     * specified lifecycle stage. It will also re-throw an exception
     * with a better error message.
     *
     * @param name the name of block that caused failure
     * @param stage the stage
     * @param t the exception thrown
     * @throws LifecycleException containing error
     */
    private void fail( final String name,
                       final int stage,
                       final Throwable t )
        throws LifecycleException
    {
        //final String reason = t.getMessage();
        final String reason = t.toString();
        final String message =
            REZ.format( "lifecycle.fail.error",
                        name,
                        new Integer( stage ), reason );
        getLogger().error( message );
        throw new LifecycleException( message, t );
    }
}
