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
package org.apache.avalon.phoenix;

import java.util.EventObject;
import org.apache.avalon.phoenix.metadata.SarMetaData;

/**
 * This is the class that is used to deliver notifications
 * about Application state changes to the
 * <code>ApplicationListener</code>s of a Server Application.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 */
public final class ApplicationEvent
    extends EventObject
{
    private final String m_name;
    private final SarMetaData m_sarMetaData;

    /**
     * Construct the <code>ApplicationEvent</code>.
     *
     * @param name the name of app
     * @param sarMetaData the SarMetaData object for app
     */
    public ApplicationEvent( final String name,
                             final SarMetaData sarMetaData )
    {
        super( name );

        if( null == name )
        {
            throw new NullPointerException( "name" );
        }
        if( null == sarMetaData )
        {
            throw new NullPointerException( "sarMetaData" );
        }

        m_name = name;
        m_sarMetaData = sarMetaData;
    }

    /**
     * Retrieve name of app.
     *
     * @return the name of app
     */
    public String getName()
    {
        return m_name;
    }

    /**
     * Retrieve the SarMetaData for app.
     *
     * @return the SarMetaData for app
     */
    public SarMetaData getSarMetaData()
    {
        return m_sarMetaData;
    }
}
