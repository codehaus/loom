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
import org.jcontainer.loom.interfaces.ContainerConstants;
import org.jcontainer.loom.tools.info.Attribute;
import org.jcontainer.loom.tools.info.ComponentInfo;
import org.jcontainer.loom.tools.metadata.ComponentMetaData;
import org.jcontainer.loom.tools.LoomToolConstants;
import org.jcontainer.loom.tools.profile.ComponentProfile;
import org.jcontainer.loom.tools.profile.PartitionProfile;

/**
 * Convert a {@link ComponentMetaData} into a {@link BlockMetaData}.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version $Revision: 1.4 $ $Date: 2003-07-19 02:12:06 $
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
            partition.getPartition( LoomToolConstants.BLOCK_PARTITION );
        final PartitionProfile listenerPartition =
            partition.getPartition( LoomToolConstants.LISTENER_PARTITION );
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
     * Convert a set of {@link ComponentProfile} object
     * into a set of {@link BlockMetaData} objects.
     *
     * @param components the {@link ComponentProfile} objects
     * @return the {@link BlockMetaData} objects
     */
    private static BlockMetaData[] toBlocks( final ComponentProfile[] components )
    {
        final ArrayList listenerSet = new ArrayList();
        for( int i = 0; i < components.length; i++ )
        {
            final ComponentProfile component = components[ i ];
            final BlockMetaData block =
                toBlockMetaData( component.getMetaData(), component.getInfo() );
            listenerSet.add( block );
        }
        return (BlockMetaData[])listenerSet.toArray( new BlockMetaData[ listenerSet.size() ] );
    }

    /**
     * Convert a {@link ComponentMetaData} object into a {@link BlockListenerMetaData} object.
     *
     * @param listeners the {@link ComponentMetaData} object
     * @return the {@link BlockListenerMetaData} object
     */
    public static BlockListenerMetaData[] toBlockListeners( final ComponentMetaData[] listeners )
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
     * Convert a {@link ComponentMetaData} object into a {@link BlockMetaData} object.
     *
     * @param component the {@link ComponentMetaData} object
     * @return the {@link BlockMetaData} object
     */
    public static BlockMetaData toBlockMetaData( final ComponentMetaData component,
                                                 final ComponentInfo info )
    {
        final boolean disableProxy = isDisableProxy( component );
        final DependencyMetaData[] dependencies =
            toPhoenixDependencys( component.getDependencies() );

        final BlockInfo blockInfo = ComponentInfoConverter.toBlockInfo( info );
        return new BlockMetaData( component.getName(),
                                  dependencies,
                                  disableProxy,
                                  blockInfo );
    }

    /**
     * Determine whether the proxy disabled flag should be
     * true for specified component.
     *
     * @param component the component
     * @return true if proxy should be disabled, false otherwise
     */
    private static boolean isDisableProxy( final ComponentMetaData component )
    {
        final Attribute[] attributes = component.getAttributes();
        for( int i = 0; i < attributes.length; i++ )
        {
            final Attribute attribute = attributes[ i ];
            if( attribute.getName().equals( ContainerConstants.DISABLE_PROXY_ATTR ) )
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Convert ContainerKit dependencys to Phoenix dependencys.
     *
     * @param dependencies the ContainerKit dependencys
     * @return the Phoenix dependencys
     */
    private static DependencyMetaData[] toPhoenixDependencys(
        final org.jcontainer.loom.tools.metadata.DependencyMetaData[] dependencies )
    {
        final ArrayList depends = new ArrayList();
        for( int i = 0; i < dependencies.length; i++ )
        {
            final DependencyMetaData dependency =
                new DependencyMetaData( dependencies[ i ].getProviderName(),
                                        dependencies[ i ].getKey(),
                                        dependencies[ i ].getAlias() );
            depends.add( dependency );
        }
        return (DependencyMetaData[])depends.toArray( new DependencyMetaData[ depends.size() ] );
    }
}
