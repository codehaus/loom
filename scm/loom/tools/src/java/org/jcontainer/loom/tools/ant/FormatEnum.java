/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.tools.ant;

import org.apache.tools.ant.types.EnumeratedAttribute;

/**
 * This is an enumeration that gives the option of either
 * outputting as xml or as a serialized format.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.3 $ $Date: 2003-10-06 10:12:54 $
 */
public class FormatEnum
    extends EnumeratedAttribute
{
    public int getTypeCode()
    {
        final String value = super.getValue();
        if( value.equals( "legacy" ) )
        {
            return MetaGenerateTask.LEGACY_TYPE;
        }
        else
        {
            return MetaGenerateTask.XML_TYPE;
        }
    }

    public String[] getValues()
    {
        return new String[]{"xml", "legacy"};
    }
}
