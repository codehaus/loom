/* ====================================================================
 * Loom Software License, version 1.1
 *
 * Copyright (c) 2003-2005, Loom Group. All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. Neither the name of the Loom Group nor the name "Loom" nor
 *    the names of its contributors may be used to endorse or promote
 *    products derived from this software without specific prior
 *    written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * ====================================================================
 *
 * Loom includes code from the Apache Software Foundation
 *
 * ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1997-2003 The Apache Software Foundation. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *    "This product includes software developed by the
 *    Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software
 *    itself, if and wherever such third-party acknowledgments
 *    normally appear.
 *
 * 4. The names "Jakarta", "Avalon", and "Apache Software Foundation"
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */
package org.codehaus.loom.components.extension;

import java.io.File;
import junit.framework.TestCase;

import org.codehaus.loom.components.extensions.pkgmgr.ExtensionManager;
import org.codehaus.loom.components.extensions.pkgmgr.OptionalPackage;
import org.codehaus.loom.components.extensions.pkgmgr.impl.DelegatingExtensionManager;
import org.codehaus.loom.extension.Extension;

/**
 * A basic test case for comparator.
 *
 * @author Peter Donald
 * @version $Revision: 1.4 $ $Date: 2005-02-22 08:57:37 $
 */
public class ComparatorTestCase
    extends TestCase
{
    private static final String NAME = "Extension1";
    private static final String VENDOR1 = "Vendor1";
    private static final String VENDOR2 = "Vendor2";

    public ComparatorTestCase( final String name )
    {
        super( name );
    }

    public void testAllNull()
    {
        runCompareTest( null, null, null, null, VENDOR1, VENDOR2 );
    }

    public void testSpecNonNullV1()
    {
        runCompareTest( "1.0", null, "1.1", null, VENDOR2, VENDOR1 );
    }

    public void testSpecNonNullV2()
    {
        runCompareTest( "1.1", null, "1.0", null, VENDOR1, VENDOR2 );
    }

    public void testSpecNonNullV3()
    {
        runCompareTest( "1.1", null, "1.1", null, VENDOR1, VENDOR2 );
    }

    public void testSpec1Null()
    {
        runCompareTest( null, null, "1.1", null, VENDOR2, VENDOR1 );
    }

    public void testSpec2Null()
    {
        runCompareTest( "1.1", null, null, null, VENDOR1, VENDOR2 );
    }

    public void testImplNull()
    {
        runCompareTest( "1.0", null, "1.0", null, VENDOR1, VENDOR2 );
    }

    public void testImplNonNullV1()
    {
        runCompareTest( "1.0", "1.1", "1.0", "1.0", VENDOR1, VENDOR2 );
    }

    public void testImplNonNullV2()
    {
        runCompareTest( "1.0", "1.0", "1.0", "1.1", VENDOR2, VENDOR1 );
    }

    public void testImplNonNullV3()
    {
        runCompareTest( "1.0", "1.1", "1.0", "1.1", VENDOR1, VENDOR2 );
    }

    public void testImpl1Null()
    {
        runCompareTest( "1.0", null, "1.0", "1.1", VENDOR2, VENDOR1 );
    }

    public void testImpl2Null()
    {
        runCompareTest( "1.0", "1.1", "1.0", null, VENDOR1, VENDOR2 );
    }

    private void runCompareTest( final String specVersion1,
                                 final String implVersion1,
                                 final String specVersion2,
                                 final String implVersion2,
                                 final String vendor1,
                                 final String vendor2 )
    {
        final ExtensionManager manager =
            createExtensionManager( specVersion1,
                                    implVersion1,
                                    specVersion2,
                                    implVersion2 );
        final OptionalPackage[] pkgs = getOptionalPackages( manager );

        assertEquals( "pkgs.length", 2, pkgs.length );

        final Extension extension1 = pkgs[ 0 ].getAvailableExtensions()[ 0 ];
        final Extension extension2 = pkgs[ 1 ].getAvailableExtensions()[ 0 ];
        assertEquals( "pkgs[0].vendor",
                      vendor1,
                      extension1.getImplementationVendor() );
        assertEquals( "pkgs[1].vendor",
                      vendor2,
                      extension2.getImplementationVendor() );
    }

    private OptionalPackage[] getOptionalPackages(
        final ExtensionManager manager )
    {
        return manager.getOptionalPackages(
            new Extension( NAME, null, null, null, null, null, null ) );
    }

    private ExtensionManager createExtensionManager( final String specVersion1,
                                                     final String implVersion1,
                                                     final String specVersion2,
                                                     final String implVersion2 )
    {
        final OptionalPackage optionalPackage1 =
            createPackage( VENDOR1, specVersion1, implVersion1 );
        final OptionalPackage optionalPackage2 =
            createPackage( VENDOR2, specVersion2, implVersion2 );
        final OptionalPackage[] pkgs =
            new OptionalPackage[]{optionalPackage1, optionalPackage2};
        return createExtensionManager( pkgs );
    }

    private OptionalPackage createPackage( final String vendor,
                                           final String specVersion,
                                           final String implVersion )
    {
        final Extension extension = new Extension( NAME,
                                                   specVersion, null,
                                                   implVersion, vendor, null,
                                                   null );
        final File file = new File( "." );
        final Extension[] available = new Extension[]{extension};
        final Extension[] required = new Extension[ 0 ];
        return new OptionalPackage( file, available, required );
    }

    private ExtensionManager createExtensionManager(
        final OptionalPackage[] packages )
    {
        final TestExtensionManager manager =
            new TestExtensionManager( packages );
        return new DelegatingExtensionManager( new ExtensionManager[]{manager} );
    }
}
