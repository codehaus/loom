/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.components.util.infobuilder;

/**
 * This is a set of constants and utility methods to enablesupport of Legacy BlockInfo files.
 *
 * @author Peter Donald
 * @version $Revision: 1.1 $ $Date: 2004-02-20 03:49:49 $
 */
class LegacyUtil
{
    /**
     * Get the location of the schema. By default it is "Foo-schema.xml" for the com.biz.Foo component.
     *
     * @param classname the classname of component
     *
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