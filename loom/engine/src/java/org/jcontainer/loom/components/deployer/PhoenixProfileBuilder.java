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
package org.jcontainer.loom.components.deployer;

import java.util.ArrayList;
import java.util.Map;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.jcontainer.loom.components.assembler.Assembler;
import org.jcontainer.loom.interfaces.ContainerConstants;
import org.jcontainer.loom.tools.factory.ComponentBundle;
import org.jcontainer.loom.tools.factory.ComponentFactory;
import org.jcontainer.loom.tools.info.ComponentInfo;
import org.jcontainer.loom.tools.infobuilder.LegacyUtil;
import org.jcontainer.loom.tools.metadata.ComponentMetaData;
import org.jcontainer.loom.tools.metadata.PartitionMetaData;
import org.jcontainer.loom.tools.profile.ProfileBuilder;
import org.jcontainer.loom.tools.profile.PartitionProfile;
import org.jcontainer.loom.tools.profile.ComponentProfile;

/**
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.2 $ $Date: 2003-07-07 13:22:19 $
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

    public PartitionProfile buildProfile( Map parameters )
        throws Exception
    {
        final PartitionMetaData metaData = m_assembler.buildAssembly( parameters );
        final ClassLoader classLoader =
            (ClassLoader)parameters.get( ContainerConstants.ASSEMBLY_CLASSLOADER );
        final ComponentFactory factory = new PhoenixComponentFactory( classLoader );
        setupLogger( factory, "factory" );

        return assembleSarProfile( metaData, factory );
    }

    private PartitionProfile assembleSarProfile( final PartitionMetaData metaData,
                                                 final ComponentFactory factory )
        throws Exception
    {
        final PartitionMetaData blockPartition =
            metaData.getPartition( ContainerConstants.BLOCK_PARTITION );
        final PartitionMetaData listenerPartition =
            metaData.getPartition( ContainerConstants.LISTENER_PARTITION );

        final PartitionProfile blockProfile = assembleProfile( blockPartition, factory );
        final PartitionProfile listenerProfile =
            assembleListenerProfile( listenerPartition );

        final PartitionProfile[] profiles = new PartitionProfile[]{blockProfile, listenerProfile};
        return new PartitionProfile( metaData,
                                     profiles,
                                     ComponentProfile.EMPTY_SET );
    }

    private org.jcontainer.loom.tools.profile.PartitionProfile assembleListenerProfile( final PartitionMetaData metaData )
    {
        final ArrayList componentSet = new ArrayList();
        final ComponentMetaData[] components = metaData.getComponents();
        for( int i = 0; i < components.length; i++ )
        {
            final ComponentMetaData component = components[ i ];
            final ComponentInfo info =
                LegacyUtil.createListenerInfo( component.getImplementationKey() );
            final ComponentProfile profile = new org.jcontainer.loom.tools.profile.ComponentProfile( info, component );
            componentSet.add( profile );
        }

        final ComponentProfile[] profiles =
            (ComponentProfile[])componentSet.toArray( new ComponentProfile[ componentSet.size() ] );
        return new PartitionProfile( metaData, PartitionProfile.EMPTY_SET, profiles );
    }

    private org.jcontainer.loom.tools.profile.PartitionProfile assembleProfile( final PartitionMetaData metaData,
                                                                                final ComponentFactory factory )
        throws Exception
    {
        final ArrayList partitionSet = new ArrayList();
        final PartitionMetaData[] partitions = metaData.getPartitions();
        for( int i = 0; i < partitions.length; i++ )
        {
            final PartitionMetaData partition = partitions[ i ];
            final org.jcontainer.loom.tools.profile.PartitionProfile profile = assembleProfile( partition, factory );
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
            final org.jcontainer.loom.tools.profile.ComponentProfile profile = new org.jcontainer.loom.tools.profile.ComponentProfile( info, component );
            componentSet.add( profile );
        }

        final org.jcontainer.loom.tools.profile.PartitionProfile[] partitionProfiles =
            (org.jcontainer.loom.tools.profile.PartitionProfile[])partitionSet.toArray( new org.jcontainer.loom.tools.profile.PartitionProfile[ partitionSet.size() ] );
        final org.jcontainer.loom.tools.profile.ComponentProfile[] componentProfiles =
            (org.jcontainer.loom.tools.profile.ComponentProfile[])componentSet.toArray( new org.jcontainer.loom.tools.profile.ComponentProfile[ componentSet.size() ] );
        return new org.jcontainer.loom.tools.profile.PartitionProfile( metaData, partitionProfiles, componentProfiles );
    }
}
