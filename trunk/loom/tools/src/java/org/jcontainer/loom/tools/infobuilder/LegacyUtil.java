/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.tools.infobuilder;

import org.jcontainer.loom.tools.info.ServiceDescriptor;
import org.realityforge.metaclass.model.Attribute;

/**
 * This is a set of constants and utility methods
 * to enablesupport of Legacy BlockInfo files.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.14 $ $Date: 2003-10-11 09:06:50 $
 */
public class LegacyUtil
{
    public static final String MX_ATTRIBUTE_NAME = "phoenix:mx";
    public static final Attribute MX_ATTRIBUTE = new Attribute( MX_ATTRIBUTE_NAME );

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
