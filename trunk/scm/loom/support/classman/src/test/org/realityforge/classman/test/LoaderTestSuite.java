/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.realityforge.classman.test;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.realityforge.classman.reader.test.ReaderTestCase;
import org.realityforge.classman.runtime.test.JoinClassLoaderTestCase;
import org.realityforge.classman.util.test.PathMatcherTestCase;
import org.realityforge.classman.verifier.test.VerifierTestCase;

/**
 * A basic test suite that tests all the Loader package.
 */
public class LoaderTestSuite
{
    public static Test suite()
    {
        final TestSuite suite = new TestSuite( "Loader Utilities" );
        suite.addTest( new TestSuite( ReaderTestCase.class ) );
        suite.addTest( new TestSuite( VerifierTestCase.class ) );
        suite.addTest( new TestSuite( JoinClassLoaderTestCase.class ) );
        suite.addTest( new TestSuite( PathMatcherTestCase.class ) );
        return suite;
    }
}
