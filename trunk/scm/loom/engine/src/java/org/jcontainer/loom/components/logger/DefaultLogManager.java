/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 *
 * This product includes software developed by the
 * Apache Software Foundation (http://www.apache.org/).
 */
package org.jcontainer.loom.components.logger;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationUtil;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.phoenix.BlockContext;
import org.jcomponent.loggerstore.DOMLog4JLoggerStoreFactory;
import org.jcomponent.loggerstore.InitialLoggerStoreFactory;
import org.jcomponent.loggerstore.Jdk14LoggerStoreFactory;
import org.jcomponent.loggerstore.LogKitLoggerStoreFactory;
import org.jcomponent.loggerstore.LoggerStore;
import org.jcomponent.loggerstore.LoggerStoreFactory;
import org.jcomponent.loggerstore.PropertyLog4JLoggerStoreFactory;
import org.jcomponent.loggerstore.SimpleLogKitLoggerStoreFactory;
import org.jcontainer.loom.components.util.ResourceUtil;
import org.jcontainer.loom.interfaces.LogManager;
import org.realityforge.configkit.PropertyExpander;
import org.realityforge.configkit.ResolverFactory;
import org.realityforge.salt.i18n.ResourceManager;
import org.realityforge.salt.i18n.Resources;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;

/**
 * Interface that is used to manage Log objects for a Sar.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 */
public class DefaultLogManager
    extends AbstractLogEnabled
    implements LogManager, Contextualizable
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( DefaultLogManager.class );
	
    private final PropertyExpander m_expander = new PropertyExpander();

    private final InitialLoggerStoreFactory m_factory = new InitialLoggerStoreFactory();

    /**
     * Hold the value of loom.home
     */
    private File m_loomHome;

    public void contextualize( final Context context ) throws ContextException
    {
        m_loomHome = (File)context.get( "loom.home" );
    }

    private Map createLoggerManagerContext( final Map appContext )
    {
        final HashMap data = new HashMap();
        data.putAll( appContext );
        data.put( "loom.home", m_loomHome );
        return data;
    }


    /**
     * Normalises file paths by replacing File.separatorChar with '/'
     * and storing the path in the map in place of the File object
     * which would be converted back to using the File.separatorChar
     * 
     * @param data the Map containing the File objects
     * @return the Map with the normalised File paths 
     */
    private Map normaliseFilePaths( final Map data ){
    	final Map map = new HashMap();
    	map.putAll( data );
        for ( Iterator i = data.keySet().iterator(); i.hasNext(); )
        {
            final Object key = i.next();
            final Object value = data.get( key );
            if ( value instanceof File )
            {
                final File file = (File)value;
				final String newPath = normalisePath( file.getPath() );
				// replace File object value with its path
                map.put( key, newPath );
            }
        }
        return map;
    }

	/**
	 * Normalises file path by replacing File.separatorChar with '/'
	 * 
	 * @param path the file path is to be normalised
	 * @return the normalised file path
	 */
	private String normalisePath( final String path ) {
		return path.replace( File.separatorChar, '/' );
	}


    /**
     * Create a Logger hierarchy for specified application.
     *
     * @param logs the configuration data for logging
     * @param context the context in which to create loggers
     * @return the Log hierarchy
     * @throws Exception if unable to create Loggers
     */
    public LoggerStore createHierarchy( final Configuration logs,
                                        final File homeDirectory,
                                        final File workDirectory,
                                        final Map context )
        throws Exception
    {
        final Map map = createLoggerManagerContext( context );
		// normalise file paths for log4j to circumvent backslash replacement
		final Map normalisedMap = normaliseFilePaths( map );
        if( null == logs )
        {
            LoggerStore store = null;
            store = scanForLoggerConfig( "SAR-INF/log4j.properties",
                                         PropertyLog4JLoggerStoreFactory.class.getName(),
                                         homeDirectory,
                                         workDirectory,
                                         normalisedMap );
            if( null != store )
            {
                return store;
            }
            store = scanForLoggerConfig( "SAR-INF/log4j.xml",
                                         DOMLog4JLoggerStoreFactory.class.getName(),
                                         homeDirectory,
                                         workDirectory,
                                         normalisedMap );
            if( null != store )
            {
                return store;
            }
            store = scanForLoggerConfig( "SAR-INF/logging.properties",
                                         Jdk14LoggerStoreFactory.class.getName(),
                                         homeDirectory,
                                         workDirectory,
                                         map );
            if( null != store )
            {
                return store;
            }
            store = scanForLoggerConfig( "SAR-INF/excalibur-logger.xml",
                                         LogKitLoggerStoreFactory.class.getName(),
                                         homeDirectory,
                                         workDirectory,
                                         map );
            if( null != store )
            {
                return store;
            }

            //TODO: Set up a default LoggerStore at this point
            final String message = "Unable to locate any logging configuration";
            throw new IllegalStateException( message );
        }
        else
        {
            final String version = logs.getAttribute( "version", "1.0" );
            if( getLogger().isDebugEnabled() )
            {
                final String message =
                    REZ.format( "logger-create",
                                context.get( BlockContext.APP_NAME ),
                                version );
                getLogger().debug( message );
            }

            if( version.equals( "1.0" ) )
            {
                final LoggerStoreFactory loggerManager = new SimpleLogKitLoggerStoreFactory();
                ContainerUtil.enableLogging( loggerManager, getLogger() );
                final HashMap config = new HashMap();
                config.put( Logger.class.getName(), getLogger() );
                config.put( Context.class.getName(), new DefaultContext( map ) );
                config.put( Configuration.class.getName(), logs );
                return loggerManager.createLoggerStore( config );
            }
            else if( version.equals( "1.1" ) )
            {
                final LoggerStoreFactory loggerManager = new LogKitLoggerStoreFactory();
                ContainerUtil.enableLogging( loggerManager, getLogger() );
                final HashMap config = new HashMap();
                config.put( Logger.class.getName(), getLogger() );
                config.put( Context.class.getName(), new DefaultContext( map ) );
                config.put( Configuration.class.getName(), logs );
                return loggerManager.createLoggerStore( config );
            }
            else if( version.equals( "log4j" ) )
            {
                final LoggerStoreFactory loggerManager = new DOMLog4JLoggerStoreFactory();
                ContainerUtil.enableLogging( loggerManager, getLogger() );
                final HashMap config = new HashMap();
                final Element element = buildLog4JConfiguration( logs );
                m_expander.expandValues( element, normalisedMap );
                config.put( Element.class.getName(), element );
                return loggerManager.createLoggerStore( config );
            }
            else
            {
                final String message =
                    "Unknown logger version '" + version + "' in environment.xml";
                throw new IllegalStateException( message );
            }
        }
    }

    private Element buildLog4JConfiguration( final Configuration logs )
    {
        final Element element = ConfigurationUtil.toElement( logs );
        final Document document = element.getOwnerDocument();
        final Element newElement = document.createElement( "log4j:configuration" );
        final NodeList childNodes = element.getChildNodes();
        final int length = childNodes.getLength();
        for( int i = 0; i < length; i++ )
        {
            final Node node = childNodes.item( i );
            final Node newNode = node.cloneNode( true );
            newElement.appendChild( newNode );
        }

        document.appendChild( newElement );
        return newElement;
    }

    private LoggerStore scanForLoggerConfig( final String location,
                                             final String classname,
                                             final File homeDirectory,
                                             final File workDirectory,
                                             final Map context )
        throws Exception
    {
        final boolean isPropertiesFile = location.endsWith( "properties" );
        final File file =
            ResourceUtil.getFileForResource( location,
                                             homeDirectory,
                                             workDirectory );
        LoggerStore store = null;
        if( null != file )
        {
            final HashMap config = new HashMap();
            if( isPropertiesFile )
            {
                final Properties properties = new Properties();
                properties.load( file.toURL().openStream() );
                final Properties newProperties =
                    m_expander.expandValues( properties, context );
                config.put( Properties.class.getName(), newProperties );
            }
            //TODO: Remove next line as it is an ugly hack!
            else if( !location.equals( "SAR-INF/excalibur-logger.xml" ) )
            {
                final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                final DocumentBuilder builder = factory.newDocumentBuilder();

                //TODO: Need to set up config files for entity resolver
                final EntityResolver resolver =
                    ResolverFactory.createResolver( getClass().getClassLoader() );
                builder.setEntityResolver( resolver );
                final Document document = builder.parse( file );
                final Element element = document.getDocumentElement();
                m_expander.expandValues( element, context );
                config.put( Element.class.getName(), element );
            }
            else
            {
                config.put( LoggerStoreFactory.URL_LOCATION, file.toURL() );
            }
            config.put( InitialLoggerStoreFactory.INITIAL_FACTORY, classname );
            store = m_factory.createLoggerStore( config );
        }
        return store;
    }
}
