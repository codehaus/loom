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

import java.util.HashMap;
import java.util.Set;
import javax.management.modelmbean.ModelMBeanInfo;

/**
 * It reprensents a managed object in the managegement space.  It is a container for
 * zero or more management topics and zero or more management lists.
 *
 * @author  robertsh
 * @version $$
 */
public class Target
{
    private final String m_name;
    private final HashMap m_topics;
    private final Object m_managedResource;

    /**
     * Creates new Target
     *
     * @param name the name for the target
     * @param managedResource the object that this managedResource represents in the management hierarchy
     */
    public Target( final String name,
                   final Object managedResource )
    {
        m_name = name;
        m_managedResource = managedResource;
        m_topics = new HashMap();
    }

    /**
     * Returns the name of the Target
     * @return  the name
     */
    public String getName()
    {
        return m_name;
    }

    /**
     * Returns the object managed by the target
     *
     * @return  the managed object
     */
    public Object getManagedResource()
    {
        return m_managedResource;
    }

    /**
     * Topics are a set of attributes and operations relevant to a particular
     * aspect of an object.  A Target must typically have at least one topic in
     * order to be manageable.
     *
     * @param topic
     */
    public void addTopic( final ModelMBeanInfo topic )
    {
        m_topics.put( topic.getDescription(), topic );
    }

    /**
     * Removes a topic for this target
     * @param name  the name of the topic to remove
     */
    public void removeTopic( final String name )
    {
        m_topics.remove( name );
    }

    /**
     * Gets a topic for this Target
     *
     * @param name the name of the topic
     * @return  the topic of that name
     */
    public ModelMBeanInfo getTopic( final String name )
    {
        return (ModelMBeanInfo)m_topics.get( name );
    }

    /**
     * Returns the Set of topics for this Target
     *
     * @return the Set of topic names
     */
    public Set getTopicNames()
    {
        return m_topics.keySet();
    }
}
