/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.realityforge.classman.runtime;

import java.io.IOException;
import java.net.URL;
import java.security.SecureClassLoader;
import java.util.Enumeration;
import java.util.Vector;

/**
 * The <tt>JoinClassLoader</tt> is a {@link ClassLoader} that joins a list of
 * ClassLoaders together. The JoinClassLoader has a list of ClassLoaders
 * specified from which resources and classes can be loaded from. The
 * JoinClassLoader will attempt to load the Class or resources from each
 * ClassLoader in succession.
 *
 * <p>Note that it is recomended that each ClassLoader be made up of distinct,
 * non-overlapping sets of resources or else ClassCastExceptions may result
 * along with other undersired behaviour. </p>
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.3 $ $Date: 2003-12-02 07:28:52 $
 */
public class JoinClassLoader
    extends SecureClassLoader
{
    /**
     * The list of classLoaders to search through each time a class or resource
     * is requested.
     */
    private final ClassLoader[] m_classLoaders;

    /**
     * Construct a join ClassLoader that defines parent ClassLoader and list of
     * ClassLoaders to search for classes or resources when requested.
     *
     * @param parent the parent classloader
     * @param classLoaders the classloaders to search
     */
    public JoinClassLoader( final ClassLoader[] classLoaders,
                            final ClassLoader parent )
    {
        super( parent );
        if( null == classLoaders )
        {
            throw new NullPointerException( "classLoaders" );
        }
        for( int i = 0; i < classLoaders.length; i++ )
        {
            if( null == classLoaders[ i ] )
            {
                throw new NullPointerException( "classLoaders[" + i + "]" );
            }
        }
        m_classLoaders = classLoaders;
    }

    /**
     * Overide findClass to find a class by searching all the ClassLoaders for
     * class.
     *
     * @param name the name of class
     * @return the Class instance
     * @throws ClassNotFoundException if unable to find class
     * @see ClassLoader#findClass
     */
    protected Class findClass( final String name )
        throws ClassNotFoundException
    {
        for( int i = 0; i < m_classLoaders.length; i++ )
        {
            try
            {
                return m_classLoaders[ i ].loadClass( name );
            }
            catch( final ClassNotFoundException cnfe )
            {
                //Not in that classloader
            }
        }

        return super.findClass( name );
    }

    /**
     * Overide findResources to retrieve all the resources from all classloaders
     * with a particular name.
     *
     * @param name the resources to search for
     * @return an enumeration of all resources with specified name
     * @throws IOException if unable to find resources
     * @see ClassLoader#findResources
     */
    protected Enumeration findResources( final String name )
        throws IOException
    {
        final Vector result = new Vector();

        for( int i = 0; i < m_classLoaders.length; i++ )
        {
            try
            {
                final Enumeration resources =
                    m_classLoaders[ i ].getResources( name );
                addAll( result, resources );
            }
            catch( final IOException ioe )
            {
                //Not in that classloader
            }
        }
        final Enumeration resources = super.findResources( name );
        addAll( result, resources );
        return result.elements();
    }

    /**
     * Add all resources specified into result vector.
     *
     * @param result the result vector
     * @param resources the enumeration of resources
     */
    private void addAll( final Vector result,
                         final Enumeration resources )
    {
        while( resources.hasMoreElements() )
        {
            result.add( resources.nextElement() );
        }
    }

    /**
     * Overide findResource to search through all classloaders.
     *
     * @param name the resources to search for
     * @return the resource URL
     * @see ClassLoader#findResource
     */
    protected URL findResource( final String name )
    {
        for( int i = 0; i < m_classLoaders.length; i++ )
        {
            final URL resource = m_classLoaders[ i ].getResource( name );
            if( null != resource )
            {
                return resource;
            }
        }
        return super.findResource( name );
    }
}
