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
package org.jcontainer.loom.interfaces;

import java.net.URL;

/**
 * MBean Interface for the Deployer to use the deploy
 * feature in the HmtlAdaptor
 *
 * @phoenix:mx-topic name="Deployer"
 *
 * @author <a href="mailto:bauer@denic.de">Joerg Bauer</a>
 * @see Deployer
 */
public interface DeployerMBean
{
    String ROLE = Deployer.class.getName();

    /**
     * Deploy an installation.
     *
     * @phoenix:mx-operation
     *
     * @param name the name of deployment
     * @param sarURL the installation to deploy
     * @throws DeploymentException if an error occurs
     * @see #deploy(String,String)
     * @see #undeploy(String)
     * @see #redeploy(String)
     */
    void deploy( String name, String sarURL )
        throws DeploymentException;

    /**
     * Deploy an installation.
     *
     * @phoenix:mx-operation
     *
     * @param name the name of deployment
     * @param location the installation to deploy
     * @throws DeploymentException if an error occurs
     * @see #deploy(String,String)
     * @see #undeploy(String)
     * @see #redeploy(String)
     */
    void deploy( String name, URL location )
        throws DeploymentException;

    /**
     * Undeploy an installation.
     *
     * @phoenix:mx-operation
     *
     * @param name the name of deployment
     * @throws DeploymentException if an error occurs
     * @see #deploy(String,String)
     * @see #deploy(String,URL)
     * @see #redeploy(String)
     */
    void undeploy( String name )
        throws DeploymentException;

    /**
     * Redeploy an installation.
     *
     * @phoenix:mx-operation
     *
     * @param name the name of deployment
     * @throws DeploymentException if an error occurs
     * @see #deploy(String,String)
     * @see #deploy(String,URL)
     * @see #undeploy(String)
     */
    void redeploy( String name )
        throws DeploymentException;
}
