/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.components.validator;

import org.jcontainer.dna.AbstractLogEnabled;
import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.jcontainer.dna.impl.ConfigurationUtil;
import org.jcontainer.dna.impl.DefaultConfiguration;
import org.jcontainer.loom.components.util.ConfigUtil;
import org.jcontainer.loom.interfaces.ConfigurationValidator;
import org.jcontainer.loom.tools.configuration.ConfigurationBuilder;
import org.jcontainer.loom.tools.info.SchemaDescriptor;
import org.jcontainer.loom.tools.profile.ComponentProfile;
import org.realityforge.configkit.ComponentConfigUtil;
import org.realityforge.configkit.ConfigValidator;
import org.realityforge.configkit.ValidationResult;
import org.w3c.dom.Element;

/**
 * This component validates the components configuration using
 * the ConfigKit toolkit.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @author <a href="mailto:proyal at apache.org">Peter Royal</a>
 * @version $Revision: 1.10 $ $Date: 2003-10-16 00:56:16 $
 * @dna.component
 */
public class DefaultConfigurationValidator
    extends AbstractLogEnabled
    implements ConfigurationValidator
{
    /**
     * Check to see if configuration is valid for specified component.
     *
     * @param component the ComponentProfile
     * @param classLoader the ClassLoader (to load schema from if necessary)
     * @return true if configuration is valid
     * @throws ConfigurationException if expected schema is missing
     */
    public boolean isValid( final ComponentProfile component,
                            final ClassLoader classLoader )
        throws ConfigurationException
    {
        final SchemaDescriptor schema = component.getInfo().getConfigurationSchema();
        if( null == schema )
        {
            return true;
        }

        final String classname =
            component.getInfo().getType().getName();
        if( getLogger().isDebugEnabled() )
        {
            final String message =
                "Validating component " + component.getMetaData().getName() +
                " of type " + classname +
                " with schema " + schema.getLocation() + " of type " + schema.getType();
            getLogger().debug( message );
        }

        //Get the uri of configuration schema type
        try
        {
            final ConfigValidator validator =
                ComponentConfigUtil.getComponentConfigValidator( classname,
                                                                 classLoader,
                                                                 schema.getLocation(),
                                                                 schema.getType() );
            if( null == validator )
            {
                final String message =
                    "Missing schema for component " + component.getMetaData().getName() +
                    " of type " + classname +
                    " with schema " + schema.getLocation() + " of type " + schema.getType();
                getLogger().warn( message );
                return false;
            }
            final Configuration configuration = component.getMetaData().getConfiguration();
            final DefaultConfiguration newConfiguration =
                new DefaultConfiguration( "root",
                                          configuration.getPath(),
                                          configuration.getLocation() );
            ConfigUtil.copy( newConfiguration, configuration );
            final Element element = ConfigurationUtil.toElement( newConfiguration );
            final ValidationResult result = validator.validate( element );
            ConfigurationBuilder.processValidationResults( result, getLogger() );
            return true;
        }
        catch( Exception e )
        {
            final String msg = component.getMetaData().getName() + " failed validation due to: "
                + e.getMessage();
            getLogger().warn( msg, e );
            return false;
        }
    }
}
