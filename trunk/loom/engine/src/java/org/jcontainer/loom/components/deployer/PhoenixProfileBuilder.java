/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.components.deployer;

import java.util.ArrayList;
import java.util.Map;
import org.jcontainer.loom.components.assembler.Assembler;
import org.jcontainer.loom.components.util.factory.ComponentFactory;
import org.jcontainer.loom.components.util.factory.DefaultComponentFactory;
import org.jcontainer.loom.components.util.info.ComponentInfo;
import org.jcontainer.loom.components.util.info.DependencyDescriptor;
import org.jcontainer.loom.components.util.info.ServiceDescriptor;
import org.jcontainer.loom.components.util.metadata.ComponentTemplate;
import org.jcontainer.loom.components.util.metadata.PartitionTemplate;
import org.jcontainer.loom.components.util.profile.ComponentProfile;
import org.jcontainer.loom.components.util.profile.PartitionProfile;
import org.jcontainer.loom.components.util.profile.ProfileBuilder;
import org.jcontainer.loom.interfaces.ContainerConstants;

/**
 * @author Peter Donald
 * @version $Revision: 1.18 $ $Date: 2003-11-29 13:44:16 $
 */
public class PhoenixProfileBuilder
    implements ProfileBuilder
{
    private final Assembler m_assembler = new Assembler();

    public PartitionProfile buildProfile( final Map parameters )
        throws Exception
    {
        final PartitionTemplate metaData = m_assembler.buildAssembly(
            parameters );
        final ClassLoader classLoader =
            (ClassLoader)parameters.get(
                ContainerConstants.ASSEMBLY_CLASSLOADER );
        final ComponentFactory factory = new DefaultComponentFactory(
            classLoader );

        return assembleSarProfile( metaData, factory, classLoader );
    }

    private PartitionProfile assembleSarProfile(
        final PartitionTemplate metaData,
        final ComponentFactory factory,
        final ClassLoader classLoader )
        throws Exception
    {
        final PartitionTemplate blockPartition =
            metaData.getPartition( ContainerConstants.BLOCK_PARTITION );
        final PartitionTemplate listenerPartition =
            metaData.getPartition( ContainerConstants.LISTENER_PARTITION );

        final PartitionProfile blockProfile = assembleProfile( blockPartition,
                                                               factory );
        final PartitionProfile listenerProfile =
            assembleListenerProfile( listenerPartition, classLoader );

        final PartitionProfile[] profiles = new PartitionProfile[]{
            blockProfile, listenerProfile};
        return new PartitionProfile( metaData,
                                     profiles,
                                     ComponentProfile.EMPTY_SET );
    }

    private PartitionProfile assembleListenerProfile(
        final PartitionTemplate metaData,
        final ClassLoader classLoader )
        throws Exception
    {
        final ArrayList componentSet = new ArrayList();
        final ComponentTemplate[] components = metaData.getComponents();
        for( int i = 0; i < components.length; i++ )
        {
            final ComponentTemplate component = components[ i ];
            final Class type = classLoader.loadClass(
                component.getImplementationKey() );
            final ComponentInfo info = createListenerInfo( type );
            final ComponentProfile profile = new ComponentProfile( info,
                                                                   component );
            componentSet.add( profile );
        }

        final ComponentProfile[] profiles =
            (ComponentProfile[])componentSet.toArray(
                new ComponentProfile[ componentSet.size() ] );
        return new PartitionProfile( metaData,
                                     PartitionProfile.EMPTY_SET,
                                     profiles );
    }

    private PartitionProfile assembleProfile( final PartitionTemplate metaData,
                                              final ComponentFactory factory )
        throws Exception
    {
        final ArrayList partitionSet = new ArrayList();
        final PartitionTemplate[] partitions = metaData.getPartitions();
        for( int i = 0; i < partitions.length; i++ )
        {
            final PartitionTemplate partition = partitions[ i ];
            final PartitionProfile profile = assembleProfile( partition,
                                                              factory );
            partitionSet.add( profile );
        }

        final ArrayList componentSet = new ArrayList();
        final ComponentTemplate[] components = metaData.getComponents();
        for( int i = 0; i < components.length; i++ )
        {
            final ComponentTemplate component = components[ i ];
            final ComponentInfo info =
                factory.createInfo( component.getImplementationKey() );
            final ComponentProfile profile = new ComponentProfile( info,
                                                                   component );
            componentSet.add( profile );
        }

        final PartitionProfile[] partitionProfiles =
            (PartitionProfile[])partitionSet.toArray(
                new PartitionProfile[ partitionSet.size() ] );
        final ComponentProfile[] componentProfiles =
            (ComponentProfile[])componentSet.toArray(
                new ComponentProfile[ componentSet.size() ] );
        return new PartitionProfile( metaData,
                                     partitionProfiles,
                                     componentProfiles );
    }

    /**
     * Create a {@link org.jcontainer.loom.components.util.info.ComponentInfo}
     * for a Listener with specified classname.
     *
     * @param type the listener type
     * @return the ComponentInfo for listener
     */
    private static ComponentInfo createListenerInfo( final Class type )
    {
        return new ComponentInfo( type,
                                  ServiceDescriptor.EMPTY_SET,
                                  DependencyDescriptor.EMPTY_SET,
                                  null );
    }
}
