/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.tools.verifier;

import org.jcontainer.dna.LogEnabled;
import org.jcontainer.dna.Logger;
import org.realityforge.metaclass.Attributes;
import org.realityforge.metaclass.model.Attribute;

/**
 * This Class verifies that an implementation is valid wrt the
 * ComponentMetaData. It performs a number of checks to make sure
 * that the implementation class is consistent with ComponentMetaData.
 * Some of the checks it performs include;
 *
 * <ul>
 *   <li>Verify that the Class objects for Component implement the
 *       service interfaces.</li>
 *   <li>Verify that the Class is a valid Avalon Component as per the
 *       rules in {@link ComponentVerifier} object.</li>
 *   <li>Verify that the Class is Composable/Serviceable if and only if
 *       dependencies are declared.</li>
 *   <li>Verify that the Class is Contextualizable if any context
 *       entrys are declared.</li>
 *   <li>Verify that the Class is Configurable if a Configuration
 *       schema is are declared.</li>
 * </ul>
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.14 $ $Date: 2003-10-16 05:21:50 $
 */
public class InfoVerifier
    implements LogEnabled
{
    /**
     * The verifier for components in assembly.
     */
    private final ComponentVerifier m_verifier = new ComponentVerifier();

    public void enableLogging( final Logger logger )
    {
        m_verifier.enableLogging( logger );
    }

    /**
     * Verfiy that specified components designate classes that implement the
     * advertised interfaces.
     *
     * @param name the name of component
     * @param type the component type
     * @throws VerifyException if an error occurs
     */
    public void verifyType( final String name, final Class type )
        throws VerifyException
    {
        final Attribute attribute =
            Attributes.getAttribute( type, "dna.component" );
        if( null == attribute )
        {
            final String message =
                "Component " + name + " does not specify correct metadata";
            throw new VerifyException( message );
        }
        final Class[] interfaces = getServiceClasses( name, type );

        m_verifier.verifyComponent( name, type, interfaces, false );
    }

    /**
     * Retrieve an array of Classes for all the services that a Component
     * offers. This method also makes sure all services offered are
     * interfaces.
     *
     * @param name the name of component
     * @param type the component type
     * @return an array of Classes for all the services
     * @throws VerifyException if an error occurs
     */
    protected Class[] getServiceClasses( final String name,
                                         final Class type )
        throws VerifyException
    {
        final ClassLoader classLoader = type.getClassLoader();
        final Attribute[] attributes =
            Attributes.getAttributes( type, "dna.service" );
        final Class[] classes = new Class[ attributes.length ];
        for( int i = 0; i < attributes.length; i++ )
        {
            final String classname = attributes[ i ].getParameter( "type" );
            try
            {
                classes[ i ] = classLoader.loadClass( classname );
            }
            catch( final Throwable t )
            {
                final String message =
                    "Unable to load service class \"" +
                    classname + "\" for Component named \"" +
                    name + "\". (Reason: " + t + ").";
                throw new VerifyException( message, t );
            }
        }

        return classes;
    }
}
