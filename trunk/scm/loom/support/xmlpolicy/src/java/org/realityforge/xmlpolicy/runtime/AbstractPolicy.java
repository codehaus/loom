/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.realityforge.xmlpolicy.runtime;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import java.util.ArrayList;
import java.util.Enumeration;
import org.realityforge.salt.io.FileUtil;

/**
 * Abstract Policy class that makes it easy to add permission
 * sets to policy.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 */
public abstract class AbstractPolicy
    extends Policy
{
    private final ArrayList m_entries = new ArrayList();

    /**
     * Overide so we can have a per-application security policy with
     * no side-effects to other applications.
     *
     * @param codeSource the CodeSource to get permissions for
     * @return the PermissionCollection
     */
    public PermissionCollection getPermissions( final CodeSource codeSource )
    {
        final CodeSource target = normalize( codeSource );

        final Permissions permissions = new Permissions();
        final int size = m_entries.size();

        for( int i = 0; i < size; i++ )
        {
            final PolicyEntry entry = (PolicyEntry)m_entries.get( i );
            if( entry.getCodeSource().implies( target ) )
            {
                copyPermissions( permissions, entry.getPermissions() );
            }
        }

        return permissions;
    }

    /**
     * Refresh policy. Ignored in this implementation.
     */
    public void refresh()
    {
    }

    /**
     * Create a set of permissions for a particular codesource.
     * These are read-write permissions and can be written till until the
     * time in which they are applied to code.
     *
     * @param codeSource the code source
     * @return the permission set
     */
    protected Permissions createPermissionSetFor( final CodeSource codeSource )
    {
        final CodeSource target = normalize( codeSource );
        final PolicyEntry entry =
            new PolicyEntry( target, new Permissions() );
        m_entries.add( entry );
        return entry.getPermissions();
    }

    /**
     * Normalizing CodeSource involves removing relative addressing
     * (like .. and .) for file urls.
     *
     * @param codeSource the codeSource to be normalized
     * @return the normalized codeSource
     */
    private CodeSource normalize( final CodeSource codeSource )
    {
        final URL initialLocation = codeSource.getLocation();

        // This is a bit of a h ack.  I don't know why CodeSource should behave like this
        // Fear not, this only seems to be a problem for home grown classloaders.
        // - Paul Hammant, Nov 2000
        if( null == initialLocation )
        {
            return codeSource;
        }

        String location = null;

        if( !initialLocation.getProtocol().equalsIgnoreCase( "file" ) )
        {
            location = initialLocation.getFile();
            location = FileUtil.normalize( location );
        }
        else
        {
            final File file = new File( initialLocation.getFile() );
            location = file.getAbsoluteFile().toString().replace( File.separatorChar, '/' );
            location = FileUtil.normalize( location );
        }

        URL finalLocation = null;
        try
        {
            finalLocation = new URL( initialLocation.getProtocol(),
                                     initialLocation.getHost(),
                                     initialLocation.getPort(),
                                     location );
        }
        catch( final MalformedURLException mue )
        {
            error( "Error building codeBase", mue );
        }

        return new CodeSource( finalLocation, codeSource.getCertificates() );
    }

    /**
     * Utility method to cpoy permissions from specified source to specified destination.
     *
     * @param destination the destination of permissions
     * @param source the source of permissions
     */
    private void copyPermissions( final Permissions destination,
                                  final Permissions source )
    {
        final Enumeration enum = source.elements();
        while( enum.hasMoreElements() )
        {
            destination.add( (Permission)enum.nextElement() );
        }
    }

    /**
     * Error occured in policy. Subclasses should overide.
     */
    protected void error( final String message,
                          final Throwable throwable )
    {
        System.err.println( message );
    }
}
