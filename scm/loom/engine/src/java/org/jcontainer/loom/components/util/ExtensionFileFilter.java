/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.components.util;

import java.io.File;
import java.io.FilenameFilter;

/**
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003-10-05 00:30:20 $
 */
public class ExtensionFileFilter
    implements FilenameFilter
{
    private String[] m_extensions;

    public ExtensionFileFilter( final String[] extensions )
    {
        m_extensions = extensions;
    }

    public ExtensionFileFilter( final String extension )
    {
        m_extensions = new String[]{extension};
    }

    public boolean accept( final File file, final String name )
    {
        for( int i = 0; i < m_extensions.length; i++ )
        {
            if( name.endsWith( m_extensions[ i ] ) )
            {
                return true;
            }
        }
        return false;
    }
}
