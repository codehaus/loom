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
package org.jcontainer.loom.components.util;

import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;

/**
 * This provides utility methods for properties.
 *
 * @author Peter Donald
 * @version CVS $Revision: 1.4 $ $Date: 2003-11-29 13:44:27 $
 * @since 4.0
 */
public final class PropertyUtil
{
    private PropertyUtil()
    {
    }

    /**
     * Resolve a string property. This evaluates all property substitutions
     * based on specified context.
     *
     * @param property the property to resolve
     * @param context the context in which to resolve property
     * @param ignoreUndefined if false will throw an PropertyException if
     * property is not found
     * @return the reolved property
     * @throws java.lang.Exception if an error occurs
     */
    public static Object resolveProperty( final String property,
                                          final Context context,
                                          final boolean ignoreUndefined )
        throws Exception
    {
        int start = findBeginning( property, 0 );
        if( -1 == start )
        {
            return property;
        }

        int end = findEnding( property, start );

        final int length = property.length();

        if( 0 == start && end == ( length - 1 ) )
        {
            return resolveValue( property.substring( start + 2, end ),
                                 context,
                                 ignoreUndefined );
        }

        final StringBuffer sb = new StringBuffer( length * 2 );
        int lastPlace = 0;

        while( true )
        {
            final Object value =
                resolveValue( property.substring( start + 2, end ),
                              context,
                              ignoreUndefined );

            sb.append( property.substring( lastPlace, start ) );
            sb.append( value );

            lastPlace = end + 1;

            start = findBeginning( property, lastPlace );
            if( -1 == start )
            {
                break;
            }

            end = findEnding( property, start );
        }

        sb.append( property.substring( lastPlace, length ) );

        return sb.toString();
    }

    private static int findBeginning( final String property,
                                      final int currentPosition )
    {
        //TODO: Check if it is commented out
        return property.indexOf( "${", currentPosition );
    }

    private static int findEnding( final String property,
                                   final int currentPosition )
        throws Exception
    {
        //TODO: Check if it is commented out
        final int index = property.indexOf( '}', currentPosition );
        if( -1 == index )
        {
            throw new Exception( "Malformed property with mismatched }'s" );
        }

        return index;
    }

    /**
     * Retrieve a value from the specified context using the specified key. If
     * there is no such value and ignoreUndefined is not false then a Exception
     * is generated.
     *
     * @param key the key of value in context
     * @param context the Context
     * @param ignoreUndefined true if undefined variables are ignored
     * @return the object retrieved from context
     * @throws java.lang.Exception if an error occurs
     */
    private static Object resolveValue( final String key,
                                        final Context context,
                                        final boolean ignoreUndefined )
        throws Exception
    {
        Object value = null;

        try
        {
            value = context.get( key );
        }
        catch( final ContextException ce )
        {
            //ignore
        }

        if( null == value )
        {
            if( ignoreUndefined )
            {
                return "";
            }
            else
            {
                throw new Exception( "Unable to find " +
                                     key +
                                     " to expand during "
                                     + "property resolution." );
            }
        }

        return value;
    }
}
