/*
 * Copyright (C) The Loom Group. All rights reserved.
 *
 * This software is published under the terms of the Loom
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.codehaus.loom.components.embeddor;

import java.util.Observable;

/**
 * @author Peter Donald
 * @version $Revision: 1.2 $ $Date: 2004-05-01 12:48:34 $
 */
class EmbeddorObservable
    extends Observable
{
    public void change()
    {
        super.setChanged();
    }
}
