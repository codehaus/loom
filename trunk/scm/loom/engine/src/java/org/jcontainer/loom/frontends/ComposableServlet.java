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
package org.jcontainer.loom.frontends;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.jcontainer.loom.components.embeddor.SingleAppEmbeddor;
import org.jcontainer.loom.interfaces.Embeddor;

/**
 * WARNING: DO NOT USE THIS SERVLET FOR PRODUCTION SERVICE. THIS IS EXPERIMENTAL.
 * Composable servlet for easy life with <tt>LoomServlet</tt>.
 *
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 * @deprecated
 */
public abstract class ComposableServlet
    extends HttpServlet
    implements Composable
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( ComposableServlet.class );

    private SingleAppEmbeddor m_embeddor;

    public void init()
        throws ServletException
    {
        super.init();

        m_embeddor = (SingleAppEmbeddor)getServletContext().getAttribute( Embeddor.ROLE );
        if( null == m_embeddor )
        {
            final String message = REZ.getString( "servlet.error.load" );
            throw new ServletException( message );
        }

        try
        {
            compose( m_embeddor );
        }
        catch( final ComponentException ce )
        {
            throw new ServletException( ce );
        }
    }

    public void compose( final ComponentManager componentManager )
        throws ComponentException
    {
    }
}
