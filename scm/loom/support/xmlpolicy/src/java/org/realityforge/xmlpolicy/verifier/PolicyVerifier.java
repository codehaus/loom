/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.realityforge.xmlpolicy.verifier;

import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.realityforge.xmlpolicy.metadata.GrantMetaData;
import org.realityforge.xmlpolicy.metadata.KeyStoreMetaData;
import org.realityforge.xmlpolicy.metadata.PermissionMetaData;
import org.realityforge.xmlpolicy.metadata.PolicyMetaData;

/**
 * Verify Policy set is valid. Validity is defined as
 * <ul>
 *   <li>All KeyStore names should be defined starting with
 *       letters or '_' and then continuing with Alpha-Numeric
 *       characters, '-', '.' or '_'.</li>
 *   <li>If signedBy is specified then keystore is specified
 *       for both grants and permissions.</li>
 *   <li>That any keystore names used by grant or permission
 *       reference actual keystores.</li>
 *   <li>If target is null then actions is null.</li>
 * </ul>
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.2 $ $Date: 2003-06-04 22:50:46 $
 */
public class PolicyVerifier
{
    private final static Resources REZ =
        ResourceManager.getPackageResources( PolicyVerifier.class );

    public void verifyPolicy( final PolicyMetaData policy )
        throws Exception
    {
        String message = null;

        message = REZ.getString( "valid-names.notice" );
        info( message );
        verifyNames( policy );

        message = REZ.getString( "valid-keyStoreReferences.notice" );
        info( message );
        verifyKeyStoreReferences( policy );

        message = REZ.getString( "valid-actions.notice" );
        info( message );
        verifyActions( policy );
    }

    /**
     * Log an informational message.
     * Sub-classes should overide this.
     *
     * @param message the message
     */
    protected void info( final String message )
    {
        //noop
    }

    /**
     * Verify that all the keystores have valid names.
     *
     * @throws Exception if validity check fails
     */
    private void verifyNames( final PolicyMetaData policy )
        throws Exception
    {
        final KeyStoreMetaData[] keyStores = policy.getKeyStores();
        for( int i = 0; i < keyStores.length; i++ )
        {
            final String name = keyStores[ i ].getName();
            verifyName( name );
        }
    }

    /**
     * Verify that each reference to a keystore is valid.
     *
     * @throws Exception if validity check fails
     */
    private void verifyKeyStoreReferences( final PolicyMetaData policy )
        throws Exception
    {
        final GrantMetaData[] grants = policy.getGrants();
        for( int i = 0; i < grants.length; i++ )
        {
            verifyKeyStore( policy, grants[ i ] );
        }
    }

    /**
     * Verify that each reference to a keystore is valid.
     *
     * @throws Exception if validity check fails
     */
    private void verifyKeyStore( final PolicyMetaData policy,
                                 final GrantMetaData grant )
        throws Exception
    {
        verifyKeyStoreReference( policy, grant.getKeyStore() );
        final PermissionMetaData[] permissions = grant.getPermissions();
        for( int j = 0; j < permissions.length; j++ )
        {
            final PermissionMetaData permission = permissions[ j ];
            verifyKeyStoreReference( policy, permission.getKeyStore() );
        }
    }

    /**
     * Verify that each reference to a keystore is valid.
     *
     * @throws Exception if validity check fails
     */
    private void verifyKeyStoreReference( final PolicyMetaData policy,
                                          final String keyStoreName )
        throws Exception
    {
        //Ignore keystores that are not specified
        if( null == keyStoreName )
        {
            return;
        }
        final KeyStoreMetaData[] keyStores = policy.getKeyStores();
        for( int i = 0; i < keyStores.length; i++ )
        {
            final KeyStoreMetaData keyStore = keyStores[ i ];
            if( keyStore.getName().equals( keyStoreName ) )
            {
                return;
            }
        }

        final String message =
            REZ.getString( "bad-keystore-reference.error",
                           keyStoreName );
        throw new Exception( message );
    }

    /**
     * Verify that all the classloaders have valid names.
     *
     * @throws Exception if validity check fails
     */
    private void verifyName( final String name )
        throws Exception
    {
        final int size = name.length();
        if( 0 == size )
        {
            final String message =
                REZ.getString( "empty-name.error",
                               name );
            throw new Exception( message );
        }
        final char ch = name.charAt( 0 );
        if( !Character.isLetter( ch ) &&
            '_' != ch )
        {
            final String message =
                REZ.getString( "name-invalid-start.error",
                               name );
            throw new Exception( message );
        }

        for( int i = 1; i < size; i++ )
        {
            final char c = name.charAt( i );
            if( !Character.isLetterOrDigit( c ) &&
                '_' != c &&
                '-' != c &&
                '.' != c )
            {
                final String message =
                    REZ.getString( "name-invalid-char.error",
                                   name,
                                   String.valueOf( c ) );
                throw new Exception( message );
            }
        }
    }

    /**
     * Verify that an action is null if a target is null.
     *
     * @throws Exception if validity check fails
     */
    private void verifyActions( final PolicyMetaData policy )
        throws Exception
    {
        final GrantMetaData[] grants = policy.getGrants();
        for( int i = 0; i < grants.length; i++ )
        {
            final GrantMetaData grant = grants[ i ];
            final PermissionMetaData[] permissions = grant.getPermissions();
            for( int j = 0; j < permissions.length; j++ )
            {
                final PermissionMetaData permission = permissions[ j ];
                final String target = permission.getTarget();
                final String action = permission.getAction();
                if( null == target && null != action )
                {
                    final String message =
                        REZ.getString( "permission-missing-action.error",
                                       grant.getCodebase(),
                                       permission.getClassname() );
                    throw new Exception( message );
                }
            }
        }
    }
}
