/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.info;

import com.thoughtworks.qdox.model.JavaClass;

/**
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003-11-27 07:03:56 $
 */
class MockJavaClass
    extends JavaClass
{
    public MockJavaClass()
    {
        super( new MockJavaSource() );
    }
}
