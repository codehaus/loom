/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.components.util.verifier;

import junit.framework.TestCase;
import org.jcontainer.dna.impl.ConsoleLogger;
import org.jcontainer.loom.components.util.info.ComponentInfo;
import org.jcontainer.loom.components.util.info.DependencyDescriptor;
import org.jcontainer.loom.components.util.info.ServiceDescriptor;
import org.jcontainer.loom.components.util.metadata.ComponentMetaData;
import org.jcontainer.loom.components.util.metadata.DependencyMetaData;
import org.jcontainer.loom.components.util.metadata.PartitionMetaData;
import org.jcontainer.loom.components.util.profile.ComponentProfile;
import org.jcontainer.loom.components.util.profile.PartitionProfile;
import org.jcontainer.loom.components.util.verifier.SarVerifier;
import org.jcontainer.loom.interfaces.ContainerConstants;
import org.jcontainer.loom.components.assembler.data.Service1;
import org.jcontainer.loom.components.assembler.data.Component1;
import org.jcontainer.loom.components.assembler.data.Component2;
import org.jcontainer.loom.components.assembler.data.Component3;

/**
 *  An basic test case for the LogManager.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003-10-16 14:45:55 $
 */
public class VerifierTestCase
    extends TestCase
{
    public void testBasic()
        throws Exception
    {
        final DependencyMetaData dependency =
            new DependencyMetaData( Service1.class.getName(),
                                    "c2",
                                    Service1.class.getName() );
        final ComponentMetaData c1MetaData =
            new ComponentMetaData( "c1",
                                   Component1.class.getName(),
                                   new DependencyMetaData[]{dependency},
                                   null,
                                   null,
                                   false );
        final ComponentMetaData c2MetaData =
            new ComponentMetaData( "c2",
                                   Component2.class.getName(),
                                   DependencyMetaData.EMPTY_SET,
                                   null,
                                   null,
                                   false );
        final PartitionMetaData listenerMetaData =
            new PartitionMetaData( ContainerConstants.LISTENER_PARTITION,
                                   new String[ 0 ],
                                   PartitionMetaData.EMPTY_SET,
                                   ComponentMetaData.EMPTY_SET );
        final PartitionMetaData blockMetaData =
            new PartitionMetaData( ContainerConstants.BLOCK_PARTITION,
                                   new String[]{ContainerConstants.LISTENER_PARTITION},
                                   PartitionMetaData.EMPTY_SET,
                                   new ComponentMetaData[]{c1MetaData, c2MetaData} );
        final PartitionMetaData metaData =
            new PartitionMetaData( "assembly1",
                                   new String[ 0 ],
                                   new PartitionMetaData[]{blockMetaData, listenerMetaData},
                                   ComponentMetaData.EMPTY_SET );
        final ComponentInfo c1Info =
            new ComponentInfo( Component1.class,
                               ServiceDescriptor.EMPTY_SET,
                               new DependencyDescriptor[]{new DependencyDescriptor( Service1.class.getName(), Service1.class.getName(), false )},
                               null );
        final ComponentInfo c2Info =
            new ComponentInfo( Component2.class,
                               new ServiceDescriptor[]{new ServiceDescriptor( Service1.class.getName() )},
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
                                  new PartitionProfile[]{blockProfile, listenerProfile},
                                  ComponentProfile.EMPTY_SET );
        verify( profile );
    }

    public void testComplex()
        throws Exception
    {
        final DependencyMetaData dependency1 =
            new DependencyMetaData( Service1.class.getName() + DependencyDescriptor.ARRAY_POSTFIX,
                                    "c2a",
                                    Service1.class.getName() );
        final DependencyMetaData dependency2 =
            new DependencyMetaData( Service1.class.getName() + DependencyDescriptor.ARRAY_POSTFIX,
                                    "c2b",
                                    Service1.class.getName() );
        final DependencyMetaData dependency3 =
            new DependencyMetaData( Service1.class.getName() + DependencyDescriptor.ARRAY_POSTFIX,
                                    "c2c",
                                    Service1.class.getName() );
        final ComponentMetaData c3MetaData =
            new ComponentMetaData( "c3",
                                   Component3.class.getName(),
                                   new DependencyMetaData[]{dependency1, dependency2, dependency3},
                                   null,
                                   null,
                                   false );
        final ComponentMetaData c2aMetaData =
            new ComponentMetaData( "c2a",
                                   Component2.class.getName(),
                                   DependencyMetaData.EMPTY_SET,
                                   null,
                                   null,
                                   false );
        final ComponentMetaData c2bMetaData =
            new ComponentMetaData( "c2b",
                                   Component2.class.getName(),
                                   DependencyMetaData.EMPTY_SET,
                                   null,
                                   null,
                                   false );
        final ComponentMetaData c2cMetaData =
            new ComponentMetaData( "c2c",
                                   Component2.class.getName(),
                                   DependencyMetaData.EMPTY_SET,
                                   null,
                                   null,
                                   false );
        final PartitionMetaData listenerMetaData =
            new PartitionMetaData( ContainerConstants.LISTENER_PARTITION,
                                   new String[ 0 ],
                                   PartitionMetaData.EMPTY_SET,
                                   ComponentMetaData.EMPTY_SET );
        final PartitionMetaData blockMetaData =
            new PartitionMetaData( ContainerConstants.BLOCK_PARTITION,
                                   new String[]{ContainerConstants.LISTENER_PARTITION},
                                   PartitionMetaData.EMPTY_SET,
                                   new ComponentMetaData[]{c2aMetaData, c2bMetaData, c2cMetaData, c3MetaData} );
        final PartitionMetaData metaData =
            new PartitionMetaData( "assembly1",
                                   new String[ 0 ],
                                   new PartitionMetaData[]{blockMetaData, listenerMetaData},
                                   ComponentMetaData.EMPTY_SET );

        final ComponentInfo c3Info =
            new ComponentInfo( Component3.class,
                               ServiceDescriptor.EMPTY_SET,
                               new DependencyDescriptor[]{new DependencyDescriptor( Service1.class.getName() + DependencyDescriptor.ARRAY_POSTFIX,
                                                                                    Service1.class.getName() + DependencyDescriptor.ARRAY_POSTFIX,
                                                                                    false )},
                               null );
        final ComponentInfo c2Info =
            new ComponentInfo( Component2.class,
                               new ServiceDescriptor[]{new ServiceDescriptor( Service1.class.getName() )},
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
                                  new ComponentProfile[]{c3Profile, c2aProfile, c2bProfile, c2cProfile} );
        final PartitionProfile listenerProfile =
            new PartitionProfile( listenerMetaData,
                                  PartitionProfile.EMPTY_SET,
                                  ComponentProfile.EMPTY_SET );
        final PartitionProfile profile =
            new PartitionProfile( metaData,
                                  new PartitionProfile[]{blockProfile, listenerProfile},
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
