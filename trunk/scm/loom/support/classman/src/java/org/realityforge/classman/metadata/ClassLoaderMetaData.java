/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.realityforge.classman.metadata;

import org.realityforge.extension.Extension;

/**
 * This class defines a specific classloader, made up of entrys, {@link
 * org.realityforge.extension.Extension} and {@link FileSetMetaData} objects.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.4 $ $Date: 2003-12-02 07:28:52 $
 */
public class ClassLoaderMetaData
{
    /**
     * The name of the current classloader. This may be used by other
     * ClassLoader definitions to refer to this ClassLoader.
     */
    private final String m_name;

    /** The name of the parent classloader. */
    private final String m_parent;

    /** The Entrys that are added to this ClassLoader. */
    private final String[] m_entrys;

    /** The Entrys that are required by this ClassLoader. */
    private final Extension[] m_extensions;

    /** The Filesets that are added to this ClassLoader. */
    private final FileSetMetaData[] m_filesets;

    public ClassLoaderMetaData( final String name,
                                final String parent,
                                final String[] entrys,
                                final Extension[] extensions,
                                final FileSetMetaData[] filesets )
    {
        m_name = name;
        m_parent = parent;
        m_entrys = entrys;
        m_extensions = extensions;
        m_filesets = filesets;
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
     * Return the name of parent Classloader.
     *
     * @return the name of parent Classloader.
     */
    public String getParent()
    {
        return m_parent;
    }

    /**
     * Return the elements added to Classloader.
     *
     * @return the elements added to Classloader.
     */
    public String[] getEntrys()
    {
        return m_entrys;
    }

    /**
     * Return the extensions added to Classloader.
     *
     * @return the extensions added to Classloader.
     */
    public Extension[] getExtensions()
    {
        return m_extensions;
    }

    /**
     * Return the filesets added to Classloader.
     *
     * @return the filesets added to Classloader.
     */
    public FileSetMetaData[] getFilesets()
    {
        return m_filesets;
    }
}
