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
package org.jcontainer.loom.components.deployer;

import java.io.InputStream;
import org.apache.avalon.framework.logger.Logger;
import org.jcontainer.loom.tools.factory.DefaultComponentFactory;
import org.jcontainer.loom.tools.info.ComponentInfo;
import org.jcontainer.loom.tools.infobuilder.LegacyBlockInfoReader;

/**
 * A Phoenix-specific {@link org.jcontainer.loom.tools.factory.ComponentFactory}
 * that makes sure {@link org.jcontainer.loom.tools.info.ComponentInfo} is loaded via BlockInfo loader.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003-06-29 04:38:21 $
 */
public class PhoenixComponentFactory
    extends DefaultComponentFactory
{
    private final LegacyBlockInfoReader m_infoReader = new LegacyBlockInfoReader();

    public PhoenixComponentFactory( final ClassLoader classLoader )
    {
        super( classLoader );
    }

    public void enableLogging( final Logger logger )
    {
        super.enableLogging( logger );
        m_infoReader.enableLogging( logger );
    }

    protected ComponentInfo createComponentInfo( final String implementationKey )
        throws Exception
    {
        final String xinfo = implementationKey.replace( '.', '/' ) + ".xinfo";
        final InputStream inputStream = getClassLoader().getResourceAsStream( xinfo );
        if( null == inputStream )
        {
            final String message =
                "Missing BlockInfo for class " + implementationKey;
            throw new Exception( message );
        }

        return m_infoReader.createComponentInfo( implementationKey, inputStream );
    }

}

