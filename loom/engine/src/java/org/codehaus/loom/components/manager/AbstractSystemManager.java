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
import java.util.Map;
import org.codehaus.dna.AbstractLogEnabled;
import org.codehaus.dna.Active;
import org.codehaus.loom.interfaces.LoomException;
import org.codehaus.loom.interfaces.SystemManager;

/**
 * This is abstract implementation of SystemManager.
 *
 * @author Peter Donald
 */
public abstract class AbstractSystemManager
    extends AbstractLogEnabled
    implements SystemManager, Active
{
    private final Map m_entries = new HashMap();

    private SubContext m_subContext;

    public void initialize()
        throws Exception
    {
        m_subContext = new SubContext( this, null, null );
    }

    public void dispose()
    {
        m_subContext = null;
    }

    /**
     * @see SystemManager#register(String, Object)
     */
    public synchronized void register( final String name,
                                       final Object object )
        throws LoomException, IllegalArgumentException
    {
        checkRegister( name, object );

        final Object exportedObject = export( name, object );
        m_entries.put( name, exportedObject );
    }

    /**
     * @see SystemManager#unregister(String)
     */
    public synchronized void unregister( final String name )
        throws LoomException
    {
        final Object entry = m_entries.remove( name );
        if( null == entry )
        {
            return;
        }

        unexport( name, entry );
    }

    /**
     * Returns the subcontext of the specified name.  If it does not exist it is
     * created.
     *
     * @return the subcontext with the specified name
     * @throws LoomException if context cannot be created or retrieved
     */
    public SystemManager getSubContext( final String parent,
                                        final String type )
        throws LoomException
    {
        return m_subContext.getSubContext( parent, type );
    }

    /**
     * Export the object to the particular management medium using the supplied
     * object and interfaces. This needs to be implemented by subclasses.
     *
     * @param name the name of object
     * @param object the object
     * @return the exported object
     * @throws LoomException if an error occurs
     */
    protected abstract Object export( String name, Object object )
        throws LoomException;

    /**
     * Stop the exported object from being managed.
     *
     * @param name the name of object
     * @param exportedObject the object return by export
     * @throws LoomException if an error occurs
     */
    protected abstract void unexport( String name, Object exportedObject )
        throws LoomException;

    /**
     * Helper method to help check before an objects registration. Verifies name
     * and object are not null and verifies no entry exists using name.
     *
     * @param name the name of object
     * @param object the object to be registered
     * @throws LoomException if name already exists
     * @throws IllegalArgumentException if name or object is null
     */
    private void checkRegister( final String name, final Object object )
        throws LoomException, IllegalArgumentException
    {
        if( null == object )
        {
            throw new NullPointerException( "object" );
        }
        if( null == name )
        {
            throw new NullPointerException( "name" );
        }

        if( null != m_entries.get( name ) )
        {
            final String message = name +
                " already registered in SystemManager";
            throw new LoomException( message );
        }
    }
}
