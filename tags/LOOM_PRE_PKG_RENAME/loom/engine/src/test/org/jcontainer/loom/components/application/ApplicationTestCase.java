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
import org.jcontainer.loom.components.assembler.data.Component1;
import org.jcontainer.loom.components.assembler.data.Component2;
import org.jcontainer.loom.components.assembler.data.Component3;
import org.jcontainer.loom.components.assembler.data.Component4;
import org.jcontainer.loom.components.assembler.data.Service1;
import org.jcontainer.loom.components.deployer.PhoenixProfileBuilder;
import org.jcontainer.loom.components.util.ConfigurationBuilder;
import org.jcontainer.loom.components.util.info.ComponentInfo;
import org.jcontainer.loom.components.util.info.DependencyDescriptor;
import org.jcontainer.loom.components.util.info.ServiceDescriptor;
import org.jcontainer.loom.components.util.metadata.ComponentTemplate;
import org.jcontainer.loom.components.util.metadata.DependencyDirective;
import org.jcontainer.loom.components.util.metadata.PartitionTemplate;
import org.jcontainer.loom.components.util.profile.ComponentProfile;
import org.jcontainer.loom.components.util.profile.PartitionProfile;
import org.jcontainer.loom.interfaces.ContainerConstants;
import org.xml.sax.InputSource;

/**
 * An basic test case for the Application.
 *
 * @author Peter Donald
 * @version $Revision: 1.18 $ $Date: 2003-11-29 13:44:29 $
 */
public class ApplicationTestCase
    extends TestCase
{
    private static final Class S1_TYPE = Service1.class;
    private static final Class C1_TYPE = Component1.class;
    private static final Class C2_TYPE = Component2.class;
    private static final Class C3_TYPE = Component3.class;
    private static final Class C4_TYPE = Component4.class;

    private static final String S1_NAME = S1_TYPE.getName();
    private static final String C1_NAME = C1_TYPE.getName();
    private static final String C2_NAME = C2_TYPE.getName();
    private static final String C3_NAME = C3_TYPE.getName();
    private static final String C4_NAME = C4_TYPE.getName();

    private static final String S1_ARRAY_NAME = S1_NAME +
        DependencyDescriptor.ARRAY_POSTFIX;
    private static final String S1_MAP_NAME = S1_NAME +
        DependencyDescriptor.MAP_POSTFIX;

    private static final ComponentInfo C1 =
        new ComponentInfo( C1_TYPE,
                           ServiceDescriptor.EMPTY_SET,
                           new DependencyDescriptor[]{
                               new DependencyDescriptor( S1_NAME,
                                                         S1_NAME,
                                                         false )},
                           null );

    private static final ComponentInfo C2 =
        new ComponentInfo( C2_TYPE,
                           new ServiceDescriptor[]{
                               new ServiceDescriptor( S1_NAME )},
                           DependencyDescriptor.EMPTY_SET,
                           null );

    private static final ComponentInfo C3 =
        new ComponentInfo( C3_TYPE,
                           ServiceDescriptor.EMPTY_SET,
                           new DependencyDescriptor[]{
                               new DependencyDescriptor( S1_ARRAY_NAME,
                                                         S1_ARRAY_NAME,
                                                         false )},
                           null );

    private static final ComponentInfo C4 =
        new ComponentInfo( C4_TYPE,
                           ServiceDescriptor.EMPTY_SET,
                           new DependencyDescriptor[]{
                               new DependencyDescriptor( S1_MAP_NAME,
                                                         S1_MAP_NAME,
                                                         false )},
                           null );

    public void testBasic()
        throws Exception
    {
        final ComponentTemplate md1 =
            new ComponentTemplate( "c1",
                                   C1_NAME,
                                   new DependencyDirective[]{
                                       new DependencyDirective( S1_NAME,
                                                                "c2",
                                                                "c2" )},
                                   null,
                                   null,
                                   false );
        final ComponentProfile cp1 = new ComponentProfile( C1, md1 );
        final ComponentTemplate md2 =
            new ComponentTemplate( "c2",
                                   C2_NAME,
                                   DependencyDirective.EMPTY_SET,
                                   null,
                                   null,
                                   true );
        final ComponentProfile cp2 = new ComponentProfile( C2, md2 );
        final PartitionTemplate blockPartitionMD =
            new PartitionTemplate( ContainerConstants.BLOCK_PARTITION,
                                   new String[]{
                                       ContainerConstants.LISTENER_PARTITION},
                                   PartitionTemplate.EMPTY_SET,
                                   new ComponentTemplate[]{md1, md2} );
        final PartitionTemplate listenerPartitionMD =
            new PartitionTemplate( ContainerConstants.LISTENER_PARTITION,
                                   new String[ 0 ],
                                   PartitionTemplate.EMPTY_SET,
                                   ComponentTemplate.EMPTY_SET );
        final PartitionTemplate partitionMD =
            new PartitionTemplate( "test",
                                   new String[ 0 ],
                                   new PartitionTemplate[]{blockPartitionMD,
                                                           listenerPartitionMD},
                                   ComponentTemplate.EMPTY_SET );

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
                                  new PartitionProfile[]{blockPartitionProfile,
                                                         listenerPartitionProfile},
                                  ComponentProfile.EMPTY_SET );
        runApplicationTest( partitionProfile );
    }

    public void testArrayAssembly()
        throws Exception
    {
        final DependencyDirective[] c3Deps =
            new DependencyDirective[]{new DependencyDirective( S1_ARRAY_NAME,
                                                               "c2a",
                                                               "c2a" ),
                                      new DependencyDirective( S1_ARRAY_NAME,
                                                               "c2b",
                                                               "c2b" ),
                                      new DependencyDirective( S1_ARRAY_NAME,
                                                               "c2c",
                                                               "c2c" )};
        final ComponentTemplate md3 =
            new ComponentTemplate( "c3",
                                   C3_NAME,
                                   c3Deps,
                                   null,
                                   null,
                                   false );
        final ComponentProfile cp3 = new ComponentProfile( C3, md3 );
        final ComponentTemplate md2a =
            new ComponentTemplate( "c2a",
                                   C2_NAME,
                                   DependencyDirective.EMPTY_SET,
                                   null,
                                   null,
                                   false );
        final ComponentProfile cp2a = new ComponentProfile( C2, md2a );
        final ComponentTemplate md2b =
            new ComponentTemplate( "c2b",
                                   C2_NAME,
                                   DependencyDirective.EMPTY_SET,
                                   null,
                                   null,
                                   false );
        final ComponentProfile cp2b = new ComponentProfile( C2, md2b );
        final ComponentTemplate md2c =
            new ComponentTemplate( "c2c",
                                   C2_NAME,
                                   DependencyDirective.EMPTY_SET,
                                   null,
                                   null,
                                   false );
        final ComponentProfile cp2c = new ComponentProfile( C2, md2c );

        final PartitionTemplate blockPartitionMD =
            new PartitionTemplate( ContainerConstants.BLOCK_PARTITION,
                                   new String[]{
                                       ContainerConstants.LISTENER_PARTITION},
                                   PartitionTemplate.EMPTY_SET,
                                   new ComponentTemplate[]{md3,
                                                           md2a,
                                                           md2b,
                                                           md2c} );
        final PartitionTemplate listenerPartitionMD =
            new PartitionTemplate( ContainerConstants.LISTENER_PARTITION,
                                   new String[ 0 ],
                                   PartitionTemplate.EMPTY_SET,
                                   ComponentTemplate.EMPTY_SET );
        final PartitionTemplate partitionMD =
            new PartitionTemplate( "test",
                                   new String[ 0 ],
                                   new PartitionTemplate[]{blockPartitionMD,
                                                           listenerPartitionMD},
                                   ComponentTemplate.EMPTY_SET );

        final PartitionProfile blockPartitionProfile =
            new PartitionProfile( blockPartitionMD,
                                  PartitionProfile.EMPTY_SET,
                                  new ComponentProfile[]{cp3,
                                                         cp2a,
                                                         cp2b,
                                                         cp2c} );
        final PartitionProfile listenerPartitionProfile =
            new PartitionProfile( listenerPartitionMD,
                                  PartitionProfile.EMPTY_SET,
                                  ComponentProfile.EMPTY_SET );
        final PartitionProfile partitionProfile =
            new PartitionProfile( partitionMD,
                                  new PartitionProfile[]{blockPartitionProfile,
                                                         listenerPartitionProfile},
                                  ComponentProfile.EMPTY_SET );
        runApplicationTest( partitionProfile );
    }

    public void testMapAssembly()
        throws Exception
    {
        final DependencyDirective[] c4Deps =
            new DependencyDirective[]{new DependencyDirective( S1_MAP_NAME,
                                                               "c2a",
                                                               "c2a" ),
                                      new DependencyDirective( S1_MAP_NAME,
                                                               "c2b",
                                                               "c2b" ),
                                      new DependencyDirective( S1_MAP_NAME,
                                                               "c2c",
                                                               "anAlias" )};
        final ComponentTemplate md4 =
            new ComponentTemplate( "c4",
                                   C4_NAME,
                                   c4Deps,
                                   null,
                                   null,
                                   false );
        final ComponentProfile cp4 = new ComponentProfile( C4, md4 );
        final ComponentTemplate md2a =
            new ComponentTemplate( "c2a",
                                   C2_NAME,
                                   DependencyDirective.EMPTY_SET,
                                   null,
                                   null,
                                   false );
        final ComponentProfile cp2a = new ComponentProfile( C2, md2a );
        final ComponentTemplate md2b =
            new ComponentTemplate( "c2b",
                                   C2_NAME,
                                   DependencyDirective.EMPTY_SET,
                                   null,
                                   null,
                                   false );
        final ComponentProfile cp2b = new ComponentProfile( C2, md2b );
        final ComponentTemplate md2c =
            new ComponentTemplate( "c2c",
                                   C2_NAME,
                                   DependencyDirective.EMPTY_SET,
                                   null,
                                   null,
                                   false );
        final ComponentProfile cp2c = new ComponentProfile( C2, md2c );

        final PartitionTemplate blockPartitionMD =
            new PartitionTemplate( ContainerConstants.BLOCK_PARTITION,
                                   new String[]{
                                       ContainerConstants.LISTENER_PARTITION},
                                   PartitionTemplate.EMPTY_SET,
                                   new ComponentTemplate[]{md4,
                                                           md2a,
                                                           md2b,
                                                           md2c} );
        final PartitionTemplate listenerPartitionMD =
            new PartitionTemplate( ContainerConstants.LISTENER_PARTITION,
                                   new String[ 0 ],
                                   PartitionTemplate.EMPTY_SET,
                                   ComponentTemplate.EMPTY_SET );
        final PartitionTemplate partitionMD =
            new PartitionTemplate( "test",
                                   new String[ 0 ],
                                   new PartitionTemplate[]{blockPartitionMD,
                                                           listenerPartitionMD},
                                   ComponentTemplate.EMPTY_SET );

        final PartitionProfile blockPartitionProfile =
            new PartitionProfile( blockPartitionMD,
                                  PartitionProfile.EMPTY_SET,
                                  new ComponentProfile[]{cp4,
                                                         cp2a,
                                                         cp2b,
                                                         cp2c} );
        final PartitionProfile listenerPartitionProfile =
            new PartitionProfile( listenerPartitionMD,
                                  PartitionProfile.EMPTY_SET,
                                  ComponentProfile.EMPTY_SET );
        final PartitionProfile partitionProfile =
            new PartitionProfile( partitionMD,
                                  new PartitionProfile[]{blockPartitionProfile,
                                                         listenerPartitionProfile},
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
        final URL resource = getClass().getResource( config );
        assertNotNull( "Config resource: " + config, resource );
        final Configuration assembly =
            ConfigurationBuilder.build(
                new InputSource( resource.toExternalForm() ),
                ConfigurationBuilder.ASSEMBLY_SCHEMA,
                new ConsoleLogger() );
        final Map parameters = new HashMap();
        parameters.put( ContainerConstants.ASSEMBLY_NAME, "test" );
        parameters.put( ContainerConstants.ASSEMBLY_DESCRIPTOR, assembly );
        parameters.put( ContainerConstants.CONFIG_DESCRIPTOR,
                        new DefaultConfiguration( "config", "", "" ) );
        parameters.put( ContainerConstants.ASSEMBLY_CLASSLOADER,
                        getClass().getClassLoader() );
        return assembler.buildProfile( parameters );
    }
}
