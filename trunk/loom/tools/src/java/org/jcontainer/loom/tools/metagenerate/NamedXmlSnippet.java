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
package org.jcontainer.loom.tools.metagenerate;

/**
 * A named XML snippet
 * @author Paul Hammant
 */
public class NamedXmlSnippet implements Comparable
{
    private final String m_name;
    private final String m_xml;

    /**
     * Construct an NamedXmlSnippet
     * @param name The node name
     * @param xml the XML
     */
    public NamedXmlSnippet( final String name, final String xml )
    {
        this.m_name = name;
        this.m_xml = xml;
    }

    /**
     * Get the name
     * @return The Name
     */
    public String getName()
    {
        return m_name;
    }

    /**
     * Get the XML
     * @return The XML
     */
    public String getXml()
    {
        return m_xml;
    }

    /**
     * From comparable
     * @param object The object to compare to.
     * @return whichever is order precidence
     */
    public int compareTo( final Object object )
    {
        final NamedXmlSnippet attr = (NamedXmlSnippet)object;
        return m_name.compareTo( attr.getName() );
    }
}
