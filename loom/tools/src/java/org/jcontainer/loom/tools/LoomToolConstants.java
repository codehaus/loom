/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.tools;

/**
 * Some constants used by Loom container when
 * building component assemblys.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003-07-03 06:44:22 $
 */
public interface LoomToolConstants
{
    /**
     * Name of partition containing blocks.
     */
    String BLOCK_PARTITION = "Loom:BlockPartition";

    /**
     * Name of partition containing listeners.
     */
    String LISTENER_PARTITION = "Loom:Listener";
}
