/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.realityforge.xmlpolicy.builder;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.CodeSource;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Permission;
import java.security.Policy;
import java.security.UnresolvedPermission;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PropertyPermission;
import java.util.StringTokenizer;
import org.realityforge.xmlpolicy.metadata.GrantMetaData;
import org.realityforge.xmlpolicy.metadata.KeyStoreMetaData;
import org.realityforge.xmlpolicy.metadata.PermissionMetaData;
import org.realityforge.xmlpolicy.metadata.PolicyMetaData;

/**
 * A Utility class that builds a Policy object from a specified
 * PolicyMetaData.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003-04-16 11:45:58 $
 */
public class PolicyBuilder
{
    /**
     * Build a policy for a specified meta data.
     *
     * @param policy the policy metadata
     * @return the Policy object
     * @throws Exception if unable to create Policy object
     */
    public Policy buildPolicy( final PolicyMetaData policy,
                               final PolicyResolver resolver )
        throws Exception
    {
        final Map keyStores =
            createKeyStores( policy.getKeyStores(), resolver );
        final Map grants = new HashMap();
        processGrants( policy.getGrants(), keyStores, grants, resolver );

        final CodeSource codeSource = createDefaultCodeSource();
        final Permission[] permissions = getDefaultPermissions();
        grants.put( codeSource, permissions );

        return resolver.createPolicy( grants );
    }

    /**
     * Porcess all the grants and build up a grant map.
     *
     * @param metaDatas the metadata
     * @param keyStores the configured keystores
     * @param grants the grant map
     * @param resolver the resolver to use to resolve locations etc
     * @throws Exception if unable to create grant map
     */
    private void processGrants( final GrantMetaData[] metaDatas,
                                final Map keyStores,
                                final Map grants,
                                final PolicyResolver resolver )
        throws Exception
    {
        for( int i = 0; i < metaDatas.length; i++ )
        {
            processGrant( metaDatas[ i ], keyStores, grants, resolver );
        }
    }

    /**
     * Porcess a grants and add to the grant map.
     *
     * @param metaData the metadata
     * @param keyStores the configured keystores
     * @param grants the grant map
     * @param resolver the resolver to use to resolve locations etc
     * @throws Exception if unable to create grant map
     */
    private void processGrant( final GrantMetaData metaData,
                               final Map keyStores,
                               final Map grants,
                               final PolicyResolver resolver )
        throws Exception
    {
        final URL url =
            resolver.resolveLocation( metaData.getCodebase() );

        final Certificate[] signers =
            getSigners( metaData.getSignedBy(),
                        metaData.getKeyStore(),
                        keyStores );
        final CodeSource codeSource = new CodeSource( url, signers );

        final Permission[] permissions =
            createPermissions( metaData.getPermissions(),
                               keyStores );
        grants.put( codeSource, permissions );
    }

    /**
     * Create all permissions for specified metadata.
     *
     * @param metaDatas the metadata
     * @param keyStores the keystores to use when loading signers
     * @return the created permissions
     * @throws Exception if unabel to create permissions
     */
    private Permission[] createPermissions( final PermissionMetaData[] metaDatas,
                                            final Map keyStores )
        throws Exception
    {
        final List set = new ArrayList();

        for( int i = 0; i < metaDatas.length; i++ )
        {
            final Permission permission =
                createPermission( metaDatas[ i ], keyStores );
            set.add( permission );
        }

        return (Permission[])set.toArray( new Permission[ set.size() ] );
    }

    /**
     * Create a permission for metadata.
     *
     * @param metaData the permission metadata
     * @param keyStores the keystore to use (if needed)
     * @return the created permission
     * @throws Exception if unable to create permission
     */
    private Permission createPermission( final PermissionMetaData metaData,
                                         final Map keyStores )
        throws Exception
    {
        final String type = metaData.getClassname();
        final String actions = metaData.getAction();
        final String signedBy = metaData.getSignedBy();
        final String keyStoreName = metaData.getKeyStore();

        String target = metaData.getTarget();
        final Certificate[] signers =
            getSigners( signedBy, keyStoreName, keyStores );
        try
        {
            return createPermission( type, target, actions, signers );
        }
        catch( final Exception e )
        {
            throw new Exception( e.getMessage() );
        }
    }

    /**
     * Create a mpa of keystores from specified metadata.
     *
     * @param metaDatas the metadata
     * @return the keystore map
     * @throws Exception if unable to create all keystores
     */
    private Map createKeyStores( final KeyStoreMetaData[] metaDatas,
                                 final PolicyResolver resolver )
        throws Exception
    {
        final Map keyStores = new HashMap();

        for( int i = 0; i < metaDatas.length; i++ )
        {
            final KeyStoreMetaData metaData = metaDatas[ i ];
            final String name = metaData.getName();

            try
            {
                final URL url =
                    resolver.resolveLocation( metaData.getLocation() );
                final KeyStore keyStore =
                    createKeyStore( metaData.getType(), url );

                keyStores.put( name, keyStore );
            }
            catch( final Exception e )
            {
                final String message =
                    "Error creating keystore " + name + ". Due to " + e;
                throw new Exception( message );
            }
        }

        return keyStores;
    }

    /**
     * Create a permission of specified class and
     * with specified target, action and signers.
     *
     * @param type the classname of Permission object
     * @param target the target of permission
     * @param actions the actions allowed on permission (if any)
     * @param signers the signers (if any)
     * @return the created Permission object
     * @throws Exception if unable to create permission
     */
    private final Permission createPermission( final String type,
                                               final String target,
                                               final String actions,
                                               final Certificate[] signers )
        throws Exception
    {
        if( null != signers )
        {
            return new UnresolvedPermission( type, target, actions, signers );
        }

        try
        {
            final Class clazz = Class.forName( type );

            Class paramClasses[] = null;
            Object params[] = null;

            if( null == actions && null == target )
            {
                paramClasses = new Class[ 0 ];
                params = new Object[ 0 ];
            }
            else if( null == actions )
            {
                paramClasses = new Class[ 1 ];
                paramClasses[ 0 ] = String.class;
                params = new Object[ 1 ];
                params[ 0 ] = target;
            }
            else
            {
                paramClasses = new Class[ 2 ];
                paramClasses[ 0 ] = String.class;
                paramClasses[ 1 ] = String.class;
                params = new Object[ 2 ];
                params[ 0 ] = target;
                params[ 1 ] = actions;
            }

            final Constructor constructor = clazz.getConstructor( paramClasses );
            return (Permission)constructor.newInstance( params );
        }
        catch( final ClassNotFoundException cnfe )
        {
            return new UnresolvedPermission( type, target, actions, signers );
        }
    }

    /**
     * A utility method to get a default codesource
     * that covers all files on fielsystem
     *
     * @return the code source
     */
    private CodeSource createDefaultCodeSource()
    {
        //Create a URL that covers whole file system.
        final URL url;
        try
        {
            url = new URL( "file:/-" );
        }
        catch( final MalformedURLException mue )
        {
            //will never happen
            throw new IllegalStateException( mue.getMessage() );
        }
        final CodeSource codeSource = new CodeSource( url, null );
        return codeSource;
    }

    /**
     * A utility method to get all the default permissions.
     */
    private Permission[] getDefaultPermissions()
    {
        final ArrayList list = new ArrayList();
        //these properties straight out ot ${java.home}/lib/security/java.policy
        list.add( new PropertyPermission( "os.name", "read" ) );
        list.add( new PropertyPermission( "os.arch", "read" ) );
        list.add( new PropertyPermission( "os.version", "read" ) );
        list.add( new PropertyPermission( "file.separator", "read" ) );
        list.add( new PropertyPermission( "path.separator", "read" ) );
        list.add( new PropertyPermission( "line.separator", "read" ) );

        list.add( new PropertyPermission( "java.version", "read" ) );
        list.add( new PropertyPermission( "java.vendor", "read" ) );
        list.add( new PropertyPermission( "java.vendor.url", "read" ) );

        list.add( new PropertyPermission( "java.class.version", "read" ) );
        list.add( new PropertyPermission( "java.vm.version", "read" ) );
        list.add( new PropertyPermission( "java.vm.vendor", "read" ) );
        list.add( new PropertyPermission( "java.vm.name", "read" ) );

        list.add( new PropertyPermission( "java.specification.version", "read" ) );
        list.add( new PropertyPermission( "java.specification.vendor", "read" ) );
        list.add( new PropertyPermission( "java.specification.name", "read" ) );
        list.add( new PropertyPermission( "java.vm.specification.version", "read" ) );
        list.add( new PropertyPermission( "java.vm.specification.vendor", "read" ) );
        list.add( new PropertyPermission( "java.vm.specification.name", "read" ) );

        return (Permission[])list.toArray( new Permission[ list.size() ] );
    }

    /**
     * Create a keystore of specified type and loading from specified url.
     *
     * @param type the type of key store
     * @param url the location of key store data
     * @return the create and configured keystore
     * @throws Exception if unable to create or load keystore
     */
    private final KeyStore createKeyStore( final String type,
                                           final URL url )
        throws Exception
    {
        final KeyStore keyStore = KeyStore.getInstance( type );
        final InputStream ins = url.openStream();
        keyStore.load( ins, null );
        return keyStore;
    }

    /**
     * Retrieve Certificates for specified signers
     * as loaded from keyStore.
     *
     * @param signedBy the signers
     * @param keyStoreName the name of keystore
     * @param keyStores the list of keystores to lookup
     * @return the certificates
     * @throws Exception if unable to get signers
     */
    private Certificate[] getSigners( final String signedBy,
                                      final String keyStoreName,
                                      final Map keyStores )
        throws Exception
    {
        if( null == signedBy )
        {
            return null;
        }
        else
        {
            final KeyStore keyStore = getKeyStore( keyStoreName, keyStores );
            return getCertificates( signedBy, keyStore );
        }
    }

    /**
     * Retrieve the set of Ceritificates for all signers.
     *
     * @param signedBy the comma separated list of signers
     * @param keyStore the keystore to look for signers certificates in
     * @return the certificate set
     * @throws Exception if unabel to create certificates
     */
    private Certificate[] getCertificates( final String signedBy,
                                           final KeyStore keyStore )
        throws Exception
    {
        final List certificateSet = new ArrayList();

        final StringTokenizer st = new StringTokenizer( signedBy, "," );
        while( st.hasMoreTokens() )
        {
            final String alias = st.nextToken().trim();
            Certificate certificate = null;

            try
            {
                certificate = keyStore.getCertificate( alias );
            }
            catch( final KeyStoreException kse )
            {
                final String message =
                    "Unable to get certificate for alias " +
                    alias + " due to " + kse;
                throw new Exception( message );
            }

            if( null == certificate )
            {
                final String message =
                    "Missing certificate for alias " + alias;
                throw new Exception( message );
            }

            if( !certificateSet.contains( certificate ) )
            {
                certificateSet.add( certificate );
            }
        }

        return (Certificate[])certificateSet.toArray( new Certificate[ certificateSet.size() ] );
    }

    /**
     * Retrieve keystore with specified name from map.
     * If missing throw an exception.
     *
     * @param keyStoreName the name of key store
     * @param keyStores the map of stores
     * @return the keystore
     * @throws Exception thrown if unable to locate keystore
     */
    private KeyStore getKeyStore( final String keyStoreName, final Map keyStores ) throws Exception
    {
        final KeyStore keyStore = (KeyStore)keyStores.get( keyStoreName );
        if( null == keyStore )
        {
            final String message = "Missing keystore named: " + keyStoreName;
            throw new Exception( message );
        }
        else
        {
            return keyStore;
        }
    }
}
