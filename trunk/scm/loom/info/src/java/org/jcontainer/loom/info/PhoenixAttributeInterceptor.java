/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.info;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.DocletTag;
import java.util.Properties;
import java.util.ArrayList;
import org.realityforge.metaclass.model.Attribute;
import org.realityforge.metaclass.tools.qdox.QDoxAttributeInterceptor;
import org.realityforge.metaclass.tools.qdox.DefaultQDoxAttributeInterceptor;

/**
 * This is an Attribute interceptor that invoked during construction
 * of ClassDescriptors that will translate legacy Loom attributes
 * into modern DNA and MX attributes.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003-10-12 04:54:12 $
 */
public class PhoenixAttributeInterceptor
    extends DefaultQDoxAttributeInterceptor
    implements QDoxAttributeInterceptor
{
    /**
     * @see QDoxAttributeInterceptor#processClassAttribute(JavaClass, Attribute)
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
            final String type = attribute.getParameter( "name", "" );
            parameters.setProperty( "type", type );
            return new Attribute( "dna.service", parameters );
        }
        else if( name.equals( "phoenix:mx-topic" ) )
        {
            return new Attribute( "mx.component" );
        }
        else if( name.equals( "phoenix:mx-proxy" ) )
        {
            final Properties parameters = new Properties();
            final String type = attribute.getParameter( "class", "" );
            parameters.setProperty( "type", type );
            return new Attribute( "mx.proxy", parameters );
        }
        else
        {
            return attribute;
        }
    }

    /**
     * @see QDoxAttributeInterceptor#processFieldAttribute(JavaField, Attribute)
     */
    public Attribute processFieldAttribute( final JavaField field,
                                            final Attribute attribute )
    {
        final String name = attribute.getName();
        if( name.equals( "phoenix:mx-attribute" ) )
        {
            final DocletTag descriptionTag =
                field.getTagByName( "phoenix:mx-description" );
            final String description;
            if( null != descriptionTag )
            {
                description = descriptionTag.getValue();
            }
            else
            {
                description = field.getComment();
            }
            final Properties parameters = new Properties();
            if( null != description )
            {
                parameters.setProperty( "description", description );
            }
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
     * @see QDoxAttributeInterceptor#processMethodAttribute(JavaMethod, Attribute)
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
            if( null != type )
            {
                parameters.setProperty( "type", type );
            }
            return new Attribute( "dna.configuration", parameters );
        }
        else if( name.equals( "phoenix:dependency" ) )
        {
            final Properties parameters = new Properties();
            final String key = attribute.getParameter( "role", null );
            final String type = attribute.getParameter( "name", "" );
            parameters.setProperty( "type", type );
            if( null != key )
            {
                parameters.setProperty( "key", key );
            }
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
            if( null != description )
            {
                parameters.setProperty( "description", description );
            }
            return new Attribute( "mx.operation", parameters );
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
     * @see QDoxAttributeInterceptor#processClassAttributes(JavaClass, Attribute[])
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
                parameters.setProperty( "type", type );
                result.add( new Attribute( "dna.service", parameters ) );
                result.add( new Attribute( "mx.interface", parameters ) );
            }
        }
        return (Attribute[])result.toArray( new Attribute[ result.size() ] );
    }

    /**
     * @see QDoxAttributeInterceptor#processMethodAttributes(JavaMethod, Attribute[])
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
                    parameters.setProperty( "name", name );
                    parameters.setProperty( "description", description );
                    result.add( new Attribute( "mx.parameter", parameters ) );
                }
            }
        }
        return (Attribute[])result.toArray( new Attribute[ result.size() ] );
    }
}
