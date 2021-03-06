/*
 * Copyright (C) The Loom Group. All rights reserved.
 *
 * This software is published under the terms of the Loom
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.codehaus.loom.components.util;

import java.util.Map;
import org.codehaus.spice.configkit.PropertyExpander;
import org.codehaus.dna.Configuration;
import org.codehaus.dna.ConfigurationException;
import org.codehaus.dna.impl.DefaultConfiguration;

/**
 * @author Peter Donald
 * @version $Revision: 1.2 $ $Date: 2004-05-01 12:48:33 $
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

    private static Configuration expandValues( final PropertyExpander expander,
                                               final Configuration input,
                                               final Map data )
        throws Exception
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
