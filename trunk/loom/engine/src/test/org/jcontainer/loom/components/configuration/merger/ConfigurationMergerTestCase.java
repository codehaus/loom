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
package org.jcontainer.loom.components.configuration.merger;

import junit.framework.TestCase;

import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.configuration.ConfigurationUtil;
import org.jcontainer.loom.components.configuration.merger.ConfigurationMerger;
import org.jcontainer.loom.components.configuration.merger.ConfigurationSplitter;

/**
 * @author <a href="mailto:proyal at apache.org">Peter Royal</a>
 */
public class ConfigurationMergerTestCase
    extends TestCase
{
    public ConfigurationMergerTestCase()
    {
        this( "Configuration merger and branching test" );
    }

    public ConfigurationMergerTestCase( String s )
    {
        super( s );
    }

    public void testAttributeOnlyMerge() throws Exception
    {
        DefaultConfiguration result = new DefaultConfiguration( "a", "b" );
        result.setAttribute( "a", "1" );

        DefaultConfiguration base = new DefaultConfiguration( "a", "b" );
        base.setAttribute( "a", "2" );

        DefaultConfiguration layer = new DefaultConfiguration( "a", "b" );
        layer.setAttribute( "a", "1" );

        assertTrue( ConfigurationUtil.equals( result, ConfigurationMerger.merge( layer, base ) ) );
        assertTrue( ConfigurationUtil.equals( layer, ConfigurationSplitter.split( result, base ) ));
    }

    public void testAddChild() throws Exception
    {
        DefaultConfiguration result = new DefaultConfiguration( "a", "b" );
        result.addChild( new DefaultConfiguration( "kid1", "b" ) );
        result.addChild( new DefaultConfiguration( "kid2", "b" ) );

        DefaultConfiguration base = new DefaultConfiguration( "a", "b" );
        base.addChild( new DefaultConfiguration( "kid1", "b" ) );

        DefaultConfiguration layer = new DefaultConfiguration( "a", "b" );
        layer.addChild( new DefaultConfiguration( "kid2", "b" ) );

        assertTrue( ConfigurationUtil.equals( result, ConfigurationMerger.merge( layer, base ) ) );
        assertTrue( ConfigurationUtil.equals( layer, ConfigurationSplitter.split( result, base ) ));
    }

    public void testOverrideChild() throws Exception
    {
        DefaultConfiguration result = new DefaultConfiguration( "a", "b" );
        DefaultConfiguration rkid1 = new DefaultConfiguration( "kid1", "b" );
        rkid1.setAttribute( "test", "1" );
        result.addChild( rkid1 );

        DefaultConfiguration base = new DefaultConfiguration( "a", "b" );
        DefaultConfiguration bkid1 = new DefaultConfiguration( "kid1", "b" );
        bkid1.setAttribute( "test", "0" );
        base.addChild( bkid1 );

        DefaultConfiguration layer = new DefaultConfiguration( "a", "b" );
        DefaultConfiguration lkid1 = new DefaultConfiguration( "kid1", "b" );
        lkid1.setAttribute( "test", "1" );
        layer.addChild( lkid1 );

        assertTrue( !ConfigurationUtil.equals( result, ConfigurationMerger.merge( layer, base ) ) );

        lkid1.setAttribute( "excalibur-configuration:merge", "true" );

        assertTrue( ConfigurationUtil.equals( result, ConfigurationMerger.merge( layer, base ) ) );
        assertTrue( ConfigurationUtil.equals( layer, ConfigurationSplitter.split( result, base ) ) );
    }
}
