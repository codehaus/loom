/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.codehaus.spice.extension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.jar.Manifest;
import junit.framework.TestCase;

/**
 * TestCases for Extension.
 *
 * @author Peter Donald
 * @version $Revision: 1.3 $ $Date: 2003-12-02 08:09:24 $
 */
public class ExtensionTestCase
    extends TestCase
{
    public void testAvailable()
        throws Exception
    {
        final String manifestString =
            "Manifest-Version: 1.0\n" +
            "Extension-Name: spice-configkit\n" +
            "Specification-Version: 1.0\n" +
            "Specification-Vendor: Spice Group\n" +
            "Implementation-Vendor-Id: org.realityforge.spice\n" +
            "Implementation-Vendor: Spice Group\n" +
            "Implementation-Version: 2.8Alpha\n";
        final Manifest manifest = getManifest( manifestString );

        final Extension[] required = Extension.getRequired( manifest );
        assertEquals( "required Count", 0, required.length );

        final Extension[] available = Extension.getAvailable( manifest );

        assertEquals( "Available Count", 1, available.length );
        assertEquals( "Available Name", "spice-configkit",
                      available[ 0 ].getExtensionName() );
        assertEquals( "Available SpecVendor", "Spice Group",
                      available[ 0 ].getSpecificationVendor() );
        assertEquals( "Available SpecVersion", "1.0",
                      available[ 0 ].getSpecificationVersion().toString() );
        assertEquals( "Available URL",
                      null,
                      available[ 0 ].getImplementationURL() );
        assertEquals( "Available ImpVendor", "Spice Group",
                      available[ 0 ].getImplementationVendor() );
        assertEquals( "Available ImpVendorId", "org.realityforge.spice",
                      available[ 0 ].getImplementationVendorID() );
        assertEquals( "Available ImpVersion", "2.8Alpha",
                      available[ 0 ].getImplementationVersion().toString() );
    }

    public void testRequired()
        throws Exception
    {
        final String manifestString =
            "Manifest-Version: 1.0\n" +
            "Extension-List: required1\n" +
            "required1-Extension-Name: spice.required1\n" +
            "required1-Specification-Version: 1.0\n" +
            "required1-Implementation-Version: 1.0.2\n" +
            "required1-Implementation-Vendor-Id: org.realityforge.spice\n" +
            "required1-Implementation-URL: http://spice.realityforge.org/required1.jar\n";
        final Manifest manifest = getManifest( manifestString );

        final Extension[] available = Extension.getAvailable( manifest );
        assertEquals( "available Count", 0, available.length );

        final Extension[] required = Extension.getRequired( manifest );

        assertEquals( "required Count", 1, required.length );
        assertEquals( "required Name", "spice.required1",
                      required[ 0 ].getExtensionName() );
        assertEquals( "required SpecVendor", null,
                      required[ 0 ].getSpecificationVendor() );
        assertEquals( "required SpecVersion", "1.0",
                      required[ 0 ].getSpecificationVersion().toString() );
        assertEquals( "required URL", "http://spice.realityforge.org/required1.jar",
                      required[ 0 ].getImplementationURL() );
        assertEquals( "required ImpVendor", null,
                      required[ 0 ].getImplementationVendor() );
        assertEquals( "required ImpVendorId", "org.realityforge.spice",
                      required[ 0 ].getImplementationVendorID() );
        assertEquals( "required ImpVersion", "1.0.2",
                      required[ 0 ].getImplementationVersion().toString() );
    }

    public void testMultiRequired()
        throws Exception
    {
        final String manifestString =
            "Manifest-Version: 1.0\n" +
            "Extension-List: required1 required2\n" +
            "required1-Extension-Name: spice.required1\n" +
            "required1-Specification-Version: 1.0\n" +
            "required1-Implementation-Version: 1.0.2\n" +
            "required1-Implementation-Vendor-Id: org.realityforge.spice\n" +
            "required1-Implementation-URL: http://spice.realityforge.org/required1.jar\n" +
            "required2-Extension-Name: spice.required2\n" +
            "required2-Specification-Version: 1.0\n" +
            "required2-Implementation-Version: 1.0.2\n" +
            "required2-Implementation-Vendor-Id: org.realityforge.spice\n" +
            "required2-Implementation-URL: http://spice.realityforge.org/required2.jar\n";
        final Manifest manifest = getManifest( manifestString );

        final Extension[] available = Extension.getAvailable( manifest );
        assertEquals( "available Count", 0, available.length );

        final Extension[] required = Extension.getRequired( manifest );
        assertEquals( "required Count", 2, required.length );
        assertEquals( "required Name", "spice.required1",
                      required[ 0 ].getExtensionName() );
        assertEquals( "required SpecVendor", null,
                      required[ 0 ].getSpecificationVendor() );
        assertEquals( "required SpecVersion", "1.0",
                      required[ 0 ].getSpecificationVersion().toString() );
        assertEquals( "required URL", "http://spice.realityforge.org/required1.jar",
                      required[ 0 ].getImplementationURL() );
        assertEquals( "required ImpVendor", null,
                      required[ 0 ].getImplementationVendor() );
        assertEquals( "required ImpVendorId", "org.realityforge.spice",
                      required[ 0 ].getImplementationVendorID() );
        assertEquals( "required ImpVersion", "1.0.2",
                      required[ 0 ].getImplementationVersion().toString() );

        assertEquals( "required Name", "spice.required2",
                      required[ 1 ].getExtensionName() );
        assertEquals( "required SpecVendor", null,
                      required[ 1 ].getSpecificationVendor() );
        assertEquals( "required SpecVersion", "1.0",
                      required[ 1 ].getSpecificationVersion().toString() );
        assertEquals( "required URL", "http://spice.realityforge.org/required2.jar",
                      required[ 1 ].getImplementationURL() );
        assertEquals( "required ImpVendor", null,
                      required[ 1 ].getImplementationVendor() );
        assertEquals( "required ImpVendorId", "org.realityforge.spice",
                      required[ 1 ].getImplementationVendorID() );
        assertEquals( "required ImpVersion", "1.0.2",
                      required[ 1 ].getImplementationVersion().toString() );
    }

    public void testAvailableAndRequired()
        throws Exception
    {
        final String manifestString =
            "Manifest-Version: 1.0\n" +
            "Extension-Name: spice-configkit\n" +
            "Specification-Version: 1.0\n" +
            "Specification-Vendor: Spice Group\n" +
            "Implementation-Vendor-Id: org.realityforge.spice\n" +
            "Implementation-Vendor: Spice Group\n" +
            "Implementation-Version: 2.8Alpha\n" +
            "Extension-List: required1\n" +
            "required1-Extension-Name: spice.required1\n" +
            "required1-Specification-Version: 1.0\n" +
            "required1-Implementation-Version: 1.0.2\n" +
            "required1-Implementation-Vendor-Id: org.realityforge.spice\n" +
            "required1-Implementation-URL: http://spice.realityforge.org/required1.jar\n";
        final Manifest manifest = getManifest( manifestString );

        final Extension[] available = Extension.getAvailable( manifest );
        assertEquals( "Available Count", 1, available.length );
        assertEquals( "Available Name", "spice-configkit",
                      available[ 0 ].getExtensionName() );
        assertEquals( "Available SpecVendor", "Spice Group",
                      available[ 0 ].getSpecificationVendor() );
        assertEquals( "Available SpecVersion", "1.0",
                      available[ 0 ].getSpecificationVersion().toString() );
        assertEquals( "Available URL",
                      null,
                      available[ 0 ].getImplementationURL() );
        assertEquals( "Available ImpVendor", "Spice Group",
                      available[ 0 ].getImplementationVendor() );
        assertEquals( "Available ImpVendorId", "org.realityforge.spice",
                      available[ 0 ].getImplementationVendorID() );
        assertEquals( "Available ImpVersion", "2.8Alpha",
                      available[ 0 ].getImplementationVersion().toString() );

        final Extension[] required = Extension.getRequired( manifest );
        assertEquals( "required Count", 1, required.length );
        assertEquals( "required Name", "spice.required1",
                      required[ 0 ].getExtensionName() );
        assertEquals( "required SpecVendor", null,
                      required[ 0 ].getSpecificationVendor() );
        assertEquals( "required SpecVersion", "1.0",
                      required[ 0 ].getSpecificationVersion().toString() );
        assertEquals( "required URL", "http://spice.realityforge.org/required1.jar",
                      required[ 0 ].getImplementationURL() );
        assertEquals( "required ImpVendor", null,
                      required[ 0 ].getImplementationVendor() );
        assertEquals( "required ImpVendorId", "org.realityforge.spice",
                      required[ 0 ].getImplementationVendorID() );
        assertEquals( "required ImpVersion", "1.0.2",
                      required[ 0 ].getImplementationVersion().toString() );
    }

    public void testRequiredWithSpacesAfterAttributes()
        throws Exception
    {
        final String manifestString =
            "Manifest-Version: 1.0 \n" +
            "Extension-List: required1 \n" +
            "required1-Extension-Name: spice.required1 \n" +
            "required1-Specification-Version: 1.0 \n" +
            "required1-Implementation-Version: 1.0.2 \n" +
            "required1-Implementation-Vendor-Id: org.realityforge.spice \n" +
            "required1-Implementation-URL: http://spice.realityforge.org/required1.jar \n";
        final Manifest manifest = getManifest( manifestString );

        final Extension[] available = Extension.getAvailable( manifest );
        assertEquals( "available Count", 0, available.length );

        final Extension[] required = Extension.getRequired( manifest );

        assertEquals( "required Count", 1, required.length );
        assertEquals( "required Name", "spice.required1",
                      required[ 0 ].getExtensionName() );
        assertEquals( "required SpecVendor", null,
                      required[ 0 ].getSpecificationVendor() );
        assertEquals( "required SpecVersion", "1.0",
                      required[ 0 ].getSpecificationVersion().toString() );
        assertEquals( "required URL", "http://spice.realityforge.org/required1.jar",
                      required[ 0 ].getImplementationURL() );
        assertEquals( "required ImpVendor", null,
                      required[ 0 ].getImplementationVendor() );
        assertEquals( "required ImpVendorId", "org.realityforge.spice",
                      required[ 0 ].getImplementationVendorID() );
        assertEquals( "required ImpVersion", "1.0.2",
                      required[ 0 ].getImplementationVersion().toString() );
    }

    public void testAvailableWithSpacesAfterAttributes()
        throws Exception
    {
        final String manifestString =
            "Manifest-Version: 1.0\n" +
            "Extension-Name: spice-configkit \n" +
            "Specification-Version: 1.0 \n" +
            "Specification-Vendor: Spice Group \n" +
            "Implementation-Vendor-Id: org.realityforge.spice \n" +
            "Implementation-Vendor: Spice Group \n" +
            "Implementation-Version: 2.8Alpha \n";
        final Manifest manifest = getManifest( manifestString );

        final Extension[] required = Extension.getRequired( manifest );
        assertEquals( "required Count", 0, required.length );

        final Extension[] available = Extension.getAvailable( manifest );

        assertEquals( "Available Count", 1, available.length );
        assertEquals( "Available Name", "spice-configkit",
                      available[ 0 ].getExtensionName() );
        assertEquals( "Available SpecVendor", "Spice Group",
                      available[ 0 ].getSpecificationVendor() );
        assertEquals( "Available SpecVersion", "1.0",
                      available[ 0 ].getSpecificationVersion().toString() );
        assertEquals( "Available URL",
                      null,
                      available[ 0 ].getImplementationURL() );
        assertEquals( "Available ImpVendor", "Spice Group",
                      available[ 0 ].getImplementationVendor() );
        assertEquals( "Available ImpVendorId", "org.realityforge.spice",
                      available[ 0 ].getImplementationVendorID() );
        assertEquals( "Available ImpVersion", "2.8Alpha",
                      available[ 0 ].getImplementationVersion().toString() );
    }

    public void testCompatabilityRezultsIncompatability()
        throws Exception
    {
        final Extension extension1 =
            new Extension( "baz", null, null, null, null, null, null );
        final Extension extension2 =
            new Extension( "foo", null, null, null, null, null, null );
        assertEquals( Extension.INCOMPATIBLE,
                      extension1.getCompatibilityWith( extension2 ) );
    }

    public void testCompatabilityRezultsNeedSpecUpgrade()
        throws Exception
    {
        final Extension extension1 =
            new Extension( "baz", "1.0", null, null, null, null, null );
        final Extension extension2 =
            new Extension( "baz", "1.1", null, null, null, null, null );
        assertEquals( Extension.REQUIRE_SPECIFICATION_UPGRADE,
                      extension1.getCompatibilityWith( extension2 ) );
    }

    public void testCompatabilityRezultsCompatLowerVersion()
        throws Exception
    {
        final Extension extension1 =
            new Extension( "baz", "1.0", null, null, null, null, null );
        final Extension extension2 =
            new Extension( "baz", "0.9", null, null, null, null, null );
        assertEquals( Extension.COMPATIBLE,
                      extension1.getCompatibilityWith( extension2 ) );
    }

    public void testCompatabilityRezultsCompatEqVersion()
        throws Exception
    {
        final Extension extension1 =
            new Extension( "baz", "1.0", null, null, null, null, null );
        final Extension extension2 =
            new Extension( "baz", "1.0", null, null, null, null, null );
        assertEquals( Extension.COMPATIBLE,
                      extension1.getCompatibilityWith( extension2 ) );
    }

    public void testCompatabilityRezultsInCompatWIthNonNullSpecVersion()
        throws Exception
    {
        final Extension extension1 =
            new Extension( "baz", null, null, null, null, null, null );
        final Extension extension2 =
            new Extension( "baz", "1.0", null, null, null, null, null );
        assertEquals( Extension.REQUIRE_SPECIFICATION_UPGRADE,
                      extension1.getCompatibilityWith( extension2 ) );
    }

    public void testCompatabilityRezultsInCompatWIthNullSpecVersion()
        throws Exception
    {
        final Extension extension1 =
            new Extension( "baz", "1.0", null, null, null, null, null );
        final Extension extension2 =
            new Extension( "baz", null, null, null, null, null, null );
        assertEquals( Extension.COMPATIBLE,
                      extension1.getCompatibilityWith( extension2 ) );
    }

    public void testCompatabilityRezultsInCompatForVendorID()
        throws Exception
    {
        final Extension extension1 =
            new Extension( "baz", null, null, null, null, "Bob", null );
        final Extension extension2 =
            new Extension( "baz", null, null, null, null, "Bob", null );
        assertEquals( Extension.COMPATIBLE,
                      extension1.getCompatibilityWith( extension2 ) );
    }

    public void testCompatabilityRezultsInCompatForNullVendorID()
        throws Exception
    {
        final Extension extension1 =
            new Extension( "baz", null, null, null, null, null, null );
        final Extension extension2 =
            new Extension( "baz", null, null, null, null, "Bob", null );
        assertEquals( Extension.REQUIRE_VENDOR_SWITCH,
                      extension1.getCompatibilityWith( extension2 ) );
    }

    public void testCompatabilityRezultsInIncompatForNullVendorID()
        throws Exception
    {
        final Extension extension1 =
            new Extension( "baz", null, null, null, null, null, null );
        final Extension extension2 =
            new Extension( "baz", null, null, null, null, "Bob", null );
        assertEquals( Extension.REQUIRE_VENDOR_SWITCH,
                      extension1.getCompatibilityWith( extension2 ) );
    }

    private Manifest getManifest( final String manifestString )
        throws IOException
    {
        final ByteArrayInputStream stream =
            new ByteArrayInputStream( manifestString.getBytes() );
        return new Manifest( stream );
    }
}