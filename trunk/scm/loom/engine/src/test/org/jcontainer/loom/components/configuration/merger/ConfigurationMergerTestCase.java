/* ====================================================================
 * JContainer Software License, version 1.1
 *
 * Copyright (c) 2003, JContainer Group. All rights reserved.
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
 * 3. Neither the name of the JContainer Group nor the name "Loom" nor
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
 * JContainer Loom includes code from the Apache Software Foundation
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
package org.jcontainer.loom.components.configuration.merger;

import junit.framework.TestCase;
import org.codehaus.dna.impl.ConfigurationUtil;
import org.codehaus.dna.impl.DefaultConfiguration;

/**
 * @author Peter Royal
 */
public class ConfigurationMergerTestCase
    extends TestCase
{
    public void testAttributeOnlyMerge()
        throws Exception
    {
        final DefaultConfiguration result = new DefaultConfiguration( "a", "",
                                                                      "" );
        result.setAttribute( "a", "1" );

        final DefaultConfiguration base = new DefaultConfiguration( "a", "",
                                                                    "" );
        base.setAttribute( "a", "2" );

        final DefaultConfiguration layer =
            new DefaultConfiguration( "a", "", "" );
        layer.setAttribute( "a", "1" );

        assertTrue(
            ConfigurationUtil.equals( result,
                                      ConfigurationMerger.merge( layer, base ) ) );
        assertTrue( ConfigurationUtil.equals( layer,
                                              ConfigurationSplitter.split(
                                                  result,
                                                  base ) ) );
    }

    public void testAddChild()
        throws Exception
    {
        final DefaultConfiguration result =
            new DefaultConfiguration( "a", "", "" );
        result.addChild( new DefaultConfiguration( "kid1", "", "" ) );
        result.addChild( new DefaultConfiguration( "kid2", "", "" ) );

        final DefaultConfiguration base =
            new DefaultConfiguration( "a", "", "" );
        base.addChild( new DefaultConfiguration( "kid1", "", "" ) );

        final DefaultConfiguration layer = new DefaultConfiguration( "a", "",
                                                                     "" );
        layer.addChild( new DefaultConfiguration( "kid2", "", "" ) );
        assertTrue(
            ConfigurationUtil.equals( result,
                                      ConfigurationMerger.merge( layer, base ) ) );
        assertTrue( ConfigurationUtil.equals( layer,
                                              ConfigurationSplitter.split(
                                                  result,
                                                  base ) ) );
    }

    public void testOverrideChild()
        throws Exception
    {
        final DefaultConfiguration result = new DefaultConfiguration( "a", "",
                                                                      "" );
        final DefaultConfiguration rkid1 = new DefaultConfiguration( "kid1",
                                                                     "",
                                                                     "" );
        rkid1.setAttribute( "test", "1" );
        result.addChild( rkid1 );

        final DefaultConfiguration base = new DefaultConfiguration( "a", "",
                                                                    "" );
        final DefaultConfiguration bkid1 = new DefaultConfiguration( "kid1",
                                                                     "",
                                                                     "" );
        bkid1.setAttribute( "test", "0" );
        base.addChild( bkid1 );

        final DefaultConfiguration layer = new DefaultConfiguration( "a", "",
                                                                     "" );
        final DefaultConfiguration lkid1 = new DefaultConfiguration( "kid1",
                                                                     "",
                                                                     "" );
        lkid1.setAttribute( "test", "1" );
        layer.addChild( lkid1 );

        assertTrue( !ConfigurationUtil.equals( result,
                                               ConfigurationMerger.merge(
                                                   layer,
                                                   base ) ) );

        lkid1.setAttribute( "excalibur-configuration:merge", "true" );

        assertTrue(
            ConfigurationUtil.equals( result,
                                      ConfigurationMerger.merge( layer, base ) ) );
        assertTrue( ConfigurationUtil.equals( layer,
                                              ConfigurationSplitter.split(
                                                  result,
                                                  base ) ) );
    }
}
