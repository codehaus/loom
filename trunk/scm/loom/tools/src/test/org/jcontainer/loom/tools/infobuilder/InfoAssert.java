/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.tools.infobuilder;

import junit.framework.Assert;
import org.jcontainer.loom.tools.info.ComponentDescriptor;
import org.jcontainer.loom.tools.info.ComponentInfo;
import org.jcontainer.loom.tools.info.ContextDescriptor;
import org.jcontainer.loom.tools.info.DependencyDescriptor;
import org.jcontainer.loom.tools.info.EntryDescriptor;
import org.jcontainer.loom.tools.info.SchemaDescriptor;
import org.jcontainer.loom.tools.info.ServiceDescriptor;
import org.realityforge.metaclass.model.Attribute;

/**
 * A set of utilities for asserting  facts about info objects.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.5 $ $Date: 2003-10-05 01:18:58 $
 */
public class InfoAssert
{
    public static void assertEqualStructure( final String message,
                                             final ComponentInfo expected,
                                             final ComponentInfo actual )
    {
        final ComponentDescriptor expectedComponent = expected.getDescriptor();
        final ComponentDescriptor actualComponent = actual.getDescriptor();
        assertEqualAttributes( message + ": Component.attribute",
                               expectedComponent.getAttributes(),
                               actualComponent.getAttributes() );

        assertEqualFeatures( message, expected, actual );
    }

    public static void assertEqualInfos( final String message,
                                         final ComponentInfo expected,
                                         final ComponentInfo actual )
    {
        final ComponentDescriptor expectedComponent = expected.getDescriptor();
        final ComponentDescriptor actualComponent = actual.getDescriptor();
        assertEqualComponents( message, expectedComponent, actualComponent );

        assertEqualFeatures( message, expected, actual );
    }

    public static void assertEqualFeatures( final String message,
                                            final ComponentInfo expected,
                                            final ComponentInfo actual )
    {
        final SchemaDescriptor expectedSchema = expected.getConfigurationSchema();
        final SchemaDescriptor actualSchema = actual.getConfigurationSchema();
        assertEqualSchema( message + "/Configuration", expectedSchema, actualSchema );

        final ContextDescriptor expectedContext = expected.getContext();
        final ContextDescriptor actualContext = actual.getContext();
        assertEqualContext( message, expectedContext, actualContext );

        final ServiceDescriptor[] expectedServices = expected.getServices();
        final ServiceDescriptor[] actualServices = actual.getServices();
        assertEqualServices( message, expectedServices, actualServices );

        final DependencyDescriptor[] expectedDeps = expected.getDependencies();
        final DependencyDescriptor[] actualDeps = actual.getDependencies();
        assertEqualDeps( message, expectedDeps, actualDeps );
    }

    private static void assertEqualSchema( final String message,
                                           final SchemaDescriptor expected,
                                           final SchemaDescriptor actual )
    {
        if( null == expected && null == actual )
        {
            return;
        }
        else if( null == expected )
        {
            Assert.fail( "Null expected but non-null actual" );
        }
        else if( null == actual )
        {
            Assert.fail( "Null actual but non-null expected" );
        }

        Assert.assertEquals( message + ": Schema.type",
                             expected.getType(),
                             actual.getType() );

        Assert.assertEquals( message + ": Schema.type",
                             expected.getType(),
                             actual.getType() );
        Assert.assertEquals( message + ": Schema.location",
                             expected.getLocation(),
                             actual.getLocation() );
    }

    public static void assertEqualDeps( final String message,
                                        final DependencyDescriptor[] expected,
                                        final DependencyDescriptor[] actual )
    {
        Assert.assertEquals( message + ": Dependencys.length", expected.length, actual.length );
        for( int i = 0; i < expected.length; i++ )
        {
            Assert.assertEquals( message + ": Dependencys[ " + i + "].service",
                                 expected[ i ].getType(),
                                 actual[ i ].getType() );
            Assert.assertEquals( message + ": Dependencys[ " + i + "].key",
                                 expected[ i ].getKey(),
                                 actual[ i ].getKey() );
            assertEqualAttributes( message + ": Dependencys[ " + i + "].attributes",
                                   expected[ i ].getAttributes(),
                                   actual[ i ].getAttributes() );
        }
    }

    public static void assertEqualServices( final String message,
                                            final ServiceDescriptor[] expected,
                                            final ServiceDescriptor[] actual )
    {
        Assert.assertEquals( message + ": Services.length", expected.length, actual.length );
        for( int i = 0; i < expected.length; i++ )
        {
            final String prefix = message + ": Services[ " + i + "]";
            final ServiceDescriptor expectedService = expected[ i ];
            final ServiceDescriptor actualService = actual[ i ];
            assertEqualService( prefix, expectedService, actualService );
        }
    }

    private static void assertEqualService( final String message,
                                            final ServiceDescriptor expected,
                                            final ServiceDescriptor actual )
    {
        Assert.assertEquals( message + ".type",
                             expected.getType(),
                             actual.getType() );
        assertEqualAttributes( message + ".attributes",
                               expected.getAttributes(),
                               actual.getAttributes() );
    }

    public static void assertEqualContext( final String message,
                                           final ContextDescriptor expected,
                                           final ContextDescriptor actual )
    {
        Assert.assertEquals( message + ": Context.type", expected.getType(), actual.getType() );
        assertEqualEntrys( message + ": Context.entrys", expected.getEntrys(), expected.getEntrys() );
        assertEqualAttributes( message + ": Context.attribute",
                               expected.getAttributes(),
                               actual.getAttributes() );
    }

    public static void assertEqualEntrys( final String message,
                                          final EntryDescriptor[] expected,
                                          final EntryDescriptor[] actual )
    {
        Assert.assertEquals( message + " Length", expected.length, actual.length );
        for( int i = 0; i < expected.length; i++ )
        {
            Assert.assertEquals( message + " [" + i + "].key",
                                 expected[ i ].getKey(), actual[ i ].getKey() );
            Assert.assertEquals( message + " [" + i + "].type",
                                 expected[ i ].getType(), actual[ i ].getType() );
            assertEqualAttributes( message + " [" + i + "].attribute",
                                   expected[ i ].getAttributes(),
                                   actual[ i ].getAttributes() );
        }
    }

    public static void assertEqualComponents( final String message,
                                              final ComponentDescriptor expected,
                                              final ComponentDescriptor actual )
    {
        Assert.assertEquals( message + ": Component.type", expected.getImplementationKey(),
                             actual.getImplementationKey() );
        assertEqualAttributes( message + ": Component.attribute",
                               expected.getAttributes(),
                               actual.getAttributes() );
    }

    public static void assertEqualParameters( final String message,
                                              final Attribute expected,
                                              final Attribute actual )
    {
        final String[] expectedNames = expected.getParameterNames();
        final String[] actualNames = actual.getParameterNames();
        Assert.assertEquals( message + " Length", expectedNames.length, actualNames.length );

        for( int i = 0; i < expectedNames.length; i++ )
        {
            final String name = expectedNames[ i ];
            Assert.assertEquals( message + " value",
                                 expected.getParameter( name ),
                                 actual.getParameter( name ) );
        }
    }

    protected static void assertEqualAttributes( final String message,
                                                 final Attribute[] expected,
                                                 final Attribute[] actual )
    {
        Assert.assertEquals( message + " Length", expected.length, actual.length );
        for( int i = 0; i < expected.length; i++ )
        {
            Assert.assertEquals( message + " [" + i + "].name",
                                 expected[ i ].getName(), actual[ i ].getName() );
            assertEqualParameters( message + " [" + i + "].parameters",
                                   expected[ i ], actual[ i ] );
        }
    }
}
