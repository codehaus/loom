/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.components.util;

import java.io.File;
import java.util.ArrayList;
import org.apache.avalon.phoenix.metadata.BlockListenerMetaData;
import org.apache.avalon.phoenix.metadata.BlockMetaData;
import org.apache.avalon.phoenix.metadata.DependencyMetaData;
import org.apache.avalon.phoenix.metadata.SarMetaData;
import org.apache.avalon.phoenix.metainfo.BlockInfo;
import org.jcontainer.loom.components.util.info.ComponentInfo;
import org.jcontainer.loom.components.util.metadata.ComponentTemplate;
import org.jcontainer.loom.components.util.profile.ComponentProfile;
import org.jcontainer.loom.components.util.profile.PartitionProfile;
import org.jcontainer.loom.interfaces.ContainerConstants;

/**
 * Convert a ComponentTemplate into a BlockMetaData.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version $Revision: 1.9 $ $Date: 2003-11-27 09:05:31 $
 */
public class ComponentMetaDataConverter
{
    private ComponentMetaDataConverter()
    {
    }

    public static SarMetaData toSarMetaData( final PartitionProfile partition,
                                             final File homeDirectory )
    {
        final PartitionProfile blockPartition =
            partition.getPartition( ContainerConstants.BLOCK_PARTITION );
        final PartitionProfile listenerPartition =
            partition.getPartition( ContainerConstants.LISTENER_PARTITION );
        final BlockListenerMetaData[] listeners =
            toBlockListeners( listenerPartition.getMetaData().getComponents() );
        final BlockMetaData[] blocks =
            toBlocks( blockPartition.getComponents() );
        return new SarMetaData( partition.getMetaData().getName(),
                                homeDirectory,
                                blocks,
                                listeners );
    }

    /**
     * Convert a set of {@link org.jcontainer.loom.components.util.profile.ComponentProfile} object
     * into a set of BlockMetaData objects.
     *
     * @param components the {@link org.jcontainer.loom.components.util.profile.ComponentProfile} objects
     * @return the BlockMetaData objects
     */
    private static BlockMetaData[] toBlocks( final ComponentProfile[] components )
    {
        final ArrayList listenerSet = new ArrayList();
        for( int i = 0; i < components.length; i++ )
        {
            final ComponentProfile component = components[ i ];
            final BlockMetaData block =
                toBlockMetaData( component.getTemplate(), component.getInfo() );
            listenerSet.add( block );
        }
        return (BlockMetaData[])listenerSet.toArray( new BlockMetaData[ listenerSet.size() ] );
    }

    /**
     * Convert a ComponentTemplate object into a {@link BlockListenerMetaData} object.
     *
     * @param listeners the ComponentTemplate object
     * @return the {@link BlockListenerMetaData} object
     */
    public static BlockListenerMetaData[] toBlockListeners( final ComponentTemplate[] listeners )
    {
        final ArrayList listenerSet = new ArrayList();
        for( int i = 0; i < listeners.length; i++ )
        {
            final BlockListenerMetaData listener =
                new BlockListenerMetaData( listeners[ i ].getName(),
                                           listeners[ i ].getImplementationKey() );
            listenerSet.add( listener );
        }
        return (BlockListenerMetaData[])listenerSet.toArray( new BlockListenerMetaData[ listenerSet.size() ] );
    }

    /**
     * Convert a ComponentTemplate object into a BlockMetaData object.
     *
     * @param component the ComponentTemplate object
     * @return the BlockMetaData object
     */
    public static BlockMetaData toBlockMetaData( final ComponentTemplate component,
                                                 final ComponentInfo info )
    {
        final boolean disableProxy = component.isDisableProxy();
        final DependencyMetaData[] dependencies =
            toPhoenixDependencys( component.getDependencies() );

        final BlockInfo blockInfo = ComponentInfoConverter.toBlockInfo( info );
        return new BlockMetaData( component.getName(),
                                  dependencies,
                                  disableProxy,
                                  blockInfo );
    }

    /**
     * Convert ContainerKit dependencys to Phoenix dependencys.
     *
     * @param dependencies the ContainerKit dependencys
     * @return the Phoenix dependencys
     */
    private static DependencyMetaData[] toPhoenixDependencys(
        final org.jcontainer.loom.components.util.metadata.DependencyDirective[] dependencies )
    {
        final ArrayList depends = new ArrayList();
        for( int i = 0; i < dependencies.length; i++ )
        {
            final DependencyMetaData dependency =
                new DependencyMetaData( dependencies[ i ].getProviderName(),
                                        dependencies[ i ].getKey() );
            depends.add( dependency );
        }
        return (DependencyMetaData[])depends.toArray( new DependencyMetaData[ depends.size() ] );
    }
}
