/*
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
package org.apache.avalon.phoenix.metainfo;

import org.apache.avalon.framework.Version;

/**
 * This class describes the meta info of a service offered by a Block.
 * Each service is defined by an interface name and the version of that
 * interface.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 */
public final class ServiceDescriptor
{
    public static final String ARRAY_POSTFIX = "[]";
    public static final String MAP_POSTFIX = "{}";

    private final Version m_version;
    private final String m_name;

    /**
     * Construct a service with specified name and version.
     *
     * @param name the name of the service
     * @param version the version of service
     */
    public ServiceDescriptor( final String name, final Version version )
    {
        m_name = name;
        m_version = version;
    }

    /**
     * Return the version of interface
     *
     * @return the version of interface
     */
    public Version getVersion()
    {
        return m_version;
    }

    /**
     * Return name of Service (which coresponds to the interface
     * name eg org.apache.block.WebServer)
     *
     * @return the name of the Service
     */
    public String getName()
    {
        return m_name;
    }

    /**
     * Return the type of component type if the service
     * is an array or Map Service. Otherwise just return the
     * name of service.
     *
     * @return the Service component type
     */
    public String getComponentType()
    {
        final String fullname = getName();
        if( isArray() )
        {
            final int end = fullname.length() - ARRAY_POSTFIX.length();
            return fullname.substring( 0, end );
        }
        else if( isMap() )
        {
            final int end = fullname.length() - MAP_POSTFIX.length();
            return fullname.substring( 0, end );
        }
        else
        {
            return fullname;
        }
    }

    /**
     * Return true if Service name designates an array of services.
     *
     * @return true if Service name designates an array of services.
     */
    public boolean isArray()
    {
        return m_name.endsWith( ARRAY_POSTFIX );
    }

    /**
     * Return true if Service name designates an map of services.
     *
     * @return true if Service name designates an map of services.
     */
    public boolean isMap()
    {
        return m_name.endsWith( MAP_POSTFIX );
    }

    /**
     * Determine if specified service will match this service.
     * To match a service has to have same name and must comply with version.
     *
     * @param other the other ServiceInfo
     * @return true if matches, false otherwise
     */
    public boolean matches( final ServiceDescriptor other )
    {
        final String name = getComponentType();
        return
            other.getName().equals( name ) &&
            other.getVersion().complies( m_version );
    }

    /**
     * Convert to a string of format name/version
     *
     * @return string describing service
     */
    public String toString()
    {
        return m_name + "/" + m_version;
    }
}
