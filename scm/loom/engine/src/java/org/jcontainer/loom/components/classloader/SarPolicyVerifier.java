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

import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.realityforge.xmlpolicy.verifier.PolicyVerifier;

/**
 * A simple adapter for verifier to support logging to container subsystems.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.2 $ $Date: 2003-08-07 16:28:51 $
 */
class SarPolicyVerifier
    extends PolicyVerifier
    implements LogEnabled
{
    private Logger m_logger;

    public void enableLogging( final Logger logger )
    {
        m_logger = logger;
    }

    protected void info( final String message )
    {
        m_logger.info( message );
    }
}
