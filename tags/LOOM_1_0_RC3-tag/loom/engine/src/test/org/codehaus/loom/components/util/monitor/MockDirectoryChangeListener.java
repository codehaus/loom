/*
 * Copyright (C) The Loom Group. All rights reserved.
 *
 * This software is published under the terms of the Loom
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.codehaus.loom.components.util.monitor;

import java.util.Collections;
import java.util.Set;

/**
 * DirectoryChangeListener used for storing a single change.
 *
 * @author Johan Sjoberg
 * @version $Revision: 1.2 $
 */
public class MockDirectoryChangeListener
    implements DirectoryChangeListener
{
    /**
     * The recorded change type
     */
    protected int m_changeType = 0;

    /**
     * The recorded set of files
     */
    protected Set m_fileSet = Collections.EMPTY_SET;

    /**
     * Record a change.
     */
    public void directoryChange( final int type, final Set fileSet )
    {
        m_changeType = type;
        m_fileSet = fileSet;
    }

    public void unableToListContents()
    {
        throw new RuntimeException( "Expected to be able to view directory contents" );
    }
}