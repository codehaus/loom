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
 * A Deployer is responsible for taking a URL (ie a jar/war/ear) and deploying
 * it to a particular "location". "location" means different things for
 * different containers. For a servlet container it may mean the place to
 * mount servlet (ie /myapp --> /myapp/Cocoon.xml is mapping cocoon servlet to
 * /myapp context).
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @see DeployerMBean
 */
public interface Deployer
{
    String ROLE = Deployer.class.getName();

    /**
     * Deploy an installation.
     *
     * @param name the name of deployment
     * @param location the installation to deploy
     * @throws DeploymentException if an error occurs
     */
    void deploy( String name, URL location )
        throws DeploymentException;

    /**
     * Undeploy and deploy an installation.
     *
     * @param name the name of deployment
     * @param location the installation to redeploy
     * @throws DeploymentException if an error occurs
     */
    void redeploy( String name, URL location )
        throws DeploymentException;

    /**
     * Undeploy a resource from a location.
     *
     * @param name the name of deployment
     * @throws DeploymentException if an error occurs
     */
    void undeploy( String name )
        throws DeploymentException;

    /**
     * Determine if a deployment has matching name.
     *
     * @param name the name of deployment
     * @return true if deployed by this deployer, false otherwise
     */
    //boolean isDeployed( String name );
}
