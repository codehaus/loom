/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.realityforge.classman.metadata;

/**
 * This class defines a classloader that "merges" multiple
 * classloaders. For this to be successful, it is required that
 * the ClassLoaders contain disjoint sets of classes (with the
 * exception of classes loaded from the system classpath).
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003-04-04 13:52:12 $
 */
public class JoinMetaData
{
    /**
     * The name of the current classloader.
     * This may be used by other ClassLoader definitions to refer
     * to this ClassLoader.
     */
    private final String m_name;

    /**
     * The list of classloaders that are merged in
     * this classloader.
     */
    private final String[] m_classloaders;

    /**
     * The definition for set of classloaders
     *
     * @param name
     * @param classloaders
     */
    public JoinMetaData( final String name,
                         final String[] classloaders )
    {
        m_name = name;
        m_classloaders = classloaders;
    }

    /**
     * Return the name of Classloader.
     *
     * @return the name of Classloader.
     */
    public String getName()
    {
        return m_name;
    }

    /**
     * Return the list of classloaders that are merged in
     * this classloader
     *
     * @return the list of classloaders that are merged
     *         in this classloader
     */
    public String[] getClassloaders()
    {
        return m_classloaders;
    }
}
