/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 *
 * This product includes software developed by the
 * Apache Software Foundation (http://www.apache.org/).
 */
package org.jcontainer.loom.components.kernel.beanshell;

import org.jcontainer.loom.components.kernel.DefaultKernel;

public class BeanShellKernel
    extends DefaultKernel
{

    /**
     * Overides Initialize from DefaultKernel
     */
    public void initialize()
        throws Exception
    {
        super.initialize();

        final BeanShellGUI beanShell = new BeanShellGUI( new BeanShellKernelProxy( this ) );
        beanShell.init();
    }
}
