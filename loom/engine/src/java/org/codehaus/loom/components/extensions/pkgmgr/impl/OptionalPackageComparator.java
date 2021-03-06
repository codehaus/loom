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
package org.codehaus.loom.components.extensions.pkgmgr.impl;

import java.util.Comparator;

import org.codehaus.loom.components.extensions.pkgmgr.OptionalPackage;
import org.codehaus.loom.extension.DeweyDecimal;
import org.codehaus.loom.extension.Extension;

/**
 * A simple class to compare two extensions and sort them on spec version and
 * then on impl version. Unspecified versions rate lower than specified
 * versions.
 *
 * @author Peter Donald
 * @version $Revision: 1.4 $ $Date: 2005-02-22 08:57:36 $
 */
class OptionalPackageComparator
    implements Comparator
{
    /** The name of extension the comparator is working with. */
    private final String m_name;

    public OptionalPackageComparator( final String name )
    {
        if( null == name )
        {
            throw new NullPointerException( "name" );
        }

        m_name = name;
    }

    public int compare( final Object o1,
                        final Object o2 )
    {
        final OptionalPackage pkg1 = (OptionalPackage)o1;
        final OptionalPackage pkg2 = (OptionalPackage)o2;
        final Extension e1 = getMatchingExtension( pkg1 );
        final Extension e2 = getMatchingExtension( pkg2 );
        int result = compareSpecVersion( e1, e2 );
        if( 0 != result )
        {
            return result;
        }
        else
        {
            return compareImplVersion( e1, e2 );
        }
    }

    private Extension getMatchingExtension( final OptionalPackage pkg )
    {
        final Extension[] extensions = pkg.getAvailableExtensions();
        for( int i = 0; i < extensions.length; i++ )
        {
            final Extension extension = extensions[ i ];
            if( extension.getExtensionName().equals( m_name ) )
            {
                return extension;
            }
        }

        final String message = "Unable to locate extension " +
            m_name + " in package " + pkg;
        throw new IllegalStateException( message );
    }

    private int compareImplVersion( final Extension e1, final Extension e2 )
    {
        final String implVersion1 = e1.getImplementationVersion();
        final String implVersion2 = e2.getImplementationVersion();
        if( null == implVersion1 && null == implVersion2 )
        {
            return 0;
        }
        else if( null != implVersion1 && null == implVersion2 )
        {
            return -1;
        }
        else if( null == implVersion1 && null != implVersion2 )
        {
            return 1;
        }
        else
        {
            return -implVersion1.compareTo( implVersion2 );
        }
    }

    private int compareSpecVersion( final Extension e1,
                                    final Extension e2 )
    {
        final DeweyDecimal specVersion1 = e1.getSpecificationVersion();
        final DeweyDecimal specVersion2 = e2.getSpecificationVersion();
        if( null == specVersion1 && null == specVersion2 )
        {
            return 0;
        }
        else if( null != specVersion1 && null == specVersion2 )
        {
            return -1;
        }
        else if( null == specVersion1 && null != specVersion2 )
        {
            return 1;
        }
        else
        {
            if( specVersion1.isEqual( specVersion2 ) )
            {
                return 0;
            }
            else if( specVersion1.isGreaterThan( specVersion2 ) )
            {
                return -1;
            }
            else
            {
                return 1;
            }
        }
    }
}
