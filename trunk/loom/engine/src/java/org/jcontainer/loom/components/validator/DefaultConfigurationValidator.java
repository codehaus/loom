/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.components.validator;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.jcontainer.loom.interfaces.ConfigurationValidator;
import org.jcontainer.loom.tools.configuration.ConfigurationBuilder;
import org.jcontainer.loom.tools.info.SchemaDescriptor;
import org.jcontainer.loom.tools.profile.ComponentProfile;
import org.jcontainer.loom.components.util.ConfigUtil;
import org.jcontainer.dna.Configurable;
import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.jcontainer.dna.AbstractLogEnabled;
import org.jcontainer.dna.impl.ConfigurationUtil;
import org.jcontainer.dna.impl.DefaultConfiguration;
import org.realityforge.configkit.ConfigValidator;
import org.realityforge.configkit.ConfigValidatorFactory;
import org.realityforge.configkit.ValidationResult;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * This component validates the components configuration using
 * the ConfigKit toolkit. For backwards compatability it also
 * allows users to specify a map between short types and formal
 * schema uris. The previous implementation of ConfigurationValidator
 * allowed arbitary strings designate a uri and by default was configured
 * with "relax-ng" mapped to "http://relaxng.org/ns/structure/1.0". However
 * this made components that had schemas dependent on kernel configuration.
 * To make components configuration independent components should not specify
 * the full uri for schema language but backwards compatability can be
 * supported via configuration such as;
 *
 * <pre>
 *  &lt;schema-type name="relax-ng" uri="http://relaxng.org/ns/structure/1.0"/&gt;
 * </pre>
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @author <a href="mailto:proyal at apache.org">Peter Royal</a>
 * @version $Revision: 1.5 $ $Date: 2003-10-05 10:07:04 $
 * @phoenix.component
 */
public class DefaultConfigurationValidator
    extends AbstractLogEnabled
    implements ConfigurationValidator, Configurable
{
    /**
     * A map between short-names and long URIs.
     */
    private final Map m_uriMap = new HashMap();

    /**
     * Setup mapping from short-types to long types.
     *
     * @param configuration the configuration
     * @throws ConfigurationException if configuration is invalid
     */
    public void configure( Configuration configuration )
        throws ConfigurationException
    {
        final Configuration[] children = configuration.getChildren();
        for( int i = 0; i < children.length; i++ )
        {
            final Configuration child = children[ i ];
            final String key = child.getAttribute( "name" );
            final String uri = child.getAttribute( "uri" );
            m_uriMap.put( key, uri );
        }
    }

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

        if( getLogger().isDebugEnabled() )
        {
            final String message =
                "Validating component " + component.getMetaData().getName() +
                " of type " + component.getInfo().getDescriptor().getImplementationKey() +
                " with schema " + schema.getLocation() + " of type " + schema.getType();
            getLogger().debug( message );
        }

        //Get the uri of configuration schema type
        final String type = getType( schema );
        if( !type.equals( schema.getType() ) )
        {
            final String message =
                "Schema type specified as " + schema.getType() +
                " was translated to URI " + type + " for component named " +
                component.getMetaData().getName() + " of type " +
                component.getInfo().getDescriptor().getImplementationKey() +
                ". It is recomended that the components Info specify the " +
                "URI rather than the type for compatability reasons.";
            System.err.println( message );
            getLogger().warn( message );
        }

        //Get the InputSource for schema
        final InputSource inputSource =
            getSchemaInputSource( component, classLoader );

        //Actually perform the validation
        try
        {
            final ConfigValidator validator = ConfigValidatorFactory.create( type, inputSource );
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

    /**
     * Get the uri of schema type.
     * It will attempt to use the type specified by the
     * SchemaDescriptor unless the configuration explicitly
     * maps that type to another URI.
     *
     * @param schema the SchemaDescriptor
     * @return the URI of schema type
     */
    private String getType( final SchemaDescriptor schema )
    {
        final String type = schema.getType();
        final String uri = (String)m_uriMap.get( type );
        if( null != uri )
        {
            return uri;
        }
        else
        {
            return type;
        }
    }

    /**
     * Get the input source for schema specified by ComponentInfo object.
     *
     * @param component the component profile
     * @param classLoader the classloader to load schema from
     * @return the InputSource for schema
     * @throws ConfigurationException if unable to locate schema
     */
    private InputSource getSchemaInputSource( final ComponentProfile component,
                                              final ClassLoader classLoader )
        throws ConfigurationException
    {
        final SchemaDescriptor schema = component.getInfo().getConfigurationSchema();
        final String resource = calcSchemaResource( component );
        final InputStream inputStream = classLoader.getResourceAsStream( resource );
        if( null == inputStream )
        {
            final String message = "Unable to find Schema for component " +
                component.getMetaData().getName() + " of type " +
                component.getInfo().getDescriptor().getImplementationKey() +
                " at location " + resource;
            throw new ConfigurationException( message, null );
        }

        final InputSource inputSource = new InputSource( inputStream );
        inputSource.setSystemId( schema.getLocation() );
        return inputSource;
    }

    /**
     * Determine the absolute name of the resource that contains schema.
     * If the location starts with a '/' then the location is absolute
     * otherwise the location is relative to the components class.
     *
     * @param component the component profile
     * @return the absolute name of schema resource
     */
    private String calcSchemaResource( final ComponentProfile component )
    {
        final SchemaDescriptor schema = component.getInfo().getConfigurationSchema();
        final String location = schema.getLocation();
        if( location.startsWith( "/" ) )
        {
            return location;
        }
        else
        {
            final String classname =
                component.getInfo().getDescriptor().getImplementationKey();
            String resource = classname;
            final int index = classname.lastIndexOf( '.' );
            resource = classname;
            if( -1 != index )
            {
                resource = classname.substring( 0, index + 1 );
            }
            resource = resource.replace( '.', '/' );
            resource += location;
            return resource;
        }
    }
}
