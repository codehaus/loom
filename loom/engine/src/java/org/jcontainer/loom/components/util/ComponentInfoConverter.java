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
package org.jcontainer.loom.components.util;

import java.util.ArrayList;
import org.apache.avalon.framework.Version;
import org.apache.avalon.phoenix.metainfo.BlockDescriptor;
import org.apache.avalon.phoenix.metainfo.BlockInfo;
import org.apache.avalon.phoenix.metainfo.DependencyDescriptor;
import org.apache.avalon.phoenix.metainfo.ServiceDescriptor;
import org.jcontainer.loom.tools.info.ComponentDescriptor;
import org.jcontainer.loom.tools.info.ComponentInfo;
import org.jcontainer.loom.tools.info.SchemaDescriptor;
import org.jcontainer.loom.tools.infobuilder.LegacyUtil;

/**
 * Convert a {@link org.jcontainer.loom.tools.info.ComponentInfo} into a {@link BlockInfo}.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003-06-29 04:38:22 $
 */
public class ComponentInfoConverter
{
    private ComponentInfoConverter()
    {
    }

    /**
     * Convert a ComponentInfo object into a BlockInfo object.
     *
     * @param component the ComponentInfo object
     * @return the BlockInfo object
     */
    public static BlockInfo toBlockInfo( final ComponentInfo component )
    {
        final BlockDescriptor descriptor = toBlockDescriptor( component );
        final ServiceDescriptor[] services = toPhoenixServices( component.getServices() );
        final ServiceDescriptor[] mxServices = getMXServices( component.getServices() );
        final DependencyDescriptor[] dependencys =
            toPhoenixDependencys( component.getDependencies() );

        return new BlockInfo( descriptor,
                              services,
                              mxServices,
                              dependencys );
    }

    /**
     * Return Phoenix Management services from Info Service array.
     *
     * @param services the services
     * @return the management services
     */
    private static ServiceDescriptor[] getMXServices(
        final org.jcontainer.loom.tools.info.ServiceDescriptor[] services )
    {
        final ArrayList serviceSet = new ArrayList();
        for( int i = 0; i < services.length; i++ )
        {
            if( LegacyUtil.isMxService( services[ i ] ) )
            {
                serviceSet.add( toPhoenixService( services[ i ] ) );
            }
        }
        return (ServiceDescriptor[])serviceSet.toArray( new ServiceDescriptor[ serviceSet.size() ] );
    }

    /**
     * Return Phoenix services from Info Service array.
     *
     * @param services the services
     * @return the Phoenix services
     */
    private static ServiceDescriptor[] toPhoenixServices(
        final org.jcontainer.loom.tools.info.ServiceDescriptor[] services )
    {
        final ArrayList serviceSet = new ArrayList();
        for( int i = 0; i < services.length; i++ )
        {
            if( !LegacyUtil.isMxService( services[ i ] ) )
            {
                serviceSet.add( toPhoenixService( services[ i ] ) );
            }
        }
        return (ServiceDescriptor[])serviceSet.toArray( new ServiceDescriptor[ serviceSet.size() ] );
    }

    /**
     * Convert Info service to Phoenix Service descriptor.
     *
     * @param service the Info Service
     * @return the Phoenix service
     */
    private static ServiceDescriptor toPhoenixService(
        final org.jcontainer.loom.tools.info.ServiceDescriptor service )
    {
        final Version version = LegacyUtil.toVersion( service );
        return new ServiceDescriptor( service.getType(), version );
    }

    /**
     * Convert Info dependencys to Phoenix dependencys.
     *
     * @param dependencies the Info dependencys
     * @return the Phoenix dependencys
     */
    private static DependencyDescriptor[] toPhoenixDependencys(
        final org.jcontainer.loom.tools.info.DependencyDescriptor[] dependencies )
    {
        final ArrayList depends = new ArrayList();
        for( int i = 0; i < dependencies.length; i++ )
        {
            depends.add( toPhoenixDependency( dependencies[ i ] ) );
        }
        return (DependencyDescriptor[])depends.toArray( new DependencyDescriptor[ depends.size() ] );
    }

    /**
     * Convert Info dependency to Phoenix dependency descriptor.
     *
     * @param dependency the Info dependency
     * @return the Phoenix dependency
     */
    private static DependencyDescriptor toPhoenixDependency(
        final org.jcontainer.loom.tools.info.DependencyDescriptor dependency )
    {
        final Version version = LegacyUtil.toVersion( dependency );
        final ServiceDescriptor service =
            new ServiceDescriptor( dependency.getType(), version );
        return new DependencyDescriptor( dependency.getKey(), service );
    }

    /**
     * Create a BlockDescriptor object from ComponentInfo.
     *
     * @param component the info
     * @return the BlockDescriptor
     */
    private static BlockDescriptor toBlockDescriptor( final ComponentInfo component )
    {
        final ComponentDescriptor descriptor = component.getDescriptor();
        final Version version = LegacyUtil.toVersion( descriptor );

        final SchemaDescriptor schema = component.getConfigurationSchema();
        String schemaType = null;
        if( null != schema )
        {
            schemaType = schema.getType();
        }

        return new BlockDescriptor( null,
                                    descriptor.getImplementationKey(),
                                    schemaType,
                                    version );
    }
}
