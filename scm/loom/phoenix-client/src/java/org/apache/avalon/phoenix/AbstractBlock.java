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
package org.apache.avalon.phoenix;

import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;

/**
 * This is an <code>AbstractBlock</code> that makes deployment a bit
 * easier.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version CVS $Revision: 1.3 $ $Date: 2003-06-29 02:52:06 $
 * @deprecated As Block interface is deprecated this class is also
 *             deprecated with no replacement.
 */
public abstract class AbstractBlock
    extends AbstractLogEnabled
    implements Block, Contextualizable, Composable, Configurable
{
    private BlockContext m_context;
    private Configuration m_configuration;
    private ComponentManager m_componentManager;

    public void contextualize( final Context context )
    {
        m_context = (BlockContext)context;
    }

    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        m_configuration = configuration;
    }

    public void compose( final ComponentManager componentManager )
        throws ComponentException
    {
        m_componentManager = componentManager;
    }

    protected final BlockContext getBlockContext()
    {
        return m_context;
    }

    /**
     * Retrieve the supplied {@link org.apache.avalon.framework.component.ComponentManager}.
     *
     * @return the supplied {@link org.apache.avalon.framework.component.ComponentManager}.
     * @deprecated {@link org.apache.avalon.framework.component.ComponentManager} is deprecated and no block should be using it.
     */
    protected final ComponentManager getComponentManager()
    {
        return m_componentManager;
    }

    /**
     * Retrieve cached configuration values.
     *
     * @return the configuration
     * @deprecated No Block should be relying on AbstractBlock to implement Configurable
     */
    protected final Configuration getConfiguration()
    {
        return m_configuration;
    }
}
