/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.realityforge.extension;

import junit.framework.TestCase;
import org.realityforge.extension.DeweyDecimal;

/**
 * TestCases for DeweyDecimal.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003-07-28 13:36:15 $
 */
public class DeweyDecimalTestCase
    extends TestCase
{
    private static final String DD1 = "1";
    private static final String DD2 = "1.0.1";
    private static final String DD3 = "1.0.2";
    private static final String DD4 = "0.9.0.2";
    private static final String DD5 = "2.0.9";

    private static final String DD6 = "2..9";
    private static final String DD7 = ".9";
    private static final String DD8 = "9.";

    public DeweyDecimalTestCase( String name )
    {
        super( name );
    }

    public void testParse()
        throws Exception
    {
        final DeweyDecimal dd1 = new DeweyDecimal( DD1 );
        final DeweyDecimal dd2 = new DeweyDecimal( DD2 );
        final DeweyDecimal dd3 = new DeweyDecimal( DD3 );
        final DeweyDecimal dd4 = new DeweyDecimal( DD4 );
        final DeweyDecimal dd5 = new DeweyDecimal( DD5 );

        assertEquals( "DeweyDecimal 1", DD1, dd1.toString() );
        assertEquals( "DeweyDecimal 2", DD2, dd2.toString() );
        assertEquals( "DeweyDecimal 3", DD3, dd3.toString() );
        assertEquals( "DeweyDecimal 4", DD4, dd4.toString() );
        assertEquals( "DeweyDecimal 5", DD5, dd5.toString() );
    }

    public void testMalParse()
        throws Exception
    {
        try
        {
            new DeweyDecimal( DD6 );
            fail( "DeweyDecimal 6 parsed!" );
        }
        catch( final NumberFormatException nfe )
        {
        }

        try
        {
            new DeweyDecimal( DD7 );
            fail( "DeweyDecimal 7 parsed!" );
        }
        catch( final NumberFormatException nfe )
        {
        }

        try
        {
            new DeweyDecimal( DD8 );
            fail( "DeweyDecimal 8 parsed!" );
        }
        catch( final NumberFormatException nfe )
        {
        }
    }

    public void testGreaterThan()
        throws Exception
    {
        final DeweyDecimal dd1 = new DeweyDecimal( DD1 );
        final DeweyDecimal dd2 = new DeweyDecimal( DD2 );
        final DeweyDecimal dd3 = new DeweyDecimal( DD3 );
        final DeweyDecimal dd4 = new DeweyDecimal( DD4 );
        final DeweyDecimal dd5 = new DeweyDecimal( DD5 );

        assertTrue( "Bad: " + DD1 + " > " + DD1, !dd1.isGreaterThan( dd1 ) );
        assertTrue( "Bad: " + DD1 + " > " + DD2, !dd1.isGreaterThan( dd2 ) );
        assertTrue( "Bad: " + DD1 + " > " + DD3, !dd1.isGreaterThan( dd3 ) );
        assertTrue( "Bad: " + DD1 + " < " + DD4, dd1.isGreaterThan( dd4 ) );
        assertTrue( "Bad: " + DD1 + " > " + DD5, !dd1.isGreaterThan( dd5 ) );

        assertTrue( "Bad: " + DD2 + " < " + DD1, dd2.isGreaterThan( dd1 ) );
        assertTrue( "Bad: " + DD2 + " > " + DD2, !dd2.isGreaterThan( dd2 ) );
        assertTrue( "Bad: " + DD2 + " > " + DD3, !dd2.isGreaterThan( dd3 ) );
        assertTrue( "Bad: " + DD2 + " < " + DD4, dd2.isGreaterThan( dd4 ) );
        assertTrue( "Bad: " + DD2 + " > " + DD5, !dd2.isGreaterThan( dd5 ) );

        assertTrue( "Bad: " + DD3 + " < " + DD1, dd3.isGreaterThan( dd1 ) );
        assertTrue( "Bad: " + DD3 + " < " + DD2, dd3.isGreaterThan( dd2 ) );
        assertTrue( "Bad: " + DD3 + " > " + DD3, !dd3.isGreaterThan( dd3 ) );
        assertTrue( "Bad: " + DD3 + " < " + DD4, dd3.isGreaterThan( dd4 ) );
        assertTrue( "Bad: " + DD3 + " > " + DD5, !dd3.isGreaterThan( dd5 ) );

        assertTrue( "Bad: " + DD4 + " > " + DD1, !dd4.isGreaterThan( dd1 ) );
        assertTrue( "Bad: " + DD4 + " > " + DD2, !dd4.isGreaterThan( dd2 ) );
        assertTrue( "Bad: " + DD4 + " > " + DD3, !dd4.isGreaterThan( dd3 ) );
        assertTrue( "Bad: " + DD4 + " > " + DD4, !dd4.isGreaterThan( dd4 ) );
        assertTrue( "Bad: " + DD4 + " > " + DD5, !dd4.isGreaterThan( dd5 ) );

        assertTrue( "Bad: " + DD5 + " < " + DD1, dd5.isGreaterThan( dd1 ) );
        assertTrue( "Bad: " + DD5 + " < " + DD2, dd5.isGreaterThan( dd2 ) );
        assertTrue( "Bad: " + DD5 + " < " + DD3, dd5.isGreaterThan( dd3 ) );
        assertTrue( "Bad: " + DD5 + " < " + DD4, dd5.isGreaterThan( dd4 ) );
        assertTrue( "Bad: " + DD5 + " > " + DD5, !dd5.isGreaterThan( dd5 ) );
    }

    public void testGreaterThanOrEqual()
        throws Exception
    {
        final DeweyDecimal dd1 = new DeweyDecimal( DD1 );
        final DeweyDecimal dd2 = new DeweyDecimal( DD2 );
        final DeweyDecimal dd3 = new DeweyDecimal( DD3 );
        final DeweyDecimal dd4 = new DeweyDecimal( DD4 );
        final DeweyDecimal dd5 = new DeweyDecimal( DD5 );

        assertTrue( "Bad: " + DD1 + " < " + DD1, dd1.isGreaterThanOrEqual( dd1 ) );
        assertTrue( "Bad: " + DD1 + " > " + DD2, !dd1.isGreaterThanOrEqual( dd2 ) );
        assertTrue( "Bad: " + DD1 + " > " + DD3, !dd1.isGreaterThanOrEqual( dd3 ) );
        assertTrue( "Bad: " + DD1 + " < " + DD4, dd1.isGreaterThanOrEqual( dd4 ) );
        assertTrue( "Bad: " + DD1 + " > " + DD5, !dd1.isGreaterThanOrEqual( dd5 ) );

        assertTrue( "Bad: " + DD2 + " < " + DD1, dd2.isGreaterThanOrEqual( dd1 ) );
        assertTrue( "Bad: " + DD2 + " < " + DD2, dd2.isGreaterThanOrEqual( dd2 ) );
        assertTrue( "Bad: " + DD2 + " > " + DD3, !dd2.isGreaterThanOrEqual( dd3 ) );
        assertTrue( "Bad: " + DD2 + " < " + DD4, dd2.isGreaterThanOrEqual( dd4 ) );
        assertTrue( "Bad: " + DD2 + " > " + DD5, !dd2.isGreaterThanOrEqual( dd5 ) );

        assertTrue( "Bad: " + DD3 + " < " + DD1, dd3.isGreaterThanOrEqual( dd1 ) );
        assertTrue( "Bad: " + DD3 + " < " + DD2, dd3.isGreaterThanOrEqual( dd2 ) );
        assertTrue( "Bad: " + DD3 + " < " + DD3, dd3.isGreaterThanOrEqual( dd3 ) );
        assertTrue( "Bad: " + DD3 + " < " + DD4, dd3.isGreaterThanOrEqual( dd4 ) );
        assertTrue( "Bad: " + DD3 + " > " + DD5, !dd3.isGreaterThanOrEqual( dd5 ) );

        assertTrue( "Bad: " + DD4 + " > " + DD1, !dd4.isGreaterThanOrEqual( dd1 ) );
        assertTrue( "Bad: " + DD4 + " > " + DD2, !dd4.isGreaterThanOrEqual( dd2 ) );
        assertTrue( "Bad: " + DD4 + " > " + DD3, !dd4.isGreaterThanOrEqual( dd3 ) );
        assertTrue( "Bad: " + DD4 + " < " + DD4, dd4.isGreaterThanOrEqual( dd4 ) );
        assertTrue( "Bad: " + DD4 + " > " + DD5, !dd4.isGreaterThanOrEqual( dd5 ) );

        assertTrue( "Bad: " + DD5 + " < " + DD1, dd5.isGreaterThanOrEqual( dd1 ) );
        assertTrue( "Bad: " + DD5 + " < " + DD2, dd5.isGreaterThanOrEqual( dd2 ) );
        assertTrue( "Bad: " + DD5 + " < " + DD3, dd5.isGreaterThanOrEqual( dd3 ) );
        assertTrue( "Bad: " + DD5 + " < " + DD4, dd5.isGreaterThanOrEqual( dd4 ) );
        assertTrue( "Bad: " + DD5 + " < " + DD5, dd5.isGreaterThanOrEqual( dd5 ) );
    }

    public void testLessThan()
        throws Exception
    {
        final DeweyDecimal dd1 = new DeweyDecimal( DD1 );
        final DeweyDecimal dd2 = new DeweyDecimal( DD2 );
        final DeweyDecimal dd3 = new DeweyDecimal( DD3 );
        final DeweyDecimal dd4 = new DeweyDecimal( DD4 );
        final DeweyDecimal dd5 = new DeweyDecimal( DD5 );

        assertTrue( "Bad: " + DD1 + " <= " + DD1, !dd1.isLessThan( dd1 ) );
        assertTrue( "Bad: " + DD1 + " <= " + DD2, dd1.isLessThan( dd2 ) );
        assertTrue( "Bad: " + DD1 + " <= " + DD3, dd1.isLessThan( dd3 ) );
        assertTrue( "Bad: " + DD1 + " >= " + DD4, !dd1.isLessThan( dd4 ) );
        assertTrue( "Bad: " + DD1 + " <= " + DD5, dd1.isLessThan( dd5 ) );

        assertTrue( "Bad: " + DD2 + " >= " + DD1, !dd2.isLessThan( dd1 ) );
        assertTrue( "Bad: " + DD2 + " <= " + DD2, !dd2.isLessThan( dd2 ) );
        assertTrue( "Bad: " + DD2 + " <= " + DD3, dd2.isLessThan( dd3 ) );
        assertTrue( "Bad: " + DD2 + " >= " + DD4, !dd2.isLessThan( dd4 ) );
        assertTrue( "Bad: " + DD2 + " <= " + DD5, dd2.isLessThan( dd5 ) );

        assertTrue( "Bad: " + DD3 + " >= " + DD1, !dd3.isLessThan( dd1 ) );
        assertTrue( "Bad: " + DD3 + " >= " + DD2, !dd3.isLessThan( dd2 ) );
        assertTrue( "Bad: " + DD3 + " <= " + DD3, !dd3.isLessThan( dd3 ) );
        assertTrue( "Bad: " + DD3 + " >= " + DD4, !dd3.isLessThan( dd4 ) );
        assertTrue( "Bad: " + DD3 + " <= " + DD5, dd3.isLessThan( dd5 ) );

        assertTrue( "Bad: " + DD4 + " <= " + DD1, dd4.isLessThan( dd1 ) );
        assertTrue( "Bad: " + DD4 + " <= " + DD2, dd4.isLessThan( dd2 ) );
        assertTrue( "Bad: " + DD4 + " <= " + DD3, dd4.isLessThan( dd3 ) );
        assertTrue( "Bad: " + DD4 + " <= " + DD4, !dd4.isLessThan( dd4 ) );
        assertTrue( "Bad: " + DD4 + " <= " + DD5, dd4.isLessThan( dd5 ) );

        assertTrue( "Bad: " + DD5 + " >= " + DD1, !dd5.isLessThan( dd1 ) );
        assertTrue( "Bad: " + DD5 + " >= " + DD2, !dd5.isLessThan( dd2 ) );
        assertTrue( "Bad: " + DD5 + " >= " + DD3, !dd5.isLessThan( dd3 ) );
        assertTrue( "Bad: " + DD5 + " >= " + DD4, !dd5.isLessThan( dd4 ) );
        assertTrue( "Bad: " + DD5 + " <= " + DD5, !dd5.isLessThan( dd5 ) );
    }

    public void testLessThanOrEqual()
        throws Exception
    {
        final DeweyDecimal dd1 = new DeweyDecimal( DD1 );
        final DeweyDecimal dd2 = new DeweyDecimal( DD2 );
        final DeweyDecimal dd3 = new DeweyDecimal( DD3 );
        final DeweyDecimal dd4 = new DeweyDecimal( DD4 );
        final DeweyDecimal dd5 = new DeweyDecimal( DD5 );

        assertTrue( "Bad: " + DD1 + " >= " + DD1, dd1.isLessThanOrEqual( dd1 ) );
        assertTrue( "Bad: " + DD1 + " <= " + DD2, dd1.isLessThanOrEqual( dd2 ) );
        assertTrue( "Bad: " + DD1 + " <= " + DD3, dd1.isLessThanOrEqual( dd3 ) );
        assertTrue( "Bad: " + DD1 + " >= " + DD4, !dd1.isLessThanOrEqual( dd4 ) );
        assertTrue( "Bad: " + DD1 + " <= " + DD5, dd1.isLessThanOrEqual( dd5 ) );

        assertTrue( "Bad: " + DD2 + " >= " + DD1, !dd2.isLessThanOrEqual( dd1 ) );
        assertTrue( "Bad: " + DD2 + " >= " + DD2, dd2.isLessThanOrEqual( dd2 ) );
        assertTrue( "Bad: " + DD2 + " <= " + DD3, dd2.isLessThanOrEqual( dd3 ) );
        assertTrue( "Bad: " + DD2 + " >= " + DD4, !dd2.isLessThanOrEqual( dd4 ) );
        assertTrue( "Bad: " + DD2 + " <= " + DD5, dd2.isLessThanOrEqual( dd5 ) );

        assertTrue( "Bad: " + DD3 + " >= " + DD1, !dd3.isLessThanOrEqual( dd1 ) );
        assertTrue( "Bad: " + DD3 + " >= " + DD2, !dd3.isLessThanOrEqual( dd2 ) );
        assertTrue( "Bad: " + DD3 + " >= " + DD3, dd3.isLessThanOrEqual( dd3 ) );
        assertTrue( "Bad: " + DD3 + " >= " + DD4, !dd3.isLessThanOrEqual( dd4 ) );
        assertTrue( "Bad: " + DD3 + " <= " + DD5, dd3.isLessThanOrEqual( dd5 ) );

        assertTrue( "Bad: " + DD4 + " <= " + DD1, dd4.isLessThanOrEqual( dd1 ) );
        assertTrue( "Bad: " + DD4 + " <= " + DD2, dd4.isLessThanOrEqual( dd2 ) );
        assertTrue( "Bad: " + DD4 + " <= " + DD3, dd4.isLessThanOrEqual( dd3 ) );
        assertTrue( "Bad: " + DD4 + " >= " + DD4, dd4.isLessThanOrEqual( dd4 ) );
        assertTrue( "Bad: " + DD4 + " <= " + DD5, dd4.isLessThanOrEqual( dd5 ) );

        assertTrue( "Bad: " + DD5 + " >= " + DD1, !dd5.isLessThanOrEqual( dd1 ) );
        assertTrue( "Bad: " + DD5 + " >= " + DD2, !dd5.isLessThanOrEqual( dd2 ) );
        assertTrue( "Bad: " + DD5 + " >= " + DD3, !dd5.isLessThanOrEqual( dd3 ) );
        assertTrue( "Bad: " + DD5 + " >= " + DD4, !dd5.isLessThanOrEqual( dd4 ) );
        assertTrue( "Bad: " + DD5 + " >= " + DD5, dd5.isLessThanOrEqual( dd5 ) );
    }
}
