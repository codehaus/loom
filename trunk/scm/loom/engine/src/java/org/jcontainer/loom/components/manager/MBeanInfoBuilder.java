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

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import javax.management.Descriptor;
import javax.management.MBeanParameterInfo;
import javax.management.modelmbean.ModelMBeanAttributeInfo;
import javax.management.modelmbean.ModelMBeanConstructorInfo;
import javax.management.modelmbean.ModelMBeanInfoSupport;
import javax.management.modelmbean.ModelMBeanNotificationInfo;
import javax.management.modelmbean.ModelMBeanOperationInfo;
import javax.management.modelmbean.RequiredModelMBean;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.jcontainer.loom.tools.configuration.ConfigurationBuilder;
import org.xml.sax.InputSource;
import org.realityforge.salt.i18n.Resources;
import org.realityforge.salt.i18n.ResourceManager;

/**
 * An MBeanInfoBuilder is responsible for building Management Topic
 * objects from Configuration objects. The format for Configuration object
 * is specified in the MxInfo specification.  The information is loaded into
 * the Target structure.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @author <a href="mailto:huw@mmlive.com">Huw Roberts</a>
 * @version $Revision: 1.3 $ $Date: 2003-08-17 18:27:33 $
 */
public final class MBeanInfoBuilder
    extends AbstractLogEnabled
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( MBeanInfoBuilder.class );
    private static final String REQ_MODEL_MBEAN =
        RequiredModelMBean.class.getName();

    public void build( final Target target,
                       final Class managedClass,
                       final Class[] interfaces )
        throws ConfigurationException
    {
        final String notice =
            REZ.format( "mxinfo.debug.building", managedClass.getName() );
        getLogger().debug( notice );

        // if the managed class has an mxinfo file, build the target from it
        // (this includes any proxies)
        Configuration config = loadMxInfo( managedClass );
        if( null != config )
        {
            final String message =
                REZ.format( "mxinfo.debug.found.mxinfo",
                               managedClass.getName() );
            getLogger().debug( message );
            buildFromMxInfo( target, managedClass, config );
        }

        // for each interface, generate a topic from its mxinfo file
        // or through introspection
        for( int i = 0, j = interfaces.length; i < j; i++ )
        {
            try
            {
                config = loadMxInfo( interfaces[ i ] );
                if( config == null )
                {
                    buildFromIntrospection( target, interfaces[ i ] );
                }
                else
                {
                    buildFromMxInfo( target, managedClass, config );
                }
            }
            catch( final Exception e )
            {
                final String message =
                    REZ.format( "mxinfo.error.target", target.getName() );
                getLogger().error( message, e );
                throw new ConfigurationException( message );
            }
        }
    }

    /**
     * Create a {@link ModelMBeanInfoSupport} object for specified classname from
     * specified configuration data.
     */
    private void buildFromMxInfo( final Target target,
                                  final Class managedClass,
                                  final Configuration config )
        throws ConfigurationException
    {
        BeanInfo beanInfo;
        try
        {
            beanInfo = Introspector.getBeanInfo( managedClass );
        }
        catch( final Exception e )
        {
            final String message =
                REZ.format( "mxinfo.error.introspect", managedClass.getName() );
            throw new ConfigurationException( message, e );
        }

        // load each topic
        final Configuration[] topicsConfig = config.getChildren( "topic" );
        for( int i = 0; i < topicsConfig.length; i++ )
        {
            final ModelMBeanInfoSupport topic =
                buildTopic( topicsConfig[ i ], beanInfo );
            target.addTopic( topic );
        }

        // load each proxy
        final Configuration[] proxysConfig = config.getChildren( "proxy" );
        for( int i = 0; i < proxysConfig.length; i++ )
        {
            final ModelMBeanInfoSupport topic =
                buildProxyTopic( proxysConfig[ i ], managedClass );
            target.addTopic( topic );
        }

    }

    /**
     * Builds a topic based on introspection of the interface
     */
    private void buildFromIntrospection( final Target target,
                                         final Class interfaceClass )
        throws ConfigurationException
    {
        try
        {
            final BeanInfo beanInfo =
                Introspector.getBeanInfo( interfaceClass );

            // do the methods
            final MethodDescriptor[] methods = beanInfo.getMethodDescriptors();
            final ArrayList operations = new ArrayList();

            for( int j = 0; j < methods.length; j++ )
            {
                // skip getters and setters
                final String name = methods[ j ].getName();
                if( !( name.startsWith( "get" ) ||
                    name.startsWith( "set" ) ||
                    name.startsWith( "is" ) ) )
                {
                    operations.add( buildOperationInfo( methods[ j ], null ) );
                }
            }

            final ModelMBeanOperationInfo[] operationList =
                (ModelMBeanOperationInfo[])
                operations.toArray( new ModelMBeanOperationInfo[ 0 ] );

            // do the attributes
            final PropertyDescriptor[] propertys = beanInfo.getPropertyDescriptors();
            final ModelMBeanAttributeInfo[] attributes =
                new ModelMBeanAttributeInfo[ propertys.length ];

            for( int j = 0; j < propertys.length; j++ )
            {
                attributes[ j ] = buildAttributeInfo( propertys[ j ], null );
            }

            final ModelMBeanConstructorInfo[] constructors =
                new ModelMBeanConstructorInfo[ 0 ];

            final ModelMBeanNotificationInfo[] notifications =
                new ModelMBeanNotificationInfo[ 0 ];

            final String shortName = getShortName( interfaceClass.getName() );
            final ModelMBeanInfoSupport topic =
                new ModelMBeanInfoSupport( REQ_MODEL_MBEAN,
                                           shortName,
                                           attributes,
                                           constructors,
                                           operationList,
                                           notifications );

            // add it to target
            final String message = REZ.format( "mxinfo.debug.adding.topic", topic.getDescription() );
            getLogger().debug( message );

            target.addTopic( topic );
        }
        catch( final Exception e )
        {
            final String message =
                REZ.format( "mxinfo.error.topic", interfaceClass );
            throw new ConfigurationException( message, e );
        }
    }

    /**
     * A utility method to build a {@link ModelMBeanInfoSupport}
     * object from specified configuration and BeanInfo.
     *
     * @return the created ModelMBeanInfoSupport
     * @throws ConfigurationException if an error occurs
     */
    private ModelMBeanInfoSupport buildTopic( final Configuration config,
                                              final BeanInfo beanInfo )
        throws ConfigurationException
    {
        final ModelMBeanAttributeInfo[] attributes =
            buildAttributeInfos( config, beanInfo );

        final ModelMBeanOperationInfo[] operations =
            buildOperationInfos( config, beanInfo );

        final ModelMBeanConstructorInfo[] constructors =
            new ModelMBeanConstructorInfo[ 0 ];

        final ModelMBeanNotificationInfo[] notifications =
            new ModelMBeanNotificationInfo[ 0 ];

        final String name = config.getAttribute( "name" );
        final ModelMBeanInfoSupport topic =
            new ModelMBeanInfoSupport( REQ_MODEL_MBEAN,
                                       name,
                                       attributes,
                                       constructors,
                                       operations,
                                       notifications );

        return topic;
    }

    /**
     * Build a topic for a proxy management class
     *
     * @param proxyTagConfig
     * @param managedClass
     * @return
     */
    private ModelMBeanInfoSupport buildProxyTopic( final Configuration proxyTagConfig,
                                                   final Class managedClass )
        throws ConfigurationException
    {
        try
        {
            final String proxyName = proxyTagConfig.getAttribute( "name" );
            final String message = REZ.format( "mxinfo.debug.building.proxy.topic", proxyName );
            getLogger().debug( message );

            final Class proxyClass = managedClass.getClassLoader().loadClass( proxyName );
            final Configuration classConfig = loadMxInfo( proxyClass );
            final Configuration topicConfig = classConfig.getChild( "topic" );
            final BeanInfo info = Introspector.getBeanInfo( proxyClass );
            final ModelMBeanInfoSupport topic = buildTopic( topicConfig, info );
            final Descriptor mBeanDescriptor = topic.getMBeanDescriptor();
            mBeanDescriptor.setField( "proxyClassName", proxyName );
            topic.setMBeanDescriptor( mBeanDescriptor );

            return topic;
        }
        catch( final Exception e )
        {
            if( e instanceof ConfigurationException )
            {
                throw (ConfigurationException)e;
            }
            else
            {
                final String message = REZ.format( "mxinfo.error.proxy", managedClass.getName() );
                throw new ConfigurationException( message );
            }
        }
    }

    /**
     * Builds the management attributes from the configuration
     *
     * @param config topic's configuration element
     * @param info managed class' BeanInfo from introspector
     * @throws ConfigurationException
     */
    private ModelMBeanAttributeInfo[] buildAttributeInfos( final Configuration config,
                                                           final BeanInfo info )
        throws ConfigurationException
    {
        final Configuration[] attributesConfig = config.getChildren( "attribute" );

        final ModelMBeanAttributeInfo[] attributeList =
            new ModelMBeanAttributeInfo[ attributesConfig.length ];

        final PropertyDescriptor[] propertys = info.getPropertyDescriptors();
        for( int i = 0; i < attributesConfig.length; i++ )
        {
            final Configuration attribute = attributesConfig[ i ];
            final String name = attribute.getAttribute( "name" );
            final PropertyDescriptor property =
                getPropertyDescriptor( name, propertys );
            attributeList[ i ] = buildAttributeInfo( property, attribute );
        }

        return attributeList;
    }

    /**
     * Builds a management config
     *
     * @param property from BeanInfo
     * @param config configuration element - can be null, in which case defaults are used
     */
    private ModelMBeanAttributeInfo buildAttributeInfo( final PropertyDescriptor property,
                                                        final Configuration config )
    {
        final String name = property.getName();
        final Method readMethod = property.getReadMethod();
        final Method writeMethod = property.getWriteMethod();
        final String type = property.getPropertyType().getName();

        String description = property.getDisplayName();
        boolean isReadable = ( readMethod != null );
        boolean isWriteable = ( writeMethod != null );

        if( config != null )
        {
            // use config info, or BeanInfo if config info is missing
            description =
                config.getAttribute( "description", description );

            // defaults to true if there is a read method, otherwise defaults to false
            isReadable =
                config.getAttributeAsBoolean( "isReadable", true ) && isReadable;

            // defaults to true if there is a write method, otherwise defaults to false
            isWriteable =
                config.getAttributeAsBoolean( "isWriteable", true ) && isWriteable;
        }

        final boolean isIs =
            ( readMethod != null ) && readMethod.getName().startsWith( "is" );

        final ModelMBeanAttributeInfo info =
            new ModelMBeanAttributeInfo( name, type, description, isReadable, isWriteable, isIs );

        // additional info needed for modelMbean to work
        final Descriptor descriptor = info.getDescriptor();
        descriptor.setField( "currencyTimeLimit", new Integer( 1 ) );
        if( null != readMethod )
        {
            descriptor.setField( "getMethod", readMethod.getName() );
        }
        if( null != writeMethod )
        {
            descriptor.setField( "setMethod", writeMethod.getName() );
        }
        info.setDescriptor( descriptor );

        return info;
    }

    /**
     *  Returns the PropertyDescriptor with the specified name from the array
     */
    private PropertyDescriptor getPropertyDescriptor( final String name,
                                                      final PropertyDescriptor[] propertys )
        throws ConfigurationException
    {
        for( int i = 0; i < propertys.length; i++ )
        {
            if( propertys[ i ].getName().equals( name ) )
            {
                return propertys[ i ];
            }
        }

        final String message =
            REZ.format( "mxinfo.error.missing.property", name );
        throw new ConfigurationException( message );
    }

    /**
     * Builds the management operations
     *
     * @param config topic configuration element to build from
     * @param info BeanInfo for managed class from introspector
     * @throws ConfigurationException
     */
    private ModelMBeanOperationInfo[] buildOperationInfos( final Configuration config,
                                                           final BeanInfo info )
        throws ConfigurationException
    {
        final Configuration[] operationsConfig =
            config.getChildren( "operation" );

        final ModelMBeanOperationInfo[] operations =
            new ModelMBeanOperationInfo[ operationsConfig.length ];

        final MethodDescriptor[] methodDescriptors = info.getMethodDescriptors();

        for( int i = 0; i < operationsConfig.length; i++ )
        {
            final Configuration operation = operationsConfig[ i ];
            final String name = operation.getAttribute( "name" );
            final MethodDescriptor method =
                getMethodDescriptor( name, methodDescriptors );
            operations[ i ] = buildOperationInfo( method, operation );
        }

        return operations;
    }

    /**
     * Builds an operation descriptor from a configuration node
     *
     * @param method method as returned from beanInfo
     * @param config configuration element, can be null
     * @throws ConfigurationException if the configiration has the wrong elements
     * @return  the operation descriptor based on the configuration
     */
    private ModelMBeanOperationInfo buildOperationInfo( final MethodDescriptor method,
                                                        final Configuration config )
        throws ConfigurationException
    {
        final ModelMBeanOperationInfo info;

        if( config == null )
        {
            info = new ModelMBeanOperationInfo( method.getDisplayName(),
                                                method.getMethod() );

        }
        else
        {
            final String name = method.getName();
            final String type = method.getMethod().getReturnType().getName();
            final String description =
                config.getAttribute( "description",
                                     method.getDisplayName() );
            final int impact =
                config.getAttributeAsInteger( "impact",
                                              ModelMBeanOperationInfo.UNKNOWN );

            final Configuration[] paramConfig =
                config.getChildren( "param" );
            final MBeanParameterInfo[] params =
                new MBeanParameterInfo[ paramConfig.length ];
            for( int i = 0; i < paramConfig.length; i++ )
            {
                params[ i ] = buildParameterInfo( paramConfig[ i ] );
            }

            info = new ModelMBeanOperationInfo( name, description, params, type, impact );
        }

        // additional info needed for modelMbean to work
        final Descriptor descriptor = info.getDescriptor();
        // TODO: might want to make this configurable. It controls the caching behavior
        // of the invoke results. MX4J appears to cache this per operation regardless
        // of what the invoke parameters are *SIGH* - PR
        descriptor.setField( "currencyTimeLimit", new Integer( 0 ) );
        info.setDescriptor( descriptor );
        return info;
    }

    /**
     *  Returns the MethodDescriptor with the specified name from the array
     */
    private MethodDescriptor getMethodDescriptor( final String name,
                                                  final MethodDescriptor[] methods )
        throws ConfigurationException
    {

        for( int i = 0; i < methods.length; i++ )
        {
            if( methods[ i ].getName().equals( name ) )
            {
                return methods[ i ];
            }
        }
        final String message = REZ.format( "mxinfo.error.missing.method", name );
        throw new ConfigurationException( message );
    }

    /**
     * Builds the param descriptor from the configuration data
     *
     * @throws ConfigurationException if configuration not structured corretly
     * @return the descriptor
     */
    private MBeanParameterInfo buildParameterInfo( final Configuration paramConfig )
        throws ConfigurationException
    {
        final String name = paramConfig.getAttribute( "name" );
        final String description = paramConfig.getAttribute( "description" );
        final String type = paramConfig.getAttribute( "type" );

        return new MBeanParameterInfo( name, type, description );
    }

    /**
     * Returns the configuration for the class or null if there is no mxinfo
     * file for it.
     *
     * @param clazz the class to load the configuration for
     * @throws ConfigurationException
     * @return the configuration file, or null if none exists
     */
    private Configuration loadMxInfo( final Class clazz )
        throws ConfigurationException
    {
        final String mxinfoName =
            "/" + clazz.getName().replace( '.', '/' ) + ".mxinfo";
        try
        {
            InputStream stream = clazz.getResourceAsStream( mxinfoName );
            if( null == stream )
            {
                return null;
            }

            final InputSource source = new InputSource( stream );

            // build with validation against DTD
            return ConfigurationBuilder.build( source, ConfigurationBuilder.MXINFO_SCHEMA, getLogger() );
        }
        catch( Exception e )
        {
            final String message =
                REZ.format( "mxinfo.error.file", mxinfoName );
            getLogger().error( message, e );
            throw new ConfigurationException( message );
        }
    }

    /**
     *  Returns the class name without the package name
     */
    private String getShortName( final String className )
    {
        return className.substring( className.lastIndexOf( '.' ) + 1 );
    }
}
