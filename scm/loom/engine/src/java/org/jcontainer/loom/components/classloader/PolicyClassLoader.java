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
package org.jcontainer.loom.components.classloader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Policy;
import java.util.Enumeration;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;

/**
 * Classloader that uses a specified {@link Policy} object
 * rather than system {@link Policy} object.
 *
 * <p>Note that parts of this were cloned from other projects</p>
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 */
class PolicyClassLoader
    extends URLClassLoader
    implements LogEnabled
{
    ///Policy to use to define permissions for classes loaded in classloader
    private final Policy m_policy;

    ///Logger to use when reporting information
    private Logger m_logger;

    /**
     * Construct a ClassLoader using specified URLs, parent
     * ClassLoader and Policy object.
     *
     * @param urls the URLs to load resources from
     * @param parent the parent ClassLoader
     * @param policy the Policy object
     */
    PolicyClassLoader( final URL[] urls,
                       final ClassLoader parent,
                       final Policy policy )
        throws MalformedURLException
    {
        super( urls, parent );

        if( null == policy )
        {
            throw new NullPointerException( "policy" );
        }
        m_policy = policy;
    }

    public void enableLogging( final Logger logger )
    {
        m_logger = logger;
    }

    protected void addURL( final URL url )
    {
        super.addURL( url );
    }

    private Logger getLogger()
    {
        return m_logger;
    }

    /**
     * Overide findClass to log debugging information
     * indicating that a class is being loaded from application
     * ClassLoader.
     *
     * @param name the name of class ot load
     * @return the Class loaded
     * @throws ClassNotFoundException if can not find class
     */
    protected Class findClass( final String name )
        throws ClassNotFoundException
    {
        if( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "findClass(" + name + ")" );
        }
        return super.findClass( name );
    }

    /**
     * Overide so we can have a per-application security policy with
     * no side-effects to other applications.
     *
     * @param codeSource the codeSource to get permissions for
     * @return the PermissionCollection
     */
    protected PermissionCollection getPermissions( final CodeSource codeSource )
    {
        if( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "getPermissions(" + codeSource + ")" );
        }
        return m_policy.getPermissions( codeSource );
    }

    /**
     * Return an enumeration of {@link URL}s representing all of the
     * resources with the given name. If no resources with this name are found,
     * return an empty enumeration.
     *
     * <p>Note that this method is overidden to provide debugging
     * information.</p>
     *
     * @param name the name of resource to look for
     * @return the Enumeration of resources
     * @throws IOException if an input/output error occurs
     */
    public Enumeration findResources( final String name )
        throws IOException
    {
        if( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "findResources(" + name + ")" );
        }

        return super.findResources( name );
    }

    /**
     * Find the resource in the ClassLoader. Return a {@link URL}
     * object if found, otherwise return null if this resource cannot
     * be found.
     *
     * <p>Note that this method is overidden to provide debugging
     * information.</p>
     *
     * @param name the name of resource to look for
     * @return the URL if found, else null
     */
    public URL findResource( final String name )
    {
        if( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "findResource(" + name + ")" );
        }

        final URL url = super.findResource( name );

        if( getLogger().isDebugEnabled() )
        {
            if( null != url )
            {
                getLogger().debug( "Resource " + name + " located (" + url + ")" );
            }
            else
            {
                getLogger().debug( "Resource " + name + " not located" );
            }
        }

        return url;
    }
}
