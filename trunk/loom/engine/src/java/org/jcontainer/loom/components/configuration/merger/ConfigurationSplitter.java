/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 *
 * This product includes software developed by the
 * Apache Software Foundation (http://www.apache.org/).
 */
package org.jcontainer.loom.components.configuration.merger;

import java.util.HashSet;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.ConfigurationUtil;
import org.apache.avalon.framework.configuration.DefaultConfiguration;

/**
 * The ConfigurationSplitter will take two Configuration objects and calculate the
 * differences between them.
 *
 * The resulting Configuration will contain special attribute that can be used by the
 * ConfigurationMerger to reconstruct the original Configuration
 *
 * @see ConfigurationMerger
 * @author <a href="mailto:proyal@apache.org">Peter Royal</a>
 */
public class ConfigurationSplitter
{
    /**
     * Split a configuration, given a merged configuration and a base configuration.
     * Functionally equivalent to producing a <i>diff</i> between the merged and base.
     *
     * @param merged Configuration that is a combination of the <i>result</i> and
     *        the <i>base</i> param
     * @param base Configuration that when merged with the <i>result</i> will yield the
     *        <i>merged</i> param
     *
     * @return a Configuration that when merged with the <i>base</i> will yeild the
     *         <i>merged</i>
     *
     * @throws ConfigurationException if unable to split
     */
    public static Configuration split( final Configuration merged, final Configuration base )
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
                                      "Merged [merged: " + merged.getLocation()
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
                                                      + name + "' and attr '" + keyAttr
                                                      + " = " + keyValue + "'" );
                }
            }

            if( null == mergedWith )
            {
                layer.addChild( kids[ i ] );
            }
            else if( !ConfigurationUtil.equals( kids[ i ], mergedWith ) )
            {
                final DefaultConfiguration layerChild = doSplit( kids[ i ], mergedWith, keyAttr );

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
                    && isUniqueAttribute( attr, c[ i ].getAttribute( attr ), i, c ) )
                {
                    if( null == uniqueAttr )
                    {
                        uniqueAttr = attr;
                    }
                    else
                    {
                        throw new ConfigurationException( "Multiple unique attributes for child "
                                                          + "[name: " + c[ 0 ].getName()
                                                          + ", unique1: " + uniqueAttr
                                                          + ", unique2: " + attr + "]" );
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
            throw new ConfigurationException( "Unable to find unique attribute for "
                                              + "children of name: " + c[ 0 ].getName() );
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
