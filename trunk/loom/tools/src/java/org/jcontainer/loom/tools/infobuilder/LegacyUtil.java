/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.tools.infobuilder;

import org.jcontainer.loom.tools.info.ComponentInfo;
import org.jcontainer.loom.tools.info.DependencyDescriptor;
import org.jcontainer.loom.tools.info.ServiceDescriptor;
import org.realityforge.metaclass.model.Attribute;

/**
 * This is a set of constants and utility methods
 * to enablesupport of Legacy BlockInfo files.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.12 $ $Date: 2003-10-11 09:03:15 $
 */
public class LegacyUtil
{
    public static final String MX_ATTRIBUTE_NAME = "phoenix:mx";
    public static final Attribute MX_ATTRIBUTE = new Attribute( MX_ATTRIBUTE_NAME );

    private LegacyUtil()
    {
    }

    /**
     * Return the version specified (if any) for feature.
     *
     * @param type the type
     * @return the translated schema type
     */
    public static String translateToSchemaUri( final String type )
    {
        if( type.equals( "relax-ng" ) )
        {
            return "http://relaxng.org/ns/structure/1.0";
        }
        else
        {
            return type;
        }
    }

    /**
     * Return true if specified service is a management service.
     *
     * @param service the service
     * @return true if specified service is a management service, false otherwise.
     */
    public static boolean isMxService( final ServiceDescriptor service )
    {
        final Attribute tag = service.getAttribute( MX_ATTRIBUTE_NAME );
        return null != tag;
    }

    /**
     * Create a {@link ComponentInfo} for a Listener with specified classname.
     *
     * @param implementationKey the classname of listener
     * @return the ComponentInfo for listener
     */
    public static ComponentInfo createListenerInfo( final String implementationKey )
    {
        return new ComponentInfo( implementationKey,
                                  Attribute.EMPTY_SET,
                                  ServiceDescriptor.EMPTY_SET,
                                  DependencyDescriptor.EMPTY_SET,
                                  null );
    }

    /**
     * Get the location of the schema. By default it is "Foo-schema.xml"
     * for the com.biz.Foo component.
     *
     * @param classname the classname of component
     * @return the location of the schema
     */
    public static String getSchemaLocationFor( final String classname )
    {
        final int index = classname.lastIndexOf( "." );
        String location = classname;
        if( -1 != index )
        {
            location = classname.substring( index + 1 );
        }
        location += "-schema.xml";
        return location;
    }
}
