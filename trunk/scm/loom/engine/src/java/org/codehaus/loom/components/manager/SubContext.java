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
package org.codehaus.loom.components.manager;

import java.util.HashMap;

import org.codehaus.loom.interfaces.LoomException;
import org.codehaus.loom.interfaces.SystemManager;
import org.codehaus.spice.salt.i18n.ResourceManager;
import org.codehaus.spice.salt.i18n.Resources;

/**
 * Implements a management context local to a particular process with which it
 * can register its managed object.  The naming scheme that results is meant to
 * be compatible with jmx.
 *
 * @author <a href="mailto:huw@apache.org">Huw Roberts</a>
 */
class SubContext
    implements SystemManager
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( SubContext.class );

    private static final String EMPTY_STRING = "";

    private final HashMap m_subcontexts = new HashMap();
    private final SystemManager m_parent;
    private final String m_name;
    private final String m_type;

    /**
     * Creates new SubContext to the specified context.  Objects registered
     * under the subcontext will be typed with the name of the context so the
     * jmx name becomes 'contextName' + 'objectNameSoFar'
     *
     * @param parent the parent context
     * @param name the subcontext name
     */
    public SubContext( final SystemManager parent,
                       final String name,
                       final String type )
    {
        if( null == parent )
        {
            throw new NullPointerException( "parent" );
        }
        m_parent = parent;
        m_name = name;
        m_type = type;
    }

    /**
     * Register an object for management. The object is exported through some
     * management scheme (typically JMX). Note that the particular management
     * scheme will most likely use reflection to extract manageable
     * information.
     *
     * @param name the name to register object under
     * @param object the object
     * @throws LoomException if an error occurs such as name already
     * registered.
     * @throws IllegalArgumentException if object is null
     */
    public void register( final String name, final Object object )
        throws LoomException, IllegalArgumentException
    {
        m_parent.register( jmxName( name ), object );
    }

    /**
     * Unregister named object.
     *
     * @param name the name of object to unregister
     * @throws LoomException if an error occurs such as when no such object
     * registered.
     */
    public void unregister( final String name )
        throws LoomException
    {
        m_parent.unregister( jmxName( name ) );
    }

    /**
     * Returns the subcontext of the specified name.  If it does not exist it is
     * created.
     *
     * @return the subcontext with the specified name
     * @throws LoomException if context cannot be created or retrieved
     */
    public SystemManager getSubContext( final String name,
                                        final String type )
        throws LoomException
    {
        if( null == type || EMPTY_STRING.equals( type ) )
        {
            final String message =
                REZ.getString( "subcontext.error.no.subcontext" );
            throw new LoomException( message );
        }
        else if( null != name && this.m_type == null )
        {
            final String message =
                REZ.getString( "subcontext.error.no.subcontext" );
            throw new LoomException( message );
        }

        // get from list if possible
        final String key = contextKey( name, type );
        SystemManager subcontext =
            (SystemManager)m_subcontexts.get( key );

        // otherwise create and add to list
        if( subcontext == null )
        {
            subcontext = new SubContext( this, name, type );
            m_subcontexts.put( key, subcontext );
        }

        return subcontext;
    }

    /**
     * Helper method used to generate the jmx name by appending the current name
     * and passing up the chain to the root context
     */
    private String jmxName( final String name )
    {
        final StringBuffer sb = new StringBuffer();
        if( null != m_name )
        {
            sb.append( m_name );
            sb.append( ',' );
        }
        if( null != m_type )
        {
            sb.append( m_type );
            sb.append( '=' );
        }
        sb.append( name );

        return sb.toString();
    }

    /**
     * Helper method to get key used to store subcontexts in m_subcontexts
     */
    private String contextKey( final String parent,
                               final String type )
    {
        return parent + "|" + type;
    }
}
