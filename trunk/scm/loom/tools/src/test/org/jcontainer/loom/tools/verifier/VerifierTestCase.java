/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.tools.verifier;

import junit.framework.TestCase;
import org.jcontainer.dna.impl.ConsoleLogger;
import org.jcontainer.loom.tools.LoomToolConstants;
import org.jcontainer.loom.tools.info.ComponentInfo;
import org.jcontainer.loom.tools.info.DependencyDescriptor;
import org.jcontainer.loom.tools.info.ServiceDescriptor;
import org.jcontainer.loom.tools.metadata.ComponentMetaData;
import org.jcontainer.loom.tools.metadata.DependencyMetaData;
import org.jcontainer.loom.tools.metadata.PartitionMetaData;
import org.jcontainer.loom.tools.profile.ComponentProfile;
import org.jcontainer.loom.tools.profile.PartitionProfile;
import org.realityforge.metaclass.model.Attribute;

/**
 *  An basic test case for the LogManager.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.14 $ $Date: 2003-10-14 08:42:16 $
 */
public class VerifierTestCase
    extends TestCase
{
    private static final String DATA_PKG = "org.jcontainer.loom.tools.data.";
    private static final String C1_IMPLEMENTATION_KEY = DATA_PKG + "Component1";
    private static final String C2_IMPLEMENTATION_KEY = DATA_PKG + "Component2";
    private static final String C3_IMPLEMENTATION_KEY = DATA_PKG + "Component3";
    private static final String C2_SERVICE = DATA_PKG + "Service2";
    private static final String C1_NAME = "c1";
    private static final String C3_NAME = "c3";

    public VerifierTestCase( final String name )
    {
        super( name );
    }

    public void testBasic()
        throws Exception
    {
        final DependencyMetaData dependency =
            new DependencyMetaData( C2_SERVICE,
                                    "c2",
                                    C2_SERVICE,
                                    Attribute.EMPTY_SET );
        final ComponentMetaData c1MetaData =
            new ComponentMetaData( C1_NAME,
                                   C1_IMPLEMENTATION_KEY,
                                   new DependencyMetaData[]{dependency},
                                   null,
                                   null,
                                   Attribute.EMPTY_SET );
        final ComponentMetaData c2MetaData =
            new ComponentMetaData( "c2",
                                   C2_IMPLEMENTATION_KEY,
                                   DependencyMetaData.EMPTY_SET,
                                   null,
                                   null,
                                   Attribute.EMPTY_SET );
        final PartitionMetaData listenerMetaData =
            new PartitionMetaData( LoomToolConstants.LISTENER_PARTITION,
                                   new String[ 0 ],
                                   PartitionMetaData.EMPTY_SET,
                                   ComponentMetaData.EMPTY_SET,
                                   Attribute.EMPTY_SET );
        final PartitionMetaData blockMetaData =
            new PartitionMetaData( LoomToolConstants.BLOCK_PARTITION,
                                   new String[]{LoomToolConstants.LISTENER_PARTITION},
                                   PartitionMetaData.EMPTY_SET,
                                   new ComponentMetaData[]{c1MetaData, c2MetaData},
                                   Attribute.EMPTY_SET );
        final PartitionMetaData metaData =
            new PartitionMetaData( "assembly1",
                                   new String[ 0 ],
                                   new PartitionMetaData[]{blockMetaData, listenerMetaData},
                                   ComponentMetaData.EMPTY_SET,
                                   Attribute.EMPTY_SET );
        final ComponentInfo c1Info =
            new ComponentInfo( C1_IMPLEMENTATION_KEY,
                               ServiceDescriptor.EMPTY_SET,
                               new DependencyDescriptor[]{new DependencyDescriptor( C2_SERVICE, C2_SERVICE, false, Attribute.EMPTY_SET )},
                               null );
        final ComponentInfo c2Info =
            new ComponentInfo( C2_IMPLEMENTATION_KEY,
                               new ServiceDescriptor[]{new ServiceDescriptor( C2_SERVICE, Attribute.EMPTY_SET )},
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
            new DependencyMetaData( C2_SERVICE + DependencyDescriptor.ARRAY_POSTFIX,
                                    "c2a",
                                    C2_SERVICE,
                                    Attribute.EMPTY_SET );
        final DependencyMetaData dependency2 =
            new DependencyMetaData( C2_SERVICE + DependencyDescriptor.ARRAY_POSTFIX,
                                    "c2b",
                                    C2_SERVICE,
                                    Attribute.EMPTY_SET );
        final DependencyMetaData dependency3 =
            new DependencyMetaData( C2_SERVICE + DependencyDescriptor.ARRAY_POSTFIX,
                                    "c2c",
                                    C2_SERVICE,
                                    Attribute.EMPTY_SET );
        final ComponentMetaData c3MetaData =
            new ComponentMetaData( C3_NAME,
                                   C3_IMPLEMENTATION_KEY,
                                   new DependencyMetaData[]{dependency1, dependency2, dependency3},
                                   null,
                                   null,
                                   Attribute.EMPTY_SET );
        final ComponentMetaData c2aMetaData =
            new ComponentMetaData( "c2a",
                                   C2_IMPLEMENTATION_KEY,
                                   DependencyMetaData.EMPTY_SET,
                                   null,
                                   null,
                                   Attribute.EMPTY_SET );
        final ComponentMetaData c2bMetaData =
            new ComponentMetaData( "c2b",
                                   C2_IMPLEMENTATION_KEY,
                                   DependencyMetaData.EMPTY_SET,
                                   null,
                                   null,
                                   Attribute.EMPTY_SET );
        final ComponentMetaData c2cMetaData =
            new ComponentMetaData( "c2c",
                                   C2_IMPLEMENTATION_KEY,
                                   DependencyMetaData.EMPTY_SET,
                                   null,
                                   null,
                                   Attribute.EMPTY_SET );
        final PartitionMetaData listenerMetaData =
            new PartitionMetaData( LoomToolConstants.LISTENER_PARTITION,
                                   new String[ 0 ],
                                   PartitionMetaData.EMPTY_SET,
                                   ComponentMetaData.EMPTY_SET,
                                   Attribute.EMPTY_SET );
        final PartitionMetaData blockMetaData =
            new PartitionMetaData( LoomToolConstants.BLOCK_PARTITION,
                                   new String[]{LoomToolConstants.LISTENER_PARTITION},
                                   PartitionMetaData.EMPTY_SET,
                                   new ComponentMetaData[]{c2aMetaData, c2bMetaData, c2cMetaData, c3MetaData},
                                   Attribute.EMPTY_SET );
        final PartitionMetaData metaData =
            new PartitionMetaData( "assembly1",
                                   new String[ 0 ],
                                   new PartitionMetaData[]{blockMetaData, listenerMetaData},
                                   ComponentMetaData.EMPTY_SET,
                                   Attribute.EMPTY_SET );

        final ComponentInfo c3Info =
            new ComponentInfo( C3_IMPLEMENTATION_KEY,
                               ServiceDescriptor.EMPTY_SET,
                               new DependencyDescriptor[]{new DependencyDescriptor( C2_SERVICE + DependencyDescriptor.ARRAY_POSTFIX,
                                                                                    C2_SERVICE + DependencyDescriptor.ARRAY_POSTFIX,
                                                                                    false,
                                                                                    Attribute.EMPTY_SET )},
                               null );
        final ComponentInfo c2Info =
            new ComponentInfo( C2_IMPLEMENTATION_KEY,
                               new ServiceDescriptor[]{new ServiceDescriptor( C2_SERVICE, Attribute.EMPTY_SET )},
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

    private void verify( final PartitionProfile profile ) throws VerifyException
    {
        final ClassLoader classLoader = getClass().getClassLoader();
        final SarVerifier verifier = new SarVerifier();
        verifier.enableLogging( new ConsoleLogger() );
        verifier.verifySar( profile, classLoader );
    }
}
