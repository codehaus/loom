/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.tools.infobuilder;

/**
 * This is a set of constants and utility methods
 * to enablesupport of Legacy BlockInfo files.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.15 $ $Date: 2003-10-15 04:20:42 $
 */
public class LegacyUtil
{
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
