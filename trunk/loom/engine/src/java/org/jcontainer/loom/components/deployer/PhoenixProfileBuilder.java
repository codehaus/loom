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
import org.jcontainer.dna.AbstractLogEnabled;
import org.jcontainer.dna.Logger;
import org.jcontainer.loom.components.assembler.Assembler;
import org.jcontainer.loom.interfaces.ContainerConstants;
import org.jcontainer.loom.tools.LoomToolConstants;
import org.jcontainer.loom.tools.factory.ComponentBundle;
import org.jcontainer.loom.tools.factory.ComponentFactory;
import org.jcontainer.loom.tools.factory.DefaultComponentFactory;
import org.jcontainer.loom.tools.info.ComponentInfo;
import org.jcontainer.loom.tools.info.DependencyDescriptor;
import org.jcontainer.loom.tools.info.ServiceDescriptor;
import org.jcontainer.loom.tools.metadata.ComponentMetaData;
import org.jcontainer.loom.tools.metadata.PartitionMetaData;
import org.jcontainer.loom.tools.profile.ComponentProfile;
import org.jcontainer.loom.tools.profile.PartitionProfile;
import org.jcontainer.loom.tools.profile.ProfileBuilder;

/**
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.11 $ $Date: 2003-10-16 00:20:28 $
 */
public class PhoenixProfileBuilder
    extends AbstractLogEnabled
    implements ProfileBuilder
{
    private final Assembler m_assembler = new Assembler();

    public void enableLogging( Logger logger )
    {
        super.enableLogging( logger );
        setupLogger( m_assembler );
    }

    public PartitionProfile buildProfile( final Map parameters )
        throws Exception
    {
        final PartitionMetaData metaData = m_assembler.buildAssembly( parameters );
        final ClassLoader classLoader =
            (ClassLoader)parameters.get( ContainerConstants.ASSEMBLY_CLASSLOADER );
        final ComponentFactory factory = new DefaultComponentFactory( classLoader );
        setupLogger( factory, "factory" );

        return assembleSarProfile( metaData, factory );
    }

    private PartitionProfile assembleSarProfile( final PartitionMetaData metaData,
                                                 final ComponentFactory factory )
        throws Exception
    {
        final PartitionMetaData blockPartition =
            metaData.getPartition( LoomToolConstants.BLOCK_PARTITION );
        final PartitionMetaData listenerPartition =
            metaData.getPartition( LoomToolConstants.LISTENER_PARTITION );

        final PartitionProfile blockProfile = assembleProfile( blockPartition, factory );
        final PartitionProfile listenerProfile =
            assembleListenerProfile( listenerPartition );

        final PartitionProfile[] profiles = new PartitionProfile[]{blockProfile, listenerProfile};
        return new PartitionProfile( metaData,
                                     profiles,
                                     ComponentProfile.EMPTY_SET );
    }

    private PartitionProfile assembleListenerProfile( final PartitionMetaData metaData )
    {
        final ArrayList componentSet = new ArrayList();
        final ComponentMetaData[] components = metaData.getComponents();
        for( int i = 0; i < components.length; i++ )
        {
            final ComponentMetaData component = components[ i ];
            final ComponentInfo info =
                createListenerInfo( component.getImplementationKey() );
            final ComponentProfile profile = new ComponentProfile( info, component );
            componentSet.add( profile );
        }

        final ComponentProfile[] profiles =
            (ComponentProfile[])componentSet.toArray( new ComponentProfile[ componentSet.size() ] );
        return new PartitionProfile( metaData, PartitionProfile.EMPTY_SET, profiles );
    }

    private PartitionProfile assembleProfile( final PartitionMetaData metaData,
                                              final ComponentFactory factory )
        throws Exception
    {
        final ArrayList partitionSet = new ArrayList();
        final PartitionMetaData[] partitions = metaData.getPartitions();
        for( int i = 0; i < partitions.length; i++ )
        {
            final PartitionMetaData partition = partitions[ i ];
            final PartitionProfile profile = assembleProfile( partition, factory );
            partitionSet.add( profile );
        }

        final ArrayList componentSet = new ArrayList();
        final ComponentMetaData[] components = metaData.getComponents();
        for( int i = 0; i < components.length; i++ )
        {
            final ComponentMetaData component = components[ i ];
            final ComponentBundle bundle =
                factory.createBundle( component.getImplementationKey() );
            final ComponentInfo info = bundle.getComponentInfo();
            final ComponentProfile profile = new ComponentProfile( info, component );
            componentSet.add( profile );
        }

        final PartitionProfile[] partitionProfiles =
            (PartitionProfile[])partitionSet.toArray( new PartitionProfile[ partitionSet.size() ] );
        final ComponentProfile[] componentProfiles =
            (ComponentProfile[])componentSet.toArray( new ComponentProfile[ componentSet.size() ] );
        return new PartitionProfile( metaData, partitionProfiles, componentProfiles );
    }

    /**
     * Create a {@link ComponentInfo} for a Listener with specified classname.
     *
     * @param implementationKey the classname of listener
     * @return the ComponentInfo for listener
     */
    private static ComponentInfo createListenerInfo( final String implementationKey )
    {
        return new ComponentInfo( implementationKey,
                                  ServiceDescriptor.EMPTY_SET,
                                  DependencyDescriptor.EMPTY_SET,
                                  null );
    }
}
