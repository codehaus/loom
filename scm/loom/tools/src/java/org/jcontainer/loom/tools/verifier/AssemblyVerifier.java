/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.tools.verifier;

import java.util.ArrayList;
import java.util.Stack;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.jcontainer.loom.tools.info.ComponentInfo;
import org.jcontainer.loom.tools.info.DependencyDescriptor;
import org.jcontainer.loom.tools.info.ServiceDescriptor;
import org.jcontainer.loom.tools.metadata.DependencyMetaData;

/**
 * This Class verifies that Sars are valid. It performs a number
 * of checks to make sure that the Sar represents a valid
 * application and excluding runtime errors will start up validly.
 * Some of the checks it performs include;
 *
 * <ul>
 *   <li>Verify names of Components contain only
 *       letters, digits or the '_' character.</li>
 *   <li>Verify that the names of the Components are unique to the
 *       Assembly.</li>
 *   <li>Verify that the specified dependeny mapping correspond to
 *       dependencies specified in ComponentInfo files.</li>
 *   <li>Verify that the inter-Component dependendencies are valid.
 *       This essentially means that if Component A requires Service S
 *       from Component B then Component B must provide Service S.</li>
 *   <li>Verify that there are no circular dependendencies between
 *       components.</li>
 *   <li>Verify that the Class objects for component implement the
 *       service interfaces.</li>
 *   <li>Verify that the Class is a valid Avalon Component as per the
 *       rules in {@link org.jcontainer.loom.tools.verifier.ComponentVerifier} object.</li>
 * </ul>
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003-06-29 00:51:07 $
 */
public class AssemblyVerifier
    extends AbstractLogEnabled
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( AssemblyVerifier.class );

    /**
     * Validate and Verify the specified assembly (ie organization
     * of components). See the Class Javadocs for the rules and
     * regulations of assembly.
     *
     * @param components the Components that make up assembly
     * @throws org.jcontainer.loom.tools.verifier.VerifyException if an error occurs
     */
    public void verifyAssembly( final org.jcontainer.loom.tools.profile.ComponentProfile[] components )
        throws VerifyException
    {
        String message;

        message = REZ.getString( "assembly.valid-names.notice" );
        getLogger().info( message );
        verifyValidNames( components );

        message = REZ.getString( "assembly.unique-names.notice" );
        getLogger().info( message );
        checkNamesUnique( components );

        message = REZ.getString( "assembly.dependencies-mapping.notice" );
        getLogger().info( message );
        verifyValidDependencies( components );

        message = REZ.getString( "assembly.dependency-references.notice" );
        getLogger().info( message );
        verifyDependencyReferences( components );

        message = REZ.getString( "assembly.nocircular-dependencies.notice" );
        getLogger().info( message );
        verifyNoCircularDependencies( components );
    }

    /**
     * Verfiy that all Components have the needed dependencies specified correctly.
     *
     * @param components the ComponentEntry objects for the components
     * @throws org.jcontainer.loom.tools.verifier.VerifyException if an error occurs
     */
    public void verifyValidDependencies( final org.jcontainer.loom.tools.profile.ComponentProfile[] components )
        throws VerifyException
    {
        for( int i = 0; i < components.length; i++ )
        {
            verifyDependenciesMap( components[ i ] );
        }
    }

    /**
     * Verfiy that there are no circular references between Components.
     *
     * @param components the ComponentEntry objects for the components
     * @throws org.jcontainer.loom.tools.verifier.VerifyException if an circular dependency error occurs
     */
    protected void verifyNoCircularDependencies( final org.jcontainer.loom.tools.profile.ComponentProfile[] components )
        throws VerifyException
    {
        for( int i = 0; i < components.length; i++ )
        {
            final org.jcontainer.loom.tools.profile.ComponentProfile component = components[ i ];

            final Stack stack = new Stack();
            stack.push( component );
            verifyNoCircularDependencies( component, components, stack );
            stack.pop();
        }
    }

    /**
     * Verfiy that there are no circular references between Components.
     *
     * @param component ???
     * @param components the ComponentEntry objects for the components
     * @param stack the ???
     * @throws org.jcontainer.loom.tools.verifier.VerifyException if an error occurs
     */
    protected void verifyNoCircularDependencies( final org.jcontainer.loom.tools.profile.ComponentProfile component,
                                                 final org.jcontainer.loom.tools.profile.ComponentProfile[] components,
                                                 final Stack stack )
        throws VerifyException
    {
        final org.jcontainer.loom.tools.profile.ComponentProfile[] dependencies = getDependencies( component, components );
        for( int i = 0; i < dependencies.length; i++ )
        {
            final org.jcontainer.loom.tools.profile.ComponentProfile dependency = dependencies[ i ];
            if( stack.contains( dependency ) )
            {
                final String trace = getDependencyTrace( dependency, stack );
                final String message =
                    REZ.getString( "assembly.circular-dependency.error",
                                   component.getMetaData().getName(),
                                   trace );
                throw new VerifyException( message );
            }

            stack.push( dependency );
            verifyNoCircularDependencies( dependency, components, stack );
            stack.pop();
        }
    }

    /**
     * Get a string defining path from top of stack till
     * it reaches specified component.
     *
     * @param component the component
     * @param stack the Stack
     * @return the path of dependency
     */
    protected String getDependencyTrace( final org.jcontainer.loom.tools.profile.ComponentProfile component,
                                         final Stack stack )
    {
        final StringBuffer sb = new StringBuffer();
        sb.append( "[ " );

        final String name = component.getMetaData().getName();
        final int size = stack.size();
        final int top = size - 1;
        for( int i = top; i >= 0; i-- )
        {
            final org.jcontainer.loom.tools.profile.ComponentProfile other = (org.jcontainer.loom.tools.profile.ComponentProfile)stack.get( i );
            if( top != i )
            {
                sb.append( ", " );
            }
            sb.append( other.getMetaData().getName() );

            if( other.getMetaData().getName().equals( name ) )
            {
                break;
            }
        }

        sb.append( ", " );
        sb.append( name );

        sb.append( " ]" );
        return sb.toString();
    }

    /**
     * Get array of dependencies for specified Component from specified
     * Component array.
     *
     * @param component the component to get dependencies of
     * @param components the total set of components in application
     * @return the dependencies of component
     */
    protected org.jcontainer.loom.tools.profile.ComponentProfile[] getDependencies( final org.jcontainer.loom.tools.profile.ComponentProfile component,
                                                                                    final org.jcontainer.loom.tools.profile.ComponentProfile[] components )
    {
        final ArrayList dependencies = new ArrayList();
        final DependencyMetaData[] deps =
            component.getMetaData().getDependencies();

        for( int i = 0; i < deps.length; i++ )
        {
            final String name = deps[ i ].getProviderName();
            final org.jcontainer.loom.tools.profile.ComponentProfile other = getComponentProfile( name, components );
            dependencies.add( other );
        }

        return (org.jcontainer.loom.tools.profile.ComponentProfile[])dependencies.toArray( new org.jcontainer.loom.tools.profile.ComponentProfile[ 0 ] );
    }

    /**
     * Verfiy that the inter-Component dependencies are valid.
     *
     * @param components the ComponentProfile objects for the components
     * @throws org.jcontainer.loom.tools.verifier.VerifyException if an error occurs
     */
    protected void verifyDependencyReferences( final org.jcontainer.loom.tools.profile.ComponentProfile[] components )
        throws VerifyException
    {
        for( int i = 0; i < components.length; i++ )
        {
            verifyDependencyReferences( components[ i ], components );
        }
    }

    /**
     * Verfiy that the inter-Component dependencies are valid for specified Component.
     *
     * @param component the ComponentProfile object for the component
     * @param others the ComponentProfile objects for the other components
     * @throws org.jcontainer.loom.tools.verifier.VerifyException if an error occurs
     */
    protected void verifyDependencyReferences( final org.jcontainer.loom.tools.profile.ComponentProfile component,
                                               final org.jcontainer.loom.tools.profile.ComponentProfile[] others )
        throws VerifyException
    {
        final ComponentInfo info = component.getInfo();
        final DependencyMetaData[] dependencies = component.getMetaData().getDependencies();

        for( int i = 0; i < dependencies.length; i++ )
        {
            final DependencyMetaData dependency = dependencies[ i ];
            final String providerName = dependency.getProviderName();
            final String key = dependency.getKey();
            final String type = info.getDependency( key ).getComponentType();

            //Get the other component that is providing service
            final org.jcontainer.loom.tools.profile.ComponentProfile provider = getComponentProfile( providerName, others );
            if( null == provider )
            {
                final String message =
                    REZ.getString( "assembly.missing-dependency.error",
                                   key,
                                   providerName,
                                   component.getMetaData().getName() );
                throw new VerifyException( message );
            }

            //make sure that the component offers service
            //that user expects it to be providing
            final ComponentInfo providerInfo = provider.getInfo();
            final ServiceDescriptor[] services = providerInfo.getServices();
            if( !hasMatchingService( type, services ) )
            {
                final String message =
                    REZ.getString( "assembly.dependency-missing-service.error",
                                   providerName,
                                   type,
                                   component.getMetaData().getName() );
                throw new VerifyException( message );
            }
        }
    }

    /**
     * Get component with specified name from specified Component array.
     *
     * @param name the name of component to get
     * @param components the array of components to search
     * @return the Component if found, else null
     */
    protected org.jcontainer.loom.tools.profile.ComponentProfile getComponentProfile( final String name,
                                                                                      final org.jcontainer.loom.tools.profile.ComponentProfile[] components )
    {
        for( int i = 0; i < components.length; i++ )
        {
            org.jcontainer.loom.tools.profile.ComponentProfile ComponentProfile = components[ i ];
            if( ComponentProfile.getMetaData().getName().equals( name ) )
            {
                return components[ i ];
            }
        }

        return null;
    }

    /**
     * Verify that the names of the specified Components are valid.
     *
     * @param components the Components Profile
     * @throws org.jcontainer.loom.tools.verifier.VerifyException if an error occurs
     */
    protected void verifyValidNames( final org.jcontainer.loom.tools.profile.ComponentProfile[] components )
        throws VerifyException
    {
        for( int i = 0; i < components.length; i++ )
        {
            org.jcontainer.loom.tools.profile.ComponentProfile ComponentProfile = components[ i ];
            final String name = ComponentProfile.getMetaData().getName();
            if( !isValidName( name ) )
            {
                final String message =
                    REZ.getString( "assembly.bad-name.error", name );
                throw new VerifyException( message );
            }
        }
    }

    /**
     * Return true if specified name is valid.
     * Valid names consist of letters, digits or the '_' character.
     *
     * @param name the name to check
     * @return true if valid, false otherwise
     */
    protected boolean isValidName( final String name )
    {
        final int size = name.length();
        for( int i = 0; i < size; i++ )
        {
            final char ch = name.charAt( i );

            if( !Character.isLetterOrDigit( ch ) && '-' != ch )
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Verify that the names of the specified components and listeners are unique.
     * It is not valid for the same name to be used in multiple components.
     *
     * @param components the Components
     * @throws org.jcontainer.loom.tools.verifier.VerifyException if an error occurs
     */
    protected void checkNamesUnique( final org.jcontainer.loom.tools.profile.ComponentProfile[] components )
        throws VerifyException
    {
        for( int i = 0; i < components.length; i++ )
        {
            org.jcontainer.loom.tools.profile.ComponentProfile ComponentProfile = components[ i ];
            final String name = ComponentProfile.getMetaData().getName();
            verifyUniqueName( components, name, i );
        }
    }

    /**
     * Verfify that specified name is unique among the specified components.
     *
     * @param components the array of components to check
     * @param name the name of component
     * @param index the index of component in array (so we can skip it)
     * @throws org.jcontainer.loom.tools.verifier.VerifyException if names are not unique
     */
    private void verifyUniqueName( final org.jcontainer.loom.tools.profile.ComponentProfile[] components,
                                   final String name,
                                   final int index )
        throws VerifyException
    {
        for( int i = 0; i < components.length; i++ )
        {
            org.jcontainer.loom.tools.profile.ComponentProfile ComponentProfile = components[ i ];
            final String other =
                ComponentProfile.getMetaData().getName();
            if( index != i && other.equals( name ) )
            {
                final String message =
                    REZ.getString( "assembly.duplicate-name.error", name );
                throw new VerifyException( message );
            }
        }
    }

    /**
     * Retrieve a list of DependencyMetaData objects for ComponentProfile
     * and verify that there is a 1 to 1 map with dependencies specified
     * in ComponentInfo.
     *
     * @param component the ComponentProfile describing the component
     * @throws org.jcontainer.loom.tools.verifier.VerifyException if an error occurs
     */
    protected void verifyDependenciesMap( final org.jcontainer.loom.tools.profile.ComponentProfile component )
        throws VerifyException
    {
        //Make sure all dependency entries specified in config file are valid
        final DependencyMetaData[] dependencySet =
            component.getMetaData().getDependencies();

        for( int i = 0; i < dependencySet.length; i++ )
        {
            final String key = dependencySet[ i ].getKey();
            final ComponentInfo info = component.getInfo();
            final DependencyDescriptor descriptor = info.getDependency( key );

            //If there is no dependency descriptor in ComponentInfo then
            //user has specified an uneeded dependency.
            if( null == descriptor )
            {
                final String message =
                    REZ.getString( "assembly.unknown-dependency.error",
                                   key,
                                   key,
                                   component.getMetaData().getName() );
                throw new VerifyException( message );
            }
        }

        //Make sure all dependencies in ComponentInfo file are satisfied
        final ComponentInfo info = component.getInfo();
        final DependencyDescriptor[] dependencies = info.getDependencies();
        for( int i = 0; i < dependencies.length; i++ )
        {
            final DependencyDescriptor dependency = dependencies[ i ];
            final DependencyMetaData dependencyMetaData =
                component.getMetaData().getDependency( dependency.getKey() );

            //If there is no metaData then the user has failed
            //to specify a needed dependency.
            if( null == dependencyMetaData && !dependency.isOptional() )
            {
                final String message =
                    REZ.getString( "assembly.unspecified-dependency.error",
                                   dependency.getKey(),
                                   component.getMetaData().getName() );
                throw new VerifyException( message );
            }
        }
    }

    /**
     * Return true if specified service reference matches any of the
     * candidate services.
     *
     * @param type the service type
     * @param candidates an array of candidate services
     * @return true if candidate services contains a service that matches
     *         specified service, false otherwise
     */
    protected boolean hasMatchingService( final String type,
                                          final ServiceDescriptor[] candidates )
    {
        for( int i = 0; i < candidates.length; i++ )
        {
            final String otherClassname = candidates[ i ].getType();
            if( otherClassname.equals( type ) )
            {
                return true;
            }
        }

        return false;
    }
}
