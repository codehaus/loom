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

import org.realityforge.extension.Extension;

/**
 * Exception indicating an extension was not found in Package Repository.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.2 $ $Date: 2003-08-02 12:26:32 $
 * @see Extension
 */
public class UnsatisfiedExtensionException
    extends Exception
{
    /**
     * The unsatisfied Extension.
     */
    private final Extension m_extension;

    /**
     * Construct the <code>UnsatisfiedPackageException</code>
     * for specified {@link Extension}.
     *
     * @param extension the extension that caused exception
     */
    public UnsatisfiedExtensionException( final Extension extension )
    {
        if( null == extension )
        {
            throw new NullPointerException( "extension" );
        }

        m_extension = extension;
    }

    /**
     * Return the unsatisfied {@link Extension} that
     * caused this exception tho be thrown.
     *
     * @return the unsatisfied Extension
     */
    public Extension getUnsatisfiedExtension()
    {
        return m_extension;
    }
}
