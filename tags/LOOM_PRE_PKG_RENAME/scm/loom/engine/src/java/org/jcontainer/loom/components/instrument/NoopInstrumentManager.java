/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.components.instrument;

import org.apache.excalibur.instrument.InstrumentManager;
import org.apache.excalibur.instrument.Instrumentable;

/**
 * @author Peter Donald
 * @version $Revision: 1.2 $ $Date: 2003-11-29 13:44:20 $
 */
public class NoopInstrumentManager
    implements InstrumentManager
{
    public void registerInstrumentable( Instrumentable instrumentable,
                                        String instrumentableName )
        throws Exception
    {
    }
}
