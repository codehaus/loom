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
package org.jcontainer.loom.tools.metagenerate;

import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.Type;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * A Mxinfo Factory
 * @author Paul Hammant
 */
public class MxinfoFactory
{

    private JavaClass m_javaClass;
    private File m_destDir;
    private ArrayList m_attributes = new ArrayList();
    private ArrayList m_operations = new ArrayList();
    private MxinfoHelper m_mxinfo;

    /**
     * Construct a factory for a class.
     * @param destDir
     * @param javaClass
     */
    public MxinfoFactory( final File destDir, final JavaClass javaClass )
    {
        m_javaClass = javaClass;
        m_destDir = destDir;
    }

    /**
     * Generate the m_mxinfo file
     * @throws java.io.IOException If a problem writing output
     */
    public void generate() throws IOException
    {
        final File file = new File( m_destDir,
                                    m_javaClass.getFullyQualifiedName().replace( '.', File.separatorChar ) + ".mxinfo" );
        file.getParentFile().mkdirs();
        m_mxinfo = new MxinfoHelper( file );
        m_mxinfo.writeHeader(
            m_javaClass.getTagByName( "phoenix:mx-topic" ).getNamedParameter( "name" ) );
        // m_attributes
        JavaMethod[] methods = m_javaClass.getMethods();
        for( int j = 0; j < methods.length; j++ )
        {
            makeAttribute( methods[ j ], m_mxinfo );
        }
        writeAttributes();
        m_mxinfo.writeOperationsHeader();
        // operations
        methods = m_javaClass.getMethods();
        for( int j = 0; j < methods.length; j++ )
        {
            makeOperation( methods[ j ], m_mxinfo );
        }
        writeOperations();
        m_mxinfo.writeFooter();
        m_mxinfo.close();
    }

    private void writeOperations() throws IOException
    {
        m_mxinfo.writeOperations( m_operations );
    }

    private void makeAttribute( final JavaMethod method, final MxinfoHelper mxinfo )
    {
        final DocletTag attribute = method.getTagByName( "phoenix:mx-attribute" );
        if( attribute != null )
        {
            String attributeName = getName( method.getName() );
            DocletTag tag = method.getTagByName( "phoenix:mx-description" );
            String comment;
            if( tag == null )
            {
                comment = method.getComment();
            }
            else
            {
                comment = tag.getValue();
            }
            Type attributeType = method.getReturns();
            String attributeTypeString =
                attributeType.getValue() + ( attributeType.isArray() ? "[]" : "" );

            NamedXmlSnippet attr = mxinfo.makeAttrLines( attributeName,
                                                         "\"" + comment + "\"",
                                                         attributeTypeString );
            m_attributes.add( attr );
        }
    }

    private void writeAttributes() throws IOException
    {
        m_mxinfo.writeAttributes( m_attributes );
    }

    private void makeOperation( final JavaMethod method, final MxinfoHelper mxinfo ) throws IOException
    {
        String xml = "";
        final DocletTag attribute = method.getTagByName( "phoenix:mx-operation" );
        if( attribute != null )
        {
            String operationName = method.getName();
            String description = method.getComment();
            Type type = method.getReturns();

            String typeString = type.getValue() + ( type.isArray() ? "[]" : "" );

            xml = xml + mxinfo.makeOperationHeader( operationName, description, typeString );
            JavaParameter[] params = method.getParameters();
            for( int i = 0; i < params.length; i++ )
            {
                xml = xml + makeOperationParameter( params[ i ], method, mxinfo );

            }
            xml = xml + mxinfo.makeOperationFooter();
            NamedXmlSnippet operation = new NamedXmlSnippet( operationName, xml );
            m_operations.add( operation );
        }
    }

    private String makeOperationParameter( final JavaParameter param, final JavaMethod method,
                                           final MxinfoHelper mxinfo ) throws IOException
    {
        final String paramName = param.getName();
        final DocletTag[] paramTags = method.getTagsByName( "param" );
        String paramDescription = "";
        for( int k = 0; k < paramTags.length; k++ )
        {
            String paramTagValue = paramTags[ k ].getValue().trim();
            if( paramTagValue.startsWith( paramName ) )
            {
                paramDescription = paramTagValue.substring(
                    paramTagValue.indexOf( " " ) + 1, paramTagValue.length() );
            }
        }
        final Type paramType = param.getType();
        final String paramTypeString = paramType.getValue() + ( paramType.isArray() ? "[]" : "" );
        return mxinfo.makeOperationParameter( paramName, paramDescription, paramTypeString );
    }

    private String getName( final String name )
    {
        String retval = name;
        if( retval.startsWith( "set" ) || retval.startsWith( "get" ) )
        {
            retval = retval.substring( 3, retval.length() );
            retval = retval.substring( 0, 1 ).toLowerCase() + retval.substring( 1, retval.length() );
        }
        else if( retval.startsWith( "is" ) )
        {
            retval = retval.substring( 2, retval.length() );
            retval = retval.substring( 0, 1 ).toLowerCase() + retval.substring( 1, retval.length() );
        }
        return retval;
    }
}
