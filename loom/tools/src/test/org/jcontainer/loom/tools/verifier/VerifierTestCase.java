/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.tools.verifier;

import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.phoenix.test.AbstractContainerTestCase;

/**
 *  An basic test case for the LogManager.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.3 $ $Date: 2003-06-29 00:46:24 $
 */
public class VerifierTestCase
    extends AbstractContainerTestCase
{
    public VerifierTestCase( final String name )
    {
        super( name );
    }

    public void testBasic()
        throws Exception
    {
        verify( "assembly1.xml" );
    }

    public void testComplex()
        throws Exception
    {
        verify( "assembly2.xml" );
    }

    protected void verify( final String config ) throws Exception
    {
        final org.jcontainer.loom.tools.profile.PartitionProfile sarMetaData = assembleSar( config );
        final ClassLoader classLoader = getClass().getClassLoader();
        final SarVerifier verifier = new SarVerifier();
        verifier.enableLogging( new ConsoleLogger() );
        verifier.verifySar( sarMetaData, classLoader );
    }
}
