/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.realityforge.xmlpolicy.reader;

import java.util.ArrayList;
import org.realityforge.xmlpolicy.metadata.GrantMetaData;
import org.realityforge.xmlpolicy.metadata.KeyStoreMetaData;
import org.realityforge.xmlpolicy.metadata.PermissionMetaData;
import org.realityforge.xmlpolicy.metadata.PolicyMetaData;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This class builds a {@link PolicyMetaData} object from
 * specified XML document.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.3 $ $Date: 2003-06-27 03:45:58 $
 */
public class PolicyReader
{
    /**
     * Build ClassLoader MetaData from a DOM tree.
     *
     * @param element the root element
     * @return the meta data
     * @throws Exception if malformed DOM
     */
    public PolicyMetaData readPolicy( final Element element )
        throws Exception
    {
        final String version = element.getAttribute( "version" );
        if( !"1.0".equals( version ) )
        {
            final String message = "Bad version:" + version;
            throw new Exception( message );
        }

        final NodeList keyStoreConfigs = element.getElementsByTagName( "keystore" );
        final KeyStoreMetaData[] keyStores = buildKeyStores( keyStoreConfigs );

        final NodeList grantConfigs =
            element.getElementsByTagName( "grant" );
        final GrantMetaData[] grants = buildGrants( grantConfigs );

        return new PolicyMetaData( keyStores, grants );
    }

    /**
     * Build an array of GrantMetaDatas from node list.
     *
     * @param elements the nodes to process
     * @return the GrantMetaData
     */
    private GrantMetaData[] buildGrants( final NodeList elements )
        throws Exception
    {
        final ArrayList grants = new ArrayList();
        final int length = elements.getLength();

        for( int i = 0; i < length; i++ )
        {
            final Element element = (Element)elements.item( i );
            final GrantMetaData grant = buildGrant( element );
            grants.add( grant );
        }

        return (GrantMetaData[])grants.toArray( new GrantMetaData[ grants.size() ] );
    }

    /**
     * Build a GrantMetaData from an element.
     *
     * @param element the nodes to process
     * @return the GrantMetaData
     */
    private GrantMetaData buildGrant( final Element element )
        throws Exception
    {
        final String codeBase = getAttribute( element, "code-base" );
        final String signedBy = getAttribute( element, "signed-by" );
        String keyStore = getAttribute( element, "key-store" );
        if( null != signedBy && null == keyStore )
        {
            keyStore = "default";
        }
        final NodeList permissionElements =
            element.getElementsByTagName( "permission" );
        final PermissionMetaData[] permissions = buildPermissions( permissionElements );
        return new GrantMetaData( codeBase, signedBy, keyStore, permissions );
    }

    /**
     * Build an array of PermissionMetaDatas from node list.
     *
     * @param elements the nodes to process
     * @return the PermissionMetaDatas
     */
    private PermissionMetaData[] buildPermissions( final NodeList elements )
        throws Exception
    {
        final ArrayList grants = new ArrayList();
        final int length = elements.getLength();

        for( int i = 0; i < length; i++ )
        {
            final Element element = (Element)elements.item( i );
            final PermissionMetaData permission = buildPermission( element );
            grants.add( permission );
        }

        return (PermissionMetaData[])grants.toArray( new PermissionMetaData[ grants.size() ] );
    }

    /**
     * Build a PermissionMetaData from an element.
     *
     * @param element the node to process
     * @return the PermissionMetaData
     */
    private PermissionMetaData buildPermission( final Element element )
        throws Exception
    {
        final String classname = getAttribute( element, "class" );
        final String target = getAttribute( element, "target" );
        final String action = getAttribute( element, "action" );
        final String signedBy = getAttribute( element, "signed-by" );
        String keyStore = getAttribute( element, "key-store" );
        if( null != signedBy && null == keyStore )
        {
            keyStore = "default";
        }
        return new PermissionMetaData( classname, target, action,
                                       signedBy, keyStore );
    }

    /**
     * Build an array of KeyStore meta datas from node list.
     *
     * @param elements the nodes to process
     * @return the keyStores
     */
    private KeyStoreMetaData[] buildKeyStores( final NodeList elements )
        throws Exception
    {
        final ArrayList keyStores = new ArrayList();
        final int length = elements.getLength();

        for( int i = 0; i < length; i++ )
        {
            final Element element = (Element)elements.item( i );
            final KeyStoreMetaData keyStore = buildKeyStore( element );
            keyStores.add( keyStore );
        }

        return (KeyStoreMetaData[])keyStores.toArray( new KeyStoreMetaData[ keyStores.size() ] );
    }

    /**
     * Build a KeyStoreMetaData from an element.
     *
     * @param element the nodes to process
     * @return the keyStore
     */
    private KeyStoreMetaData buildKeyStore( final Element element )
        throws Exception
    {
        final String name = element.getAttribute( "name" );
        final String location = getAttribute( element, "location" );
        final String type = getAttribute( element, "type" );
        return new KeyStoreMetaData( name, location, type );
    }

    /**
     * Utility method to get value of attribute. If attribute
     * has a empty/null value or does not appear in XML then return
     * null, elese return value.
     *
     * @param element the element
     * @param name the attribute name
     * @return the cleaned attribute value
     */
    private String getAttribute( final Element element,
                                 final String name )
    {
        final String value = element.getAttribute( name );
        if( "".equals( value ) )
        {
            return null;
        }
        else
        {
            return value;
        }
    }
}
