/* ====================================================================
 * JContainer Software License, version 1.1
 *
 * Copyright (c) 2003, JContainer Group. All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. Neither the name of the JContainer Group nor the name "Loom" nor
 *    the names of its contributors may be used to endorse or promote
 *    products derived from this software without specific prior
 *    written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * ====================================================================
 *
 * JContainer Loom includes code from the Apache Software Foundation
 *
 * ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1997-2003 The Apache Software Foundation. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *    "This product includes software developed by the
 *    Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software
 *    itself, if and wherever such third-party acknowledgments
 *    normally appear.
 *
 * 4. The names "Jakarta", "Avalon", and "Apache Software Foundation"
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
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
import org.jcontainer.loom.tools.LoomToolConstants;

/**
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.6 $ $Date: 2003-08-17 18:27:32 $
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
                LegacyUtil.createListenerInfo( component.getImplementationKey() );
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
}
