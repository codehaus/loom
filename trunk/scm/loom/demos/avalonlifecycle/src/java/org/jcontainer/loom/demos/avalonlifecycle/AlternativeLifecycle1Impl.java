/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 *
 * This product includes software developed by the
 * Apache Software Foundation (http://www.apache.org/).
 */
package org.jcontainer.loom.demos.avalonlifecycle;

/**
 * A demo of the lifecycle methods.  Mount the SAR fle contaning there blocks in Loom, go
 * to the JMX console ..
 *   http://localhost:8082/mbean?objectname=Loom%3Aapplication%3Ddemo-avalonlifecycle%2Ctopic%3DApplication
 * .. and try stopt/starting the blocks.
 *
 * You'll also need to see the src/conf/assembly.xml file to understand
 * how this can replace the other class
 *
 * @phoenix:block
 * @phoenix:service name="org.jcontainer.loom.demos.avalonlifecycle.Lifecycle1"
 * @author  Paul Hammant <Paul_Hammant@yahoo.com>
 * @version 1.0
 */
public class AlternativeLifecycle1Impl implements Lifecycle1
{

    /**
     * The method from our service interface -> Lifecycle1
     * @return
     */
    public int myServiceMethod()
    {
        return 456;
    }

}
