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
package org.jcontainer.loom.components.manager;

final class ManagedEntry
{
    ///Object passed in for management
    private final Object m_object;

    ///Interfaces object wants to be managed through (can be null)
    private final Class[] m_interfaces;

    ///Object representation when exported (usually a proxy)
    private final Object m_exportedObject;

    ManagedEntry( final Object object,
                  final Class[] interfaces,
                  final Object exportedObject )
    {
        m_object = object;
        m_interfaces = interfaces;
        m_exportedObject = exportedObject;
    }

    Object getObject()
    {
        return m_object;
    }

    Class[] getInterfaces()
    {
        return m_interfaces;
    }

    Object getExportedObject()
    {
        return m_exportedObject;
    }
}
