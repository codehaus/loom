/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.tools.qdox;

import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.Type;
import org.jcontainer.loom.tools.info.ContextDescriptor;

/**
 * This is an abstract base class that is used to build a ComponentInfo object
 * from QDoxs JavaClass object model. Subclasses interpret different dialects
 * of javadocs markup.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003-06-26 08:42:06 $
 */
class AbstractInfoBuilder
{
    protected static final String LOGGER_CLASS =
        "org.apache.avalon.framework.logger.Logger";
    protected static final String CONTEXT_CLASS = ContextDescriptor.DEFAULT_TYPE;
    protected static final String COMPONENT_MANAGER_CLASS =
        "org.apache.avalon.framework.component.ComponentManager";
    protected static final String SERVICE_MANAGER_CLASS =
        "org.apache.avalon.framework.service.ServiceManager";
    protected static final String CONFIGURATION_CLASS =
        "org.apache.avalon.framework.configuration.Configuration";
    protected static final String PARAMETERS_CLASS =
        "org.apache.avalon.framework.parameters.Parameters";

    /**
     * Resolve the specified type.
     * Resolving essentially means finding the fully qualified name of
     * a class from just it's short name.
     *
     * @param javaClass the java class relative to which the type must be resolved
     * @param type the unresolved type
     * @return the resolved type
     */
    protected String resolveType( final JavaClass javaClass,
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
    protected JavaMethod getLifecycleMethod( final JavaClass javaClass,
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
    protected String getNamedParameter( final DocletTag tag,
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
    protected String getNamedParameter( final DocletTag tag, final String name )
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
