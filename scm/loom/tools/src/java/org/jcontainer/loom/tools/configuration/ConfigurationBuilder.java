/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.tools.configuration;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.avalon.framework.CascadingException;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.SAXConfigurationHandler;
import org.apache.avalon.framework.logger.Logger;
import org.realityforge.configkit.ConfigValidator;
import org.realityforge.configkit.ConfigValidatorFactory;
import org.realityforge.configkit.ResolverFactory;
import org.realityforge.configkit.ValidateException;
import org.realityforge.configkit.ValidationIssue;
import org.realityforge.configkit.ValidationResult;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * Utility class used to load Configuration trees from XML files.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.6 $ $Date: 2003-07-19 03:06:33 $
 */
public class ConfigurationBuilder
{
    public static final String COMPONENTINFO_SCHEMA = "-//LOOM/Component Info DTD Version 1.0//EN";
    public static final String BLOCKINFO_SCHEMA = "-//LOOM/Block Info DTD Version 1.0//EN";
    public static final String MXINFO_SCHEMA = "-//LOOM/Mx Info DTD Version 1.0//EN";
    public static final String ASSEMBLY_SCHEMA = "-//LOOM/Assembly DTD Version 1.1//EN";

    /**
     * The resolver that builder uses.
     */
    private static EntityResolver c_resolver;

    /**
     * Build a configuration object using an XML InputSource object, and
     * optionally validate the xml against the DTD.
     */
    public static Configuration build( final InputSource input,
                                       final String publicId,
                                       final Logger logger )
        throws Exception
    {
        setupResolver();
        final SAXConfigurationHandler handler = new SAXConfigurationHandler();
        if( null == publicId )
        {
            final SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            saxParserFactory.setNamespaceAware( false );
            final SAXParser saxParser = saxParserFactory.newSAXParser();
            final XMLReader reader = saxParser.getXMLReader();
            reader.setEntityResolver( c_resolver );
            reader.setContentHandler( handler );
            reader.setErrorHandler( handler );
            reader.parse( input );
        }
        else
        {
            final InputSource inputSource = c_resolver.resolveEntity( publicId, null );
            if( null == inputSource )
            {
                final String message = "Unable to locate schema with publicID=" + publicId;
                throw new IllegalStateException( message );
            }

            final ConfigValidator validator =
                ConfigValidatorFactory.create( inputSource, c_resolver );
            final ValidationResult result = validator.validate( input, (ContentHandler)handler );
            processValidationResults( result, logger );
        }
        return handler.getConfiguration();
    }

    /**
     * Process validation results. Print out any warnings or
     * errors and if validation failed then throw an exception.
     *
     * @param result the validation results
     * @param logger the logger to print messages to
     * @throws java.lang.Exception if validation failed
     */
    public static void processValidationResults( final ValidationResult result,
                                                 final Logger logger )
        throws Exception
    {
        if( !result.isValid() )
        {
            final ValidationIssue[] issues = result.getIssues();
            for( int i = 0; i < issues.length; i++ )
            {
                final ValidationIssue issue = issues[ i ];
                final SAXParseException exception = issue.getException();
                final String message = exception.getMessage() + " on line " + exception.getLineNumber();
                if( issue.isWarning() )
                {
                    logger.info( message );
                }
                else if( issue.isError() )
                {
                    logger.warn( message );
                }
                else if( issue.isFatalError() )
                {
                    logger.error( message );
                }
            }
            final ValidateException exception = result.getException();
            throw new CascadingException( exception.getMessage(), exception );
        }
    }

    private static void setupResolver()
        throws ParserConfigurationException, SAXException, IOException
    {
        if( null == c_resolver )
        {
            c_resolver =
                ResolverFactory.createResolver( ConfigurationBuilder.class.getClassLoader() );
        }
    }
}
