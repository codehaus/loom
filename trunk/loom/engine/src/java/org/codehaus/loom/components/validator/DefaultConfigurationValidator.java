/*
 * Copyright (C) The Loom Group. All rights reserved.
 *
 * This software is published under the terms of the Loom
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.codehaus.loom.components.validator;

import org.codehaus.loom.components.util.ConfigUtil;
import org.codehaus.loom.components.util.ConfigurationBuilder;
import org.codehaus.loom.components.util.info.SchemaDescriptor;
import org.codehaus.loom.components.util.profile.ComponentProfile;
import org.codehaus.loom.interfaces.ConfigurationValidator;
import org.codehaus.spice.configkit.ComponentConfigUtil;
import org.codehaus.spice.configkit.ConfigValidator;
import org.codehaus.spice.configkit.ValidationResult;
import org.codehaus.dna.AbstractLogEnabled;
import org.codehaus.dna.Configuration;
import org.codehaus.dna.ConfigurationException;
import org.codehaus.dna.impl.ConfigurationUtil;
import org.codehaus.dna.impl.DefaultConfiguration;
import org.w3c.dom.Element;

/**
 * This component validates the components configuration using the ConfigKit
 * toolkit.
 *
 * @author Peter Donald
 * @author Peter Royal
 * @version $Revision: 1.3 $ $Date: 2004-07-02 23:24:30 $
 * @dna.component
 */
public class DefaultConfigurationValidator
    extends AbstractLogEnabled
    implements ConfigurationValidator
{
    /**
     * Check to see if configuration is valid for specified component.
     * <br/>
     * Schema type declarations of type 'relax-ng' will be substituted with
     * the full name space 'http://relaxng.org/ns/structure/1.0'. The 'relax-ng'
     * schema-type is deprecated and shouldn't be used anymore.
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
        final SchemaDescriptor schema = component.getInfo()
          .getConfigurationSchema();
        if( null == schema )
        {
            return true;
        }

        final String classname =
            component.getInfo().getType().getName();
        if( getLogger().isDebugEnabled() )
        {
            final String message =
                "Validating component " +
                component.getTemplate().getName() +
                " of type " +
                classname +
                " with schema " +
                schema.getLocation() +
                " of type " +
                schema.getType();
            getLogger().debug( message );
        }

        //Get the uri of configuration schema type
        try
        {
            String schemaType;
            if( "relax-ng".equals( schema.getType() ) )
            {
                if( getLogger().isDebugEnabled() )
                {
                    final String message = "Schema declaration of type [relax-ng]"
                      + " is deprecated. Use the full namespace instead.";
                    getLogger().debug( message );
                }
                schemaType = "http://relaxng.org/ns/structure/1.0";
            }
            else
            {
                schemaType = schema.getType();
            }
            final ConfigValidator validator = ComponentConfigUtil
              .getComponentConfigValidator( classname,
                                                          classLoader,
                                                          schema.getLocation(),
                                                          schemaType );
            if( null == validator )
            {
                final String message =
                    "Missing schema for component " +
                    component.getTemplate().getName() +
                    " of type " +
                    classname +
                    " with schema " +
                    schema.getLocation() +
                    " of type " +
                    schema.getType();
                getLogger().warn( message );
                return false;
            }
            final Configuration configuration = component.getTemplate()
                .getConfiguration();
            final DefaultConfiguration newConfiguration =
                new DefaultConfiguration( "root",
                                          configuration.getPath(),
                                          configuration.getLocation() );
            ConfigUtil.copy( newConfiguration, configuration );
            final Element element = ConfigurationUtil.toElement(
                newConfiguration );
            final ValidationResult result = validator.validate( element );
            ConfigurationBuilder.processValidationResults( result,
                                                           getLogger() );
            return true;
        }
        catch( Exception e )
        {
            final String msg = component.getTemplate().getName() +
                " failed validation due to: "
                + e.getMessage();
            getLogger().warn( msg, e );
            return false;
        }
    }
}
