/*
 * Copyright (C) The Loom Group. All rights reserved.
 *
 * This software is published under the terms of the Loom
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.codehaus.loom.components.util.verifier;

import junit.framework.TestCase;
import org.codehaus.dna.impl.ConsoleLogger;
import org.codehaus.loom.components.assembler.data.Component1;
import org.codehaus.loom.components.assembler.data.Component2;
import org.codehaus.loom.components.assembler.data.Component3;
import org.codehaus.loom.components.assembler.data.Service1;
import org.codehaus.loom.components.util.info.ComponentInfo;
import org.codehaus.loom.components.util.info.DependencyDescriptor;
import org.codehaus.loom.components.util.info.ServiceDescriptor;
import org.codehaus.loom.components.util.metadata.ComponentTemplate;
import org.codehaus.loom.components.util.metadata.DependencyDirective;
import org.codehaus.loom.components.util.metadata.PartitionTemplate;
import org.codehaus.loom.components.util.profile.ComponentProfile;
import org.codehaus.loom.components.util.profile.PartitionProfile;
import org.codehaus.loom.components.util.verifier.SarVerifier;
import org.codehaus.loom.interfaces.ContainerConstants;

/**
 * An basic test case for the LogManager.
 *
 * @author Peter Donald
 * @version $Revision: 1.2 $ $Date: 2004-05-01 12:48:35 $
 */
public class VerifierTestCase
    extends TestCase
{
    public void testBasic()
        throws Exception
    {
        final DependencyDirective dependency =
            new DependencyDirective( Service1.class.getName(),
                                     "c2",
                                     Service1.class.getName() );
        final ComponentTemplate c1MetaData =
            new ComponentTemplate( "c1",
                                   Component1.class.getName(),
                                   new DependencyDirective[]{dependency},
                                   null,
                                   null,
                                   false );
        final ComponentTemplate c2MetaData =
            new ComponentTemplate( "c2",
                                   Component2.class.getName(),
                                   DependencyDirective.EMPTY_SET,
                                   null,
                                   null,
                                   false );
        final PartitionTemplate listenerMetaData =
            new PartitionTemplate( ContainerConstants.LISTENER_PARTITION,
                                   new String[ 0 ],
                                   PartitionTemplate.EMPTY_SET,
                                   ComponentTemplate.EMPTY_SET );
        final PartitionTemplate blockMetaData =
            new PartitionTemplate( ContainerConstants.BLOCK_PARTITION,
                                   new String[]{
                                       ContainerConstants.LISTENER_PARTITION},
                                   PartitionTemplate.EMPTY_SET,
                                   new ComponentTemplate[]{c1MetaData,
                                                           c2MetaData} );
        final PartitionTemplate metaData =
            new PartitionTemplate( "assembly1",
                                   new String[ 0 ],
                                   new PartitionTemplate[]{blockMetaData,
                                                           listenerMetaData},
                                   ComponentTemplate.EMPTY_SET );
        final ComponentInfo c1Info =
            new ComponentInfo( Component1.class,
                               ServiceDescriptor.EMPTY_SET,
                               new DependencyDescriptor[]{
                                   new DependencyDescriptor(
                                       Service1.class.getName(),
                                       Service1.class.getName(),
                                       false )},
                               null );
        final ComponentInfo c2Info =
            new ComponentInfo( Component2.class,
                               new ServiceDescriptor[]{
                                   new ServiceDescriptor(
                                       Service1.class.getName() )},
                               DependencyDescriptor.EMPTY_SET,
                               null );
        final ComponentProfile c1Profile =
            new ComponentProfile( c1Info, c1MetaData );
        final ComponentProfile c2Profile =
            new ComponentProfile( c2Info, c2MetaData );
        final PartitionProfile blockProfile =
            new PartitionProfile( blockMetaData,
                                  PartitionProfile.EMPTY_SET,
                                  new ComponentProfile[]{c1Profile, c2Profile} );
        final PartitionProfile listenerProfile =
            new PartitionProfile( listenerMetaData,
                                  PartitionProfile.EMPTY_SET,
                                  ComponentProfile.EMPTY_SET );
        final PartitionProfile profile =
            new PartitionProfile( metaData,
                                  new PartitionProfile[]{blockProfile,
                                                         listenerProfile},
                                  ComponentProfile.EMPTY_SET );
        verify( profile );
    }

    public void testComplex()
        throws Exception
    {
        final DependencyDirective dependency1 =
            new DependencyDirective(
                Service1.class.getName() + DependencyDescriptor.ARRAY_POSTFIX,
                "c2a",
                Service1.class.getName() );
        final DependencyDirective dependency2 =
            new DependencyDirective(
                Service1.class.getName() + DependencyDescriptor.ARRAY_POSTFIX,
                "c2b",
                Service1.class.getName() );
        final DependencyDirective dependency3 =
            new DependencyDirective(
                Service1.class.getName() + DependencyDescriptor.ARRAY_POSTFIX,
                "c2c",
                Service1.class.getName() );
        final ComponentTemplate c3MetaData =
            new ComponentTemplate( "c3",
                                   Component3.class.getName(),
                                   new DependencyDirective[]{dependency1,
                                                             dependency2,
                                                             dependency3},
                                   null,
                                   null,
                                   false );
        final ComponentTemplate c2aMetaData =
            new ComponentTemplate( "c2a",
                                   Component2.class.getName(),
                                   DependencyDirective.EMPTY_SET,
                                   null,
                                   null,
                                   false );
        final ComponentTemplate c2bMetaData =
            new ComponentTemplate( "c2b",
                                   Component2.class.getName(),
                                   DependencyDirective.EMPTY_SET,
                                   null,
                                   null,
                                   false );
        final ComponentTemplate c2cMetaData =
            new ComponentTemplate( "c2c",
                                   Component2.class.getName(),
                                   DependencyDirective.EMPTY_SET,
                                   null,
                                   null,
                                   false );
        final PartitionTemplate listenerMetaData =
            new PartitionTemplate( ContainerConstants.LISTENER_PARTITION,
                                   new String[ 0 ],
                                   PartitionTemplate.EMPTY_SET,
                                   ComponentTemplate.EMPTY_SET );
        final PartitionTemplate blockMetaData =
            new PartitionTemplate( ContainerConstants.BLOCK_PARTITION,
                                   new String[]{
                                       ContainerConstants.LISTENER_PARTITION},
                                   PartitionTemplate.EMPTY_SET,
                                   new ComponentTemplate[]{c2aMetaData,
                                                           c2bMetaData,
                                                           c2cMetaData,
                                                           c3MetaData} );
        final PartitionTemplate metaData =
            new PartitionTemplate( "assembly1",
                                   new String[ 0 ],
                                   new PartitionTemplate[]{blockMetaData,
                                                           listenerMetaData},
                                   ComponentTemplate.EMPTY_SET );

        final ComponentInfo c3Info =
            new ComponentInfo( Component3.class,
                               ServiceDescriptor.EMPTY_SET,
                               new DependencyDescriptor[]{
                                   new DependencyDescriptor(
                                       Service1.class.getName() +
                                       DependencyDescriptor.ARRAY_POSTFIX,
                                       Service1.class.getName() +
                                       DependencyDescriptor.ARRAY_POSTFIX,
                                       false )},
                               null );
        final ComponentInfo c2Info =
            new ComponentInfo( Component2.class,
                               new ServiceDescriptor[]{
                                   new ServiceDescriptor(
                                       Service1.class.getName() )},
                               DependencyDescriptor.EMPTY_SET,
                               null );
        final ComponentProfile c3Profile =
            new ComponentProfile( c3Info, c3MetaData );
        final ComponentProfile c2aProfile =
            new ComponentProfile( c2Info, c2aMetaData );
        final ComponentProfile c2bProfile =
            new ComponentProfile( c2Info, c2bMetaData );
        final ComponentProfile c2cProfile =
            new ComponentProfile( c2Info, c2cMetaData );
        final PartitionProfile blockProfile =
            new PartitionProfile( blockMetaData,
                                  PartitionProfile.EMPTY_SET,
                                  new ComponentProfile[]{c3Profile,
                                                         c2aProfile,
                                                         c2bProfile,
                                                         c2cProfile} );
        final PartitionProfile listenerProfile =
            new PartitionProfile( listenerMetaData,
                                  PartitionProfile.EMPTY_SET,
                                  ComponentProfile.EMPTY_SET );
        final PartitionProfile profile =
            new PartitionProfile( metaData,
                                  new PartitionProfile[]{blockProfile,
                                                         listenerProfile},
                                  ComponentProfile.EMPTY_SET );
        verify( profile );
    }

    private void verify( final PartitionProfile profile )
        throws Exception
    {
        final ClassLoader classLoader = getClass().getClassLoader();
        final SarVerifier verifier = new SarVerifier();
        verifier.enableLogging( new ConsoleLogger() );
        verifier.verifySar( profile, classLoader );
    }
}
