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

import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.Set;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.modelmbean.ModelMBeanInfo;
import org.jcontainer.loom.interfaces.ContainerConstants;
import org.jcontainer.loom.interfaces.LoomException;
import org.realityforge.metaclass.jmx.MBeanInfoBuilder;
import org.realityforge.metaclass.jmx.WrapperModelMBean;
import org.realityforge.salt.i18n.ResourceManager;
import org.realityforge.salt.i18n.Resources;

/**
 * An abstract class via which JMX Managers can extend.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @author <a href="mailto:Huw@mmlive.com">Huw Roberts</a>
 * @version $Revision: 1.7 $ $Date: 2003-10-16 09:22:06 $
 */
public abstract class AbstractJMXManager
    extends AbstractSystemManager
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( AbstractJMXManager.class );

    private static final MBeanInfoBuilder INFO_BUILDER = new MBeanInfoBuilder();

    private MBeanServer m_mBeanServer;
    private String m_domain = ContainerConstants.SOFTWARE;

    public void initialize()
        throws Exception
    {
        super.initialize();

        final MBeanServer mBeanServer = createMBeanServer();
        setMBeanServer( mBeanServer );
    }

    public void dispose()
    {
        setMBeanServer( null );

        super.dispose();
    }

    /**
     * Export the object to the particular management medium using
     * the supplied object and interfaces.
     * This needs to be implemented by subclasses.
     *
     * @param name the name of object
     * @param object the object
     * @return the exported object
     * @throws LoomException if an error occurs
     */
    protected Object export( final String name,
                             final Object object )
        throws LoomException
    {
        try
        {
            final Target target = createTarget( name, object );
            exportTarget( target );
            return target;
        }
        catch( final Exception e )
        {
            final String message = REZ.format( "jmxmanager.error.export.fail", name );
            getLogger().error( message, e );
            throw new LoomException( message, e );
        }
    }

    /**
     * Stop the exported object from being managed.
     *
     * @param name the name of object
     * @param exportedObject the object return by export
     * @throws LoomException if an error occurs
     */
    protected void unexport( final String name,
                             final Object exportedObject )
        throws LoomException
    {
        try
        {
            final Target target = (Target)exportedObject;
            final Set topicNames = target.getTopicNames();
            final Iterator i = topicNames.iterator();

            while( i.hasNext() )
            {
                final String topicName = (String)i.next();
                final ObjectName objectName = createObjectName( name, topicName );
                getMBeanServer().unregisterMBean( objectName );
            }
        }
        catch( final Exception e )
        {
            final String message =
                REZ.format( "jmxmanager.error.unexport.fail", name );
            getLogger().error( message, e );
            throw new LoomException( message, e );
        }
    }

    protected MBeanServer getMBeanServer()
    {
        return m_mBeanServer;
    }

    protected void setMBeanServer( final MBeanServer mBeanServer )
    {
        m_mBeanServer = mBeanServer;
    }

    protected String getDomain()
    {
        return m_domain;
    }

    protected void setDomain( final String domain )
    {
        m_domain = domain;
    }

    /**
     * Creates a new MBeanServer.
     * The subclass should implement this to create specific MBeanServer.
     */
    protected abstract MBeanServer createMBeanServer()
        throws Exception;

    /**
     * Creates a target that can then be exported for management. A topic is created
     * for each interface and for topics specified in the mxinfo file, if present
     *
     * @param name name of the target
     * @param object managed object
     * @return  the management target
     */
    protected Target createTarget( final String name,
                                   final Object object )
        throws Exception
    {
        final Class type = object.getClass();
        final ModelMBeanInfo[] infos = INFO_BUILDER.buildMBeanInfos( type );
        final Target target = new Target( name, object );
        for( int i = 0; i < infos.length; i++ )
        {
            target.addTopic( infos[ i ] );
        }
        return target;
    }

    /**
     * Exports the target to the management repository.  This is done by exporting
     * each topic in the target.
     *
     * @param target the management target
     */
    protected void exportTarget( final Target target )
        throws Exception
    {
        // loop through each topic and export it
        final Set topicNames = target.getTopicNames();
        final Iterator i = topicNames.iterator();
        while( i.hasNext() )
        {
            final String topicName = (String)i.next();
            final ModelMBeanInfo topic = target.getTopic( topicName );
            final String targetName = target.getName();
            final Object managedResource = target.getManagedResource();
            Object targetObject = managedResource;
            if( topic.getMBeanDescriptor().getFieldValue( "proxyClassName" ) != null )
            {
                targetObject = createManagementProxy( topic, managedResource );
            }

            // use a proxy adapter class to manage object
            exportTopic( topic, targetObject, targetName, topicName );
        }
    }

    /**
     * Exports the topic to the management repository. The name of the topic in the
     * management repository will be the target name + the topic name
     *
     * @param topic the descriptor for the topic
     * @param target to be managed
     * @param targetName the target's name
     */
    protected Object exportTopic( final ModelMBeanInfo topic,
                                  final Object target,
                                  final String targetName,
                                  final String topicName )
        throws Exception
    {
        final Object mBean = new WrapperModelMBean( topic, target );
        final ObjectName objectName = createObjectName( targetName, topicName );
        getMBeanServer().registerMBean( mBean, objectName );
        return mBean;
    }

    /**
     * Create JMX name for object.
     *
     * @param name the name of object
     * @return the {@link ObjectName} representing object
     * @throws MalformedObjectNameException if malformed name
     */
    private ObjectName createObjectName( final String name,
                                         final String topicName )
        throws MalformedObjectNameException
    {
        return new ObjectName( getDomain() + ":" + name + ",topic=" + topicName );
    }

    /**
     * Instantiates a proxy management object for the target object
     *
     * this should move out of bridge and into Registry, it isn't specifically for jmx
     */
    private Object createManagementProxy( final ModelMBeanInfo topic,
                                          final Object managedObject )
        throws Exception
    {
        final String proxyClassname = (String)topic.getMBeanDescriptor().getFieldValue( "proxyClassName" );
        final ClassLoader classLoader = managedObject.getClass().getClassLoader();
        final Class proxyClass = classLoader.loadClass( proxyClassname );
        final Class[] argTypes = new Class[]{Object.class};
        final Object[] argValues = new Object[]{managedObject};
        final Constructor constructor = proxyClass.getConstructor( argTypes );
        return constructor.newInstance( argValues );
    }
}
