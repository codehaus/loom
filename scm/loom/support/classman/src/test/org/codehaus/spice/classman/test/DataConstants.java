/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.codehaus.spice.classman.test;

import org.codehaus.spice.extension.Extension;

/**
 * Constants used in the tests.
 *
 * @author Peter Donald
 * @version $Revision: 1.1 $ $Date: 2003-12-02 08:17:22 $
 */
public interface DataConstants
{
    String DATA1_RESOURCE = "org/codehaus/spice/classman/test/data/cl1/Resource.properties";
    String DATA1_CLASS = "org.codehaus.spice.classman.test.data.cl1.Data1";
    String DATA1_CLASS2 = "org.codehaus.spice.classman.test.data.cl1.Data2";
    String DATA2_CLASS = "org.codehaus.spice.classman.test.data.cl2.CL2Data";
    String DATA3_CLASS = "org.codehaus.spice.classman.test.data.cl3.CL3Data";
    String DATA4_CLASS = "org.codehaus.spice.classman.test.data.cl4.CL4Data";
    Extension EXTENSION =
        new Extension( "Foo-Ext", null, null, null, null, null, null );
}
