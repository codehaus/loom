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

import java.io.File;
import java.io.InputStream;
import org.apache.avalon.framework.logger.Logger;
import org.apache.excalibur.instrument.InstrumentManager;

/**
 * Manage the "context" in which Applications operate.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 */
public interface ApplicationContext
{
    String ROLE = ApplicationContext.class.getName();

    File getHomeDirectory();

    org.jcontainer.loom.tools.profile.PartitionProfile getPartitionProfile();

    /**
     * A application can request that it be be shutdown. In most cases
     * the kernel will schedule the shutdown to occur in another thread.
     */
    void requestShutdown();

    /**
     * Export specified object into management system.
     * The object is exported using specifed interface
     * and using the specified name.
     *
     * @param name the name of object to export
     * @param interfaceClasses the interface of object with which to export
     * @param object the actual object to export
     */
    void exportObject( String name, Class[] interfaceClasses, Object object )
        throws Exception;

    /**
     * Unexport specified object from management system.
     *
     * @param name the name of object to unexport
     */
    void unexportObject( String name )
        throws Exception;

    /**
     * Get ClassLoader for the current application.
     *
     * @return the ClassLoader
     */
    ClassLoader getClassLoader();

    /**
     * This method grants access to a named ClassLoader. The ClassLoaders
     * for an application are declared in the <tt>environment.xml</tt>
     * descriptor. See the Specification for details.
     */
    ClassLoader getClassLoader( String name )
        throws Exception;

    /**
     * Retrieve a resource from the SAR file. The specified
     * name is relative the root of the archive. So you could
     * use it to retrieve a html page from within sar by loading
     * the resource named "data/main.html" or similar.
     */
    InputStream getResourceAsStream( String name );

    /**
     * Get logger with category for application.
     * Note that this name may not be the absolute category.
     *
     * @param name the name of logger
     * @return the Logger
     */
    Logger getLogger( String name )
        throws Exception;

    /**
     * Get the instrument manager to use for this application
     *
     * @return the InstrumentManager
     */
    InstrumentManager getInstrumentManager();

    /**
     * Get the name to use for the instrumentables for the specified component
     *
     * @param component the component
     * @return the name to use for Instrumentables
     */
    String getInstrumentableName( String component );
}
