/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.components.embeddor;

import java.util.Observable;

/**
 * @author Peter Donald
 * @version $Revision: 1.2 $ $Date: 2003-11-29 13:44:16 $
 */
class EmbeddorObservable
    extends Observable
{
    public void change()
    {
        super.setChanged();
    }
}
