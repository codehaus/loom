/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.tools.infobuilder;

import java.io.OutputStream;
import org.jcontainer.loom.tools.info.ComponentInfo;

/**
 * Simple interface used to write {@link org.jcontainer.loom.tools.info.ComponentInfo}
 * objects to a stream. Different implementations will write to
 * different output formats.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003-06-26 09:34:20 $
 */
public interface InfoWriter
{
    /**
     * Write a {@link org.jcontainer.loom.tools.info.ComponentInfo} to a stream
     *
     * @param info the Info to write out
     * @param outputStream the stream to write info to
     * @throws java.lang.Exception if unable to write info
     */
    void writeComponentInfo( ComponentInfo info,
                             OutputStream outputStream )
        throws Exception;
}
