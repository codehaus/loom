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
package org.jcontainer.loom.components.embeddor;

import org.apache.avalon.framework.configuration.Configuration;

/**
 * @author <a href="bauer@denic.de">Joerg Bauer</a>
 */
public class EmbeddorEntry
{
    private final String m_role;
    private final String m_classname;
    private final String m_loggerName;
    private final Configuration m_configuration;
    private Object m_object;

    public EmbeddorEntry( final String role,
                          final String classname,
                          final String loggerName,
                          final Configuration configuration )
    {
        m_role = role;
        m_classname = classname;
        m_loggerName = loggerName;
        m_configuration = configuration;
    }

    public String getRole()
    {
        return m_role;
    }

    public Configuration getConfiguration()
    {
        return m_configuration;
    }

    public String getClassName()
    {
        return m_classname;
    }

    public Object getObject()
    {
        return m_object;
    }

    public void setObject( final Object object )
    {
        m_object = object;
    }

    public String getLoggerName()
    {
        return m_loggerName;
    }
}
