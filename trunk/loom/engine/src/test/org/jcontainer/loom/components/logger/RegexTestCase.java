/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.components.logger;

import java.io.File;

import junit.framework.TestCase;

import org.apache.oro.text.perl.Perl5Util;

/**
 * A test case for regex processing
 * @author <a href="mailto:mauro.talevi at aquilonia.org">Mauro Talevi</a>
 */
public class RegexTestCase extends TestCase
{
    public RegexTestCase(final String name)
    {
        super(name);
    }
    
    public void testReplaceBackslashes(){
        testWithJDK14( "target\\data", "target/data" );
        testWithJDK14( "target\\data\\logs", "target/data/logs" );
        testWithOroPerl5( "target\\data", "target/data" );
        testWithOroPerl5( "target\\data\\logs", "target/data/logs" );
    }
    
    public void testWithJDK14( final String input, final String output ){
        final File file = new File( input );
        final String path = file.getPath();
        final String newPath = path.replaceAll("\\\\","/");
        assertEquals("JDK14 replaceAll", newPath, output );
    }

    public void testWithOroPerl5( final String input, final String output ){
        final File file = new File( input );
        final String path = file.getPath();
        final Perl5Util perl5 = new Perl5Util(); 
        final String newPath = perl5.substitute( "s/\\\\/\\//g", file.getPath() );
        assertEquals("Oro Perl5 global substitute", newPath, output );
    }
    
}