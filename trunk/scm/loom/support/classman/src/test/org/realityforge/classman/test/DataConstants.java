/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.realityforge.classman.test;

import org.realityforge.extension.Extension;

/**
 * Constants used in the tests.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.3 $ $Date: 2003-08-01 02:51:55 $
 */
public interface DataConstants
{
    String DATA1_RESOURCE = "org/realityforge/classman/test/data/cl1/Resource.properties";
    String DATA1_CLASS = "org.realityforge.classman.test.data.cl1.Data1";
    String DATA1_CLASS2 = "org.realityforge.classman.test.data.cl1.Data2";
    String DATA2_CLASS = "org.realityforge.classman.test.data.cl2.CL2Data";
    String DATA3_CLASS = "org.realityforge.classman.test.data.cl3.CL3Data";
    String DATA4_CLASS = "org.realityforge.classman.test.data.cl4.CL4Data";
    Extension EXTENSION =
        new Extension( "Foo-Ext", null, null, null, null, null, null );
}
