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
package org.jcontainer.loom.components.classloader;

import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.util.Map;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.realityforge.xmlpolicy.runtime.DefaultPolicy;

/**
 * Policy that extracts information from policy files.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 */
class SarPolicy
    extends DefaultPolicy
    implements LogEnabled, Initializable
{
    private Logger m_logger;
    private final Map m_grants;

    public SarPolicy( final Map grants )
        throws Exception
    {
        m_grants = grants;
    }

    public void enableLogging( final Logger logger )
    {
        m_logger = logger;
    }

    public void initialize()
        throws Exception
    {
        processGrants( m_grants );
    }

    public PermissionCollection getPermissions( final CodeSource codeSource )
    {
        if( m_logger.isDebugEnabled() )
        {
            m_logger.debug( "getPermissions(" + codeSource.getLocation() + ");" );
        }

        return super.getPermissions( codeSource );
    }

    protected Permissions createPermissionSetFor( final CodeSource codeSource )
    {
        if( m_logger.isDebugEnabled() )
        {
            m_logger.debug( "createPermissionSetFor(" + codeSource + ");" );
        }

        return super.createPermissionSetFor( codeSource );
    }

    protected void error( final String message,
                          final Throwable throwable )
    {
        m_logger.error( message, throwable );
    }
}
