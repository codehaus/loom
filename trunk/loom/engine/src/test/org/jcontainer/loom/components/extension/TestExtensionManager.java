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
package org.jcontainer.loom.components.extension;

import org.apache.avalon.excalibur.extension.Extension;
import org.jcontainer.loom.components.extensions.pkgmgr.ExtensionManager;
import org.jcontainer.loom.components.extensions.pkgmgr.OptionalPackage;

/**
 * a class to help test sorting of ExtensionManager.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003-07-18 01:05:02 $
 */
class TestExtensionManager
    implements ExtensionManager
{
    private final OptionalPackage[] m_optionalPackages;

    public TestExtensionManager( final OptionalPackage[] optionalPackages )
    {
        m_optionalPackages = optionalPackages;
    }

    public OptionalPackage[] getOptionalPackages( final Extension extension )
    {
        return m_optionalPackages;
    }
}
