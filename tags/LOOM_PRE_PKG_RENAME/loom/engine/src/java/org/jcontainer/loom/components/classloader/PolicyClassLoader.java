/* ====================================================================
 * JContainer Software License, version 1.1
 *
 * Copyright (c) 2003, JContainer Group. All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. Neither the name of the JContainer Group nor the name "Loom" nor
 *    the names of its contributors may be used to endorse or promote
 *    products derived from this software without specific prior
 *    written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * ====================================================================
 *
 * JContainer Loom includes code from the Apache Software Foundation
 *
 * ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1997-2003 The Apache Software Foundation. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *    "This product includes software developed by the
 *    Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software
 *    itself, if and wherever such third-party acknowledgments
 *    normally appear.
 *
 * 4. The names "Jakarta", "Avalon", and "Apache Software Foundation"
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */
package org.jcontainer.loom.components.classloader;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Policy;
import java.util.Enumeration;
import org.jcontainer.dna.LogEnabled;
import org.jcontainer.dna.Logger;

/**
 * Classloader that uses a specified {@link Policy} object rather than system
 * {@link Policy} object.
 *
 * <p>Note that parts of this were cloned from other projects</p>
 *
 * @author Peter Donald
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
     * Construct a ClassLoader using specified URLs, parent ClassLoader and
     * Policy object.
     *
     * @param urls the URLs to load resources from
     * @param parent the parent ClassLoader
     * @param policy the Policy object
     */
    PolicyClassLoader( final URL[] urls,
                       final ClassLoader parent,
                       final Policy policy )
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

    /**
     * Overide findClass to log debugging information indicating that a class is
     * being loaded from application ClassLoader.
     *
     * @param name the name of class ot load
     * @return the Class loaded
     * @throws ClassNotFoundException if can not find class
     */
    protected Class findClass( final String name )
        throws ClassNotFoundException
    {
        if( m_logger.isDebugEnabled() )
        {
            m_logger.debug( "findClass(" + name + ")" );
        }
        return super.findClass( name );
    }

    /**
     * Overide so we can have a per-application security policy with no
     * side-effects to other applications.
     *
     * @param codeSource the codeSource to get permissions for
     * @return the PermissionCollection
     */
    protected PermissionCollection getPermissions( final CodeSource codeSource )
    {
        if( m_logger.isDebugEnabled() )
        {
            m_logger.debug( "getPermissions(" + codeSource + ")" );
        }
        return m_policy.getPermissions( codeSource );
    }

    /**
     * Return an enumeration of {@link URL}s representing all of the resources
     * with the given name. If no resources with this name are found, return an
     * empty enumeration.
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
        if( m_logger.isDebugEnabled() )
        {
            m_logger.debug( "findResources(" + name + ")" );
        }

        return super.findResources( name );
    }

    /**
     * Find the resource in the ClassLoader. Return a {@link URL} object if
     * found, otherwise return null if this resource cannot be found.
     *
     * <p>Note that this method is overidden to provide debugging
     * information.</p>
     *
     * @param name the name of resource to look for
     * @return the URL if found, else null
     */
    public URL findResource( final String name )
    {
        if( m_logger.isDebugEnabled() )
        {
            m_logger.debug( "findResource(" + name + ")" );
        }

        final URL url = super.findResource( name );

        if( m_logger.isDebugEnabled() )
        {
            if( null != url )
            {
                m_logger.debug( "Resource " + name + " located (" + url + ")" );
            }
            else
            {
                m_logger.debug( "Resource " + name + " not located" );
            }
        }

        return url;
    }
}
