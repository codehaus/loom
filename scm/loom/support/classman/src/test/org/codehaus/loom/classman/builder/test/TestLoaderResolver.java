/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.codehaus.loom.classman.builder.test;

import org.codehaus.loom.classman.builder.SimpleLoaderResolver;
import org.codehaus.loom.classman.test.DataConstants;
import org.codehaus.loom.extension.Extension;

import java.io.File;
import java.net.URL;
import java.util.Set;

/**
 *
 * @author Peter Donald
 * @version $Revision: 1.1 $ $Date: 2004-04-19 22:19:25 $
 */
class TestLoaderResolver
    extends SimpleLoaderResolver
{
    public TestLoaderResolver( final File baseDirectory )
    {
        super( baseDirectory );
    }

    public URL resolveExtension( final Extension extension )
        throws Exception
    {
        if( extension == DataConstants.EXTENSION )
        {
            return new URL( "file:/" );
        }
        else
        {
            return super.resolveExtension( extension );
        }
    }

    protected void scanDependencies( Extension[] required,
                                     Extension[] available,
                                     Set dependencies,
                                     Set unsatisfied )
    {
        super.scanDependencies( required, available, dependencies, unsatisfied );
    }
}
