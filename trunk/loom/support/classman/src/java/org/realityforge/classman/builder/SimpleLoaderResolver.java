/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.realityforge.classman.builder;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.jar.Manifest;
import org.codehaus.spice.salt.i18n.ResourceManager;
import org.codehaus.spice.salt.i18n.Resources;
import org.codehaus.spice.salt.io.FileUtil;
import org.codehaus.spice.salt.io.PathMatcher;
import org.realityforge.classman.runtime.JoinClassLoader;
import org.realityforge.extension.Extension;

/**
 * This is a class that performs resolver that; <ul> <li>creates "Join"
 * ClassLoaders using the  {@link JoinClassLoader} class</li> <li>creates normal
 * ClassLoaders using a {@link URLClassLoader}. It also makes sure that all
 * dependencies of jars (as declared using the JDK1.3 "Optional Pakcages" Spec)
 * are present in classloader.</li> <li>The locations are resolved to a single
 * base directiory.</li> <li>The Extensions are not resolved but may be by
 * subclasses.</li> <li>FileSets are currently unsupported and throw a {@link
 * UnsupportedOperationException} if attempt to be constructed.</li> </ul>
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.9 $ $Date: 2003-12-02 07:28:52 $
 */
public class SimpleLoaderResolver
    implements LoaderResolver
{
    /** i18n utils for presenting messages. */
    private static final Resources REZ =
        ResourceManager.getPackageResources( SimpleLoaderResolver.class );

    /** The base directory relative to which to aquire files. */
    private File m_baseDirectory;

    /**
     * Create a resolver that resolves all files according to specied
     * baseDirectory.
     *
     * @param baseDirectory the base directory
     */
    public SimpleLoaderResolver( final File baseDirectory )
    {
        setBaseDirectory( baseDirectory );
    }

    /**
     * Retrieve a URL for specified extension.
     *
     * @param extension the extension
     * @return the URL
     * @throws Exception if unable to locate URL for extension
     */
    public URL resolveExtension( final Extension extension )
        throws Exception
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Resolve a location to a particular URL.
     *
     * @param location the location
     * @return the URL
     * @throws Exception if unable to resolve location
     */
    public URL resolveURL( final String location )
        throws Exception
    {
        final File file = getFileFor( location );
        String url = file.toURL().toString();
        if( file.isDirectory() )
        {
            url += "/";
        }
        return new URL( url );
    }

    /**
     * Resolve a fileset.
     *
     * @param baseDirectory the base directory of fileset
     * @param includes the list of ant-style includes
     * @param excludes the list of ant style excludes
     * @return the URLs contained within fileset
     * @throws Exception if unable to resolve fileset
     */
    public URL[] resolveFileSet( final String baseDirectory,
                                 final String[] includes,
                                 final String[] excludes )
        throws Exception
    {
        final File base = getFileFor( "." );
        return resolveFileSet( base, baseDirectory, includes, excludes );
    }

    /**
     * Create a Join ClassLoader for specified ClassLoaders. Use {@link
     * JoinClassLoader} to implement functionality.
     *
     * @param classLoaders the ClassLoaders to "join"
     * @return the joined ClassLoader
     * @throws Exception if unable to create classloader
     */
    public ClassLoader createJoinClassLoader( final ClassLoader[] classLoaders )
        throws Exception
    {
        return new JoinClassLoader( classLoaders,
                                    ClassLoader.getSystemClassLoader() );
    }

    /**
     * Create a ClassLoader with specified parent and containing specified URLs.
     * This implementation just creates it using the default URLClassLoader.
     *
     * @param parent the parent classloader
     * @param urls the URLs that the ClassLoader should contain
     * @return the newly created ClassLoader
     * @throws Exception if unable to create classloader
     */
    public ClassLoader createClassLoader( final ClassLoader parent,
                                          final URL[] urls )
        throws Exception
    {
        final URL[] classpath = determineCompleteClasspath( urls );
        return new URLClassLoader( classpath, parent );
    }

    /**
     * Retrieve the complete classpath given an input set of URLs. The complete
     * classpath includes all URLs for extensions required by the jars
     * (according to the "Optional Package" Spec).
     *
     * @param urls the urls
     * @return the complete set of URLs for classpath
     * @throws Exception if unable to determine complete classpath set
     */
    protected final URL[] determineCompleteClasspath( final URL[] urls )
        throws Exception
    {
        final ArrayList classpathSet = new ArrayList();

        //Add all supplied URLS to classpath
        for( int i = 0; i < urls.length; i++ )
        {
            final URL url = urls[ i ];
            classpathSet.add( url );
        }

        //Add all the optional packages that are declared as
        // dependencies of class path elements
        final File[] files = getOptionalPackagesFor( urls );
        for( int i = 0; i < files.length; i++ )
        {
            final File file = files[ i ];
            classpathSet.add( file.toURL() );
        }

        //Define final classpath with all dependencies added
        return (URL[])classpathSet.toArray( new URL[ classpathSet.size() ] );
    }

    /**
     * Utility class to retrieve a file object for specified location.
     *
     * @param location which to get file for.
     * @return the file for specified location
     */
    protected File getFileFor( final String location )
        throws IOException
    {
        File base = getBaseDirectory();
        if( null == base )
        {
            base = new File( "." );
        }

        return new File( base, location ).getCanonicalFile();
    }

    /**
     * Return the base directory against which to resolve relative files.
     *
     * @return the base directory against which to resolve relative files.
     */
    protected File getBaseDirectory()
    {
        return m_baseDirectory;
    }

    /**
     * Set the base directory.
     *
     * @param baseDirectory the base directory.
     */
    protected void setBaseDirectory( File baseDirectory )
    {
        m_baseDirectory = baseDirectory;
    }

    /**
     * Retrieve the files for the optional packages required by the jars in
     * ClassPath.
     *
     * @param classPath the Classpath array
     * @return the files that need to be added to ClassLoader
     */
    protected final File[] getOptionalPackagesFor( final URL[] classPath )
        throws Exception
    {
        final Manifest[] manifests = getManifests( classPath );
        final Extension[] available = Extension.getAvailable( manifests );
        final Extension[] required = Extension.getRequired( manifests );

        if( isDebugEnabled() )
        {
            final String message1 =
                REZ.format( "available-extensions",
                            Arrays.asList( available ) );
            debug( message1 );
            final String message2 =
                REZ.format( "required-extensions",
                            Arrays.asList( required ) );
            debug( message2 );
        }

        if( 0 == required.length )
        {
            return new File[ 0 ];
        }

        final Set dependencies = new HashSet();
        final Set unsatisfied = new HashSet();

        scanDependencies( required,
                          available,
                          dependencies,
                          unsatisfied );

        final int size = unsatisfied.size();
        if( 0 != size )
        {
            final Iterator iterator = unsatisfied.iterator();
            while( iterator.hasNext() )
            {
                final Extension extension = (Extension)iterator.next();
                final Object[] params = new Object[]
                {
                    extension.getExtensionName(),
                    extension.getSpecificationVendor(),
                    extension.getSpecificationVersion(),
                    extension.getImplementationVendor(),
                    extension.getImplementationVendorID(),
                    extension.getImplementationVersion(),
                    extension.getImplementationURL()
                };
                final String message = REZ.format( "missing.extension",
                                                   params );
                warn( message );
            }

            final String message =
                REZ.format( "unsatisfied.extensions", new Integer( size ) );
            throw new Exception( message );
        }

        if( isDebugEnabled() )
        {
            final String message =
                REZ.format( "optional-packages-added", dependencies );
            debug( message );
        }

        return (File[])dependencies.toArray( new File[ dependencies.size() ] );
    }

    /**
     * Attempt to locate a set of dependencies that transitively satisfy all
     * required extensions.
     *
     * @param required the required extensions
     * @param available the extensions already available
     * @param dependencies the set of dependencies collected by this method
     * @param unsatisfied this method should place any unsatisfied depenencies
     * into this set
     */
    protected void scanDependencies( final Extension[] required,
                                     final Extension[] available,
                                     final Set dependencies,
                                     final Set unsatisfied )
    {
        throw new UnsupportedOperationException();
    }

    /**
     * write out a warning message. Subclasses may overide this method to
     * redirect logging as appropriate.
     *
     * @param message the warning message
     */
    protected void warn( final String message )
    {
    }

    /**
     * Determine if debug messages are turned on. Subclasses should overide this
     * method.
     *
     * @return true if debugging enabled.
     */
    protected boolean isDebugEnabled()
    {
        return false;
    }

    /**
     * write out a debug message. Subclasses may overide this method to redirect
     * logging as appropriate.
     *
     * @param message the debug message
     */
    protected void debug( final String message )
    {
    }

    /**
     * Retrieve all the Manifests from the specified Classlpath.
     *
     * @param classPath the classpath
     * @return the set of manifests on the classpath
     * @throws Exception if there is an error reading manifests from files on
     * classpath
     */
    private Manifest[] getManifests( final URL[] classPath )
        throws Exception
    {
        final ArrayList manifests = new ArrayList();

        for( int i = 0; i < classPath.length; i++ )
        {
            final URL element = classPath[ i ];
            if( element.getFile().endsWith( ".jar" ) )
            {
                try
                {
                    final URL url = new URL( "jar:" + element + "!/" );
                    final JarURLConnection connection =
                        (JarURLConnection)url.openConnection();
                    final Manifest manifest = connection.getManifest();
                    if( null != manifest )
                    {
                        manifests.add( manifest );
                    }
                }
                catch( final IOException ioe )
                {
                    final String message =
                        REZ.format( "bad-classpath-entry", element );
                    throw new Exception( message );
                }
            }
        }

        return (Manifest[])manifests.toArray( new Manifest[ 0 ] );
    }

    /**
     * Resolve a fileset in a particular hierarchy.
     *
     * @param base the file hierarchy to use
     * @param baseDirectory the base directory (relative to base)
     * @param includes the ant-style include patterns
     * @param excludes the ant-style exclude patterns
     * @return the resolved URLs for fileset
     */
    protected final URL[] resolveFileSet( final File base,
                                          final String baseDirectory,
                                          final String[] includes,
                                          final String[] excludes )
    {
        //woefully inefficient .. but then again - no need
        //for efficency here
        final String newBaseDirectory = FileUtil.normalize( baseDirectory );
        final String[] newIncludes = prefixPatterns( newBaseDirectory,
                                                     includes );
        final String[] newExcludes = prefixPatterns( newBaseDirectory,
                                                     excludes );
        final PathMatcher matcher = new PathMatcher( newIncludes,
                                                     newExcludes );
        final File[] files = FileUtil.resolveFileSet( base, matcher );
        try
        {
            return FileUtil.toURLs( files );
        }
        catch( IOException ioe )
        {
            throw new IllegalArgumentException( ioe.getMessage() );
        }
    }

    /**
     * Return a new array with specified prefix added to start of every element
     * in supplied array.
     *
     * @param prefix the prefix
     * @param patterns the source array
     * @return a new array with all elements having prefix added
     */
    private String[] prefixPatterns( final String prefix,
                                     final String[] patterns )
    {
        if( 0 == prefix.length() || ".".equals( prefix ) )
        {
            return patterns;
        }

        final String[] newPatterns = new String[ patterns.length ];
        for( int i = 0; i < newPatterns.length; i++ )
        {
            newPatterns[ i ] = prefix + "/" + patterns[ i ];
        }
        return newPatterns;
    }
}