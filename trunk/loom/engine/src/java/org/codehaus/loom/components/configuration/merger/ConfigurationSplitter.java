/* ====================================================================
 * Loom Software License, version 1.1
 *
 * Copyright (c) 2003, Loom Group. All rights reserved.
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
package org.codehaus.loom.components.configuration.merger;

import java.util.HashSet;
import org.codehaus.dna.Configuration;
import org.codehaus.dna.ConfigurationException;
import org.codehaus.dna.impl.ConfigurationUtil;
import org.codehaus.dna.impl.DefaultConfiguration;

/**
 * The ConfigurationSplitter will take two Configuration objects and calculate
 * the differences between them.
 *
 * The resulting Configuration will contain special attribute that can be used
 * by the ConfigurationMerger to reconstruct the original Configuration
 *
 * @author <a href="mailto:proyal@apache.org">Peter Royal</a>
 * @see ConfigurationMerger
 */
public class ConfigurationSplitter
{
    /**
     * Split a configuration, given a merged configuration and a base
     * configuration. Functionally equivalent to producing a <i>diff</i> between
     * the merged and base.
     *
     * @param merged Configuration that is a combination of the <i>result</i>
     * and the <i>base</i> param
     * @param base Configuration that when merged with the <i>result</i> will
     * yield the <i>merged</i> param
     * @return a Configuration that when merged with the <i>base</i> will yeild
     *         the <i>merged</i>
     * @throws ConfigurationException if unable to split
     */
    public static Configuration split( final Configuration merged,
                                       final Configuration base )
        throws ConfigurationException
    {
        final DefaultConfiguration layer = doSplit( merged, base, false );

        layer.makeReadOnly();

        return layer;
    }

    private static DefaultConfiguration doSplit( final Configuration merged,
                                                 final Configuration base,
                                                 final boolean isMerged )
        throws ConfigurationException
    {
        final DefaultConfiguration layer =
            new DefaultConfiguration( base.getName(),
                                      merged.getPath(),
                                      "Merged [merged: " +
                                      merged.getLocation()
                                      + ", base: " + base.getLocation() + "]" );

        copyAttributes( layer, merged, base );
        copyValue( layer, merged, base );
        copyChildren( layer, merged, base );

        if( isMerged )
        {
            layer.setAttribute( Constants.MERGE_ATTR, "true" );
        }

        return layer;
    }

    private static DefaultConfiguration doSplit( final Configuration merged,
                                                 final Configuration base,
                                                 final String keyAttr )
        throws ConfigurationException
    {
        final DefaultConfiguration layer = doSplit( merged, base, true );

        if( null != keyAttr )
        {
            layer.setAttribute( Constants.KEY_ATTR, keyAttr );
        }

        return layer;
    }

    private static void copyChildren( final DefaultConfiguration layer,
                                      final Configuration merged,
                                      final Configuration base )
        throws ConfigurationException
    {
        final Configuration[] kids = merged.getChildren();

        for( int i = 0; i < kids.length; i++ )
        {
            final Configuration mergedChild = kids[ i ];
            final String name = mergedChild.getName();
            final Configuration[] mc = merged.getChildren( name );
            final Configuration[] bc = base.getChildren( name );

            Configuration mergedWith;
            String keyAttr = null;

            if( bc.length > mc.length )
            {
                throw new UnsupportedOperationException( "Unable to mask children from base "
                                                         + "in layer" );
            }
            else if( bc.length == 0 )
            {
                mergedWith = null;
            }
            else if( mc.length == 1 && bc.length == 1 )
            {
                mergedWith = bc[ 0 ];
            }
            else //we know that mc.length > 1 here, so find the "key" attribute
            {
                keyAttr = findUniqueAttributeName( mc );

                final String keyValue = mergedChild.getAttribute( keyAttr );
                final Configuration[] matches =
                    ConfigurationMerger.match( base, name, keyAttr, keyValue );

                if( matches.length == 1 )
                {
                    mergedWith = matches[ 0 ];
                }
                else
                {
                    throw new ConfigurationException( "Multiple children in base with name '"
                                                      +
                                                      name +
                                                      "' and attr '" +
                                                      keyAttr
                                                      + " = " + keyValue + "'",
                                                      merged.getPath(),
                                                      merged.getLocation() );
                }
            }

            if( null == mergedWith )
            {
                layer.addChild( kids[ i ] );
            }
            else if( !ConfigurationUtil.equals( kids[ i ], mergedWith ) )
            {
                final DefaultConfiguration layerChild = doSplit( kids[ i ],
                                                                 mergedWith,
                                                                 keyAttr );

                layerChild.makeReadOnly();

                layer.addChild( layerChild );
            }
        }
    }

    private static String findUniqueAttributeName( final Configuration[] c )
        throws ConfigurationException
    {
        final HashSet testedAttributes = new HashSet();
        String uniqueAttr = null;

        for( int i = 0; i < c.length; i++ )
        {
            final String[] attrs = c[ i ].getAttributeNames();

            for( int j = 0; j < attrs.length; j++ )
            {
                final String attr = attrs[ j ];

                if( !testedAttributes.contains( attr )
                    &&
                    isUniqueAttribute( attr,
                                       c[ i ].getAttribute( attr ),
                                       i,
                                       c ) )
                {
                    if( null == uniqueAttr )
                    {
                        uniqueAttr = attr;
                    }
                    else
                    {
                        throw new ConfigurationException(
                            "Multiple unique attributes for child "
                            +
                            "[name: " +
                            c[ 0 ].getName()
                            +
                            ", unique1: " +
                            uniqueAttr
                            +
                            ", unique2: " +
                            attr +
                            "]",
                            "",
                            "" );
                    }
                }
                else
                {
                    testedAttributes.add( attr );
                }
            }
        }

        if( null == uniqueAttr )
        {
            throw new ConfigurationException(
                "Unable to find unique attribute for "
                +
                "children of name: " +
                c[ 0 ].getName(),
                c[ 0 ].getPath(),
                c[ 0 ].getLocation() );
        }

        return uniqueAttr;
    }

    private static boolean isUniqueAttribute( final String attr,
                                              final String value,
                                              final int currentConfig,
                                              final Configuration[] c )
    {
        for( int i = 0; i < c.length; i++ )
        {
            if( i != currentConfig )
            {
                try
                {
                    if( value.equals( c[ i ].getAttribute( attr ) ) )
                    {
                        return false;
                    }
                }
                catch( ConfigurationException e )
                {
                    return false;
                }
            }
        }

        return true;
    }

    private static void copyValue( final DefaultConfiguration layer,
                                   final Configuration merged,
                                   final Configuration base )
    {
        final String value = merged.getValue( null );

        if( null != value )
        {
            try
            {
                final String baseValue = base.getValue();

                if( !value.equals( baseValue ) )
                {
                    layer.setValue( value );
                }
            }
            catch( ConfigurationException e )
            {
                layer.setValue( value );
            }
        }
    }

    private static void copyAttributes( final DefaultConfiguration layer,
                                        final Configuration merged,
                                        final Configuration base )
        throws ConfigurationException
    {
        final String[] mergedAttr = merged.getAttributeNames();

        for( int i = 0; i < mergedAttr.length; i++ )
        {
            final String value = merged.getAttribute( mergedAttr[ i ] );

            try
            {
                final String baseValue = base.getAttribute( mergedAttr[ i ] );

                if( !value.equals( baseValue ) )
                {
                    layer.setAttribute( mergedAttr[ i ], value );
                }
            }
            catch( ConfigurationException e )
            {
                //not in base add to layer
                layer.setAttribute( mergedAttr[ i ], value );
            }
        }
    }

}
