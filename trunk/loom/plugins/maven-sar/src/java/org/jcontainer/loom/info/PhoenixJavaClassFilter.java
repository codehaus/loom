/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.info;

import org.realityforge.metaclass.tools.compiler.JavaClassFilter;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.DocletTag;

/**
 * Filter that only accepts classes annotated with Phoenix
 * metadata. The onyl classes processed are those the JavaDoc tags
 * "phoenix:mx-topic" or "phoenix:block" at the class level.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003-11-01 13:15:31 $
 */
public class PhoenixJavaClassFilter
    implements JavaClassFilter
{
    /**
     * @see JavaClassFilter#filterClass(JavaClass)
     */
    public JavaClass filterClass( final JavaClass javaClass )
    {
        final DocletTag dnaTag = javaClass.getTagByName( "dna.component" );
        final DocletTag mxTag = javaClass.getTagByName( "mx.component" );
        final DocletTag pmxTag = javaClass.getTagByName( "phoenix:mx-topic" );
        final DocletTag blockTag = javaClass.getTagByName( "phoenix:block" );
        if( null != pmxTag ||
            null != mxTag ||
            null != dnaTag ||
            null != blockTag )
        {
            return javaClass;
        }
        else
        {
            return null;
        }
    }
}
