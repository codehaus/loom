/*
 * Copyright (C) The Loom Group. All rights reserved.
 *
 * This software is published under the terms of the Loom
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.codehaus.loom.components.util.monitor;

import java.util.Set;

/**
 * Interface implemented by classes that want to recieve
 * notifications about changes of a directory's content.
 *
 * @author Johan Sjoberg
 * @version $Revision: 1.1 $
 */
public interface DirectoryChangeListener
{
    /** Addition of files */
    public static final int ADDITION = 1;

    /** Removal of files */
    public static final int REMOVAL = 2;

    /** Modification of files */
    public static final int MODIFICATION = 3;

    /**
     * Indication that some file or files have been changed.
     *
     * @param type Type of change to the directory
     * @param fileSet a Set of files
     */
    public void directoryChange( int type, Set fileSet );
}