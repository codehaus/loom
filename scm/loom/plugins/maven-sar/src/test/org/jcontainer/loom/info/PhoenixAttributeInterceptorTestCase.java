/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.info;

import junit.framework.TestCase;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaMethod;
import org.realityforge.metaclass.model.Attribute;
import java.util.Properties;
import java.util.ArrayList;

/**
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003-11-01 13:15:31 $
 */
public class PhoenixAttributeInterceptorTestCase
    extends TestCase
{
    public void testProcessClassAttributeWithoutTransformations()
        throws Exception
    {
        final PhoenixAttributeInterceptor interceptor = new PhoenixAttributeInterceptor();
        final Attribute attribute = new Attribute( "ignored" );
        final Attribute result =
            interceptor.processClassAttribute( new JavaClass(), attribute );
        assertNotNull( "attribute", result );
        assertEquals( "attribute.name", "ignored", result.getName() );
        assertEquals( "attribute.value", null, result.getValue() );
        assertEquals( "attribute.parameterCount", 0, result.getParameterCount() );
    }

    public void testProcessClassAttributeWithPhoenixBlock()
        throws Exception
    {
        final PhoenixAttributeInterceptor interceptor = new PhoenixAttributeInterceptor();
        final Attribute attribute = new Attribute( "phoenix:block" );
        final Attribute result =
            interceptor.processClassAttribute( new JavaClass(), attribute );
        assertNotNull( "attribute", result );
        assertEquals( "attribute.name", "dna.component", result.getName() );
        assertEquals( "attribute.value", null, result.getValue() );
        assertEquals( "attribute.parameterCount", 0, result.getParameterCount() );
    }

    public void testProcessClassAttributeWithPhoenixMxTopic()
        throws Exception
    {
        final PhoenixAttributeInterceptor interceptor = new PhoenixAttributeInterceptor();
        final Properties parameters = new Properties();
        parameters.setProperty( "name", "MyTopic" );
        final Attribute attribute = new Attribute( "phoenix:mx-topic", parameters );
        final Attribute result =
            interceptor.processClassAttribute( new JavaClass(), attribute );
        assertNotNull( "attribute", result );
        assertEquals( "attribute.name", "mx.component", result.getName() );
        assertEquals( "attribute.value", null, result.getValue() );
        assertEquals( "attribute.parameterCount", 1, result.getParameterCount() );
        assertEquals( "attribute.parameter(description)", "MyTopic", result.getParameter( "description" ) );
    }

    public void testProcessClassAttributeWithPhoenixService()
        throws Exception
    {
        final PhoenixAttributeInterceptor interceptor = new PhoenixAttributeInterceptor();
        final Properties parameters = new Properties();
        parameters.setProperty( "name", "X" );
        final Attribute attribute = new Attribute( "phoenix:service", parameters );
        final Attribute result =
            interceptor.processClassAttribute( new JavaClass(), attribute );
        assertNotNull( "attribute", result );
        assertEquals( "attribute.name", "dna.service", result.getName() );
        assertEquals( "attribute.value", null, result.getValue() );
        assertEquals( "attribute.parameterCount", 1, result.getParameterCount() );
        assertEquals( "attribute.parameter(type)", "X", result.getParameter( "type" ) );
    }

    public void testProcessClassAttributeWithPhoenixMXProxy()
        throws Exception
    {
        final PhoenixAttributeInterceptor interceptor = new PhoenixAttributeInterceptor();
        final Properties parameters = new Properties();
        parameters.setProperty( "class", "X" );
        final Attribute attribute = new Attribute( "phoenix:mx-proxy", parameters );
        final Attribute result =
            interceptor.processClassAttribute( new JavaClass(), attribute );
        assertNotNull( "attribute", result );
        assertEquals( "attribute.name", "mx.proxy", result.getName() );
        assertEquals( "attribute.value", null, result.getValue() );
        assertEquals( "attribute.parameterCount", 1, result.getParameterCount() );
        assertEquals( "attribute.parameter(type)", "X", result.getParameter( "type" ) );
    }

    public void testProcessMethodAttributeWithoutTransformation()
        throws Exception
    {
        final PhoenixAttributeInterceptor interceptor = new PhoenixAttributeInterceptor();
        final Attribute attribute = new Attribute( "ignore-me" );
        final Attribute result =
            interceptor.processMethodAttribute( new JavaMethod(), attribute );
        assertNotNull( "attribute", result );
        assertEquals( "attribute.name", "ignore-me", result.getName() );
        assertEquals( "attribute.value", null, result.getValue() );
        assertEquals( "attribute.parameterCount", 0, result.getParameterCount() );
    }

    public void testProcessMethodAttributeWithMXDescription()
        throws Exception
    {
        final PhoenixAttributeInterceptor interceptor = new PhoenixAttributeInterceptor();
        final Attribute attribute = new Attribute( "phoenix:mx-description" );
        final Attribute result =
            interceptor.processMethodAttribute( new JavaMethod(), attribute );
        assertNull( "attribute", result );
    }

    public void testProcessMethodAttributeWithMXAttributeAndNoDescriptionOrComment()
        throws Exception
    {
        final PhoenixAttributeInterceptor interceptor = new PhoenixAttributeInterceptor();
        final Attribute attribute = new Attribute( "phoenix:mx-attribute" );
        final Attribute result =
            interceptor.processMethodAttribute( new JavaMethod(), attribute );
        assertNotNull( "attribute", result );
        assertEquals( "attribute.name", "mx.attribute", result.getName() );
        assertEquals( "attribute.value", null, result.getValue() );
        assertEquals( "attribute.parameterCount", 0, result.getParameterCount() );
    }

    public void testProcessMethodAttributeWithMXAttributeAndDescription()
        throws Exception
    {
        final String description = "A Random Thought";
        final PhoenixAttributeInterceptor interceptor = new PhoenixAttributeInterceptor();
        final Attribute attribute = new Attribute( "phoenix:mx-attribute" );
        final JavaMethod method = new JavaMethod();
        final ArrayList tags = new ArrayList();
        tags.add( new DocletTag( "phoenix:mx-description", description ) );
        method.setTags( tags );
        final Attribute result =
            interceptor.processMethodAttribute( method, attribute );
        assertNotNull( "attribute", result );
        assertEquals( "attribute.name", "mx.attribute", result.getName() );
        assertEquals( "attribute.value", null, result.getValue() );
        assertEquals( "attribute.parameterCount", 1, result.getParameterCount() );
        assertEquals( "attribute.parameter(description)",
                      description, result.getParameter( "description" ) );
    }

    public void testProcessMethodAttributeWithMXAttributeAndComment()
        throws Exception
    {
        final String description = "A Random Thought";
        final PhoenixAttributeInterceptor interceptor = new PhoenixAttributeInterceptor();
        final Attribute attribute = new Attribute( "phoenix:mx-attribute" );
        final JavaMethod method = new JavaMethod();
        method.setComment( description );
        final Attribute result =
            interceptor.processMethodAttribute( method, attribute );
        assertNotNull( "attribute", result );
        assertEquals( "attribute.name", "mx.attribute", result.getName() );
        assertEquals( "attribute.value", null, result.getValue() );
        assertEquals( "attribute.parameterCount", 1, result.getParameterCount() );
        assertEquals( "attribute.parameter(description)",
                      description, result.getParameter( "description" ) );
    }

    public void testProcessMethodAttributeWithMXOperationAndNoDescriptionOrComment()
        throws Exception
    {
        final PhoenixAttributeInterceptor interceptor = new PhoenixAttributeInterceptor();
        final Attribute attribute = new Attribute( "phoenix:mx-operation" );
        final Attribute result =
            interceptor.processMethodAttribute( new JavaMethod(), attribute );
        assertNotNull( "attribute", result );
        assertEquals( "attribute.name", "mx.operation", result.getName() );
        assertEquals( "attribute.value", null, result.getValue() );
        assertEquals( "attribute.parameterCount", 0, result.getParameterCount() );
    }

    public void testProcessMethodAttributeWithMXOperationAndDescription()
        throws Exception
    {
        final String description = "A Random Thought";
        final PhoenixAttributeInterceptor interceptor = new PhoenixAttributeInterceptor();
        final Attribute attribute = new Attribute( "phoenix:mx-operation" );
        final JavaMethod field = new JavaMethod();
        final ArrayList tags = new ArrayList();
        tags.add( new DocletTag( "phoenix:mx-description", description ) );
        field.setTags( tags );
        final Attribute result =
            interceptor.processMethodAttribute( field, attribute );
        assertNotNull( "attribute", result );
        assertEquals( "attribute.name", "mx.operation", result.getName() );
        assertEquals( "attribute.value", null, result.getValue() );
        assertEquals( "attribute.parameterCount", 1, result.getParameterCount() );
        assertEquals( "attribute.parameter(description)",
                      description, result.getParameter( "description" ) );
    }

    public void testProcessMethodAttributeWithMXOperationAndComment()
        throws Exception
    {
        final String description = "A Random Thought";
        final PhoenixAttributeInterceptor interceptor = new PhoenixAttributeInterceptor();
        final Attribute attribute = new Attribute( "phoenix:mx-operation" );
        final JavaMethod field = new JavaMethod();
        field.setComment( description );
        final Attribute result =
            interceptor.processMethodAttribute( field, attribute );
        assertNotNull( "attribute", result );
        assertEquals( "attribute.name", "mx.operation", result.getName() );
        assertEquals( "attribute.value", null, result.getValue() );
        assertEquals( "attribute.parameterCount", 1, result.getParameterCount() );
        assertEquals( "attribute.parameter(description)",
                      description, result.getParameter( "description" ) );
    }

    public void testProcessMethodAttributeWithPhoenixConfigurationSchema()
        throws Exception
    {
        final PhoenixAttributeInterceptor interceptor = new PhoenixAttributeInterceptor();
        final Attribute attribute = new Attribute( "phoenix:configuration-schema" );
        final JavaClass javaClass = new JavaClass();
        javaClass.setName( "com.biz.MyClass" );
        final JavaMethod method = new JavaMethod();
        method.setParentClass( javaClass );
        final Attribute result =
            interceptor.processMethodAttribute( method, attribute );
        assertNotNull( "attribute", result );
        assertEquals( "attribute.name", "dna.configuration", result.getName() );
        assertEquals( "attribute.value", null, result.getValue() );
        assertEquals( "attribute.parameterCount", 0, result.getParameterCount() );
    }

    public void testProcessMethodAttributeWithPhoenixConfigurationSchemaSpecifyingType()
        throws Exception
    {
        final PhoenixAttributeInterceptor interceptor = new PhoenixAttributeInterceptor();
        final Properties parameters = new Properties();
        final String type = "BobbaFett";
        parameters.setProperty( "type", type );
        final Attribute attribute = new Attribute( "phoenix:configuration-schema", parameters );
        final JavaClass javaClass = new JavaClass();
        javaClass.setName( "com.biz.MyClass" );
        final JavaMethod method = new JavaMethod();
        method.setParentClass( javaClass );
        final Attribute result =
            interceptor.processMethodAttribute( method, attribute );
        assertNotNull( "attribute", result );
        assertEquals( "attribute.name", "dna.configuration", result.getName() );
        assertEquals( "attribute.value", null, result.getValue() );
        assertEquals( "attribute.parameterCount", 1, result.getParameterCount() );
        assertEquals( "attribute.parameter(type)",
                      type, result.getParameter( "type" ) );
    }

    public void testProcessMethodAttributeWithPhoenixConfigurationSchemaSpecifyingRelaxType()
        throws Exception
    {
        final PhoenixAttributeInterceptor interceptor = new PhoenixAttributeInterceptor();
        final Properties parameters = new Properties();
        parameters.setProperty( "type", "relax-ng" );
        final Attribute attribute = new Attribute( "phoenix:configuration-schema", parameters );
        final JavaClass javaClass = new JavaClass();
        javaClass.setName( "com.biz.MyClass" );
        final JavaMethod method = new JavaMethod();
        method.setParentClass( javaClass );
        final Attribute result =
            interceptor.processMethodAttribute( method, attribute );
        assertNotNull( "attribute", result );
        assertEquals( "attribute.name", "dna.configuration", result.getName() );
        assertEquals( "attribute.value", null, result.getValue() );
        assertEquals( "attribute.parameterCount", 1, result.getParameterCount() );
        assertEquals( "attribute.parameter(type)",
                      "http://relaxng.org/ns/structure/1.0",
                      result.getParameter( "type" ) );
    }

    public void testProcessMethodAttributeWithPhoenixDependency()
        throws Exception
    {
        final PhoenixAttributeInterceptor interceptor = new PhoenixAttributeInterceptor();
        final Properties parameters = new Properties();
        final String type = "BobbaFett";
        parameters.setProperty( "name", type );
        final Attribute attribute = new Attribute( "phoenix:dependency", parameters );
        final Attribute result =
            interceptor.processMethodAttribute( new JavaMethod(), attribute );
        assertNotNull( "attribute", result );
        assertEquals( "attribute.name", "dna.dependency", result.getName() );
        assertEquals( "attribute.value", null, result.getValue() );
        assertEquals( "attribute.parameterCount", 1, result.getParameterCount() );
        assertEquals( "attribute.parameter(type)",
                      type, result.getParameter( "type" ) );
    }

    public void testProcessMethodAttributeWithPhoenixDependencyWithRoleSpecified()
        throws Exception
    {
        final PhoenixAttributeInterceptor interceptor = new PhoenixAttributeInterceptor();
        final Properties parameters = new Properties();
        final String type = "BobbaFett";
        final String key = "myKey";
        parameters.setProperty( "name", type );
        parameters.setProperty( "role", key );
        final Attribute attribute = new Attribute( "phoenix:dependency", parameters );
        final Attribute result =
            interceptor.processMethodAttribute( new JavaMethod(), attribute );
        assertNotNull( "attribute", result );
        assertEquals( "attribute.name", "dna.dependency", result.getName() );
        assertEquals( "attribute.value", null, result.getValue() );
        assertEquals( "attribute.parameterCount", 2, result.getParameterCount() );
        assertEquals( "attribute.parameter(type)",
                      type, result.getParameter( "type" ) );
        assertEquals( "attribute.parameter(key)",
                      key, result.getParameter( "key" ) );
    }

    public void testProcessClassAttributesWithoutMXService()
        throws Exception
    {
        final PhoenixAttributeInterceptor interceptor = new PhoenixAttributeInterceptor();
        final Attribute[] input = new Attribute[]{new Attribute( "RandomJAttribute" )};
        final Attribute[] attributes =
            interceptor.processClassAttributes( new JavaClass(), input );
        assertEquals( "attributes.length", 1, attributes.length );
        assertEquals( "attributes[0].name", "RandomJAttribute", attributes[ 0 ].getName() );
        assertEquals( "attributes[0].value", null, attributes[ 0 ].getValue() );
        assertEquals( "attributes[0].parameterCount", 0, attributes[ 0 ].getParameterCount() );
    }

    public void testProcessClassAttributesWithMXService()
        throws Exception
    {
        final PhoenixAttributeInterceptor interceptor = new PhoenixAttributeInterceptor();
        final Properties parameters = new Properties();
        final String type = "PerformMagicService";
        parameters.setProperty( "name", type );
        final Attribute attribute = new Attribute( "phoenix:mx", parameters );
        final Attribute[] input = new Attribute[]{attribute};
        final Attribute[] attributes =
            interceptor.processClassAttributes( new JavaClass(), input );
        assertEquals( "attributes.length", 2, attributes.length );
        assertEquals( "attributes[0].name", "dna.service", attributes[ 0 ].getName() );
        assertEquals( "attributes[0].value", null, attributes[ 0 ].getValue() );
        assertEquals( "attributes[0].parameterCount", 1, attributes[ 0 ].getParameterCount() );
        assertEquals( "attributes[0].parameter(type)",
                      type, attributes[ 0 ].getParameter( "type" ) );
        assertEquals( "attributes[1].name", "mx.interface", attributes[ 1 ].getName() );
        assertEquals( "attributes[1].value", null, attributes[ 1 ].getValue() );
        assertEquals( "attributes[1].parameterCount", 2, attributes[ 1 ].getParameterCount() );
        assertEquals( "attributes[1].parameter(type)",
                      type, attributes[ 1 ].getParameter( "type" ) );
        assertEquals( "attributes[1].parameter(topic)",
                      "PerformMagicService", attributes[ 1 ].getParameter( "type" ) );
    }

    public void testProcessMethodAttributesWithoutMXOperation()
        throws Exception
    {
        final PhoenixAttributeInterceptor interceptor = new PhoenixAttributeInterceptor();
        final Attribute[] input = new Attribute[]{new Attribute( "RandomJAttribute" )};
        final Attribute[] attributes =
            interceptor.processMethodAttributes( new JavaMethod(), input );
        assertEquals( "attributes.length", 1, attributes.length );
        assertEquals( "attributes[0].name", "RandomJAttribute", attributes[ 0 ].getName() );
        assertEquals( "attributes[0].value", null, attributes[ 0 ].getValue() );
        assertEquals( "attributes[0].parameterCount", 0, attributes[ 0 ].getParameterCount() );

    }

    public void testProcessMethodAttributesWithMXOperationWithoutParameters()
        throws Exception
    {
        final PhoenixAttributeInterceptor interceptor = new PhoenixAttributeInterceptor();
        final Attribute[] input = new Attribute[]{new Attribute( "mx.operation" )};
        final Attribute[] attributes =
            interceptor.processMethodAttributes( new JavaMethod(), input );
        assertEquals( "attributes.length", 1, attributes.length );
        assertEquals( "attributes[0].name", "mx.operation", attributes[ 0 ].getName() );
        assertEquals( "attributes[0].value", null, attributes[ 0 ].getValue() );
        assertEquals( "attributes[0].parameterCount", 0, attributes[ 0 ].getParameterCount() );
    }

    public void testProcessMethodAttributesWithMXOperationWithMalformedParameter()
        throws Exception
    {
        final PhoenixAttributeInterceptor interceptor = new PhoenixAttributeInterceptor();
        final Attribute[] input =
            new Attribute[]{new Attribute( "mx.operation" ),
                            new Attribute( "param", "var" )};
        final Attribute[] attributes =
            interceptor.processMethodAttributes( new JavaMethod(), input );
        assertEquals( "attributes.length", 2, attributes.length );
        assertEquals( "attributes[0].name", "mx.operation", attributes[ 0 ].getName() );
        assertEquals( "attributes[0].value", null, attributes[ 0 ].getValue() );
        assertEquals( "attributes[0].parameterCount", 0, attributes[ 0 ].getParameterCount() );
        assertEquals( "attributes[1].name", "param", attributes[ 1 ].getName() );
        assertEquals( "attributes[1].value", "var", attributes[ 1 ].getValue() );
        assertEquals( "attributes[1].parameterCount", 0, attributes[ 1 ].getParameterCount() );
    }

    public void testProcessMethodAttributesWithMXOperationWithParameter()
        throws Exception
    {
        final PhoenixAttributeInterceptor interceptor = new PhoenixAttributeInterceptor();
        final Attribute[] input =
            new Attribute[]{new Attribute( "mx.operation" ),
                            new Attribute( "param", "var This is the description" )};
        final Attribute[] attributes =
            interceptor.processMethodAttributes( new JavaMethod(), input );
        assertEquals( "attributes.length", 3, attributes.length );
        assertEquals( "attributes[0].name", "mx.operation", attributes[ 0 ].getName() );
        assertEquals( "attributes[0].value", null, attributes[ 0 ].getValue() );
        assertEquals( "attributes[0].parameterCount", 0, attributes[ 0 ].getParameterCount() );
        assertEquals( "attributes[1].name", "param", attributes[ 1 ].getName() );
        assertEquals( "attributes[1].value", "var This is the description", attributes[ 1 ].getValue() );
        assertEquals( "attributes[1].parameterCount", 0, attributes[ 1 ].getParameterCount() );
        assertEquals( "attributes[2].name", "mx.parameter", attributes[ 2 ].getName() );
        assertEquals( "attributes[2].value", null, attributes[ 2 ].getValue() );
        assertEquals( "attributes[2].parameterCount", 2, attributes[ 2 ].getParameterCount() );
        assertEquals( "attributes[2].parameter(name)",
                      "var",
                      attributes[ 2 ].getParameter( "name" ) );
        assertEquals( "attributes[2].parameter(description)",
                      "This is the description",
                      attributes[ 2 ].getParameter( "description" ) );
    }
}
