/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.tools.metaclass;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.beans.BeanInfo;
import java.beans.Introspector;
import org.jcontainer.dna.Configuration;
import org.realityforge.metaclass.model.Attribute;
import org.realityforge.metaclass.model.ClassDescriptor;
import org.realityforge.metaclass.model.MethodDescriptor;
import org.realityforge.metaclass.model.ParameterDescriptor;
import org.realityforge.metaclass.model.FieldDescriptor;

/**
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.2 $ $Date: 2003-10-15 00:33:40 $
 */
public class LoomClassDescriptorBuilder
{
    private static final String RETURN_TYPE = "";
    private static final int MODIFIERS = Modifier.PUBLIC;

    private static final String CONFIGURE_METHOD_NAME = "configure";
    private static final String CONFIGURATION_CLASSNAME =
        "org.apache.avalon.framework.configuration.Configuration";
    private static final ParameterDescriptor[] CONFIGURE_METHOD_PARAMETERS =
        new ParameterDescriptor[]{new ParameterDescriptor( "", CONFIGURATION_CLASSNAME )};

    private static final String COMPOSE_METHOD_NAME = "compose";
    private static final String COMPONENT_MANAGER_CLASSNAME =
        "org.apache.avalon.framework.component.ComponentManager";
    private static final ParameterDescriptor[] COMPOSE_METHOD_PARAMETERS =
        new ParameterDescriptor[]{new ParameterDescriptor( "", COMPONENT_MANAGER_CLASSNAME )};

    private static final String SERVICE_METHOD_NAME = "compose";
    private static final String SERVICE_MANAGER_CLASSNAME =
        "org.apache.avalon.framework.service.ServiceManager";
    private static final ParameterDescriptor[] SERVICE_METHOD_PARAMETERS =
        new ParameterDescriptor[]{new ParameterDescriptor( "", SERVICE_MANAGER_CLASSNAME )};

    public ClassDescriptor buildFromBlockInfo( final Class type,
                                               final Configuration blockInfo,
                                               final Configuration mxInfo )
        throws Exception
    {
        final List classAttributes = new ArrayList();
        final List methodDescriptors = new ArrayList();

        if( null != blockInfo )
        {
            buildFromBlockInfo( blockInfo, methodDescriptors, classAttributes );
        }

        if( null != mxInfo )
        {
            final Configuration topic = mxInfo.getChild( "topic" );
            final String description = topic.getAttribute( "name" );
            final BeanInfo beanInfo =
                Introspector.getBeanInfo( type );
            final Configuration[] attributes = topic.getChildren( "attribute" );
            for( int i = 0; i < attributes.length; i++ )
            {
                Configuration attribute = attributes[ i ];
                final String name = attribute.getAttribute( "name" );
                final String description =
                    attribute.getAttribute( "description", "" );

            }

            /*
            <topic name="ftpServer" >

      <!-- attributes -->
      <attribute
        name="addressString"
        description="Address String"
        isWriteable="no"
        type="java.lang.String"
      />
      <attribute
        name="serverAddress"
        description="Server bind address."
        isWriteable="no"
        type="java.net.InetAddress"
      />

      <!-- operations -->
      <operation
        name="getDefaultRoot"
        description="Gets the default root"
        type="java.lang.String"
      >
      </operation>
      <operation
        name="getServerPort"
        description="Returns port that the server listens on"
        type="java.lang.String"
      >
        <param
          name="instance"
          description="no description"
          type="java.lang.Integer"
        />
      </operation>

    </topic>
            */
        }

        final Attribute[] attributes = (Attribute[])classAttributes.
            toArray( new Attribute[ classAttributes.size() ] );
        final MethodDescriptor[] methods = (MethodDescriptor[])methodDescriptors.
            toArray( new MethodDescriptor[ methodDescriptors.size() ] );
        return new ClassDescriptor( type.getName(),
                                    Modifier.PUBLIC,
                                    attributes,
                                    FieldDescriptor.EMPTY_SET,
                                    methods );
    }

    private void buildFromBlockInfo( final Configuration blockInfo,
                                     final List methodDescriptors,
                                     final List classAttributes )
        throws Exception
    {
        final String schemaType = blockInfo.
            getChild( "block" ).getChild( "schema-type" ).getValue( "" );
        if( !"".equals( schemaType ) )
        {
            final MethodDescriptor method =
                createConfigureMethodDescriptor( schemaType );
            methodDescriptors.add( method );
        }

        classAttributes.add( new Attribute( "dna.component" ) );
        setupServiceAttributes( classAttributes, blockInfo );

        setupDependencies( blockInfo, methodDescriptors );
    }

    private void setupDependencies( final Configuration configuration,
                                    final List methods )
        throws Exception
    {
        final Attribute[] attributes = createDependencyAttributes( configuration );
        if( attributes.length > 0 )
        {
            final MethodDescriptor compose =
                new MethodDescriptor( COMPOSE_METHOD_NAME,
                                      RETURN_TYPE,
                                      MODIFIERS,
                                      COMPOSE_METHOD_PARAMETERS,
                                      attributes );
            final MethodDescriptor service =
                new MethodDescriptor( SERVICE_METHOD_NAME,
                                      RETURN_TYPE,
                                      MODIFIERS,
                                      SERVICE_METHOD_PARAMETERS,
                                      attributes );
            methods.add( compose );
            methods.add( service );
        }
    }

    private Attribute[] createDependencyAttributes( final Configuration configuration )
        throws Exception
    {
        final List dependencyAttributes = new ArrayList();
        final Configuration[] dependencies = configuration.
            getChild( "dependencies" ).getChildren( "dependency" );
        for( int i = 0; i < dependencies.length; i++ )
        {
            final Configuration dependency = dependencies[ i ];
            final String type =
                dependency.getChild( "service" ).getAttribute( "name" );
            final String key =
                dependency.getChild( "role" ).getValue( null );
            final Properties parameters = new Properties();
            parameters.setProperty( "type", type );
            if( null != key )
            {
                parameters.setProperty( "key", key );
            }
            dependencyAttributes.add( new Attribute( "dna.dependency", parameters ) );
        }
        return (Attribute[])dependencyAttributes.toArray( new Attribute[ dependencyAttributes.size() ] );
    }

    private void setupServiceAttributes( final List classAttributes, final Configuration configuration )
        throws Exception
    {
        final Configuration[] services =
            configuration.getChild( "services" ).getChildren( "service" );
        for( int i = 0; i < services.length; i++ )
        {
            final String type = services[ i ].getAttribute( "name" );
            addServiceAttribute( classAttributes, type, false );
        }

        final Configuration[] maps = configuration.
            getChild( "management-access-points" ).getChildren( "service" );
        for( int i = 0; i < maps.length; i++ )
        {
            final String type = maps[ i ].getAttribute( "name" );
            addServiceAttribute( classAttributes, type, false );
            addServiceAttribute( classAttributes, type, true );
        }
    }

    private MethodDescriptor createConfigureMethodDescriptor( final String schemaType )
    {
        final Properties parameters = new Properties();
        parameters.setProperty( "type", schemaType );
        final Attribute attribute =
            new Attribute( "dna.configuration", parameters );
        final MethodDescriptor method =
            new MethodDescriptor( CONFIGURE_METHOD_NAME,
                                  RETURN_TYPE,
                                  MODIFIERS,
                                  CONFIGURE_METHOD_PARAMETERS,
                                  new Attribute[]{attribute} );
        return method;
    }

    private void addServiceAttribute( final List classAttributes,
                                      final String type,
                                      final boolean isManagement )
    {
        final Properties parameters = new Properties();
        parameters.setProperty( "type", type );
        classAttributes.add( new Attribute( "dna.service", parameters ) );
        if( isManagement )
        {
            classAttributes.add( new Attribute( "mx.interface", parameters ) );
        }
    }
}
