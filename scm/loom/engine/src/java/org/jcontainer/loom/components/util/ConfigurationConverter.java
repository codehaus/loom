/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.components.util;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfiguration;

/**
 * Utility class to convert DNA Configuration objects
 * into Avalon Configuraiton objects.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003-10-04 23:59:55 $
 */
public class ConfigurationConverter
{
    public static Configuration toConfiguration( final org.jcontainer.dna.Configuration configuration )
    {
        final DefaultConfiguration result =
            new DefaultConfiguration( configuration.getName(),
                                      configuration.getLocation() );
        final String[] names = configuration.getAttributeNames();
        for( int i = 0; i < names.length; i++ )
        {
            final String name = names[ i ];
            final String value = configuration.getAttribute( name, null );
            result.setAttribute( name, value );
        }

        final org.jcontainer.dna.Configuration[] children =
            configuration.getChildren();
        for( int i = 0; i < children.length; i++ )
        {
            final Configuration child = toConfiguration( children[ i ] );
            result.addChild( child );
        }

        final String value = configuration.getValue( null );
        if( null != value )
        {
            result.setValue( value );
        }

        return result;
    }
}
