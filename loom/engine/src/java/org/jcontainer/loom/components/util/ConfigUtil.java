/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.components.util;

import org.jcontainer.dna.impl.DefaultConfiguration;
import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;

/**
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003-10-05 03:25:09 $
 */
public class ConfigUtil
{
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
