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
package org.jcontainer.loom.components.manager;

import java.io.File;
import javax.management.Attribute;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import mx4j.adaptor.rmi.jrmp.JRMPAdaptorMBean;
import mx4j.log.Log;
import mx4j.util.StandardMBeanProxy;
import org.jcontainer.dna.Configurable;
import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.jcontainer.loom.interfaces.ContainerConstants;
import org.realityforge.salt.i18n.ResourceManager;
import org.realityforge.salt.i18n.Resources;

/**
 * This component is responsible for managing loom instance.
 *
 * @author <a href="mail@leosimons.com">Leo Simons</a>
 * @author Peter Donald
 * @author <a href="mailto:Huw@mmlive.com">Huw Roberts</a>
 */
public class MX4JSystemManager
    extends AbstractJMXManager
    implements Configurable
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( MX4JSystemManager.class );

    private static final String DEFAULT_NAMING_FACTORY =
        "com.sun.jndi.rmi.profile.RegistryContextFactory";
    private static final String DEFAULT_HTTPADAPTER_HOST = "localhost";
    private static final int DEFAULT_HTTPADAPTER_PORT =
        Integer.getInteger( "loom.adapter.http", 8082 ).intValue();
    private static final int DEFAULT_RMIREGISTRY_PORT =
        Integer.getInteger( "loom.rmiregistry.port", 1099 ).intValue();

    private String m_host;
    private int m_port;
    private boolean m_rmi;
    private int m_rmi_registry_port;
    private String m_stylesheetDir;
    private String m_namingFactory;
    private String m_password;
    private String m_username;
    private boolean m_http;

    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        m_host = configuration.getChild( "manager-adaptor-host" ).
            getValue( DEFAULT_HTTPADAPTER_HOST );

        m_port = configuration.getChild( "manager-adaptor-port" ).
            getValueAsInteger( DEFAULT_HTTPADAPTER_PORT );

        //This is for backwards compatability with old-style
        //RI JMX implementation
        m_port = configuration.getChild( "port" ).
            getValueAsInteger( m_port );

        getLogger().debug( "MX4J HTTP listener port: " + m_port );

        m_rmi =
        configuration.getChild( "enable-rmi-adaptor" ).getValueAsBoolean(
            false );
        m_rmi_registry_port =
        configuration.getChild( "rmi-registry-port" ).getValueAsInteger(
            DEFAULT_RMIREGISTRY_PORT );
        m_http =
        configuration.getChild( "enable-http-adaptor" ).getValueAsBoolean(
            false );

        m_namingFactory =
        configuration.getChild( "rmi-naming-factory" ).getValue(
            DEFAULT_NAMING_FACTORY );

        final String stylesheets =
            configuration.getChild( "stylesheets-dir" ).getValue( null );
        if( null != stylesheets )
        {
            m_stylesheetDir = new File( stylesheets ).getAbsolutePath();
            getLogger().debug( "MX4J XSL Stylesheet Dir: " + m_stylesheetDir );
        }

        /*<user>
           <name>user</name>
           <password>passwd</password>
        </user>*/
        final Configuration userConfig = configuration.getChild( "user" );
        m_username = userConfig.getChild( "name" ).getValue( null );
        m_password = userConfig.getChild( "password" ).getValue( null );
    }

    public void initialize()
        throws Exception
    {
        super.initialize();

        final MBeanServer mBeanServer = getMBeanServer();

        if( m_http )
        {
            startHttpAdaptor( mBeanServer );
        }

        if( m_rmi )
        {
            startRMIAdaptor( mBeanServer );
        }
    }

    public void dispose()
    {
        final MBeanServer mBeanServer = getMBeanServer();

        if( m_http )
        {
            stopHttpAdaptor( mBeanServer );
        }
        if( m_rmi )
        {
            stopRMIAdaptor( mBeanServer );
        }

        super.dispose();
    }

    private void startHttpAdaptor( final MBeanServer mBeanServer )
        throws Exception
    {
        final ObjectName adaptorName = new ObjectName( "Http:name=HttpAdaptor" );
        mBeanServer.createMBean( "mx4j.adaptor.http.HttpAdaptor",
                                 adaptorName,
                                 null );
        mBeanServer.setAttribute( adaptorName,
                                  new Attribute( "Host", m_host ) );
        mBeanServer.setAttribute( adaptorName,
                                  new Attribute( "Port",
                                                 new Integer( m_port ) ) );

        if( null != m_username )
        {
            configureAuthentication( mBeanServer, adaptorName );
        }

        configureProcessor( mBeanServer, adaptorName );

        // starts the server
        mBeanServer.invoke( adaptorName, "start", null, null );
    }

    private void configureProcessor( final MBeanServer mBeanServer,
                                     final ObjectName adaptorName )
        throws Exception
    {
        final ObjectName processorName = new ObjectName(
            "Http:name=XSLTProcessor" );
        mBeanServer.createMBean( "mx4j.adaptor.http.XSLTProcessor",
                                 processorName,
                                 null );
        mBeanServer.setAttribute( adaptorName,
                                  new Attribute( "ProcessorName",
                                                 processorName ) );

        if( null != m_stylesheetDir )
        {
            final Attribute stylesheetDir = new Attribute( "File",
                                                           m_stylesheetDir );
            mBeanServer.setAttribute( processorName, stylesheetDir );
        }

        final Attribute useCache =
            new Attribute( "UseCache", Boolean.FALSE );
        mBeanServer.setAttribute( processorName, useCache );
    }

    private void configureAuthentication( final MBeanServer mBeanServer,
                                          final ObjectName adaptorName )
        throws InstanceNotFoundException,
               MBeanException,
               ReflectionException,
               AttributeNotFoundException,
               InvalidAttributeValueException
    {
        // add user names
        mBeanServer.invoke( adaptorName,
                            "addAuthorization",
                            new Object[]{m_username, m_password},
                            new String[]{"java.lang.String",
                                         "java.lang.String"} );

        // use basic authentication
        mBeanServer.setAttribute( adaptorName,
                                  new Attribute( "AuthenticationMethod",
                                                 "basic" ) );
    }

    private void stopHttpAdaptor( final MBeanServer server )
    {
        stopJMXMBean( server, "Http:name=HttpAdaptor" );
    }

    private void startRMIAdaptor( final MBeanServer server )
        throws Exception
    {
        // Create and start the naming service
        final ObjectName naming = new ObjectName( "Naming:type=rmiregistry" );
        server.createMBean( "mx4j.tools.naming.NamingService",
                            naming,
                            null,
                            new Object[]{new Integer( m_rmi_registry_port )},
                            new String[]{"int"}
        );
        server.invoke( naming, "start", null, null );

        // Create the JRMP adaptor
        final ObjectName adaptor = new ObjectName( "Adaptor:protocol=JRMP" );
        server.createMBean( "mx4j.adaptor.rmi.jrmp.JRMPAdaptor",
                            adaptor,
                            null );
        final JRMPAdaptorMBean mbean =
            (JRMPAdaptorMBean)StandardMBeanProxy.create(
                JRMPAdaptorMBean.class,
                server,
                adaptor );
        // Set the JNDI name with which will be registered
        mbean.setJNDIName( "jrmp" );

        mbean.putNamingProperty( javax.naming.Context.INITIAL_CONTEXT_FACTORY,
                                 m_namingFactory );
        mbean.putNamingProperty( javax.naming.Context.PROVIDER_URL,
                                 "rmi://localhost:" + m_rmi_registry_port );

        // Register the JRMP adaptor in JNDI and start it
        mbean.start();
    }

    private void stopRMIAdaptor( final MBeanServer server )
    {
        // stop the JRMP adaptor
        stopJMXMBean( server, "Adaptor:protocol=JRMP" );
        // stop the naming service
        stopJMXMBean( server, "Naming:type=rmiregistry" );
    }

    protected MBeanServer createMBeanServer()
        throws Exception
    {
        MX4JLoggerAdapter.setLogger( getLogger() );
        Log.redirectTo( new MX4JLoggerAdapter() );
        return MBeanServerFactory.createMBeanServer(
            ContainerConstants.SOFTWARE );
    }

    private void stopJMXMBean( final MBeanServer mBeanServer,
                               final String name )
    {
        try
        {
            final ObjectName objectName = new ObjectName( name );
            mBeanServer.invoke( objectName, "stop", null, null );
        }
        catch( final Exception e )
        {
            final String message =
                REZ.format( "jmxmanager.error.jmxmbean.dispose", name );
            getLogger().error( message, e );
        }
    }
}
