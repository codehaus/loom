/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.components.application;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.impl.ConsoleLogger;
import org.jcontainer.dna.impl.DefaultConfiguration;
import org.jcontainer.loom.components.deployer.PhoenixProfileBuilder;
import org.jcontainer.loom.interfaces.ContainerConstants;
import org.jcontainer.loom.tools.LoomToolConstants;
import org.jcontainer.loom.tools.configuration.ConfigurationBuilder;
import org.jcontainer.loom.tools.info.ComponentInfo;
import org.jcontainer.loom.tools.info.DependencyDescriptor;
import org.jcontainer.loom.tools.info.ServiceDescriptor;
import org.jcontainer.loom.tools.metadata.ComponentMetaData;
import org.jcontainer.loom.tools.metadata.DependencyMetaData;
import org.jcontainer.loom.tools.metadata.PartitionMetaData;
import org.jcontainer.loom.tools.profile.ComponentProfile;
import org.jcontainer.loom.tools.profile.PartitionProfile;
import org.xml.sax.InputSource;

/**
 *  An basic test case for the Application.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.11 $ $Date: 2003-10-15 04:20:42 $
 */
public class ApplicationTestCase
    extends TestCase
{
    private static final String PKG_NAME =
        "org.jcontainer.loom.components.application.data.";
    private static final String S1_NAME = PKG_NAME + "Service1";
    private static final String S1_ARRAY_NAME = S1_NAME + DependencyDescriptor.ARRAY_POSTFIX;
    private static final String S1_MAP_NAME = S1_NAME + DependencyDescriptor.MAP_POSTFIX;
    private static final String C1_NAME = PKG_NAME + "Component1";
    private static final String C2_NAME = PKG_NAME + "Component2";
    private static final String C3_NAME = PKG_NAME + "Component3";
    private static final String C4_NAME = PKG_NAME + "Component4";

    private static final ComponentInfo C1 =
        new ComponentInfo( C1_NAME,
                           ServiceDescriptor.EMPTY_SET,
                           new DependencyDescriptor[]{new DependencyDescriptor( S1_NAME,
                                                                                S1_NAME,
                                                                                false)},
                           null
        );

    private static final ComponentInfo C2 =
        new ComponentInfo( C2_NAME,
                           new ServiceDescriptor[]{new ServiceDescriptor( S1_NAME )},
                           DependencyDescriptor.EMPTY_SET,
                           null
        );

    private static final ComponentInfo C3 =
        new ComponentInfo( C3_NAME,
                           ServiceDescriptor.EMPTY_SET,
                           new DependencyDescriptor[]{new DependencyDescriptor( S1_ARRAY_NAME,
                                                                                S1_ARRAY_NAME,
                                                                                false )},
                           null
        );

    private static final ComponentInfo C4 =
        new ComponentInfo( C4_NAME,
                           ServiceDescriptor.EMPTY_SET,
                           new DependencyDescriptor[]{new DependencyDescriptor( S1_MAP_NAME,
                                                                                S1_MAP_NAME,
                                                                                false )},
                           null
        );

    public ApplicationTestCase( final String name )
    {
        super( name );
    }

    public void testBasic()
        throws Exception
    {
        final ComponentMetaData md1 =
            new ComponentMetaData( "c1",
                                   C1_NAME,
                                   new DependencyMetaData[]{new DependencyMetaData( S1_NAME, "c2", "c2")},
                                   null,
                                   null,
                                   false );
        final ComponentProfile cp1 = new ComponentProfile( C1, md1 );
        final ComponentMetaData md2 =
            new ComponentMetaData( "c2",
                                   C2_NAME,
                                   DependencyMetaData.EMPTY_SET,
                                   null,
                                   null,
                                   true );
        final ComponentProfile cp2 = new ComponentProfile( C2, md2 );
        final PartitionMetaData blockPartitionMD =
            new PartitionMetaData( LoomToolConstants.BLOCK_PARTITION,
                                   new String[]{LoomToolConstants.LISTENER_PARTITION},
                                   PartitionMetaData.EMPTY_SET,
                                   new ComponentMetaData[]{md1, md2} );
        final PartitionMetaData listenerPartitionMD =
            new PartitionMetaData( LoomToolConstants.LISTENER_PARTITION,
                                   new String[ 0 ],
                                   PartitionMetaData.EMPTY_SET,
                                   ComponentMetaData.EMPTY_SET );
        final PartitionMetaData partitionMD =
            new PartitionMetaData( "test",
                                   new String[ 0 ],
                                   new PartitionMetaData[]{blockPartitionMD, listenerPartitionMD},
                                   ComponentMetaData.EMPTY_SET );

        final PartitionProfile blockPartitionProfile =
            new PartitionProfile( blockPartitionMD,
                                  PartitionProfile.EMPTY_SET,
                                  new ComponentProfile[]{cp1, cp2} );
        final PartitionProfile listenerPartitionProfile =
            new PartitionProfile( listenerPartitionMD,
                                  PartitionProfile.EMPTY_SET,
                                  ComponentProfile.EMPTY_SET );
        final PartitionProfile partitionProfile =
            new PartitionProfile( partitionMD,
                                  new PartitionProfile[]{blockPartitionProfile, listenerPartitionProfile},
                                  ComponentProfile.EMPTY_SET );
        runApplicationTest( partitionProfile );
    }

    public void testArrayAssembly()
        throws Exception
    {
        final DependencyMetaData[] c3Deps =
            new DependencyMetaData[]{new DependencyMetaData( S1_ARRAY_NAME,
                                                             "c2a",
                                                             "c2a" ),
                                     new DependencyMetaData( S1_ARRAY_NAME,
                                                             "c2b",
                                                             "c2b" ),
                                     new DependencyMetaData( S1_ARRAY_NAME,
                                                             "c2c",
                                                             "c2c" )};
        final ComponentMetaData md3 =
            new ComponentMetaData( "c3",
                                   C3_NAME,
                                   c3Deps,
                                   null,
                                   null,
                                   false );
        final ComponentProfile cp3 = new ComponentProfile( C3, md3 );
        final ComponentMetaData md2a =
            new ComponentMetaData( "c2a",
                                   C2_NAME,
                                   DependencyMetaData.EMPTY_SET,
                                   null,
                                   null,
                                   false );
        final ComponentProfile cp2a = new ComponentProfile( C2, md2a );
        final ComponentMetaData md2b =
            new ComponentMetaData( "c2b",
                                   C2_NAME,
                                   DependencyMetaData.EMPTY_SET,
                                   null,
                                   null,
                                   false );
        final ComponentProfile cp2b = new ComponentProfile( C2, md2b );
        final ComponentMetaData md2c =
            new ComponentMetaData( "c2c",
                                   C2_NAME,
                                   DependencyMetaData.EMPTY_SET,
                                   null,
                                   null,
                                   false );
        final ComponentProfile cp2c = new ComponentProfile( C2, md2c );

        final PartitionMetaData blockPartitionMD =
            new PartitionMetaData( LoomToolConstants.BLOCK_PARTITION,
                                   new String[]{LoomToolConstants.LISTENER_PARTITION},
                                   PartitionMetaData.EMPTY_SET,
                                   new ComponentMetaData[]{md3, md2a, md2b, md2c} );
        final PartitionMetaData listenerPartitionMD =
            new PartitionMetaData( LoomToolConstants.LISTENER_PARTITION,
                                   new String[ 0 ],
                                   PartitionMetaData.EMPTY_SET,
                                   ComponentMetaData.EMPTY_SET );
        final PartitionMetaData partitionMD =
            new PartitionMetaData( "test",
                                   new String[ 0 ],
                                   new PartitionMetaData[]{blockPartitionMD, listenerPartitionMD},
                                   ComponentMetaData.EMPTY_SET );

        final PartitionProfile blockPartitionProfile =
            new PartitionProfile( blockPartitionMD,
                                  PartitionProfile.EMPTY_SET,
                                  new ComponentProfile[]{cp3, cp2a, cp2b, cp2c} );
        final PartitionProfile listenerPartitionProfile =
            new PartitionProfile( listenerPartitionMD,
                                  PartitionProfile.EMPTY_SET,
                                  ComponentProfile.EMPTY_SET );
        final PartitionProfile partitionProfile =
            new PartitionProfile( partitionMD,
                                  new PartitionProfile[]{blockPartitionProfile, listenerPartitionProfile},
                                  ComponentProfile.EMPTY_SET );
        runApplicationTest( partitionProfile );
    }

    public void testMapAssembly()
        throws Exception
    {
        final DependencyMetaData[] c4Deps =
            new DependencyMetaData[]{new DependencyMetaData( S1_MAP_NAME,
                                                             "c2a",
                                                             "c2a" ),
                                     new DependencyMetaData( S1_MAP_NAME,
                                                             "c2b",
                                                             "c2b" ),
                                     new DependencyMetaData( S1_MAP_NAME,
                                                             "c2c",
                                                             "anAlias" )};
        final ComponentMetaData md4 =
            new ComponentMetaData( "c4",
                                   C4_NAME,
                                   c4Deps,
                                   null,
                                   null,
                                   false );
        final ComponentProfile cp4 = new ComponentProfile( C4, md4 );
        final ComponentMetaData md2a =
            new ComponentMetaData( "c2a",
                                   C2_NAME,
                                   DependencyMetaData.EMPTY_SET,
                                   null,
                                   null,
                                   false );
        final ComponentProfile cp2a = new ComponentProfile( C2, md2a );
        final ComponentMetaData md2b =
            new ComponentMetaData( "c2b",
                                   C2_NAME,
                                   DependencyMetaData.EMPTY_SET,
                                   null,
                                   null,
                                   false );
        final ComponentProfile cp2b = new ComponentProfile( C2, md2b );
        final ComponentMetaData md2c =
            new ComponentMetaData( "c2c",
                                   C2_NAME,
                                   DependencyMetaData.EMPTY_SET,
                                   null,
                                   null,
                                   false );
        final ComponentProfile cp2c = new ComponentProfile( C2, md2c );

        final PartitionMetaData blockPartitionMD =
            new PartitionMetaData( LoomToolConstants.BLOCK_PARTITION,
                                   new String[]{LoomToolConstants.LISTENER_PARTITION},
                                   PartitionMetaData.EMPTY_SET,
                                   new ComponentMetaData[]{md4, md2a, md2b, md2c} );
        final PartitionMetaData listenerPartitionMD =
            new PartitionMetaData( LoomToolConstants.LISTENER_PARTITION,
                                   new String[ 0 ],
                                   PartitionMetaData.EMPTY_SET,
                                   ComponentMetaData.EMPTY_SET );
        final PartitionMetaData partitionMD =
            new PartitionMetaData( "test",
                                   new String[ 0 ],
                                   new PartitionMetaData[]{blockPartitionMD, listenerPartitionMD},
                                   ComponentMetaData.EMPTY_SET );

        final PartitionProfile blockPartitionProfile =
            new PartitionProfile( blockPartitionMD,
                                  PartitionProfile.EMPTY_SET,
                                  new ComponentProfile[]{cp4, cp2a, cp2b, cp2c} );
        final PartitionProfile listenerPartitionProfile =
            new PartitionProfile( listenerPartitionMD,
                                  PartitionProfile.EMPTY_SET,
                                  ComponentProfile.EMPTY_SET );
        final PartitionProfile partitionProfile =
            new PartitionProfile( partitionMD,
                                  new PartitionProfile[]{blockPartitionProfile, listenerPartitionProfile},
                                  ComponentProfile.EMPTY_SET );
        runApplicationTest( partitionProfile );
    }

    private void runApplicationTest( final PartitionProfile sarMetaData )
        throws Exception
    {
        final DefaultApplication application = new DefaultApplication();
        application.enableLogging( new ConsoleLogger() );
        final MockApplicationContext context =
            new MockApplicationContext( sarMetaData, new ConsoleLogger() );
        application.setApplicationContext( context );
        application.initialize();
        application.dispose();
    }

    protected PartitionProfile assembleSar( final String config )
        throws Exception
    {
        final PhoenixProfileBuilder assembler = new PhoenixProfileBuilder();
        assembler.enableLogging( new ConsoleLogger() );
        final URL resource = getClass().getResource( config );
        assertNotNull( "Config resource: " + config, resource );
        final Configuration assembly =
            ConfigurationBuilder.build( new InputSource( resource.toExternalForm() ),
                                        ConfigurationBuilder.ASSEMBLY_SCHEMA,
                                        new ConsoleLogger() );
        final Map parameters = new HashMap();
        parameters.put( ContainerConstants.ASSEMBLY_NAME, "test" );
        parameters.put( ContainerConstants.ASSEMBLY_DESCRIPTOR, assembly );
        parameters.put( ContainerConstants.CONFIG_DESCRIPTOR, new DefaultConfiguration( "config", "", "" ) );
        parameters.put( ContainerConstants.ASSEMBLY_CLASSLOADER, getClass().getClassLoader() );
        return assembler.buildProfile( parameters );
    }
}
