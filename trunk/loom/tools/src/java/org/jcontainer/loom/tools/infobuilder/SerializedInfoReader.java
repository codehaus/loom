/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.tools.infobuilder;

import java.io.InputStream;
import java.io.ObjectInputStream;
import org.jcontainer.loom.tools.info.ComponentInfo;

/**
 * Create {@link org.jcontainer.loom.tools.info.ComponentInfo} objects from stream made up of
 * serialized object.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.2 $ $Date: 2003-06-26 09:35:27 $
 */
public class SerializedInfoReader
    implements InfoReader
{
    public ComponentInfo createComponentInfo( final String implementationKey,
                                              final InputStream inputStream )
        throws Exception
    {
        final ObjectInputStream ois = new ObjectInputStream( inputStream );
        return (ComponentInfo)ois.readObject();
    }
}
