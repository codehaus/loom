/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
 package org.jcontainer.loom.tools.info;

import org.jcontainer.loom.tools.info.EntryDescriptor;

/**
 * A descriptor describing the Context that the component
 * is passed to describe information about Runtime environment
 * of Component. It contains information such as;
 * <ul>
 *   <li>type: the type of the Context if it
 *       differs from base Context class (Such as
 *       <a href="http://jakarta.apache.org/avalon/phoenix">
 *       Phoenixes</a> BlockContext).</li>
 *   <li>entrys: a list of entrys contained in context</li>
 * </ul>
 *
 * <p>Associated with each Context is a set of arbitrary
 * Attributes that can be used to store extra information
 * about Context requirements.</p>
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003-06-26 08:37:43 $
 */
public class ContextDescriptor
    extends FeatureDescriptor
{
    /**
     * The default type of the context.
     */
    public static final String DEFAULT_TYPE =
        "org.apache.avalon.framework.context.Context";

    /**
     * A constant for an empty context with standard type.
     */
    public static final ContextDescriptor EMPTY_CONTEXT =
        new ContextDescriptor( DEFAULT_TYPE, EntryDescriptor.EMPTY_SET, Attribute.EMPTY_SET );

    /**
     * The type of the context. (ie a name of the context
     * interface that is required by the component).
     */
    private final String m_type;

    /**
     * The set of entrys to populate the context with
     * for this component.
     */
    private final EntryDescriptor[] m_entrys;

    /**
     * Create a descriptor.
     *
     * @throws java.lang.NullPointerException if type or entrys argument is null
     * @throws java.lang.IllegalArgumentException if the classname format is invalid
     */
    public ContextDescriptor( final String type,
                              final EntryDescriptor[] entrys,
                              final Attribute[] attribute )
    {
        super( attribute );

        if( null == type )
        {
            throw new NullPointerException( "type" );
        }
        if( null == entrys )
        {
            throw new NullPointerException( "entrys" );
        }

        m_type = type;
        m_entrys = entrys;
    }

    /**
     * Return the type of Context class.
     *
     * @return the type of Context class.
     */
    public String getType()
    {
        return m_type;
    }

    /**
     * Return the entrys contained in the context.
     *
     * @return the entrys contained in the context.
     */
    public EntryDescriptor[] getEntrys()
    {
        return m_entrys;
    }

    /**
     * Return the entry with specified key.
     *
     * @return the entry with specified key.
     */
    public EntryDescriptor getEntry( final String key )
    {
        for( int i = 0; i < m_entrys.length; i++ )
        {
            final EntryDescriptor entry = m_entrys[ i ];
            if( entry.getKey().equals( key ) )
            {
                return entry;
            }
        }
        return null;
    }
}
