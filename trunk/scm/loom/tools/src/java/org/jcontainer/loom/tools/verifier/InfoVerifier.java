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
import org.jcontainer.loom.tools.info.ComponentInfo;
import org.jcontainer.loom.tools.info.ServiceDescriptor;
import org.realityforge.salt.i18n.ResourceManager;
import org.realityforge.salt.i18n.Resources;
import org.realityforge.metaclass.model.Attribute;
import org.realityforge.metaclass.Attributes;

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
 * @version $Revision: 1.12 $ $Date: 2003-10-16 04:35:58 $
 */
public class InfoVerifier
    implements LogEnabled
{
    /**
     * I18n utils.
     */
    private static final Resources REZ =
        ResourceManager.getPackageResources( InfoVerifier.class );

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
     * advertised interfaces. And confrorm to expectations of {@link ComponentInfo}.
     *
     * @param name the name of component
     * @param type the component type
     * @throws VerifyException if an error occurs
     */
    public void verifyType( final String name,
                            final ComponentInfo info,
                            final Class type )
        throws VerifyException
    {
        final Attribute attribute =
            Attributes.getAttribute( type, "dna.component" );
        if( null == attribute )
        {

        }
        final Class[] interfaces =
            getServiceClasses( name,
                               info.getServices(),
                               type.getClassLoader() );

        m_verifier.verifyComponent( name, type, interfaces, false );
    }

    /**
     * Retrieve an array of Classes for all the services that a Component
     * offers. This method also makes sure all services offered are
     * interfaces.
     *
     * @param name the name of component
     * @param services the services the component offers
     * @param classLoader the classLoader
     * @return an array of Classes for all the services
     * @throws VerifyException if an error occurs
     */
    protected Class[] getServiceClasses( final String name,
                                         final ServiceDescriptor[] services,
                                         final ClassLoader classLoader )
        throws VerifyException
    {
        final Class[] classes = new Class[ services.length ];
        for( int i = 0; i < services.length; i++ )
        {
            final String classname = services[ i ].getType();
            try
            {
                classes[ i ] = classLoader.loadClass( classname );
            }
            catch( final Throwable t )
            {
                final String message =
                    REZ.format( "metadata.bad-service-class.error",
                                name,
                                classname,
                                t.toString() );
                throw new VerifyException( message, t );
            }
        }

        return classes;
    }
}
