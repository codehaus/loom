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
package org.jcontainer.loom.components.extensions.pkgmgr.impl;

import java.util.Comparator;
import org.realityforge.extension.DeweyDecimal;
import org.realityforge.extension.Extension;
import org.jcontainer.loom.components.extensions.pkgmgr.OptionalPackage;

/**
 * A simple class to compare two extensions and sort them
 * on spec version and then on impl version. Unspecified
 * versions rate lower than specified versions.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.2 $ $Date: 2003-08-02 12:26:32 $
 */
class OptionalPackageComparator
    implements Comparator
{
    /**
     * The name of extension the comparator is working with.
     */
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
