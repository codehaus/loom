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
package org.jcontainer.loom.components.manager;

import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.Set;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.modelmbean.ModelMBean;
import javax.management.modelmbean.ModelMBeanInfo;
import org.jcontainer.loom.interfaces.ManagerException;
import org.realityforge.salt.i18n.Resources;
import org.realityforge.salt.i18n.ResourceManager;

/**
 * An abstract class via which JMX Managers can extend.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @author <a href="mailto:Huw@mmlive.com">Huw Roberts</a>
 * @version $Revision: 1.2 $ $Date: 2003-07-13 00:15:36 $
 */
public abstract class AbstractJMXManager
    extends AbstractSystemManager
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( AbstractJMXManager.class );
    private MBeanInfoBuilder topicBuilder;
    private MBeanServer m_mBeanServer;
    private String m_domain = "Phoenix";

    public void initialize()
        throws Exception
    {
        super.initialize();

        final MBeanServer mBeanServer = createMBeanServer();
        setMBeanServer( mBeanServer );

        topicBuilder = new MBeanInfoBuilder();
        setupLogger( topicBuilder );
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
     * @param interfaces the interfaces
     * @return the exported object
     * @throws ManagerException if an error occurs
     */
    protected Object export( final String name,
                             final Object object,
                             final Class[] interfaces )
        throws ManagerException
    {
        try
        {
            final Target target = createTarget( name, object, interfaces );
            exportTarget( target );
            return target;
        }
        catch( final Exception e )
        {
            final String message = REZ.format( "jmxmanager.error.export.fail", name );
            getLogger().error( message, e );
            throw new ManagerException( message, e );
        }
    }

    /**
     * Stop the exported object from being managed.
     *
     * @param name the name of object
     * @param exportedObject the object return by export
     * @throws ManagerException if an error occurs
     */
    protected void unexport( final String name,
                             final Object exportedObject )
        throws ManagerException
    {
        try
        {
            final Target target = (Target)exportedObject;
            final Set topicNames = target.getTopicNames();
            final Iterator i = topicNames.iterator();

            while( i.hasNext() )
            {
                final ObjectName objectName =
                    createObjectName( name, target.getTopic( (String)i.next() ) );

                getMBeanServer().unregisterMBean( objectName );
            }
        }
        catch( final Exception e )
        {
            final String message =
                REZ.format( "jmxmanager.error.unexport.fail", name );
            getLogger().error( message, e );
            throw new ManagerException( message, e );
        }
    }

    /**
     * Verify that an interface conforms to the requirements of management medium.
     *
     * @param clazz the interface class
     * @throws ManagerException if verification fails
     */
    protected void verifyInterface( final Class clazz )
        throws ManagerException
    {
        //TODO: check it extends all right things and that it
        //has all the right return types etc. Blocks must have
        //interfaces extending Service (or Manageable)
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
     * @param interfaces interfaces to be exported
     * @return  the management target
     */
    protected Target createTarget( final String name,
                                   final Object object,
                                   final Class[] interfaces )
    {
        final Target target = new Target( name, object );
        try
        {
            topicBuilder.build( target, object.getClass(), interfaces );
        }
        catch( final Exception e )
        {
            getLogger().debug( e.getMessage(), e );
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
            exportTopic( topic,
                         targetObject,
                         targetName );
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
                                  final String targetName )
        throws Exception
    {
        final Object mBean = createMBean( topic, target );
        final ObjectName objectName = createObjectName( targetName, topic );
        getMBeanServer().registerMBean( mBean, objectName );

        // debugging stuff.
        /*
        ModelMBean modelMBean = (ModelMBean)mBean;
        ModelMBeanInfo modelMBeanInfo = (ModelMBeanInfo)modelMBean.getMBeanInfo();
        MBeanAttributeInfo[] attList = modelMBeanInfo.getAttributes();
        for( int i = 0; i < attList.length; i++ )
        {
            ModelMBeanAttributeInfo mbai = (ModelMBeanAttributeInfo)attList[ i ];
            Descriptor d = mbai.getDescriptor();
            String[] fieldNames = d.getFieldNames();
            for( int j = 0; j < fieldNames.length; j++ )
            {
                String fieldName = fieldNames[ j ];
                System.out.println( "Field name = " + fieldName +
                                    " / value = " + d.getFieldValue( fieldName ) +
                                    "::" +mbai.getType() + " value " +
                modelMBean.getAttribute( mbai.getName() ) + " for " + mbai.getName() );
            }
        }
        */

        return mBean;
    }

    /**
     * Create a MBean for specified object.
     * The following policy is used top create the MBean...
     *
     * @param target the object to create MBean for
     * @return the MBean to be exported
     * @throws ManagerException if an error occurs
     */
    private Object createMBean( final ModelMBeanInfo topic,
                                final Object target )
        throws ManagerException
    {
        final String className = topic.getClassName();
        // Load the ModelMBean implementation class
        Class clazz;
        try
        {
            clazz = Class.forName( className );
        }
        catch( Exception e )
        {
            final String message =
                REZ.format( "jmxmanager.error.mbean.load.class",
                            className );
            getLogger().error( message, e );
            throw new ManagerException( message, e );
        }

        // Create a new ModelMBean instance
        ModelMBean mbean;
        try
        {
            mbean = (ModelMBean)clazz.newInstance();
            mbean.setModelMBeanInfo( topic );
        }
        catch( final Exception e )
        {
            final String message =
                REZ.format( "jmxmanager.error.mbean.instantiate",
                            className );
            getLogger().error( message, e );
            throw new ManagerException( message, e );
        }

        // Set the managed resource (if any)
        try
        {
            if( null != target )
            {
                mbean.setManagedResource( target, "ObjectReference" );
            }
        }
        catch( Exception e )
        {
            final String message =
                REZ.format( "jmxmanager.error.mbean.set.resource",
                            className );
            getLogger().error( message, e );
            throw new ManagerException( message, e );
        }

        return mbean;
    }

    /**
     * Create JMX name for object.
     *
     * @param name the name of object
     * @return the {@link ObjectName} representing object
     * @throws MalformedObjectNameException if malformed name
     */
    private ObjectName createObjectName( final String name, final ModelMBeanInfo topic )
        throws MalformedObjectNameException
    {
        return new ObjectName( getDomain() + ":" + name + ",topic=" + topic.getDescription() );
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
