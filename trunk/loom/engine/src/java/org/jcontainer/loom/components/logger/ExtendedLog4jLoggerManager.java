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
package org.jcontainer.loom.components.logger;

import java.util.HashMap;
import java.util.Map;
import org.apache.avalon.excalibur.logger.Log4JLoggerManager;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.ConfigurationUtil;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.phoenix.BlockContext;
import org.apache.log4j.xml.DOMConfigurator;
import org.realityforge.configkit.PropertyExpander;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A LoggerManager for Log4j that will configure the Log4j subsystem
 * using specified configuration.
 *
 * @author <a href="mailto:Ole.Bulbuk at ebp.de">Ole Bulbuk</a>
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003-06-29 04:38:22 $
 */
public class ExtendedLog4jLoggerManager
    extends Log4JLoggerManager
    implements Contextualizable, Configurable, Initializable
{
    private final PropertyExpander m_expander = new PropertyExpander();
    private final Map m_data = new HashMap();
    private Element m_element;

    public void contextualize( Context context )
        throws ContextException
    {
        extractData( context, BlockContext.APP_HOME_DIR );
        extractData( context, BlockContext.APP_NAME );
        //extractData( context, "phoenix.home" );
    }

    private void extractData( Context context, final String key ) throws ContextException
    {
        m_data.put( key, context.get( key ) );
    }

    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        m_element = ConfigurationUtil.toElement( configuration );
    }

    public void initialize()
        throws Exception
    {
        m_expander.expandValues( m_element, m_data );
        final Document document = m_element.getOwnerDocument();
        final Element newElement = document.createElement( "log4j:configuration" );
        final NodeList childNodes = m_element.getChildNodes();
        final int length = childNodes.getLength();
        for( int i = 0; i < length; i++ )
        {
            final Node node = childNodes.item( i );
            final Node newNode = node.cloneNode( true );
            newElement.appendChild( newNode );
        }

        document.appendChild( newElement );
        DOMConfigurator.configure( newElement );
    }
}

