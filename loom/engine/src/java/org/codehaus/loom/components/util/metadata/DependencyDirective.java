/* ====================================================================
 * Loom Software License, version 1.1
 *
 * Copyright (c) 2003-2005, Loom Group. All rights reserved.
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
 * 3. Neither the name of the Loom Group nor the name "Loom" nor
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
 * Loom includes code from the Apache Software Foundation
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
package org.codehaus.loom.components.util.metadata;

/**
 * The DependencyDirective is the mapping of a component as a dependency of
 * another component. Each component declares dependencies (via ComponentInfo)
 * and for each dependency there must be a coressponding DependencyDirective
 * which has a matching key. The name value in DependencyDirective object must
 * refer to another Component that implements a service as specified in
 * DependencyInfo.
 *
 * <p>Note that it is invalid to have circular dependencies.</p>
 *
 * @author Peter Donald
 * @version $Revision: 1.4 $ $Date: 2005-02-22 08:57:36 $
 */
public final class DependencyDirective
{
    /** Empty set of dependencys. */
    public static final DependencyDirective[] EMPTY_SET = new DependencyDirective[ 0 ];

    /** The key that the client component will use to access a dependency. */
    private final String m_key;

    /**
     * the name of the component profile that represents a component type that
     * is capable of fullfilling the dependency.
     */
    private final String m_providerName;

    /**
     * The key that is used when the dependency is a map dependency. Usually
     * this defaults to the same value as the key.
     */
    private final String m_alias;

    /**
     * Create Association between key and provider.
     *
     * @param key the key the client uses to access component
     * @param providerName the name of ComponentTemplate that is associated as a
     * service provider
     */
    public DependencyDirective( final String key,
                                final String providerName,
                                final String alias )
    {
        if( null == key )
        {
            throw new NullPointerException( "key" );
        }
        if( null == providerName )
        {
            throw new NullPointerException( "providerName" );
        }
        if( null == alias )
        {
            throw new NullPointerException( "alias" );
        }
        m_key = key;
        m_providerName = providerName;
        m_alias = alias;
    }

    /**
     * Return the key that will be used by a component instance to access a
     * dependent service.
     *
     * @return the name that the client component will use to access
     *         dependency.
     * @see org.apache.avalon.framework.service.ServiceManager#lookup(
        *      java.lang.String )
     */
    public String getKey()
    {
        return m_key;
    }

    /**
     * Return the name of a ComponentTemplate instance that will used to fulfill
     * the dependency.
     *
     * @return the name of the Component that will provide the dependency.
     */
    public String getProviderName()
    {
        return m_providerName;
    }

    /**
     * The key under which the dependency is placed in map if dependency is a
     * Map dependency.
     *
     * @return the key under which the dependency is placed in map if dependency
     *         is a Map dependency.
     */
    public String getAlias()
    {
        return m_alias;
    }
}
