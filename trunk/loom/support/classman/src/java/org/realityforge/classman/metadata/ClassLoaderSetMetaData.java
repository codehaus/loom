/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.realityforge.classman.metadata;

/**
 * This class defines a set of ClassLoaders and the default ClassLoader to use.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.3 $ $Date: 2003-12-02 07:28:52 $
 */
public class ClassLoaderSetMetaData
{
    /**
     * The name of the current classloader. This may be used by other
     * ClassLoader definitions to refer to this ClassLoader.
     */
    private final String m_default;

    /** The set of ClassLoaders predefined by the application. */
    private final String[] m_predefined;

    /** The classloaders defined in set. */
    private final ClassLoaderMetaData[] m_classLoaders;

    /** The joining classloaders defined in set. */
    private final JoinMetaData[] m_joins;

    /**
     * Construct set with specified set and ClassLoaders.
     *
     * @param aDefault the name of default ClassLoader
     * @param classLoaders the ClassLoaders in set
     */
    public ClassLoaderSetMetaData( final String aDefault,
                                   final String[] predefined,
                                   final ClassLoaderMetaData[] classLoaders,
                                   final JoinMetaData[] joins )
    {
        if( null == aDefault )
        {
            throw new NullPointerException( "aDefault" );
        }
        if( null == classLoaders )
        {
            throw new NullPointerException( "classLoaders" );
        }
        if( null == joins )
        {
            throw new NullPointerException( "joins" );
        }
        if( null == predefined )
        {
            throw new NullPointerException( "predefined" );
        }

        m_default = aDefault;
        m_predefined = predefined;
        m_classLoaders = classLoaders;
        m_joins = joins;
    }

    /**
     * Return the default ClassLoader name.
     *
     * @return the default ClassLoader name.
     */
    public String getDefault()
    {
        return m_default;
    }

    /**
     * Return the set of predefined ClassLoaders.
     *
     * @return the set of predefined ClassLoaders.
     */
    public String[] getPredefined()
    {
        return m_predefined;
    }

    /**
     * Return the classloaders in set.
     *
     * @return the classloaders in set.
     */
    public ClassLoaderMetaData[] getClassLoaders()
    {
        return m_classLoaders;
    }

    /**
     * Return the "join" classloaders in set.
     *
     * @return the "join" classloaders in set.
     */
    public JoinMetaData[] getJoins()
    {
        return m_joins;
    }

    /**
     * Return true if specified name, designates a predefined ClassLoader.
     *
     * @return true if specified name, designates a predefined ClassLoader.
     */
    public boolean isPredefined( final String name )
    {
        for( int i = 0; i < m_predefined.length; i++ )
        {
            if( m_predefined[ i ].equals( name ) )
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Return the classloader with specified name.
     *
     * @return the classloader with specified name
     */
    public ClassLoaderMetaData getClassLoader( final String name )
    {
        for( int i = 0; i < m_classLoaders.length; i++ )
        {
            final ClassLoaderMetaData classLoader = m_classLoaders[ i ];
            if( classLoader.getName().equals( name ) )
            {
                return classLoader;
            }
        }
        return null;
    }

    /**
     * Return the "join" classloader with specified name.
     *
     * @return the "join" classloader with specified name
     */
    public JoinMetaData getJoin( final String name )
    {
        for( int i = 0; i < m_joins.length; i++ )
        {
            final JoinMetaData join = m_joins[ i ];
            if( join.getName().equals( name ) )
            {
                return join;
            }
        }
        return null;
    }
}
