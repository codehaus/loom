/*
 * Copyright (C) The Loom Group. All rights reserved.
 *
 * This software is published under the terms of the Loom
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.codehaus.loom.components.instrument;

import org.codehaus.spice.alchemist.instrument.DNAInstrumentManager;
import org.apache.excalibur.instrument.manager.DefaultInstrumentManager;


/**
 * LoomInstrumentManager is a wrapper around ExcaliburInstrumentManager
 * from the <a href="http://excalibur.apache.org">Apache Excalibur</a> project.
 *
 * @author Johan Sjoberg
 * @version $Revision: 1.3 $
 *
 * @dna.component
 */
public class LoomInstrumentManager extends DNAInstrumentManager
{
    public LoomInstrumentManager()
    {
        super( new DefaultInstrumentManager() );
    }
}
