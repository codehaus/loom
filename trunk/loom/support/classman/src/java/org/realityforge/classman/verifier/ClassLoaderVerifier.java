/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.realityforge.classman.verifier;

import org.realityforge.classman.metadata.ClassLoaderMetaData;
import org.realityforge.classman.metadata.ClassLoaderSetMetaData;
import org.realityforge.classman.metadata.JoinMetaData;
import org.realityforge.salt.i18n.Resources;
import org.realityforge.salt.i18n.ResourceManager;

/**
 * Verify ClassLoader set is valid. Validity is defined as
 * <ul>
 *   <li>With exception of predefined names, all ClassLoader
 *       names should be defined starting with letters or '_'
 *       and then continuing with Alpha-Numeric characters,
 *       '-', '.' or '_'.</li>
 *   <li>No ClassLoader can have a parent ClassLoader that is
 *       not predefined or not defined in ClassLoaderSet.</li>
 *   <li>No "join" ClassLoader can link against a non-existent
 *       ClassLoader.</li>
 *   <li>No "join" ClassLoader can join multiple instances
 *       of same ClassLoader.</li>
 *   <li>No ClassLoader can have multiple entrys that point
 *       to the same location.</li>
 *   <li>No ClassLoader (either predefined, join or regular)
 *       can have the same name.</li>
 *   <li>The default ClassLoader must exist.</li>
 *   <li>There must be no circular dependencies between join
 *       classloaders.</li>
 * </ul>
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.4 $ $Date: 2003-06-27 03:42:13 $
 */
public class ClassLoaderVerifier
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( ClassLoaderVerifier.class );

    public void verifyClassLoaderSet( final ClassLoaderSetMetaData set )
        throws Exception
    {
        String message = null;

        message = REZ.getString( "valid-names.notice" );
        info( message );
        verifyNames( set );

        message = REZ.getString( "valid-parents.notice" );
        info( message );
        verifyParents( set );

        message = REZ.getString( "valid-links.notice" );
        info( message );
        verifyLinks( set );

        message = REZ.getString( "default-loader.notice" );
        info( message );
        verifyDefaultLoaderExists( set );

        message = REZ.getString( "unique-classloader.notice" );
        info( message );
        verifyUniqueClassLoaderNames( set );

        message = REZ.getString( "unique-joins.notice" );
        info( message );
        verifyUniqueJoinNames( set );

        message = REZ.getString( "unique-predefined.notice" );
        info( message );
        verifyUniquePredefinedNames( set );

        message = REZ.getString( "unique-joins-entrys.notice" );
        info( message );
        verifyUniqueJoinEntrys( set );

        message = REZ.getString( "unique-classpath-entrys.notice" );
        info( message );
        verifyUniqueClassLoaderEntrys( set );

        //TODO: Verify that the joins form a directed graph with no loops
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
     * Verify that all the classloaders have valid names.
     *
     * @throws Exception if validity check fails
     */
    private void verifyNames( ClassLoaderSetMetaData set )
        throws Exception
    {
        final ClassLoaderMetaData[] classLoaders = set.getClassLoaders();
        for( int i = 0; i < classLoaders.length; i++ )
        {
            final String name = classLoaders[ i ].getName();
            verifyName( name );
        }

        final JoinMetaData[] joins = set.getJoins();
        for( int i = 0; i < joins.length; i++ )
        {
            final String name = joins[ i ].getName();
            verifyName( name );
        }
    }

    /**
     * Verify that all the classloaders have valid parents.
     *
     * @throws Exception if validity check fails
     */
    private void verifyParents( ClassLoaderSetMetaData set )
        throws Exception
    {
        final ClassLoaderMetaData[] classLoaders = set.getClassLoaders();
        for( int i = 0; i < classLoaders.length; i++ )
        {
            final ClassLoaderMetaData classLoader = classLoaders[ i ];
            final String parent = classLoader.getParent();
            if( !isLoaderDefined( parent, set ) )
            {
                final String message =
                    REZ.format( "invalid-parent.error",
                                   classLoader.getName(),
                                   parent );
                throw new Exception( message );
            }
        }
    }

    /**
     * Verify that each join ClassLoader only
     * links to ClassLoaders that exist.
     *
     * @throws Exception if validity check fails
     */
    private void verifyLinks( final ClassLoaderSetMetaData set )
        throws Exception
    {
        final JoinMetaData[] joins = set.getJoins();
        for( int i = 0; i < joins.length; i++ )
        {
            verifyLinks( joins[ i ], set );
        }
    }

    /**
     * Verify that each join ClassLoader only
     * links to ClassLoaders that exist.
     *
     * @throws Exception if validity check fails
     */
    private void verifyLinks( final JoinMetaData join,
                              final ClassLoaderSetMetaData set )
        throws Exception
    {
        final String[] classloaders = join.getClassloaders();
        for( int i = 0; i < classloaders.length; i++ )
        {
            final String classloader = classloaders[ i ];
            if( !isLoaderDefined( classloader, set ) )
            {
                final String message =
                    REZ.format( "bad-join-link.error",
                                   join.getName(),
                                   classloader );
                throw new Exception( message );
            }
        }
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
                REZ.format( "empty-name.error",
                               name );
            throw new Exception( message );
        }
        final char ch = name.charAt( 0 );
        if( !Character.isLetter( ch ) &&
            '_' != ch )
        {
            final String message =
                REZ.format( "name-invalid-start.error",
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
                    REZ.format( "name-invalid-char.error",
                                   name,
                                   String.valueOf( c ) );
                throw new Exception( message );
            }
        }
    }

    /**
     * Verify that each regular ClassLoader only
     * contains unique entrys.
     *
     * @throws Exception if validity check fails
     */
    private void verifyUniqueClassLoaderEntrys( final ClassLoaderSetMetaData set )
        throws Exception
    {
        final ClassLoaderMetaData[] classLoaders = set.getClassLoaders();
        for( int i = 0; i < classLoaders.length; i++ )
        {
            verifyUniqueClassLoaderEntrys( classLoaders[ i ] );
        }
    }

    /**
     * Verify that each regular ClassLoader only
     * contains unique entrys.
     *
     * @throws Exception if validity check fails
     */
    private void verifyUniqueClassLoaderEntrys( final ClassLoaderMetaData classLoader )
        throws Exception
    {
        final String[] entrys = classLoader.getEntrys();
        for( int i = 0; i < entrys.length; i++ )
        {
            final String location = entrys[ i ];
            for( int j = i + 1; j < entrys.length; j++ )
            {
                if( location.equals( entrys[ j ] ) )
                {
                    final String message =
                        REZ.format( "classloader-dup-entrys.error",
                                       classLoader.getName(),
                                       location );
                    throw new Exception( message );
                }
            }
        }
    }

    /**
     * Verify that each join only contains unique classloaders.
     *
     * @throws Exception if validity check fails
     */
    private void verifyUniqueJoinEntrys( final ClassLoaderSetMetaData set )
        throws Exception
    {
        final JoinMetaData[] joins = set.getJoins();
        for( int i = 0; i < joins.length; i++ )
        {
            verifyUniqueJoinEntrys( joins[ i ] );
        }
    }

    /**
     * Verify that specified join only contains unique classloaders.
     *
     * @throws Exception if validity check fails
     */
    private void verifyUniqueJoinEntrys( final JoinMetaData join )
        throws Exception
    {
        final String[] classloaders = join.getClassloaders();
        for( int j = 0; j < classloaders.length; j++ )
        {
            final String name = classloaders[ j ];
            for( int k = j + 1; k < classloaders.length; k++ )
            {
                final String other = classloaders[ k ];
                if( other.equals( name ) )
                {
                    final String message =
                        REZ.format( "join-dup-entrys.error",
                                       join.getName(),
                                       name );
                    throw new Exception( message );
                }
            }
        }
    }

    /**
     * Verify that the Predefined names are unique set.
     *
     * @throws Exception if validity check fails
     */
    private void verifyUniquePredefinedNames( final ClassLoaderSetMetaData set )
        throws Exception
    {
        final String[] predefined = set.getPredefined();
        for( int i = 0; i < predefined.length; i++ )
        {
            final String name = predefined[ i ];
            for( int j = i + 1; j < predefined.length; j++ )
            {
                final String other = predefined[ j ];
                if( other.equals( name ) )
                {
                    final String message =
                        REZ.format( "duplicate-name.error",
                                       "predefined",
                                       "predefined",
                                       name );
                    throw new Exception( message );
                }
            }
        }
    }

    /**
     * Verify that the ClassLoader names are unique throughout the set.
     *
     * @param set the set of ClassLoader defs to search in
     * @throws Exception if validity check fails
     */
    private void verifyUniqueClassLoaderNames( final ClassLoaderSetMetaData set )
        throws Exception
    {
        final ClassLoaderMetaData[] classLoaders = set.getClassLoaders();
        for( int i = 0; i < classLoaders.length; i++ )
        {
            final ClassLoaderMetaData classLoader = classLoaders[ i ];
            verifyUniqueName( set,
                              classLoader.getName(),
                              "classloader",
                              classLoader );
        }
    }

    /**
     * Verify that the specified name is unique in set
     * except for specified entity.
     *
     * @param set the set of classloaders
     * @param name the name
     * @param type the type of classloder (used for exception messages)
     * @param entity the entity to skip (ie the one the name refers to)
     * @throws Exception if validity check fails
     */
    private void verifyUniqueName( final ClassLoaderSetMetaData set,
                                   final String name,
                                   final String type,
                                   final Object entity )
        throws Exception
    {
        //Make sure our join does not have same name as a
        //predefined ClassLoader
        if( set.isPredefined( name ) )
        {
            final String message =
                REZ.format( "duplicate-name.error",
                               type,
                               "predefined",
                               name );
            throw new Exception( message );
        }

        //Make sure no joins have same name as our join
        final JoinMetaData[] joins = set.getJoins();
        for( int j = 0; j < joins.length; j++ )
        {
            final JoinMetaData other = joins[ j ];
            if( other == entity )
            {
                continue;
            }
            if( other.getName().equals( name ) )
            {
                final String message =
                    REZ.format( "duplicate-name.error",
                                   type,
                                   "join",
                                   name );
                throw new Exception( message );
            }
        }

        final ClassLoaderMetaData[] classLoaders = set.getClassLoaders();
        for( int j = 0; j < classLoaders.length; j++ )
        {
            final ClassLoaderMetaData other = classLoaders[ j ];
            if( other == entity )
            {
                continue;
            }
            if( other.getName().equals( name ) )
            {
                final String message =
                    REZ.format( "duplicate-name.error",
                                   type,
                                   "classloader",
                                   name );
                throw new Exception( message );
            }
        }
    }

    /**
     * Verify that the join names are unique throughout the set.
     *
     * @param set the set of ClassLoader defs to search in
     * @throws Exception if validity check fails
     */
    private void verifyUniqueJoinNames( final ClassLoaderSetMetaData set )
        throws Exception
    {
        final JoinMetaData[] joins = set.getJoins();
        for( int i = 0; i < joins.length; i++ )
        {
            final JoinMetaData join = joins[ i ];
            verifyUniqueName( set,
                              join.getName(),
                              "join",
                              join );
        }
    }

    /**
     * Verify that the default loader is defined.
     *
     * @param set the set of ClassLoader defs to search in
     * @throws Exception if validity check fails
     */
    private void verifyDefaultLoaderExists( final ClassLoaderSetMetaData set )
        throws Exception
    {
        final String name = set.getDefault();
        if( !isLoaderDefined( name, set ) )
        {
            final String message =
                REZ.format( "missing-default-loader.error",
                               name );
            throw new Exception( message );
        }
    }

    /**
     * Return true if specified loader is defined in set.
     *
     * @param name the name of loader
     * @param set the set to search
     * @return true if specified loader is defined in set.
     */
    private boolean isLoaderDefined( final String name,
                                     final ClassLoaderSetMetaData set )
    {
        if( set.isPredefined( name ) )
        {
            return true;
        }
        else if( null != set.getClassLoader( name ) )
        {
            return true;
        }
        else if( null != set.getJoin( name ) )
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
