/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.realityforge.classman.builder.test;

import org.realityforge.classman.builder.SimpleLoaderResolver;
import org.realityforge.classman.test.DataConstants;
import org.realityforge.extension.Extension;
import java.io.File;
import java.net.URL;
import java.util.Set;

/**
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003-08-01 02:51:55 $
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
