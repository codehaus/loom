/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.tools.info;

/**
 * A descriptor describing the Loggers that the Component
 * will use. The name of each Logger is relative to the
 * Logger passed to the component (namespace separator is '.').
 * The name "", designates the root logger.
 *
 * <p>Associated with each Logger is a set of arbitrary
 * Attributes that can be used to store extra information
 * about Logger requirements.</p>
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.3 $ $Date: 2003-06-29 01:07:35 $
 */
public class LoggerDescriptor
    extends FeatureDescriptor
{
    /**
     * A empty array of logger descriptors
     */
    public static final LoggerDescriptor[] EMPTY_SET = new LoggerDescriptor[ 0 ];

    /**
     * The name of the logger.
     */
    private final String m_name;

    /**
     * Create a descriptor for Logger.
     *
     * @exception java.lang.NullPointerException if name argument is null
     */
    public LoggerDescriptor( final String name,
                             final Attribute[] attribute )
    {
        super( attribute );
        if( null == name )
        {
            throw new NullPointerException( "name" );
        }

        m_name = name;
    }

    /**
     * Return the name of logger.
     *
     * @return the name of Logger.
     */
    public String getName()
    {
        return m_name;
    }
}
