/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.tools.infobuilder;

import java.io.InputStream;
import org.jcontainer.loom.tools.info.ComponentInfo;
import org.jcontainer.dna.AbstractLogEnabled;
import org.jcontainer.dna.Logger;
import org.realityforge.salt.i18n.ResourceManager;
import org.realityforge.salt.i18n.Resources;

/**
 * A InfoBuilder is responsible for building {@link ComponentInfo}
 * objects from Configuration objects. The format for Configuration object
 * is specified in the <a href="package-summary.html#external">package summary</a>.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.6 $ $Date: 2003-10-05 10:07:04 $
 */
public final class InfoBuilder
    extends AbstractLogEnabled
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( InfoBuilder.class );

    private final InfoReader m_xmlInfoCreator = createXMLInfoCreator();
    private final InfoReader m_serialInfoCreator = new SerializedInfoReader();
    private final InfoReader m_legacyInfoCreator = createLegacyInfoCreator();

    /**
     * Setup logging for all subcomponents
     */
    public void enableLogging( final Logger logger )
    {
        super.enableLogging( logger );
        setupLogger( m_serialInfoCreator );
        if( null != m_xmlInfoCreator )
        {
            setupLogger( m_xmlInfoCreator );
        }
        if( null != m_legacyInfoCreator )
        {
            setupLogger( m_legacyInfoCreator );
        }
        if( null != m_legacyInfoCreator )
        {
            setupLogger( m_legacyInfoCreator );
        }
    }

    /**
     * Create a {@link ComponentInfo} object for specified Class.
     *
     * @param clazz The class of Component
     * @return the created ComponentInfo
     * @throws Exception if an error occurs
     */
    public ComponentInfo buildComponentInfo( final Class clazz )
        throws Exception
    {
        return buildComponentInfo( clazz.getName(), clazz.getClassLoader() );
    }

    /**
     * Create a {@link ComponentInfo} object for specified
     * classname, in specified ClassLoader.
     *
     * @param classname The classname of Component
     * @param classLoader the ClassLoader to load info from
     * @return the created ComponentInfo
     * @throws Exception if an error occurs
     */
    public ComponentInfo buildComponentInfo( final String classname,
                                             final ClassLoader classLoader )
        throws Exception
    {
        ComponentInfo info = buildComponentFromSer( classname, classLoader );
        if( null != info )
        {
            return info;
        }

        info = buildComponentFromLegacy( classname, classLoader );
        if( null != info )
        {
            return info;
        }
        else
        {
            return buildComponentFromXML( classname, classLoader );
        }
    }

    /**
     * Build {@link ComponentInfo} from the XML descriptor format.
     *
     * @param classname The classname of Component
     * @param classLoader the ClassLoader to load info from
     * @return the created {@link ComponentInfo}
     * @throws java.lang.Exception if an error occurs
     */
    private ComponentInfo buildComponentFromSer( final String classname,
                                                 final ClassLoader classLoader )
        throws Exception
    {
        final String xinfo = deriveResourceName( classname, "-info.ser" );
        final InputStream inputStream = classLoader.getResourceAsStream( xinfo );
        if( null == inputStream )
        {
            return null;
        }

        return m_serialInfoCreator.createComponentInfo( classname, inputStream );
    }

    /**
     * Build {@link ComponentInfo} from the legacy XML descriptor format.
     *
     * @param classname The classname of Component
     * @param classLoader the ClassLoader to load info from
     * @return the created {@link ComponentInfo}
     * @throws java.lang.Exception if an error occurs
     */
    private ComponentInfo buildComponentFromLegacy( final String classname,
                                                    final ClassLoader classLoader )
        throws Exception
    {
        final String xinfo = deriveResourceName( classname, ".xinfo" );
        final InputStream inputStream = classLoader.getResourceAsStream( xinfo );
        if( null == inputStream )
        {
            return null;
        }

        if( null != m_legacyInfoCreator )
        {
            return m_legacyInfoCreator.createComponentInfo( classname, inputStream );
        }
        else
        {
            return null;
        }
    }

    /**
     * Build ComponentInfo from the XML descriptor format.
     *
     * @param classname The classname of Component
     * @param classLoader the ClassLoader to load info from
     * @return the created ComponentInfo
     * @throws Exception if an error occurs
     */
    private ComponentInfo buildComponentFromXML( final String classname,
                                                 final ClassLoader classLoader )
        throws Exception
    {
        final String xinfo = deriveResourceName( classname, "-info.xml" );
        final InputStream inputStream = classLoader.getResourceAsStream( xinfo );
        if( null == inputStream )
        {
            final String message =
                REZ.format( "builder.missing-info.error",
                            classname );
            throw new Exception( message );
        }

        final InfoReader xmlInfoCreator = getXMLInfoCreator( classname );
        return xmlInfoCreator.createComponentInfo( classname, inputStream );
    }

    /**
     * Utility to get xml info builder, else throw
     * an exception if missing descriptor.
     *
     * @return the InfoReader
     */
    private InfoReader getXMLInfoCreator( final String classname )
        throws Exception
    {
        if( null != m_xmlInfoCreator )
        {
            return m_xmlInfoCreator;
        }
        else
        {
            final String message =
                REZ.format( "builder.missing-xml-creator.error",
                            classname );
            throw new Exception( message );
        }
    }

    /**
     * Utility to get {@link XMLInfoReader} if XML files are on
     * ClassPath.
     *
     * @return the XML {@link InfoReader}
     */
    private static InfoReader createXMLInfoCreator()
    {
        try
        {
            return new XMLInfoReader();
        }
        catch( final Exception e )
        {
            //Ignore it if ClassNot found due to no
            //XML Classes on classpath
            return null;
        }
    }

    /**
     * Utility to get {@link LegacyBlockInfoReader} if XML files are on
     * ClassPath.
     *
     * @return the Legacy {@link InfoReader}
     */
    private static InfoReader createLegacyInfoCreator()
    {
        try
        {
            return new LegacyBlockInfoReader();
        }
        catch( final Exception e )
        {
            //Ignore it if ClassNot found due to no
            //XML Classes on classpath
            return null;
        }
    }

    /**
     * Derive the resourcename for specified class using specified postfix.
     *
     * @param classname the name of class
     * @param postfix the postfix to add to end of resource
     * @return the name of resource
     */
    private String deriveResourceName( final String classname,
                                       final String postfix )
    {
        return classname.replace( '.', '/' ) + postfix;
    }
}
