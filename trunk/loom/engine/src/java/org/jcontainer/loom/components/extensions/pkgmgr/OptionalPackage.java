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
package org.jcontainer.loom.components.extensions.pkgmgr;

import java.io.File;
import org.jcontainer.loom.extension.Extension;

/**
 * This contains the required meta-data for an "Optional Package" (formerly
 * known as "Standard Extension") as described in the manifest of a JAR file.
 *
 * @author Peter Donald
 */
public final class OptionalPackage
{
    private final File m_file;
    private final Extension[] m_available;
    private final Extension[] m_required;

    /**
     * Convert a list of OptionalPackages into a list of Files.
     *
     * @param packages the list of packages
     * @return the list of files
     */
    public static final File[] toFiles( final OptionalPackage[] packages )
    {
        final File[] results = new File[ packages.length ];

        for( int i = 0; i < packages.length; i++ )
        {
            results[ i ] = packages[ i ].getFile();
        }

        return results;
    }

    /**
     * Constructor for OptionalPackage. No parameter is allowed to be null.
     *
     * @param file absolute location of file
     * @param available the list of Extensions Optional Package provides
     * @param required the list of Extensions Optional Package requires
     */
    public OptionalPackage( final File file,
                            final Extension[] available,
                            final Extension[] required )
    {
        if( null == file )
        {
            throw new NullPointerException( "file" );
        }

        if( null == available )
        {
            throw new NullPointerException( "available" );
        }

        if( null == required )
        {
            throw new NullPointerException( "required" );
        }

        m_file = file;
        m_available = available;
        m_required = required;
    }

    /**
     * Return <code>File</code> object in which OptionalPackage is contained.
     *
     * @return the file object for OptionalPackage
     */
    public File getFile()
    {
        return m_file;
    }

    /**
     * Return <code>Extension</code>s which OptionalPackage requires to
     * operate.
     *
     * @return the extensions required by OptionalPackage
     */
    public Extension[] getRequiredExtensions()
    {
        return m_required;
    }

    /**
     * Return <code>Extension</code>s which OptionalPackage makes available.
     *
     * @return the extensions made available by OptionalPackage
     */
    public Extension[] getAvailableExtensions()
    {
        return m_available;
    }

    /**
     * Return <code>true</code> if any of the available <code>Extension</code>s
     * are compatible with specified extension. Otherwise return
     * <code>false</code>.
     *
     * @param extension the extension
     * @return true if compatible, false otherwise
     */
    public boolean isCompatible( final Extension extension )
    {
        for( int i = 0; i < m_available.length; i++ )
        {
            if( m_available[ i ].isCompatibleWith( extension ) )
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Return a String representation of this object.
     *
     * @return the string representation of object
     */
    public String toString()
    {
        final StringBuffer sb = new StringBuffer( "OptionalPackage[" );
        sb.append( m_file );

        sb.append( ", Available[" );
        for( int i = 0; i < m_available.length; i++ )
        {
            if( 0 != i )
            {
                sb.append( " " );
            }
            sb.append( m_available[ i ].getExtensionName() );
        }

        sb.append( "], Required[" );
        for( int i = 0; i < m_required.length; i++ )
        {
            if( 0 != i )
            {
                sb.append( " " );
            }
            sb.append( m_required[ i ].getExtensionName() );
        }

        sb.append( "] ]" );

        return sb.toString();
    }
}
