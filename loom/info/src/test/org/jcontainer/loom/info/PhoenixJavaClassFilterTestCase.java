/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.info;

import junit.framework.TestCase;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.DocletTag;
import java.util.ArrayList;

/**
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.2 $ $Date: 2003-10-16 08:43:28 $
 */
public class PhoenixJavaClassFilterTestCase
    extends TestCase
{
    public void testClassWithoutPhoenixMetaData()
        throws Exception
    {
        final PhoenixJavaClassFilter filter = new PhoenixJavaClassFilter();
        final JavaClass javaClass = new JavaClass();
        final JavaClass result = filter.filterClass( javaClass );
        assertNull( "javaClass", result );
    }

    public void testClassWithPhoenixMxTopic()
        throws Exception
    {
        final PhoenixJavaClassFilter filter = new PhoenixJavaClassFilter();
        final JavaClass javaClass = new JavaClass();
        final ArrayList tags = new ArrayList();
        tags.add( new DocletTag( "phoenix:mx-topic", "" ) );
        javaClass.setTags( tags );
        final JavaClass result = filter.filterClass( javaClass );
        assertEquals( "javaClass", javaClass, result );
    }

    public void testClassWithPhoenixBlock()
        throws Exception
    {
        final PhoenixJavaClassFilter filter = new PhoenixJavaClassFilter();
        final JavaClass javaClass = new JavaClass();
        final ArrayList tags = new ArrayList();
        tags.add( new DocletTag( "phoenix:block", "" ) );
        javaClass.setTags( tags );
        final JavaClass result = filter.filterClass( javaClass );
        assertEquals( "javaClass", javaClass, result );
    }

    public void testClassWithDNAComponent()
        throws Exception
    {
        final PhoenixJavaClassFilter filter = new PhoenixJavaClassFilter();
        final JavaClass javaClass = new JavaClass();
        final ArrayList tags = new ArrayList();
        tags.add( new DocletTag( "dna.component", "" ) );
        javaClass.setTags( tags );
        final JavaClass result = filter.filterClass( javaClass );
        assertEquals( "javaClass", javaClass, result );
    }
}
