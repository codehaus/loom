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
package org.jcontainer.loom.tools.qdox;

import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.Type;
import java.util.ArrayList;
import org.jcontainer.loom.tools.info.ComponentInfo;
import org.jcontainer.loom.tools.info.DependencyDescriptor;
import org.jcontainer.loom.tools.info.SchemaDescriptor;
import org.jcontainer.loom.tools.info.ServiceDescriptor;
import org.jcontainer.loom.tools.infobuilder.LegacyUtil;
import org.realityforge.metaclass.model.Attribute;

/**
 * Build a ComponentInfo object by interpreting Phoenix style javadoc
 * markup in source.
 *
 * @author Paul Hammant
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.3 $ $Date: 2003-10-14 08:42:16 $
 */
public class BlockInfoBuilder
{
    private static final String COMPONENT_MANAGER_CLASS =
        "org.apache.avalon.framework.component.ComponentManager";
    private static final String SERVICE_MANAGER_CLASS =
        "org.apache.avalon.framework.service.ServiceManager";
    private static final String CONFIGURATION_CLASS =
        "org.apache.avalon.framework.configuration.Configuration";

    /**
     * Build a ComponentInfo object for specified class.
     *
     * @param javaClass the class
     * @return the ComponentInfo object
     */
    public ComponentInfo buildComponentInfo( final JavaClass javaClass )
    {
        final ServiceDescriptor[] services = buildServices( javaClass );
        final SchemaDescriptor schema = buildConfigurationSchema( javaClass );
        final DependencyDescriptor[] dependencies = buildDependencies( javaClass );

        return new ComponentInfo( javaClass.getFullyQualifiedName(),
                                  services,
                                  dependencies,
                                  schema );
    }

    /**
     * Build the set of service descriptors for specified class.
     *
     * @param javaClass the class
     * @return the set of service descriptors
     */
    private ServiceDescriptor[] buildServices( final JavaClass javaClass )
    {
        final ArrayList services = new ArrayList();
        final DocletTag[] serviceTags = javaClass.getTagsByName( "phoenix:service" );
        for( int i = 0; i < serviceTags.length; i++ )
        {
            final String type = getNamedParameter( serviceTags[ i ], "name" );
            final ServiceDescriptor service = new ServiceDescriptor( type, Attribute.EMPTY_SET );
            services.add( service );
        }
        final DocletTag[] mxTags = javaClass.getTagsByName( "phoenix:mx" );
        for( int i = 0; i < mxTags.length; i++ )
        {
            final String type = getNamedParameter( mxTags[ i ], "name" );
            final ServiceDescriptor service =
                new ServiceDescriptor( type, new Attribute[]{LegacyUtil.MX_ATTRIBUTE} );
            services.add( service );
        }
        return (ServiceDescriptor[])services.toArray( new ServiceDescriptor[ services.size() ] );
    }

    /**
     * Build the schema descriptor for specified class.
     *
     * @param javaClass the class
     * @return the schema descriptor
     */
    private SchemaDescriptor buildConfigurationSchema( final JavaClass javaClass )
    {
        final JavaMethod method =
            getLifecycleMethod( javaClass, "configure", CONFIGURATION_CLASS );
        if( null == method )
        {
            return null;
        }

        final DocletTag tag =
            method.getTagByName( "phoenix:configuration-schema" );
        if( null == tag )
        {
            return null;
        }
        final String type = getNamedParameter( tag, "type" );
        final String classname = javaClass.getFullyQualifiedName();
        final String location = LegacyUtil.getSchemaLocationFor( classname );
        return new SchemaDescriptor( location, type, Attribute.EMPTY_SET );
    }

    /**
     * Build the set of dependency descriptors for specified class.
     *
     * @param javaClass the class
     * @return the set of dependency descriptors
     */
    private DependencyDescriptor[] buildDependencies( final JavaClass javaClass )
    {
        JavaMethod method =
            getLifecycleMethod( javaClass, "compose", COMPONENT_MANAGER_CLASS );

        //If no compose then try for a service method ...
        if( null == method )
        {
            method =
                getLifecycleMethod( javaClass, "service", SERVICE_MANAGER_CLASS );
        }

        if( null == method )
        {
            return DependencyDescriptor.EMPTY_SET;
        }
        else
        {
            final ArrayList deps = new ArrayList();
            final DocletTag[] tags = method.getTagsByName( "phoenix:dependency" );
            for( int i = 0; i < tags.length; i++ )
            {
                final DocletTag tag = tags[ i ];
                final String unresolvedType = getNamedParameter( tag, "name" );
                final String type = resolveType( javaClass, unresolvedType );
                final String key = getNamedParameter( tag, "role", type );
                final DependencyDescriptor dependency =
                    new DependencyDescriptor( key, type, false, Attribute.EMPTY_SET );
                deps.add( dependency );
            }
            return (DependencyDescriptor[])deps.toArray( new DependencyDescriptor[ deps.size() ] );
        }
    }

    /**
     * Resolve the specified type.
     * Resolving essentially means finding the fully qualified name of
     * a class from just it's short name.
     *
     * @param javaClass the java class relative to which the type must be resolved
     * @param type the unresolved type
     * @return the resolved type
     */
    private String resolveType( final JavaClass javaClass,
                                final String type )
    {
        return javaClass.getParentSource().resolveType( type );
    }

    /**
     * Retrieve a method with specified name and one parameter of specified
     * type. The method must also return void.
     *
     * @param javaClass the java class to retrieve method for
     * @param methodName the name of the method
     * @param parameterType the class name of parameter
     * @return the method if such a method exists
     */
    private JavaMethod getLifecycleMethod( final JavaClass javaClass,
                                           final String methodName,
                                           final String parameterType )
    {
        final JavaMethod[] methods = javaClass.getMethods();
        for( int i = 0; i < methods.length; i++ )
        {
            final JavaMethod method = methods[ i ];
            if( methodName.equals( method.getName() ) &&
                method.getReturns().equals( new Type( "void", 0 ) ) &&
                method.getParameters().length == 1 &&
                method.getParameters()[ 0 ].getType().getValue().equals( parameterType ) )
            {
                return method;
            }
        }
        return null;
    }

    /**
     * Retrieve specified named parameter from tag. If the parameter
     * does not exist then return specified default value.
     *
     * @param tag the tag
     * @param name the name of parameter
     * @return the value of named parameter
     */
    private String getNamedParameter( final DocletTag tag,
                                      final String name,
                                      final String defaultValue )
    {
        String value = tag.getNamedParameter( name );
        if( null == value )
        {
            return defaultValue;
        }
        value = value.trim();
        if( value.startsWith( "\"" ) || value.startsWith( "'" ) )
        {
            value = value.substring( 1 );
        }
        if( value.endsWith( "\"" ) || value.endsWith( "'" ) )
        {
            value = value.substring( 0, value.length() - 1 );
        }
        return value;
    }

    /**
     * Retrieve specified named parameter from tag. If the parameter
     * does not exist then throw an exception.
     *
     * @param tag the tag
     * @param name the name of parameter
     * @return the value of named parameter
     */
    private String getNamedParameter( final DocletTag tag, final String name )
    {
        final String value = getNamedParameter( tag, name, null );
        if( null == value )
        {
            final String message =
                "Malformed tag '" + tag.getName() + "'. " +
                "Missing required parameter '" + name + "'";
            throw new IllegalArgumentException( message );
        }
        return value;
    }
}
