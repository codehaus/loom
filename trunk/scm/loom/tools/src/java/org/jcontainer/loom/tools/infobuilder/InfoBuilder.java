/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.tools.infobuilder;

import java.io.InputStream;
import org.jcontainer.dna.AbstractLogEnabled;
import org.jcontainer.dna.Logger;
import org.jcontainer.loom.tools.info.ComponentInfo;

/**
 * A InfoBuilder is responsible for building {@link ComponentInfo}
 * objects from Configuration objects. The format for Configuration object
 * is specified in the <a href="package-summary.html#external">package summary</a>.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.11 $ $Date: 2003-10-06 13:36:59 $
 */
public final class InfoBuilder
    extends AbstractLogEnabled
{
    /**
     * The InfoReader.
     */
    private final BlockInfoReader m_infoCreator = new BlockInfoReader();

    /**
     * Setup logging for all subcomponents
     */
    public void enableLogging( final Logger logger )
    {
        super.enableLogging( logger );
        setupLogger( m_infoCreator );
    }

    /**
     * Create a {@link ComponentInfo} object for specified
     * classname, in specified ClassLoader.
     *
     * @param classname The classname of Component
     * @param classLoader the ClassLoader to load info from
     * @return the created ComponentInfo
     * @throws Exception if an error occurs
     */
    public ComponentInfo buildComponentInfo( final String classname,
                                             final ClassLoader classLoader )
        throws Exception
    {
        final String xinfo = classname.replace( '.', '/' ) + ".xinfo";
        final InputStream inputStream = classLoader.getResourceAsStream( xinfo );
        if( null == inputStream )
        {
            return null;
        }
        return m_infoCreator.createComponentInfo( classname, inputStream );
    }
}
