/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.codehaus.loom.extension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.jar.Manifest;

import org.codehaus.loom.extension.Specification;

import junit.framework.TestCase;

/**
 * TestCases for Specification.
 *
 * @author Peter Donald
 * @version $Revision: 1.1 $ $Date: 2004-04-19 21:40:38 $
 */
public class SpecificationTestCase
    extends TestCase
{
    public void testSingleSpecification()
        throws Exception
    {
        final String manifest =
            "Manifest-Version: 1.0\n" +
            "\n" +
            "Name: org/realityforge/dve\n" +
            "Specification-Title: org.realityforge.dve\n" +
            "Specification-Version: 1.0.2\n" +
            "Specification-Vendor: Peter Donald\n" +
            "Implementation-Title: DVE vi OS3P\n" +
            "Implementation-Vendor: Peter Donald\n" +
            "Implementation-Version: 1.0.2Alpha\n";
        final Specification[] specifications = getSpecifications( manifest );

        assertEquals( "Count", 1, specifications.length );
        assertEquals( "Name", "org.realityforge.dve",
                      specifications[ 0 ].getSpecificationTitle() );
        assertEquals( "SpecVendor", "Peter Donald",
                      specifications[ 0 ].getSpecificationVendor() );
        assertEquals( "SpecVersion",
                      "1.0.2",
                      specifications[ 0 ].getSpecificationVersion().toString() );
        assertEquals( "ImpVendor", "Peter Donald",
                      specifications[ 0 ].getImplementationVendor() );
        assertEquals( "ImpTitle", "DVE vi OS3P",
                      specifications[ 0 ].getImplementationTitle() );
        assertEquals( "ImpVersion",
                      "1.0.2Alpha",
                      specifications[ 0 ].getImplementationVersion().toString() );
    }

    public void testSingleSpecificationWithSpacesAtEOL()
        throws Exception
    {
        final String manifest =
            "Manifest-Version: 1.0\n" +
            "\n" +
            "Name: org/realityforge/dve \n" +
            "Specification-Title: org.realityforge.dve \n" +
            "Specification-Version: 1.0.2 \n" +
            "Specification-Vendor: Peter Donald \n" +
            "Implementation-Title: DVE vi OS3P \n" +
            "Implementation-Vendor: Peter Donald \n" +
            "Implementation-Version: 1.0.2Alpha \n";
        final Specification[] specifications = getSpecifications( manifest );

        assertEquals( "Count", 1, specifications.length );
        assertEquals( "Name", "org.realityforge.dve",
                      specifications[ 0 ].getSpecificationTitle() );
        assertEquals( "SpecVendor", "Peter Donald",
                      specifications[ 0 ].getSpecificationVendor() );
        assertEquals( "SpecVersion",
                      "1.0.2",
                      specifications[ 0 ].getSpecificationVersion().toString() );
        assertEquals( "ImpVendor", "Peter Donald",
                      specifications[ 0 ].getImplementationVendor() );
        assertEquals( "ImpTitle", "DVE vi OS3P",
                      specifications[ 0 ].getImplementationTitle() );
        assertEquals( "ImpVersion",
                      "1.0.2Alpha",
                      specifications[ 0 ].getImplementationVersion().toString() );
    }

    public void testSingleSpecificationWithMissingSpecificationVersion()
        throws Exception
    {
        final String manifestString =
            "Manifest-Version: 1.0\n" +
            "\n" +
            "Name: org/realityforge/dve\n" +
            "Specification-Title: org.realityforge.dve\n" +
            "Specification-Vendor: Peter Donald\n" +
            "Implementation-Title: DVE vi OS3P\n" +
            "Implementation-Vendor: Peter Donald\n" +
            "Implementation-Version: 1.0.2Alpha\n";
        final Manifest manifest = getManifest( manifestString );
        try
        {
            Specification.getSpecifications( manifest );
        }
        catch( final Throwable t )
        {
            return;
        }
        fail( "Missing SpecificationVersion parsed" );
    }

    public void testSingleSpecificationWithMissingSpecificationVendor()
        throws Exception
    {
        final String manifestString =
            "Manifest-Version: 1.0\n" +
            "\n" +
            "Name: org/realityforge/dve\n" +
            "Specification-Title: org.realityforge.dve\n" +
            "Specification-Version: 1.0.2\n" +
            "Implementation-Title: DVE vi OS3P\n" +
            "Implementation-Vendor: Peter Donald\n" +
            "Implementation-Version: 1.0.2Alpha\n";
        final Manifest manifest = getManifest( manifestString );
        try
        {
            Specification.getSpecifications( manifest );
        }
        catch( final Throwable t )
        {
            return;
        }
        fail( "Missing SpecificationVendor parsed" );
    }

    public void testSingleSpecificationMissingImplementationTitle()
        throws Exception
    {
        final String manifestString =
            "Manifest-Version: 1.0\n" +
            "\n" +
            "Name: org/realityforge/dve\n" +
            "Specification-Title: org.realityforge.dve\n" +
            "Specification-Version: 1.0.2\n" +
            "Specification-Vendor: Peter Donald\n" +
            "Implementation-Vendor: Peter Donald\n" +
            "Implementation-Version: 1.0.2Alpha\n";
        final Manifest manifest = getManifest( manifestString );
        try
        {
            Specification.getSpecifications( manifest );
        }
        catch( final Throwable t )
        {
            return;
        }
        fail( "Missing ImplementationTitle parsed" );
    }

    public void testSingleSpecificationMissingImplementationVendor()
        throws Exception
    {
        final String manifestString =
            "Manifest-Version: 1.0\n" +
            "\n" +
            "Name: org/realityforge/dve\n" +
            "Specification-Title: org.realityforge.dve\n" +
            "Specification-Version: 1.0.2\n" +
            "Specification-Vendor: Peter Donald\n" +
            "Implementation-Title: DVE vi OS3P\n" +
            "Implementation-Version: 1.0.2Alpha\n";
        final Manifest manifest = getManifest( manifestString );
        try
        {
            Specification.getSpecifications( manifest );
        }
        catch( final Throwable t )
        {
            return;
        }
        fail( "Missing ImplementationVendor parsed" );
    }

    public void testSingleSpecificationMissingImplementationVersion()
        throws Exception
    {
        final String manifestString =
            "Manifest-Version: 1.0\n" +
            "\n" +
            "Name: org/realityforge/dve\n" +
            "Specification-Title: org.realityforge.dve\n" +
            "Specification-Version: 1.0.2\n" +
            "Specification-Vendor: Peter Donald\n" +
            "Implementation-Title: DVE vi OS3P\n" +
            "Implementation-Vendor: Peter Donald\n";
        final Manifest manifest = getManifest( manifestString );
        try
        {
            Specification.getSpecifications( manifest );
        }
        catch( final Throwable t )
        {
            return;
        }
        fail( "Missing ImplementationVersion parsed" );
    }

    public void testSingleSpecificationWithMultipleSections()
        throws Exception
    {
        final String manifestString =
            "Manifest-Version: 1.0\n" +
            "\n" +
            "Name: org/realityforge/dve\n" +
            "Specification-Title: org.realityforge.dve\n" +
            "Specification-Version: 1.0.2\n" +
            "Specification-Vendor: Peter Donald\n" +
            "Implementation-Title: DVE vi OS3P\n" +
            "Implementation-Vendor: Peter Donald\n" +
            "Implementation-Version: 1.0.2Alpha\n" +
            "\n" +
            "Name: org/realityforge/dve/input\n" +
            "Specification-Title: org.realityforge.dve\n" +
            "Specification-Version: 1.0.2\n" +
            "Specification-Vendor: Peter Donald\n" +
            "Implementation-Title: DVE vi OS3P\n" +
            "Implementation-Vendor: Peter Donald\n" +
            "Implementation-Version: 1.0.2Alpha\n" +
            "\n" +
            "Name: org/realityforge/dve/sim\n" +
            "Specification-Title: org.realityforge.dve\n" +
            "Specification-Version: 1.0.2\n" +
            "Specification-Vendor: Peter Donald\n" +
            "Implementation-Title: DVE vi OS3P\n" +
            "Implementation-Vendor: Peter Donald\n" +
            "Implementation-Version: 1.0.2Alpha\n";
        final Specification[] specifications = getSpecifications(
            manifestString );

        assertEquals( "Count", 1, specifications.length );
        final String[] sections = specifications[ 0 ].getSections();
        assertEquals( "sections.length", 3, sections.length );
        final HashSet set = new HashSet();
        set.addAll( Arrays.asList( sections ) );
        assertTrue( "sections.contains(org/realityforge/dve)",
                    set.contains( "org/realityforge/dve" ) );
        assertTrue( "sections.contains(org/realityforge/dve/input)",
                    set.contains( "org/realityforge/dve/input" ) );
        assertTrue( "sections.contains(org/realityforge/dve/sim)",
                    set.contains( "org/realityforge/dve/sim" ) );
        assertEquals( "Name", "org.realityforge.dve",
                      specifications[ 0 ].getSpecificationTitle() );
        assertEquals( "SpecVendor", "Peter Donald",
                      specifications[ 0 ].getSpecificationVendor() );
        assertEquals( "SpecVersion",
                      "1.0.2",
                      specifications[ 0 ].getSpecificationVersion().toString() );
        assertEquals( "ImpVendor", "Peter Donald",
                      specifications[ 0 ].getImplementationVendor() );
        assertEquals( "ImpTitle", "DVE vi OS3P",
                      specifications[ 0 ].getImplementationTitle() );
        assertEquals( "ImpVersion",
                      "1.0.2Alpha",
                      specifications[ 0 ].getImplementationVersion().toString() );
    }

    public void testMultipleSpecificationWithMultipleSections()
        throws Exception
    {
        final String manifestString =
            "Manifest-Version: 1.0\n" +
            "\n" +
            "Name: org/realityforge/dve\n" +
            "Specification-Title: org.realityforge.dve\n" +
            "Specification-Version: 1.0.2\n" +
            "Specification-Vendor: Peter Donald\n" +
            "Implementation-Title: DVE vi OS3P\n" +
            "Implementation-Vendor: Peter Donald\n" +
            "Implementation-Version: 1.0.2Alpha\n" +
            "\n" +
            "Name: org/realityforge/dve/input\n" +
            "Specification-Title: org.realityforge.dve\n" +
            "Specification-Version: 1.0.2\n" +
            "Specification-Vendor: Peter Donald\n" +
            "Implementation-Title: DVE vi OS3P\n" +
            "Implementation-Vendor: Peter Donald\n" +
            "Implementation-Version: 1.0.2Alpha\n" +
            "\n" +
            "Name: org/realityforge/dve/sim\n" +
            "Specification-Title: org.realityforge.dve\n" +
            "Specification-Version: 1.0.2\n" +
            "Specification-Vendor: Peter Donald\n" +
            "Implementation-Title: DVE vi OS3P\n" +
            "Implementation-Vendor: Peter Donald\n" +
            "Implementation-Version: 1.0.2Alpha\n" +
            "\n" +
            "Name: com/biz/foo\n" +
            "Specification-Title: com.biz.foo\n" +
            "Specification-Version: 1.0.2\n" +
            "Specification-Vendor: Peter Donald\n" +
            "Implementation-Title: DVE vi OS3P\n" +
            "Implementation-Vendor: Peter Donald\n" +
            "Implementation-Version: 1.0.2Alpha\n";
        final Specification[] specifications = getSpecifications(
            manifestString );

        assertEquals( "Count", 2, specifications.length );
        Specification dveSpecification;
        Specification fooSpecification;
        if( 3 == specifications[ 0 ].getSections().length )
        {
            dveSpecification = specifications[ 0 ];
            fooSpecification = specifications[ 1 ];
        }
        else
        {
            dveSpecification = specifications[ 1 ];
            fooSpecification = specifications[ 0 ];
        }

        final String[] sections = dveSpecification.getSections();
        assertEquals( "sections.length", 3, sections.length );
        assertEquals( "sections.length", 3, sections.length );
        final HashSet set = new HashSet();
        set.addAll( Arrays.asList( sections ) );
        assertTrue( "sections.contains(org/realityforge/dve)",
                    set.contains( "org/realityforge/dve" ) );
        assertTrue( "sections.contains(org/realityforge/dve/input)",
                    set.contains( "org/realityforge/dve/input" ) );
        assertTrue( "sections.contains(org/realityforge/dve/sim)",
                    set.contains( "org/realityforge/dve/sim" ) );
        assertEquals( "Name", "org.realityforge.dve",
                      dveSpecification.getSpecificationTitle() );
        assertEquals( "SpecVendor", "Peter Donald",
                      dveSpecification.getSpecificationVendor() );
        assertEquals( "SpecVersion", "1.0.2",
                      dveSpecification.getSpecificationVersion().toString() );
        assertEquals( "ImpVendor", "Peter Donald",
                      dveSpecification.getImplementationVendor() );
        assertEquals( "ImpTitle", "DVE vi OS3P",
                      dveSpecification.getImplementationTitle() );
        assertEquals( "ImpVersion", "1.0.2Alpha",
                      dveSpecification.getImplementationVersion().toString() );

        assertEquals( "sections.length",
                      1,
                      fooSpecification.getSections().length );
        assertEquals( "sections[0]",
                      "com/biz/foo",
                      fooSpecification.getSections()[ 0 ] );
        assertEquals( "Name", "com.biz.foo",
                      fooSpecification.getSpecificationTitle() );
        assertEquals( "SpecVendor", "Peter Donald",
                      fooSpecification.getSpecificationVendor() );
        assertEquals( "SpecVersion", "1.0.2",
                      fooSpecification.getSpecificationVersion().toString() );
        assertEquals( "ImpVendor", "Peter Donald",
                      fooSpecification.getImplementationVendor() );
        assertEquals( "ImpTitle", "DVE vi OS3P",
                      fooSpecification.getImplementationTitle() );
        assertEquals( "ImpVersion", "1.0.2Alpha",
                      fooSpecification.getImplementationVersion().toString() );
    }

    public void testCompatible()
        throws Exception
    {
        final String title = "org.realityforge.dve";
        final String version = "1.0.2";
        final String vendor = "Peter Donald";
        final String implTitle = "DVE vi OS3P";
        final String implVendor = "Peter Donald";
        final String implVersion = "1.0.2Alpha";

        final Specification req1 =
            new Specification( title, version, vendor,
                               implTitle, implVersion, implVendor );
        final Specification req2 =
            new Specification( title, version, vendor,
                               null, null, null );
        final Specification req3 =
            new Specification( title, "1.0.1", vendor,
                               null, null, null );
        final Specification req4 =
            new Specification( title, version, null,
                               null, null, null );
        final Specification req5 =
            new Specification( "another title", version, vendor,
                               implTitle, implVersion, implVendor );

        final Specification avail1 =
            new Specification( title, version, vendor,
                               implTitle, implVersion, implVendor );
        final Specification avail2 =
            new Specification( title, version, vendor,
                               implTitle, "another version", implVendor );
        final Specification avail3 =
            new Specification( title, version, vendor,
                               implTitle, implVersion, "another vendor" );

        assertTrue( "avail1.isCompatibleWith( req1 )",
                    avail1.isCompatibleWith( req1 ) );
        assertTrue( "avail1.isCompatibleWith( req2 )",
                    avail1.isCompatibleWith( req2 ) );
        assertTrue( "avail1.isCompatibleWith( req3 )",
                    avail1.isCompatibleWith( req3 ) );
        assertTrue( "avail1.isCompatibleWith( req4 )",
                    avail1.isCompatibleWith( req4 ) );
        assertTrue( "!avail1.isCompatibleWith( req5 )",
                    !avail1.isCompatibleWith( req5 ) );

        assertTrue( "!avail2.isCompatibleWith( req1 )",
                    !avail2.isCompatibleWith( req1 ) );
        assertTrue( "avail2.isCompatibleWith( req2 )",
                    avail2.isCompatibleWith( req2 ) );
        assertTrue( "avail2.isCompatibleWith( req3 )",
                    avail2.isCompatibleWith( req3 ) );
        assertTrue( "avail2.isCompatibleWith( req4 )",
                    avail2.isCompatibleWith( req4 ) );
        assertTrue( "!avail2.isCompatibleWith( req5 )",
                    !avail2.isCompatibleWith( req5 ) );

        assertTrue( "!avail3.isCompatibleWith( req1 )",
                    !avail3.isCompatibleWith( req1 ) );
        assertTrue( "avail3.isCompatibleWith( req2 )",
                    avail3.isCompatibleWith( req2 ) );
        assertTrue( "avail3.isCompatibleWith( req3 )",
                    avail3.isCompatibleWith( req3 ) );
        assertTrue( "avail3.isCompatibleWith( req4 )",
                    avail3.isCompatibleWith( req4 ) );
        assertTrue( "!avail3.isCompatibleWith( req5 )",
                    !avail3.isCompatibleWith( req5 ) );
    }

    private Specification[] getSpecifications( final String input )
        throws Exception
    {
        final Manifest manifest = getManifest( input );
        return Specification.getSpecifications( manifest );
    }

    private Manifest getManifest( final String manifestString )
        throws IOException
    {
        final ByteArrayInputStream stream =
            new ByteArrayInputStream( manifestString.getBytes() );
        return new Manifest( stream );
    }
}