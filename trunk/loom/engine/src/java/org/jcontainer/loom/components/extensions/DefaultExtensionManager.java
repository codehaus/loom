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
package org.jcontainer.loom.components.extensions;

import java.io.File;
import java.util.ArrayList;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.jcontainer.loom.components.extensions.pkgmgr.ExtensionManager;
import org.jcontainer.loom.components.extensions.pkgmgr.OptionalPackage;
import org.jcontainer.loom.interfaces.ExtensionManagerMBean;
import org.realityforge.salt.i18n.ResourceManager;
import org.realityforge.salt.i18n.Resources;

/**
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.2 $ $Date: 2003-07-13 00:15:36 $
 */
public class DefaultExtensionManager
    extends org.jcontainer.loom.components.extensions.pkgmgr.impl.DefaultExtensionManager
    implements LogEnabled, Parameterizable, Initializable, Disposable,
    ExtensionManager, ExtensionManagerMBean
{
    private final static Resources REZ =
        ResourceManager.getPackageResources( DefaultExtensionManager.class );

    private Logger m_logger;
    private String m_rawPath;

    public void enableLogging( final Logger logger )
    {
        m_logger = logger;
    }

    public void parameterize( final Parameters parameters )
        throws ParameterException
    {
        final String phoenixHome = parameters.getParameter( "phoenix.home" );
        final String defaultExtPath = phoenixHome + File.separator + "ext";
        m_rawPath = parameters.getParameter( "phoenix.ext.path", defaultExtPath );
    }

    public void initialize()
        throws Exception
    {
        setPath( m_rawPath );
        rescanPath();
    }

    public void dispose()
    {
        clearCache();
    }

    public void rescanPath()
    {
        super.scanPath();

        //Display a list of packages once they have been added.
        if( m_logger.isDebugEnabled() )
        {
            final ArrayList list = new ArrayList();
            final OptionalPackage[] optionalPackages = getAllOptionalPackages();
            for( int i = 0; i < optionalPackages.length; i++ )
            {
                list.add( optionalPackages[ i ].getFile() );
            }

            final String message =
                REZ.format( "extension.packages.notice", list );
            m_logger.debug( message );
        }
    }

    protected void debug( final String message )
    {
        m_logger.debug( message );
    }
}
