/* ====================================================================
 * JContainer Software License, version 1.1
 *
 * Copyright (c) 2003, JContainer Group. All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. Neither the name of the JContainer Group nor the name "Loom" nor
 *    the names of its contributors may be used to endorse or promote
 *    products derived from this software without specific prior
 *    written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * ====================================================================
 *
 * JContainer Loom includes code from the Apache Software Foundation
 *
 * ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1997-2003 The Apache Software Foundation. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *    "This product includes software developed by the
 *    Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software
 *    itself, if and wherever such third-party acknowledgments
 *    normally appear.
 *
 * 4. The names "Jakarta", "Avalon", and "Apache Software Foundation"
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */
package org.jcontainer.loom.components.validator;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.ConfigurationUtil;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.jcontainer.loom.interfaces.ConfigurationValidator;
import org.jcontainer.loom.tools.configuration.ConfigurationBuilder;
import org.jcontainer.loom.tools.info.SchemaDescriptor;
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
 * @version $Revision: 1.2 $ $Date: 2003-08-17 18:27:33 $
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
    public boolean isValid( final org.jcontainer.loom.tools.profile.ComponentProfile component,
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
            final DefaultConfiguration newConfiguration = new DefaultConfiguration( "root", configuration.getLocation() );
            newConfiguration.addAll( configuration );
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
    private InputSource getSchemaInputSource( final org.jcontainer.loom.tools.profile.ComponentProfile component, final ClassLoader classLoader ) throws ConfigurationException
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
            throw new ConfigurationException( message );
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
    private String calcSchemaResource( final org.jcontainer.loom.tools.profile.ComponentProfile component )
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
