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
package org.jcontainer.loom.demos.helloworld;


/**
 * Specifies methods to export via Management interface.
 *
 * @phoenix:mx-topic name="Greeting"
 *
 * @author  Huw Roberts <huw@mmlive.com>
 * @version 1.0
 */
public interface HelloWorldServerMBean
{
    /**
     * The greeting that is returned to each HTTP request
     *
     * @phoenix:mx-attribute
     */
    public void setGreeting( final String greeting );

    /**
     * Gets the greeting that is returned to each HTTP request
     *
     */
    String getGreeting();
}
