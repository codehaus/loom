/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.apache.avalon.phoenix;

import java.io.File;
import java.io.InputStream;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.logger.Logger;

/**
 * Context via which Blocks communicate with container.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 */
public interface BlockContext
    extends Context
{
    String APP_NAME = "app.name";
    String APP_HOME_DIR = "app.home";
    String NAME = "block.name";

    /**
     * Base directory of .sar application.
     *
     * TODO: Should this be getHomeDirectory() or getWorkingDirectory() or other?
     * TODO: Should a Block be able to declare it doesn't use the Filesystem? If
     * it declares this then it would be an error to call this method.
     *
     * @return the base directory
     */
    File getBaseDirectory();

    /**
     * Retrieve name of block.
     *
     * @return the name of block
     */
    String getName();

    /**
     * A block can request that the application it resides in be
     * shut down. This method will schedule the blocks application
     * for shutdown. Note that this is just a request and the kernel
     * may or may not honour the request (by default the request will
     * be honored).
     */
    void requestShutdown();

    /**
     * Retrieve a resource from the SAR file. The specified
     * name is relative the root of the archive. So you could
     * use it to retrieve a html page from within sar by loading
     * the resource named "data/main.html" or similar.
     * Names may be prefixed with '/' character.
     *
     * @param name the name of resource
     * @return the InputStream for resource or null if no such resource
     */
    InputStream getResourceAsStream( String name );

    /**
     * Retrieve logger coresponding to named category.
     *
     * @return the logger
     * @deprecated This allows block writers to "break-out" of their logging
     *             hierarchy which is considered bad form. Replace by
     *             Logger.getChildLogger(String) where original logger is aquired
     *             via AbstractLogEnabled.
     */
    Logger getLogger( String name );

    /**
     * This method gives the block access to a named {@link ClassLoader}.
     * The {@link ClassLoader}s for an application are declared in the
     * <tt>environment.xml</tt> descriptor. See the Specification for details.
     *
     * @param name the name of the classloader
     * @return the classloader
     * @throws Exception if no such {@link ClassLoader}
     */
    ClassLoader getClassLoader( String name )
        throws Exception;

    /**
     * Retrieve the proxy for this object.
     * Each Block is referenced by other Blocks via their Proxy. When Phoenix
     * shuts down the Block, it can automatically invalidate the proxy. Thus
     * any attempt to call a method on a "dead"/shutdown object will result in
     * an {@link IllegalStateException}. This is desirable as it will
     * stop objects from using the Block when it is in an invalid state.
     *
     * <p>The proxy also allows Phoenix to associate "Context" information with
     * the object. For instance, a Block may expect to run with a
     * specific ContextClassLoader set. However if this Block were to be passed
     * to another component that processed the Block in a thread that did not
     * have the correct context information setup, then the Block could fail
     * to perform as expected. By passing the proxy instead, the correct context
     * information is maintained by Phoenix.</p>
     *
     * <p>Note that only interfaces that the Block declares as offered services
     * will actually be implemented by the proxy.</p>
     */
    //Object getProxy();

    /**
     * This method is similar to {@link #getProxy()} except that it operates
     * on arbitrary objects. It will in effect proxy all interfaces that the
     * component supports.
     *
     * <p>Proxying arbitrary objects is useful for the same reason it is useful
     * to proxy the Block. Thus it is recomended that when needed you pass
     * Proxys of objects to minimize the chance of incorrect behaviour.</p>
     */
    //Object getProxy( Object other );

    /**
     * This method generates a Proxy of the specified object using the
     * specified interfaces. In other respects it is identical to
     * getProxy( Object other )
     */
    //Object getProxy( Object other, Class[] interfaces );

    /**
     * Method via which Blocks export their children to management
     * subsystem.
     *
     * @param name the name under which child is registered ('.' separated)
     * @param child the child object
     * @param interfaces the interfaces to export
     */
    //void export( String name, Object child, Class[] interfaces );

    /**
     * Method to unexport child object from management subsystem.
     *
     * @param name the name of child
     */
    //void unexport( String name );

    /**
     * Retrieve the MBeanServer for this application.
     *
     * NOTE: Unsure if this will ever be implemented
     * may be retrievable via CM instead, or perhaps in
     * a directory or whatever.
     */
    //MBeanServer getMBeanServer();
}
