/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.info;

import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import java.util.ArrayList;
import java.util.Properties;
import org.realityforge.metaclass.model.Attribute;
import org.realityforge.metaclass.tools.qdox.DefaultQDoxAttributeInterceptor;
import org.realityforge.metaclass.tools.qdox.QDoxAttributeInterceptor;

/**
 * This is an Attribute interceptor that invoked during construction of
 * ClassDescriptors that will translate legacy Loom attributes into modern DNA
 * and MX attributes.
 *
 * @author Peter Donald
 * @version $Revision: 1.2 $ $Date: 2003-11-29 13:44:31 $
 */
public class PhoenixAttributeInterceptor
    extends DefaultQDoxAttributeInterceptor
    implements QDoxAttributeInterceptor
{
    /**
     * @see QDoxAttributeInterceptor#processClassAttribute(JavaClass,
        *      Attribute)
     */
    public Attribute processClassAttribute( final JavaClass clazz,
                                            final Attribute attribute )
    {
        final String name = attribute.getName();
        if( name.equals( "phoenix:block" ) )
        {
            return new Attribute( "dna.component" );
        }
        else if( name.equals( "phoenix:service" ) )
        {
            final Properties parameters = new Properties();
            final String type = attribute.getParameter( "name", null );
            setParameter( parameters, "type", type );
            return new Attribute( "dna.service", parameters );
        }
        else if( name.equals( "phoenix:mx-topic" ) )
        {
            final String description = attribute.getParameter( "name", "" );
            final Properties parameters = new Properties();
            setParameter( parameters, "description", description );
            return new Attribute( "mx.component", parameters );
        }
        else if( name.equals( "phoenix:mx-proxy" ) )
        {
            final Properties parameters = new Properties();
            final String type = attribute.getParameter( "class", "" );
            setParameter( parameters, "type", type );
            return new Attribute( "mx.proxy", parameters );
        }
        else
        {
            return attribute;
        }
    }

    /**
     * @see QDoxAttributeInterceptor#processMethodAttribute(JavaMethod,
        *      Attribute)
     */
    public Attribute processMethodAttribute( final JavaMethod method,
                                             final Attribute attribute )
    {
        final String name = attribute.getName();
        if( name.equals( "phoenix:configuration-schema" ) )
        {
            final String type =
                attribute.getParameter( "type", null );
            final Properties parameters = new Properties();
            if( "relax-ng".equals( type ) )
            {
                setParameter( parameters,
                              "type",
                              "http://relaxng.org/ns/structure/1.0" );
            }
            else
            {
                setParameter( parameters, "type", type );
            }
            return new Attribute( "dna.configuration", parameters );
        }
        else if( name.equals( "phoenix:dependency" ) )
        {
            final Properties parameters = new Properties();
            final String key = attribute.getParameter( "role", null );
            final String type = attribute.getParameter( "name", null );
            setParameter( parameters, "type", type );
            setParameter( parameters, "key", key );
            return new Attribute( "dna.dependency", parameters );
        }
        else if( name.equals( "phoenix:mx-operation" ) )
        {
            final DocletTag descriptionTag =
                method.getTagByName( "phoenix:mx-description" );
            final String description;
            if( null != descriptionTag )
            {
                description = descriptionTag.getValue();
            }
            else
            {
                description = method.getComment();
            }
            final Properties parameters = new Properties();
            setParameter( parameters, "description", description );
            return new Attribute( "mx.operation", parameters );
        }
        if( name.equals( "phoenix:mx-attribute" ) )
        {
            final DocletTag descriptionTag =
                method.getTagByName( "phoenix:mx-description" );
            final String description;
            if( null != descriptionTag )
            {
                description = descriptionTag.getValue();
            }
            else
            {
                description = method.getComment();
            }
            final Properties parameters = new Properties();
            setParameter( parameters, "description", description );
            return new Attribute( "mx.attribute", parameters );
        }
        else if( name.equals( "phoenix:mx-description" ) )
        {
            return null;
        }
        else
        {
            return attribute;
        }
    }

    /**
     * @see QDoxAttributeInterceptor#processClassAttributes(JavaClass,
        *      Attribute[])
     */
    public Attribute[] processClassAttributes( final JavaClass clazz,
                                               final Attribute[] attributes )
    {
        final ArrayList result = new ArrayList();
        for( int i = 0; i < attributes.length; i++ )
        {
            final Attribute attribute = attributes[ i ];
            final String name = attribute.getName();
            if( !name.equals( "phoenix:mx" ) )
            {
                result.add( attribute );
            }
            else
            {
                final Properties parameters = new Properties();
                final String type = attribute.getParameter( "name", "" );
                setParameter( parameters, "type", type );
                result.add( new Attribute( "dna.service", parameters ) );

                final Properties mxParameters = new Properties();
                setParameter( mxParameters, "type", type );
                final String topic = type.substring(
                    type.lastIndexOf( '.' ) + 1 );
                setParameter( mxParameters, "topic", topic );

                result.add( new Attribute( "mx.interface", mxParameters ) );
            }
        }
        return (Attribute[])result.toArray( new Attribute[ result.size() ] );
    }

    /**
     * @see QDoxAttributeInterceptor#processMethodAttributes(JavaMethod,
        *      Attribute[])
     */
    public Attribute[] processMethodAttributes( final JavaMethod method,
                                                final Attribute[] attributes )
    {
        final ArrayList result = new ArrayList();

        boolean isMxOperation = false;
        for( int i = 0; i < attributes.length; i++ )
        {
            final Attribute attribute = attributes[ i ];
            if( attribute.getName().equals( "mx.operation" ) )
            {
                isMxOperation = true;
            }
            result.add( attribute );
        }
        if( isMxOperation )
        {
            for( int i = 0; i < attributes.length; i++ )
            {
                final Attribute attribute = attributes[ i ];
                if( attribute.getName().equals( "param" ) )
                {
                    final String value = attribute.getValue();
                    final int index = value.indexOf( " " );
                    if( -1 == index )
                    {
                        continue;
                    }
                    final String name = value.substring( 0, index ).trim();
                    final String description = value.substring( index + 1 );
                    final Properties parameters = new Properties();
                    setParameter( parameters, "name", name );
                    setParameter( parameters, "description", description );
                    result.add( new Attribute( "mx.parameter", parameters ) );
                }
            }
        }
        return (Attribute[])result.toArray( new Attribute[ result.size() ] );
    }

    /**
     * Set parameter if value not null.
     *
     * @param parameters the parameters object
     * @param key the key
     * @param value the value
     */
    private void setParameter( final Properties parameters,
                               final String key,
                               final String value )
    {
        if( null != value )
        {
            parameters.setProperty( key, value );
        }
    }
}
