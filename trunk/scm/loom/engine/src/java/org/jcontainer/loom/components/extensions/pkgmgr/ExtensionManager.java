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
package org.jcontainer.loom.components.extensions.pkgmgr;

import org.apache.avalon.excalibur.extension.Extension;

/**
 * <p>Interface used to store a collection of "Optional Packages"
 * (formerly known as "Standard Extensions"). It is assumed that each
 * "Optional Package" is represented by a single file on the file system.</p>
 *
 * <p>This repository is responsible for storing the local repository of
 * packages. The method used to locate packages on local filesystem
 * and install packages is not specified.</p>
 *
 * <p>For more information about optional packages, see the document
 * <em>Optional Package Versioning</em> in the documentation bundle for your
 * Java2 Standard Edition package, in file
 * <code>guide/extensions/versioning.html</code></p>.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003-06-29 04:41:32 $
 */
public interface ExtensionManager
{
    String ROLE = ExtensionManager.class.getName();

    /**
     * Return all the {@link OptionalPackage}s that satisfy specified
     * {@link Extension}. The array must be sorted with the packages that
     * "best" satisfy the Extension earlier in the array. Note that the
     * definition of "best" is implementation dependent.
     *
     * @param extension Description of the extension that needs to be provided by
     *                  optional packages
     * @return an array of optional packages that satisfy extension and
     *         the extensions dependencies
     * @see OptionalPackage
     * @see Extension
     */
    OptionalPackage[] getOptionalPackages( Extension extension );
}
