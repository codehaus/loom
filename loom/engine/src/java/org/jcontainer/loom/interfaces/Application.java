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

/**
 * The Application is a self-contained component that performs a specific
 * function.
 *
 * Example ServerApplications may be a Mail Server, File Server, Directory Server etc.
 * Example JesktopApplications may be a Spreadsheet program, browser, mail client
 * Example WebApplications may be a particular website or application within a website
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 */
public interface Application
{
    /** Role String for interface */
    String ROLE = Application.class.getName();

    /**
     * Set the context in which the Application is "executed".
     *
     * @param context the applications context
     */
    void setApplicationContext( ApplicationContext context );

    /**
     * Retrieve names of Blocks contained in application.
     *
     * @return
     */
    String[] getBlockNames();

    /**
     * Retrieve Block with specified name.
     * If no such block exists a null  will be returned.
     *
     * @param name the name of block to retrieve
     * @return the coresponding block or null if none
     */
    Object getBlock( String name );
}
