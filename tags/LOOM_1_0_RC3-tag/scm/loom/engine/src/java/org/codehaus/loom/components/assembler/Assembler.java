/* ====================================================================
 * Loom Software License, version 1.1
 *
 * Copyright (c) 2003, Loom Group. All rights reserved.
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
 * 3. Neither the name of the Loom Group nor the name "Loom" nor
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
 * Loom includes code from the Apache Software Foundation
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
package org.codehaus.loom.components.assembler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.loom.components.util.metadata.ComponentTemplate;
import org.codehaus.loom.components.util.metadata.DependencyDirective;
import org.codehaus.loom.components.util.metadata.MetaDataBuilder;
import org.codehaus.loom.components.util.metadata.PartitionTemplate;
import org.codehaus.loom.interfaces.ContainerConstants;
import org.codehaus.loom.interfaces.LoomException;
import org.codehaus.spice.salt.i18n.ResourceManager;
import org.codehaus.spice.salt.i18n.Resources;
import org.codehaus.dna.Configuration;
import org.codehaus.dna.ConfigurationException;

/**
 * Assemble a {@link PartitionTemplate} object from a Configuration object. The
 * Configuration object represents the assembly descriptor and is in the format
 * specified for <tt>assembly.xml</tt> files.
 *
 * @author Peter Donald
 * @version $Revision: 1.3 $ $Date: 2004-08-17 23:14:32 $
 */
public class Assembler
    implements MetaDataBuilder
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( Assembler.class );

    /**
     * Create a {@link PartitionTemplate} object based on specified name and
     * assembly configuration. This implementation takes two parameters. {@link
     * ContainerConstants#ASSEMBLY_NAME} specifies the name of the assembly and
     * {@link ContainerConstants#ASSEMBLY_DESCRIPTOR} specifies the
     * configuration tree to use when assembling Partition.
     *
     * @param parameters the parameters for constructing assembly
     * @return the new PartitionTemplate
     * @throws LoomException if an error occurs
     */
    public PartitionTemplate buildAssembly( final Map parameters )
        throws Exception
    {
        final String name =
            (String)parameters.get( ContainerConstants.ASSEMBLY_NAME );
        final Configuration assembly =
            (Configuration)parameters.get(
                ContainerConstants.ASSEMBLY_DESCRIPTOR );
        final Configuration config =
            (Configuration)parameters.get(
                ContainerConstants.CONFIG_DESCRIPTOR );
        return assembleSar( name, config, assembly );
    }

    /**
     * Create a {@link PartitionTemplate} object based on specified name and
     * assembly configuration.
     *
     * @param name the name of Sar
     * @param assembly the assembly configuration object
     * @return the new PartitionTemplate
     * @throws LoomException if an error occurs
     */
    private PartitionTemplate assembleSar( final String name,
                                           final Configuration config,
                                           final Configuration assembly )
        throws LoomException
    {
        final Configuration[] blockConfig = assembly.getChildren( "block" );
        final ComponentTemplate[] blocks = buildBlocks( blockConfig, config );
        final PartitionTemplate blockPartition =
            new PartitionTemplate( ContainerConstants.BLOCK_PARTITION,
                                   new String[]{
                                       ContainerConstants.LISTENER_PARTITION},
                                   PartitionTemplate.EMPTY_SET,
                                   blocks );

        final Configuration[] listenerConfig = assembly.getChildren(
            "listener" );
        final ComponentTemplate[] listeners = buildBlockListeners(
            listenerConfig, config );
        final PartitionTemplate listenerPartition =
            new PartitionTemplate( ContainerConstants.LISTENER_PARTITION,
                                   new String[ 0 ],
                                   PartitionTemplate.EMPTY_SET,
                                   listeners );

        final PartitionTemplate[] partitions =
            new PartitionTemplate[]{blockPartition, listenerPartition};

        return new PartitionTemplate( name,
                                      new String[ 0 ],
                                      partitions,
                                      ComponentTemplate.EMPTY_SET );
    }

    /**
     * Create an array of {@link ComponentTemplate} objects to represent the
     * &lt;block .../&gt; sections in <tt>assembly.xml</tt>.
     *
     * @param blocks the list of Configuration objects for blocks
     * @return the BlockMetaData array
     * @throws LoomException if an error occurs
     */
    private ComponentTemplate[] buildBlocks( final Configuration[] blocks,
                                             final Configuration config )
        throws LoomException
    {
        final ArrayList blockSet = new ArrayList();
        for( int i = 0; i < blocks.length; i++ )
        {
            blockSet.add( buildBlock( blocks[ i ], config ) );
        }

        return (ComponentTemplate[])blockSet.toArray(
            new ComponentTemplate[ blockSet.size() ] );
    }

    /**
     * Create a single {@link ComponentTemplate} object to represent specified
     * &lt;block .../&gt; section.
     *
     * @param block the Configuration object for block
     * @return the BlockMetaData object
     * @throws LoomException if an error occurs
     */
    private ComponentTemplate buildBlock( final Configuration block,
                                          final Configuration config )
        throws LoomException
    {
        try
        {
            final String name = block.getAttribute( "name" );
            final String classname = block.getAttribute( "class" );
            final Configuration proxy = block.getChild( "proxy" );

            final boolean disableProxy =
                proxy.getAttributeAsBoolean( "disable", false );

            final Configuration[] provides = block.getChildren( "provide" );
            final DependencyDirective[] dependencys = buildDependencies(
                provides );

            final Configuration configuration = config.getChild( name );

            return new ComponentTemplate( name, classname,
                                          dependencys, null,
                                          configuration,
                                          disableProxy );
        }
        catch( final ConfigurationException ce )
        {
            final String message =
                REZ.format( "block-entry-malformed",
                            block.getLocation(),
                            ce.getMessage() );
            throw new LoomException( message, ce );
        }
    }

    /**
     * Create an array of {@link ComponentTemplate} objects to represent the
     * &lt;listener .../&gt; sections in <tt>assembly.xml</tt>.
     *
     * @param listenerConfigs the list of Configuration objects for
     * listenerConfigs
     * @return the array of listeners
     * @throws LoomException if an error occurs
     */
    private ComponentTemplate[] buildBlockListeners(
        final Configuration[] listenerConfigs,
        final Configuration config )
        throws LoomException
    {
        final List listeners = new ArrayList();
        for( int i = 0; i < listenerConfigs.length; i++ )
        {
            final ComponentTemplate listener = buildBlockListener(
                listenerConfigs[ i ], config );
            listeners.add( listener );
        }
        return (ComponentTemplate[])listeners.
            toArray( new ComponentTemplate[ listeners.size() ] );
    }

    /**
     * Create a {@link ComponentTemplate} object to represent the specified
     * &lt;listener .../&gt; section.
     *
     * @param listener the Configuration object for listener
     * @return the BlockListenerMetaData object
     * @throws LoomException if an error occurs
     */
    ComponentTemplate buildBlockListener( final Configuration listener,
                                          final Configuration config )
        throws LoomException
    {
        try
        {
            final String name = listener.getAttribute( "name" );
            final String classname = listener.getAttribute( "class" );
            final Configuration configuration = config.getChild( name );
            return new ComponentTemplate( name, classname,
                                          DependencyDirective.EMPTY_SET,
                                          null,
                                          configuration,
                                          false );
        }
        catch( final ConfigurationException ce )
        {
            final String message =
                REZ.format( "listener-entry-malformed",
                            listener.getLocation(),
                            ce.getMessage() );
            throw new LoomException( message, ce );
        }
    }

    /**
     * Helper method to build an array of DependencyMetaDatas from input config
     * data.
     *
     * @param provides the set of provides elements for block
     * @return the created DependencyDirective array
     * @throws ConfigurationException if config data is malformed
     */
    DependencyDirective[] buildDependencies( final Configuration[] provides )
        throws ConfigurationException
    {
        final ArrayList dependencies = new ArrayList();
        for( int j = 0; j < provides.length; j++ )
        {
            final DependencyDirective directive = buildDependency(
                provides[ j ] );
            dependencies.add( directive );
        }

        return (DependencyDirective[])dependencies.toArray(
            new DependencyDirective[ dependencies.size() ] );
    }

    /**
     * Parse a dependency directive from provide.
     *
     * @param provide the provide element
     * @return the directive
     * @throws ConfigurationException if element malformed
     */
    DependencyDirective buildDependency( final Configuration provide )
        throws ConfigurationException
    {
        final String requiredName = provide.getAttribute( "name" );
        final String alias = provide.getAttribute( "alias", requiredName );
        final String key = provide.getAttribute( "role" );
        return new DependencyDirective( key, requiredName, alias );
    }
}
