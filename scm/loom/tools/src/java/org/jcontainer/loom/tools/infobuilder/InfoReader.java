/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.tools.infobuilder;

import java.io.InputStream;
import org.jcontainer.loom.tools.info.ComponentInfo;

/**
 * Simple interface used to create {@link org.jcontainer.loom.tools.info.ComponentInfo}
 * objects from a stream. This abstraction was primarily created
 * so that the Info objesct could be built from non-XML sources
 * and no XML classes need be in the classpath.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003-06-26 09:34:20 $
 */
public interface InfoReader
{
    /**
     * Create a {@link org.jcontainer.loom.tools.info.ComponentInfo} from stream
     *
     * @param implementationKey the name of component type that we are looking up
     * @param inputStream the stream that the resource is loaded from
     * @return the newly created {@link org.jcontainer.loom.tools.info.ComponentInfo}
     * @throws java.lang.Exception if unable to create info
     */
    ComponentInfo createComponentInfo( String implementationKey,
                                       InputStream inputStream )
        throws Exception;
}
