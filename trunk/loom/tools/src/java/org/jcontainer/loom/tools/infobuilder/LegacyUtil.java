/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.tools.infobuilder;

import java.util.Properties;
import org.apache.avalon.framework.Version;
import org.jcontainer.loom.tools.info.ComponentDescriptor;
import org.jcontainer.loom.tools.info.ComponentInfo;
import org.jcontainer.loom.tools.info.ContextDescriptor;
import org.jcontainer.loom.tools.info.DependencyDescriptor;
import org.jcontainer.loom.tools.info.EntryDescriptor;
import org.jcontainer.loom.tools.info.FeatureDescriptor;
import org.jcontainer.loom.tools.info.LoggerDescriptor;
import org.jcontainer.loom.tools.info.ServiceDescriptor;
import org.realityforge.metaclass.model.Attribute;

/**
 * This is a set of constants and utility methods
 * to enablesupport of Legacy BlockInfo files.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.4 $ $Date: 2003-10-05 01:06:31 $
 */
public class LegacyUtil
{
    public static final String MX_ATTRIBUTE_NAME = "phoenix:mx";
    public static final Attribute MX_ATTRIBUTE = new Attribute( MX_ATTRIBUTE_NAME );
    public static final String VERSION_ATTRIBUTE_NAME = "phoenix:version";
    public static final String VERSION_ATTRIBUTE_PARAMETER = "version";
    public static final ContextDescriptor CONTEXT_DESCRIPTOR =
        new ContextDescriptor( "org.apache.avalon.phoenix.BlockContext",
                               EntryDescriptor.EMPTY_SET,
                               Attribute.EMPTY_SET );

    private LegacyUtil()
    {
    }

    /**
     * Return the version specified (if any) for feature.
     *
     * @param type the type
     * @return the translated schema type
     */
    public static String translateToSchemaUri( final String type )
    {
        if( type.equals( "relax-ng" ) )
        {
            return "http://relaxng.org/ns/structure/1.0";
        }
        else
        {
            return type;
        }
    }

    /**
     * Return the version specified (if any) for feature.
     *
     * @param feature the feature
     * @return the version string
     */
    public static String getVersionString( final FeatureDescriptor feature )
    {
        final Attribute tag = feature.getAttribute( "avalon" );
        if( null != tag )
        {
            return tag.getParameter( "version" );
        }
        return null;
    }

    public static Attribute createVersionAttribute( final String version )
    {
        final Properties parameters = new Properties();
        parameters.setProperty( VERSION_ATTRIBUTE_PARAMETER, version );
        return new Attribute( VERSION_ATTRIBUTE_NAME, parameters );
    }

    /**
     * Return true if specified service is a management service.
     *
     * @param service the service
     * @return true if specified service is a management service, false otherwise.
     */
    public static boolean isMxService( final ServiceDescriptor service )
    {
        final Attribute tag = service.getAttribute( MX_ATTRIBUTE_NAME );
        return null != tag;
    }

    /**
     * Create a version for a feature. Defaults to 1.0 if not specified.
     *
     * @param feature the feature
     * @return the Version object
     */
    public static Version toVersion( final FeatureDescriptor feature )
    {
        final String version = getVersionString( feature );
        if( null == version )
        {
            return new Version( 1, 0, 0 );
        }
        else
        {
            return Version.getVersion( version );
        }
    }

    /**
     * Create a {@link ComponentInfo} for a Listener with specified classname.
     *
     * @param implementationKey the classname of listener
     * @return the ComponentInfo for listener
     */
    public static ComponentInfo createListenerInfo( final String implementationKey )
    {
        final ComponentDescriptor descriptor =
            new ComponentDescriptor( implementationKey, Attribute.EMPTY_SET );
        return new ComponentInfo( descriptor,
                                  ServiceDescriptor.EMPTY_SET,
                                  LoggerDescriptor.EMPTY_SET,
                                  ContextDescriptor.EMPTY_CONTEXT,
                                  DependencyDescriptor.EMPTY_SET,
                                  null,
                                  null );
    }

    /**
     * Get the location of the schema. By default it is "Foo-schema.xml"
     * for the com.biz.Foo component.
     *
     * @param classname the classname of component
     * @return the location of the schema
     */
    public static String getSchemaLocationFor( final String classname )
    {
        final int index = classname.lastIndexOf( "." );
        String location = classname;
        if( -1 != index )
        {
            location = classname.substring( index + 1 );
        }
        location += "-schema.xml";
        return location;
    }
}
