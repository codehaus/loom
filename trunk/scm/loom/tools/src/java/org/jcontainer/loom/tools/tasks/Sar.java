/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.tools.tasks;

import org.jcontainer.loom.tools.ant.SarTask;
import org.apache.tools.ant.BuildException;

/**
 * Creates a Sar archive.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @author <a href="mailto:stefan.bodewig@epost.de">Stefan Bodewig</a>
 * @deprecated Use SarTask instead
 */
public class Sar
    extends SarTask
{
    public void execute()
        throws BuildException
    {
        log( "Sar task has been replaced with task defined by " + SarTask.class.getName() );
        super.execute();
    }
}
