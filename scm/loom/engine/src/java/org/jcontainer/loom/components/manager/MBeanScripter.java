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

import javax.management.Attribute;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

/**
 * Support JMX MBean lifecycle.
 *
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 */
public class MBeanScripter
{
    private final MBeanServer m_mBeanServer;
    private final Configuration m_configuration;
    private final ObjectName m_objectName;

    public MBeanScripter( final MBeanServer mBeanServer,
                          final Configuration configuration )
        throws ConfigurationException, MalformedObjectNameException
    {
        m_mBeanServer = mBeanServer;
        m_configuration = configuration;
        m_objectName = new ObjectName( m_configuration.getAttribute( "name" ) );
    }

    public String getName()
    {
        return m_objectName.getCanonicalName();
    }

    public ObjectName getObjectName()
    {
        return m_objectName;
    }

    /**
     * Create MBean and invoke startup operations.
     */
    public void startup()
        throws Exception
    {
        m_mBeanServer.createMBean( m_configuration.getAttribute( "class" ),
                                   getObjectName(), null );

        setAttributes();
        setUses();
        invokeStartupOperations();
    }

    /**
     * Invoke shutdown operations.
     */
    public void shutdown()
        throws Exception
    {
        invokeShutdownOperations();
    }

    private void setAttributes()
        throws Exception
    {
        final Configuration[] attributes =
            m_configuration.getChildren( "attribute" );
        for( int i = 0; i < attributes.length; i++ )
        {
            setAttribute( attributes[ i ] );
        }
    }

    private void setAttribute( final Configuration attribute )
        throws Exception
    {
        final String name = attribute.getAttribute( "name" );
        final String type = attribute.getAttribute( "type" );
        final String rawValue = attribute.getValue( null );
        Object value = null;
        if( null != rawValue )
        {
            final Class valueClass = Class.forName( type );
            value = convertToObject( valueClass, rawValue );
        }
        m_mBeanServer.setAttribute( getObjectName(),
                                    new Attribute( name, value ) );
    }

    private void setUses()
        throws Exception
    {
        final Configuration[] uses = m_configuration.getChildren( "use" );
        for( int i = 0; i < uses.length; i++ )
        {
            setUse( uses[ i ] );
        }
    }

    private void setUse( final Configuration use )
        throws Exception
    {
        final String name = use.getAttribute( "name" );
        final String value = use.getValue();
        final Attribute ref = new Attribute( name, new ObjectName( value ) );
        m_mBeanServer.setAttribute( getObjectName(), ref );
    }

    private void invokeStartupOperations()
        throws Exception
    {
        final Configuration[] invokes =
            m_configuration.getChild( "startup", true ).getChildren( "invoke" );
        invokeOperations( invokes );
    }

    private void invokeShutdownOperations()
        throws Exception
    {
        final Configuration[] invokes =
            m_configuration.getChild( "startup", true ).getChildren( "invoke" );
        invokeOperations( invokes );
    }

    private void invokeOperations( final Configuration[] invokes )
        throws Exception
    {
        for( int i = 0; i < invokes.length; i++ )
        {
            final Configuration invoke = invokes[ i ];
            invokeOperation( invoke );
        }
    }

    private void invokeOperation( final Configuration invoke )
        throws Exception
    {
        final String operationName = invoke.getAttribute( "name" );
        final Configuration[] paramConfs = invoke.getChildren( "parameter" );
        final String[] types = new String[ paramConfs.length ];
        final Object[] values = new Object[ paramConfs.length ];
        for( int i = 0; i < paramConfs.length; i++ )
        {
            final String type = paramConfs[ i ].getAttribute( "type" );
            final String rawValue = paramConfs[ i ].getValue( null );
            Object value = null;
            if( null != rawValue )
            {
                final Class valueClass = Class.forName( type );
                value = convertToObject( valueClass, rawValue );
            }
            types[ i ] = type;
            values[ i ] = value;
        }
        m_mBeanServer.invoke( getObjectName(), operationName, values, types );
    }

    private Object convertToObject( final Class valueClass, final String s )
    {
        Object value = null;
        if( valueClass.equals( String.class ) )
        {
            value = s;
        }
        else if( valueClass.equals( Byte.class ) )
        {
            value = new Byte( Byte.parseByte( s ) );
        }
        else if( valueClass.equals( Short.class ) )
        {
            value = new Short( Short.parseShort( s ) );
        }
        else if( valueClass.equals( Integer.class ) )
        {
            value = new Integer( Integer.parseInt( s ) );
        }
        else if( valueClass.equals( Long.class ) )
        {
            value = new Long( Long.parseLong( s ) );
        }
        else if( valueClass.equals( Float.class ) )
        {
            value = new Float( Float.parseFloat( s ) );
        }
        else if( valueClass.equals( Double.class ) )
        {
            value = new Double( Double.parseDouble( s ) );
        }
        else if( valueClass.equals( Character.class ) )
        {
            value = new Character( s.charAt( 0 ) );
        }
        else if( valueClass.equals( Boolean.class ) )
        {
            value = new Boolean( s );
        }
        else
        {
            throw new UnsupportedOperationException( "can't yet convert " + valueClass );
        }
        return value;
    }
}
