/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.components.util;

import java.util.ArrayList;
import org.apache.avalon.framework.Version;
import org.apache.avalon.phoenix.metainfo.BlockDescriptor;
import org.apache.avalon.phoenix.metainfo.BlockInfo;
import org.apache.avalon.phoenix.metainfo.DependencyDescriptor;
import org.apache.avalon.phoenix.metainfo.ServiceDescriptor;
import org.jcontainer.loom.components.util.info.ComponentInfo;
import org.jcontainer.loom.components.util.info.SchemaDescriptor;

/**
 * Convert a ComponentInfo into a BlockInfo.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version $Revision: 1.10 $ $Date: 2003-11-03 06:11:26 $
 */
public class ComponentInfoConverter
{
    private static final Version VERSION = new Version( 1, 0, 0 );

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
        final ServiceDescriptor[] mxServices = new ServiceDescriptor[ 0 ];
        final DependencyDescriptor[] dependencys =
            toPhoenixDependencys( component.getDependencies() );

        return new BlockInfo( descriptor,
                              services,
                              mxServices,
                              dependencys );
    }

    /**
     * Return Phoenix services from Info Service array.
     *
     * @param services the services
     * @return the Phoenix services
     */
    private static ServiceDescriptor[] toPhoenixServices(
        final org.jcontainer.loom.components.util.info.ServiceDescriptor[] services )
    {
        final ArrayList serviceSet = new ArrayList();
        for( int i = 0; i < services.length; i++ )
        {
            serviceSet.add( toPhoenixService( services[ i ] ) );
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
        final org.jcontainer.loom.components.util.info.ServiceDescriptor service )
    {
        return new ServiceDescriptor( service.getType(), VERSION );
    }

    /**
     * Convert Info dependencys to Phoenix dependencys.
     *
     * @param dependencies the Info dependencys
     * @return the Phoenix dependencys
     */
    private static DependencyDescriptor[] toPhoenixDependencys(
        final org.jcontainer.loom.components.util.info.DependencyDescriptor[] dependencies )
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
        final org.jcontainer.loom.components.util.info.DependencyDescriptor dependency )
    {
        final ServiceDescriptor service =
            new ServiceDescriptor( dependency.getType(), VERSION );
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
        final SchemaDescriptor schema = component.getConfigurationSchema();
        String schemaType = null;
        if( null != schema )
        {
            schemaType = schema.getType();
        }

        return new BlockDescriptor( null,
                                    component.getType().getName(),
                                    schemaType,
                                    VERSION );
    }
}
