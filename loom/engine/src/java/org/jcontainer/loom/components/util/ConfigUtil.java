/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.components.util;

import java.util.Map;
import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.jcontainer.dna.impl.DefaultConfiguration;
import org.realityforge.configkit.PropertyExpander;

/**
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.2 $ $Date: 2003-10-05 08:10:20 $
 */
public class ConfigUtil
{
    public static Configuration expandValues( final Configuration input,
                                              final Map data )
        throws Exception
    {
        final PropertyExpander expander = new PropertyExpander();
        return expandValues( expander, input, data );
    }

    private static Configuration expandValues( final PropertyExpander expander, final Configuration input, final Map data ) throws Exception
    {
        final DefaultConfiguration output =
            new DefaultConfiguration( input.getName(),
                                      input.getPath(),
                                      input.getLocation() );
        final String[] names = input.getAttributeNames();
        for( int i = 0; i < names.length; i++ )
        {
            final String name = names[ i ];
            final String value = input.getAttribute( name );
            final String newValue = expander.expandValues( value, data );
            output.setAttribute( name, newValue );
        }

        final Configuration[] children = input.getChildren();
        for( int i = 0; i < children.length; i++ )
        {
            final Configuration child =
                expandValues( expander, children[ i ], data );
            output.addChild( child );
        }

        final String content = input.getValue( null );
        if( null != content )
        {
            final String newValue =
                expander.expandValues( content, data );
            output.setValue( newValue );
        }

        return output;
    }

    public static boolean equals( final Configuration configuration1,
                                  final Configuration configuration2 )
    {
        final Configuration[] children1 = configuration1.getChildren();
        final Configuration[] children2 = configuration2.getChildren();
        if( children1.length != children2.length )
        {
            return false;
        }
        else
        {
            for( int i = 0; i < children1.length; i++ )
            {
                if( !equals( children1[ i ], children2[ i ] ) )
                {
                    return false;
                }
            }
        }

        final String[] names1 = configuration1.getAttributeNames();
        final String[] names2 = configuration2.getAttributeNames();
        if( names1.length != names2.length )
        {
            return false;
        }
        else
        {
            for( int i = 0; i < names1.length; i++ )
            {
                final String value1 =
                    configuration1.getAttribute( names1[ i ], null );
                final String value2 =
                    configuration2.getAttribute( names1[ i ], null );
                if( !value1.equals( value2 ) )
                {
                    return false;
                }
            }
        }

        final String value1 = configuration1.getValue( null );
        final String value2 = configuration2.getValue( null );
        if( null == value1 && null == value2 )
        {
            return true;
        }
        else if( null != value1 && null != value2 )
        {
            return value1.equals( value2 );
        }
        else
        {
            return false;
        }
    }

    public static void copy( final DefaultConfiguration newConfiguration,
                             final Configuration configuration )
        throws ConfigurationException
    {
        final Configuration[] children = configuration.getChildren();
        for( int i = 0; i < children.length; i++ )
        {
            newConfiguration.addChild( children[ i ] );
        }
        final String[] names = configuration.getAttributeNames();
        for( int i = 0; i < names.length; i++ )
        {
            final String name = names[ i ];
            final String value = configuration.getAttribute( name );
            newConfiguration.setAttribute( name, value );
        }
        final String content = configuration.getValue( null );
        if( null != content )
        {
            newConfiguration.setValue( content );
        }
    }
}
