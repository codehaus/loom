/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.components.util.verifier;

import java.util.ArrayList;
import java.util.Stack;
import org.jcontainer.dna.AbstractLogEnabled;
import org.jcontainer.loom.components.util.info.ComponentInfo;
import org.jcontainer.loom.components.util.info.DependencyDescriptor;
import org.jcontainer.loom.components.util.info.ServiceDescriptor;
import org.jcontainer.loom.components.util.metadata.DependencyDirective;
import org.jcontainer.loom.components.util.profile.ComponentProfile;
import org.realityforge.salt.i18n.ResourceManager;
import org.realityforge.salt.i18n.Resources;

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
 *       rules in {@link org.jcontainer.loom.components.util.verifier.ComponentVerifier} object.</li>
 * </ul>
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.3 $ $Date: 2003-11-03 06:43:15 $
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
     * @throws Exception if an error occurs
     */
    public void verifyAssembly( final ComponentProfile[] components )
        throws Exception
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
     * @throws Exception if an error occurs
     */
    public void verifyValidDependencies( final ComponentProfile[] components )
        throws Exception
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
     * @throws Exception if an circular dependency error occurs
     */
    protected void verifyNoCircularDependencies( final ComponentProfile[] components )
        throws Exception
    {
        for( int i = 0; i < components.length; i++ )
        {
            final ComponentProfile component = components[ i ];

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
     * @throws Exception if an error occurs
     */
    protected void verifyNoCircularDependencies( final ComponentProfile component,
                                                 final ComponentProfile[] components,
                                                 final Stack stack )
        throws Exception
    {
        final ComponentProfile[] dependencies = getDependencies( component, components );
        for( int i = 0; i < dependencies.length; i++ )
        {
            final ComponentProfile dependency = dependencies[ i ];
            if( stack.contains( dependency ) )
            {
                final String trace = getDependencyTrace( dependency, stack );
                final String message =
                    REZ.format( "assembly.circular-dependency.error",
                                component.getTemplate().getName(),
                                trace );
                throw new Exception( message );
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
    protected String getDependencyTrace( final ComponentProfile component,
                                         final Stack stack )
    {
        final StringBuffer sb = new StringBuffer();
        sb.append( "[ " );

        final String name = component.getTemplate().getName();
        final int size = stack.size();
        final int top = size - 1;
        for( int i = top; i >= 0; i-- )
        {
            final ComponentProfile other = (ComponentProfile)stack.get( i );
            if( top != i )
            {
                sb.append( ", " );
            }
            sb.append( other.getTemplate().getName() );

            if( other.getTemplate().getName().equals( name ) )
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
    protected ComponentProfile[] getDependencies( final ComponentProfile component,
                                                                                    final ComponentProfile[] components )
    {
        final ArrayList dependencies = new ArrayList();
        final DependencyDirective[] deps =
            component.getTemplate().getDependencies();

        for( int i = 0; i < deps.length; i++ )
        {
            final String name = deps[ i ].getProviderName();
            final ComponentProfile other = getComponentProfile( name, components );
            dependencies.add( other );
        }

        return (ComponentProfile[])dependencies.toArray( new ComponentProfile[ 0 ] );
    }

    /**
     * Verfiy that the inter-Component dependencies are valid.
     *
     * @param components the ComponentProfile objects for the components
     * @throws Exception if an error occurs
     */
    protected void verifyDependencyReferences( final ComponentProfile[] components )
        throws Exception
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
     * @throws Exception if an error occurs
     */
    protected void verifyDependencyReferences( final ComponentProfile component,
                                               final ComponentProfile[] others )
        throws Exception
    {
        final ComponentInfo info = component.getInfo();
        final DependencyDirective[] dependencies = component.getTemplate().getDependencies();

        for( int i = 0; i < dependencies.length; i++ )
        {
            final DependencyDirective dependency = dependencies[ i ];
            final String providerName = dependency.getProviderName();
            final String key = dependency.getKey();
            final String type = info.getDependency( key ).getComponentType();

            //Get the other component that is providing service
            final ComponentProfile provider = getComponentProfile( providerName, others );
            if( null == provider )
            {
                final String message =
                    REZ.format( "assembly.missing-dependency.error",
                                key,
                                providerName,
                                component.getTemplate().getName() );
                throw new Exception( message );
            }

            //make sure that the component offers service
            //that user expects it to be providing
            final ComponentInfo providerInfo = provider.getInfo();
            final ServiceDescriptor[] services = providerInfo.getServices();
            if( !hasMatchingService( type, services ) )
            {
                final String message =
                    REZ.format( "assembly.dependency-missing-service.error",
                                providerName,
                                type,
                                component.getTemplate().getName() );
                throw new Exception( message );
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
    protected ComponentProfile getComponentProfile( final String name,
                                                                                      final ComponentProfile[] components )
    {
        for( int i = 0; i < components.length; i++ )
        {
            ComponentProfile ComponentProfile = components[ i ];
            if( ComponentProfile.getTemplate().getName().equals( name ) )
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
     * @throws Exception if an error occurs
     */
    protected void verifyValidNames( final ComponentProfile[] components )
        throws Exception
    {
        for( int i = 0; i < components.length; i++ )
        {
            ComponentProfile ComponentProfile = components[ i ];
            final String name = ComponentProfile.getTemplate().getName();
            if( !isValidName( name ) )
            {
                final String message =
                    REZ.format( "assembly.bad-name.error", name );
                throw new Exception( message );
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
     * @throws Exception if an error occurs
     */
    protected void checkNamesUnique( final ComponentProfile[] components )
        throws Exception
    {
        for( int i = 0; i < components.length; i++ )
        {
            ComponentProfile ComponentProfile = components[ i ];
            final String name = ComponentProfile.getTemplate().getName();
            verifyUniqueName( components, name, i );
        }
    }

    /**
     * Verfify that specified name is unique among the specified components.
     *
     * @param components the array of components to check
     * @param name the name of component
     * @param index the index of component in array (so we can skip it)
     * @throws Exception if names are not unique
     */
    private void verifyUniqueName( final ComponentProfile[] components,
                                   final String name,
                                   final int index )
        throws Exception
    {
        for( int i = 0; i < components.length; i++ )
        {
            ComponentProfile ComponentProfile = components[ i ];
            final String other =
                ComponentProfile.getTemplate().getName();
            if( index != i && other.equals( name ) )
            {
                final String message =
                    REZ.format( "assembly.duplicate-name.error", name );
                throw new Exception( message );
            }
        }
    }

    /**
     * Retrieve a list of DependencyDirective objects for ComponentProfile
     * and verify that there is a 1 to 1 map with dependencies specified
     * in ComponentInfo.
     *
     * @param component the ComponentProfile describing the component
     * @throws Exception if an error occurs
     */
    protected void verifyDependenciesMap( final ComponentProfile component )
        throws Exception
    {
        //Make sure all dependency entries specified in config file are valid
        final DependencyDirective[] dependencySet =
            component.getTemplate().getDependencies();

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
                    REZ.format( "assembly.unknown-dependency.error",
                                key,
                                key,
                                component.getTemplate().getName() );
                throw new Exception( message );
            }
        }

        //Make sure all dependencies in ComponentInfo file are satisfied
        final ComponentInfo info = component.getInfo();
        final DependencyDescriptor[] dependencies = info.getDependencies();
        for( int i = 0; i < dependencies.length; i++ )
        {
            final DependencyDescriptor dependency = dependencies[ i ];
            final DependencyDirective dependencyMetaData =
                component.getTemplate().getDependency( dependency.getKey() );

            //If there is no metaData then the user has failed
            //to specify a needed dependency.
            if( null == dependencyMetaData && !dependency.isOptional() )
            {
                final String message =
                    REZ.format( "assembly.unspecified-dependency.error",
                                dependency.getKey(),
                                component.getTemplate().getName() );
                throw new Exception( message );
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
