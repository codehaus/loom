/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 *
 * This product includes software developed by the
�*�Apache Software Foundation (http://www.apache.org/).
 */
package org.jcontainer.loom.tools.verifier;

import org.apache.avalon.framework.logger.Logger;
import org.jcontainer.loom.tools.LoomToolConstants;
import org.jcontainer.loom.tools.metadata.ComponentMetaData;
import org.jcontainer.loom.tools.profile.ComponentProfile;
import org.jcontainer.loom.tools.profile.PartitionProfile;
import org.realityforge.salt.i18n.ResourceManager;
import org.realityforge.salt.i18n.Resources;

/**
 * This Class verifies that Sars are valid. It performs a number
 * of checks to make sure that the Sar represents a valid
 * application and excluding runtime errors will start up validly.
 * Some of the checks it performs include;
 *
 * <ul>
 *   <li>Verify names of Sar, Blocks and BlockListeners contain only
 *       letters, digits or the '_' character.</li>
 *   <li>Verify that the names of the Blocks and BlockListeners are
 *       unique to Sar.</li>
 *   <li>Verify that the dependendencies specified in assembly.xml
 *       correspond to dependencies specified in BlockInfo files.</li>
 *   <li>Verify that the inter-block dependendencies specified in
 *       assembly.xml are valid. This essentially means that if
 *       Block A requires Service S from Block B then Block B must
 *       provide Service S.</li>
 *   <li>Verify that there are no circular dependendencies between
 *       blocks.</li>
 *   <li>Verify that the Class objects for Blocks support the Block
 *       interface and any specified Services.</li>
 *   <li>Verify that the Class objects for BlockListeners support the
 *       BlockListener interface.</li>
 * </ul>
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.11 $ $Date: 2003-07-07 13:13:00 $
 */
public class SarVerifier
    extends AssemblyVerifier
{
    /**
     * I18n utils.
     */
    private static final Resources REZ =
        ResourceManager.getPackageResources( SarVerifier.class );

    /**
     * The Block class used to warn about deprecation.
     */
    private static final Class BLOCK_CLASS =
        getClass( "org.apache.avalon.phoenix.Block" );

    /**
     * The BlockListener class used to warn about deprecation.
     */
    private static final Class BLOCKLISTENER_CLASS =
        getClass( "org.apache.avalon.phoenix.BlockListener" );

    /**
     * The Verifier for Info instances.
     */
    private final InfoVerifier m_infoVerifier = new InfoVerifier();

    public void enableLogging( final Logger logger )
    {
        super.enableLogging( logger );
        m_infoVerifier.enableLogging( logger );
    }

    /**
     * Verify the specified {@link PartitionProfile} object.
     * The rules used to verify {@link PartitionProfile} are specified
     * in the Class javadocs.
     *
     * @param profile the Sar profile
     * @param classLoader the classloader to load types from
     * @throws VerifyException if an error occurs
     */
    public void verifySar( final PartitionProfile profile,
                           final ClassLoader classLoader )
        throws VerifyException
    {
        final ComponentProfile[] blocks =
            profile.getPartition( LoomToolConstants.BLOCK_PARTITION ).getComponents();
        final ComponentProfile[] listeners =
            profile.getPartition( LoomToolConstants.LISTENER_PARTITION ).getComponents();

        String message;

        message = REZ.getString( "verify-valid-names" );
        getLogger().info( message );
        verifySarName( profile.getMetaData().getName() );
        verifyValidNames( listeners );

        super.verifyAssembly( blocks );

        message = REZ.getString( "verify-unique-names" );
        getLogger().info( message );
        checkNamesUnique( blocks, listeners );

        message = REZ.getString( "verify-block-type" );
        getLogger().info( message );
        verifyBlocksType( blocks, classLoader );

        message = REZ.getString( "verify-listener-type" );
        getLogger().info( message );
        verifyListenersType( listeners, classLoader );
    }

    /**
     * Verfiy that all Blocks specify classes that implement the
     * advertised interfaces.
     *
     * @param blocks the ComponentProfile objects for the blocks
     * @param classLoader the Classloader to load type from
     * @throws VerifyException if an error occurs
     */
    private void verifyBlocksType( final ComponentProfile[] blocks,
                                   final ClassLoader classLoader )
        throws VerifyException
    {
        for( int i = 0; i < blocks.length; i++ )
        {
            verifyBlockType( blocks[ i ], classLoader );
        }
    }

    /**
     * Verfiy that specified Block designate classes that implement the
     * advertised interfaces.
     *
     * @param block the BlockMetaData object for the blocks
     * @param classLoader the classloader to load type from
     * @throws VerifyException if an error occurs
     */
    private void verifyBlockType( final ComponentProfile block,
                                  final ClassLoader classLoader )
        throws VerifyException
    {
        final ComponentMetaData metaData = block.getMetaData();
        final Class clazz = loadClass( "block", metaData, classLoader );

        m_infoVerifier.verifyType( metaData.getName(),
                                   metaData.getImplementationKey(),
                                   block.getInfo(),
                                   classLoader );

        if( BLOCK_CLASS.isAssignableFrom( clazz ) )
        {
            final String message =
                REZ.format( "verifier.implements-block.error",
                            metaData.getName(),
                            metaData.getImplementationKey() );
            getLogger().error( message );
            System.err.println( message );
        }
    }

    private Class loadClass( final String type,
                             final ComponentMetaData metaData,
                             final ClassLoader classLoader )
        throws VerifyException
    {
        try
        {
            return classLoader.loadClass( metaData.getImplementationKey() );
        }
        catch( final Exception e )
        {
            final String message =
                REZ.format( "bad-" + type + "-class",
                            metaData.getName(),
                            metaData.getImplementationKey(),
                            e.getMessage() );
            throw new VerifyException( message );
        }
    }

    /**
     * Verfiy that all listeners implement BlockListener.
     *
     * @param listeners the BlockListenerMetaData objects for the listeners
     * @param classLoader the classloader to load type from
     * @throws VerifyException if an error occurs
     */
    private void verifyListenersType( final ComponentProfile[] listeners,
                                      final ClassLoader classLoader )
        throws VerifyException
    {
        for( int i = 0; i < listeners.length; i++ )
        {
            verifyListenerType( listeners[ i ], classLoader );
        }
    }

    /**
     * Verfiy that specified Listener class implements the BlockListener interface.
     *
     * @param listener the BlockListenerMetaData object for the listener
     * @param classLoader the classloader to laod type from
     * @throws VerifyException if an error occurs
     */
    private void verifyListenerType( final ComponentProfile listener,
                                     final ClassLoader classLoader )
        throws VerifyException
    {
        final ComponentMetaData metaData = listener.getMetaData();
        final Class clazz = loadClass( "listener", metaData, classLoader );
        if( !BLOCKLISTENER_CLASS.isAssignableFrom( clazz ) )
        {
            final String message =
                REZ.format( "listener-noimpl-listener",
                            metaData,
                            metaData );
            throw new VerifyException( message );
        }
    }

    /**
     * Verify that the Sat name specified is valid.
     *
     * @param name the sar name
     * @throws VerifyException if an error occurs
     */
    private void verifySarName( final String name )
        throws VerifyException
    {
        if( !isValidName( name ) )
        {
            final String message = REZ.format( "invalid-sar-name", name );
            throw new VerifyException( message );
        }
    }

    /**
     * Return true if specified name is valid.
     * Valid names consist of letters, digits or the '-' & '.' characters.
     *
     * @param name the name to check
     * @return true if valid, false otherwise
     */
    public boolean isValidName( final String name )
    {
        final int size = name.length();
        for( int i = 0; i < size; i++ )
        {
            final char ch = name.charAt( i );

            if( !Character.isLetterOrDigit( ch ) && '-' != ch && '.' != ch )
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Verify that the names of the specified blocks and listeners are unique.
     * It is not valid for the same name to be used in multiple Blocks and or
     * BlockListeners.
     *
     * @param blocks the Blocks
     * @param listeners the listeners
     * @throws VerifyException if an error occurs
     */
    private void checkNamesUnique( final ComponentProfile[] blocks,
                                   final ComponentProfile[] listeners )
        throws VerifyException
    {
        for( int i = 0; i < blocks.length; i++ )
        {
            final String name = blocks[ i ].getMetaData().getName();
            checkNameUnique( name, blocks, listeners, i, -1 );
        }

        for( int i = 0; i < listeners.length; i++ )
        {
            final String name = listeners[ i ].getMetaData().getName();
            checkNameUnique( name, blocks, listeners, -1, i );
        }
    }

    /**
     * Verify that the specified name is unique among specified blocks
     * and listeners except for those indexes specified.
     *
     * @param name the name to check for
     * @param blocks the Blocks
     * @param listeners the listeners
     * @param blockIndex the index of block that is allowed to
     *                   match in name (or -1 if name designates a listener)
     * @param listenerIndex the index of listener that is allowed to
     *                      match in name (or -1 if name designates a block)
     * @throws VerifyException if an error occurs
     */
    private void checkNameUnique( final String name,
                                  final ComponentProfile[] blocks,
                                  final ComponentProfile[] listeners,
                                  final int blockIndex,
                                  final int listenerIndex )
        throws VerifyException
    {
        //Verify no blocks have the same name
        for( int i = 0; i < blocks.length; i++ )
        {
            final String other = blocks[ i ].getMetaData().getName();
            if( blockIndex != i && name.equals( other ) )
            {
                final String message = REZ.format( "duplicate-name", name );
                throw new VerifyException( message );
            }
        }

        //Verify no listeners have the same name
        for( int i = 0; i < listeners.length; i++ )
        {
            final String other = listeners[ i ].getMetaData().getName();
            if( listenerIndex != i && name.equals( other ) )
            {
                final String message = REZ.format( "duplicate-name", name );
                throw new VerifyException( message );
            }
        }
    }

    /**
     * Load class with specified name and throw a
     * IllegalStateException if unable to load class.
     *
     * @param classname the name of class to load
     * @return the Class object
     */
    private static Class getClass( final String classname )
    {
        try
        {
            return Class.forName( classname );
        }
        catch( final ClassNotFoundException cnfe )
        {
            final String message = "Unable to locate class " +
                classname + " due to " + cnfe;
            throw new IllegalStateException( message );
        }
    }
}