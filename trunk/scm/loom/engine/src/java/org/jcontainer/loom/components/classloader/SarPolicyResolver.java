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

import java.io.File;
import java.net.URL;
import java.security.Policy;
import java.util.Map;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.jcontainer.loom.components.util.ResourceUtil;
import org.realityforge.xmlpolicy.builder.PolicyResolver;

/**
 * A basic resolver that resolves container specific features.
 * (like remapping URLs).
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.2 $ $Date: 2003-08-07 16:28:51 $
 */
class SarPolicyResolver
    extends AbstractLogEnabled
    implements PolicyResolver
{
    private final File m_baseDirectory;
    private final File m_workDirectory;

    SarPolicyResolver( final File baseDirectory,
                       final File workDirectory )
    {
        m_workDirectory = workDirectory;
        m_baseDirectory = baseDirectory;
    }

    public Policy createPolicy( final Map grants )
        throws Exception
    {
        final SarPolicy sarPolicy = new SarPolicy( grants );
        ContainerUtil.enableLogging( sarPolicy, getLogger() );
        ContainerUtil.initialize( sarPolicy );
        return sarPolicy;
    }

    public URL resolveLocation( String location )
        throws Exception
    {
        if( null == location )
        {
            return null;
        }
        else
        {
            location = ResourceUtil.expandSarURL( location,
                                                  m_baseDirectory,
                                                  m_workDirectory );
            return new URL( location );
        }
    }
}
