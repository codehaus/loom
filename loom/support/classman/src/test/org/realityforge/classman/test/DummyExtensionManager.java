/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.realityforge.classman.test;

import org.apache.avalon.excalibur.packagemanager.ExtensionManager;
import org.apache.avalon.excalibur.packagemanager.OptionalPackage;
import org.apache.avalon.excalibur.extension.Extension;
import java.io.File;

/**
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003-04-04 13:52:14 $
 */
public class DummyExtensionManager
    implements ExtensionManager
{
    public static final Extension EXTENSION =
        new Extension( "Foo-Ext", null, null, null, null, null, null );
    private static final OptionalPackage OPTIONAL_PACKAGE =
        new OptionalPackage( new File( "." ), new Extension[]{EXTENSION}, new Extension[ 0 ] );

    public OptionalPackage[] getOptionalPackages( Extension extension )
    {
        if( EXTENSION.isCompatibleWith( extension ) )
        {
            return new OptionalPackage[]{OPTIONAL_PACKAGE};
        }
        else
        {
            return new OptionalPackage[ 0 ];
        }
    }
}
