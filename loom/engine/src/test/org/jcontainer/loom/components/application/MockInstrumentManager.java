/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.components.application;

import org.apache.excalibur.instrument.InstrumentManager;
import org.apache.excalibur.instrument.Instrumentable;

/**
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003-07-19 02:25:05 $
 */
class MockInstrumentManager
    implements InstrumentManager
{
    public void registerInstrumentable( Instrumentable instrumentable, String instrumentableName )
        throws Exception
    {
    }
}
