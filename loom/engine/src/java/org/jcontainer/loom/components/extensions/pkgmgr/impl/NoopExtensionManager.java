/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 *
 * This product includes software developed by the
 * Apache Software Foundation (http://www.apache.org/).
 */
package org.jcontainer.loom.components.extensions.pkgmgr.impl;

import org.apache.avalon.excalibur.extension.Extension;
import org.jcontainer.loom.components.extensions.pkgmgr.ExtensionManager;
import org.jcontainer.loom.components.extensions.pkgmgr.OptionalPackage;

/**
 * A Noop ExtensionManager that can't provide any extensions.
 * This is for use in certain environments (ala Servlets) that
 * require apps to be be self-contained.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003-06-29 04:41:32 $
 */
public class NoopExtensionManager
    implements ExtensionManager
{
    /**
     * Return an empty array of {@link OptionalPackage}s.
     *
     * @param extension the extension looking for
     * @see ExtensionManager#getOptionalPackages
     */
    public OptionalPackage[] getOptionalPackages( final Extension extension )
    {
        return new OptionalPackage[ 0 ];
    }
}
