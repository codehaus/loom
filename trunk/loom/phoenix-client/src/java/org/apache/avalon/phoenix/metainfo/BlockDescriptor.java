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
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 */
public class BlockDescriptor
{
    /**
     * The short name of the Block. Useful for displaying
     * human readable strings describing the type in
     * assembly tools or generators.
     */
    private final String m_name;
    private final String m_implementationKey;
    private final Version m_version;
    private final String m_schemaType;

    public BlockDescriptor( final String name,
                            final String implementationKey,
                            final String schemaType,
                            final Version version )
    {
        m_name = name;
        m_implementationKey = implementationKey;
        m_version = version;
        m_schemaType = schemaType;
    }

    /**
     * Retrieve the name of Block type.
     *
     * @return the name of Block type.
     */
    public String getName()
    {
        return m_name;
    }

    /**
     * Retrieve the Class Name of Block.
     *
     * @return the Class Name of block
     * @see #getImplementationKey
     * @deprecated Deprecated and replaced by {@link #getImplementationKey}
     */
    public String getClassname()
    {
        return getImplementationKey();
    }

    /**
     * Retrieve the implementation key for the Block.
     * Usually the keys is a classname.
     *
     * @return the implementation key for the Block
     */
    public String getImplementationKey()
    {
        return m_implementationKey;
    }

    /**
     * Retrieve Version of current Block.
     *
     * @return the version of block
     */
    public Version getVersion()
    {
        return m_version;
    }

    /**
     * Retrieve the Schema Type of Block
     *
     * @return the Schema Type of block
     */
    public String getSchemaType()
    {
        return m_schemaType;
    }
}

