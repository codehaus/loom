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
package org.jcontainer.loom.components.manager;

import java.util.HashMap;
import java.util.Set;
import javax.management.modelmbean.ModelMBeanInfo;

/**
 * It reprensents a managed object in the managegement space.  It is a container for
 * zero or more management topics and zero or more management lists.
 *
 * @author  robertsh
 * @version $$
 */
public class Target
{
    private final String m_name;
    private final HashMap m_topics;
    private final Object m_managedResource;

    /**
     * Creates new Target
     *
     * @param name the name for the target
     * @param managedResource the object that this managedResource represents in the management hierarchy
     */
    public Target( final String name,
                   final Object managedResource )
    {
        m_name = name;
        m_managedResource = managedResource;
        m_topics = new HashMap();
    }

    /**
     * Returns the name of the Target
     * @return  the name
     */
    public String getName()
    {
        return m_name;
    }

    /**
     * Returns the object managed by the target
     *
     * @return  the managed object
     */
    public Object getManagedResource()
    {
        return m_managedResource;
    }

    /**
     * Topics are a set of attributes and operations relevant to a particular
     * aspect of an object.  A Target must typically have at least one topic in
     * order to be manageable.
     *
     * @param topic
     */
    public void addTopic( final ModelMBeanInfo topic )
    {
        m_topics.put( topic.getDescription(), topic );
    }

    /**
     * Removes a topic for this target
     * @param name  the name of the topic to remove
     */
    public void removeTopic( final String name )
    {
        m_topics.remove( name );
    }

    /**
     * Gets a topic for this Target
     *
     * @param name the name of the topic
     * @return  the topic of that name
     */
    public ModelMBeanInfo getTopic( final String name )
    {
        return (ModelMBeanInfo)m_topics.get( name );
    }

    /**
     * Returns the Set of topics for this Target
     *
     * @return the Set of topic names
     */
    public Set getTopicNames()
    {
        return m_topics.keySet();
    }
}
