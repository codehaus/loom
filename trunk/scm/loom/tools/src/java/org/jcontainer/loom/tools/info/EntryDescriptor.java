/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.tools.info;

import java.io.Serializable;

/**
 * A descriptor that describes a value that must be placed
 * in components Context. It contains information about;
 * <ul>
 *   <li>key: the key that component uses to look up entry</li>
 *   <li>type: the class/interface of the entry</li>
 *   <li>isOptional: true if entry is optional rather than required</li>
 * </ul>
 *
 * <p>See the <a href="../../../../../context.html">Entries</a> document
 * for a list of widely recognized entry values and a recomended
 * naming scheme for other entrys.</p>
 *
 * <p>Also associated with each entry is a set of arbitrary
 * Attributes that can be used to store extra information
 * about entry. See {@link org.jcontainer.loom.tools.info.ComponentDescriptor} for example
 * of how to declare the container specific Attributes.</p>
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003-06-26 08:37:43 $
 */
public final class EntryDescriptor
    extends FeatureDescriptor
    implements Serializable
{
    /**
     * Emprty set of entrys.
     */
    public static final EntryDescriptor[] EMPTY_SET = new EntryDescriptor[ 0 ];

    /**
     * The name the component uses to lookup entry.
     */
    private final String m_key;

    /**
     * The class/interface of the Entry.
     */
    private final String m_type;

    /**
     * True if entry is optional, false otherwise.
     */
    private final boolean m_optional;

    /**
     * Construct an Entry.
     */
    public EntryDescriptor( final String key,
                            final String type,
                            final boolean optional,
                            final Attribute[] attribute )
    {
        super( attribute );
        if( null == key )
        {
            throw new NullPointerException( "key" );
        }
        if( null == type )
        {
            throw new NullPointerException( "type" );
        }

        m_key = key;
        m_type = type;
        m_optional = optional;
    }

    /**
     * Return the key that Component uses to lookup entry.
     *
     * @return the key that Component uses to lookup entry.
     */
    public String getKey()
    {
        return m_key;
    }

    /**
     * Return the key type of value that is stored in Context.
     *
     * @return the key type of value that is stored in Context.
     */
    public String getType()
    {
        return m_type;
    }

    /**
     * Return true if entry is optional, false otherwise.
     *
     * @return true if entry is optional, false otherwise.
     */
    public boolean isOptional()
    {
        return m_optional;
    }
}
