/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.tools.infobuilder;

import java.io.ObjectOutputStream;
import java.io.OutputStream;
import org.jcontainer.loom.tools.info.ComponentInfo;

/**
 * Write {@link org.jcontainer.loom.tools.info.ComponentInfo} objects to a stream as serialized objects.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003-06-26 09:34:20 $
 */
public class SerializedInfoWriter
    implements InfoWriter
{
    public void writeComponentInfo( final ComponentInfo info,
                                    final OutputStream outputStream )
        throws Exception
    {
        final ObjectOutputStream oos = new ObjectOutputStream( outputStream );
        oos.writeObject( info );
        oos.flush();
    }
}
