/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.tools.ant;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.jcontainer.dna.impl.ConsoleLogger;
import org.jcontainer.loom.tools.info.ComponentInfo;
import org.jcontainer.loom.tools.infobuilder.BlockInfoReader;
import org.jcontainer.loom.tools.verifier.InfoVerifier;

/**
 * Simple task to load a {@link ComponentInfo} descriptor,
 * a component class and verify that the implementation class
 * is compatible with the {@link ComponentInfo}.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.10 $ $Date: 2003-10-16 04:32:55 $
 */
public class ComponentVerifierTask
    extends Task
{
    private Path m_classpath;
    private String m_classname;

    public void setClassname( String classname )
    {
        m_classname = classname;
    }

    public Path createClasspath()
    {
        if( m_classpath == null )
        {
            m_classpath = new Path( getProject() );
        }
        return m_classpath.createPath();
    }

    public void execute()
        throws BuildException
    {
        if( null == m_classname )
        {
            final String message = "User did not specify classname";
            throw new BuildException( message );
        }
        if( null == m_classpath )
        {
            final String message = "User did not specify classpath";
            throw new BuildException( message );
        }

        final AntClassLoader classLoader = new AntClassLoader( getProject(), m_classpath );

        final BlockInfoReader builder = new BlockInfoReader();
        final InfoVerifier verifier = new InfoVerifier();
        verifier.enableLogging( new ConsoleLogger( ConsoleLogger.LEVEL_INFO ) );

        try
        {
            final Class type =
                classLoader.loadClass( m_classname );
            final ComponentInfo componentInfo =
                builder.buildComponentInfo( type );
            verifier.verifyType( "test",
                                 componentInfo,
                                 type );
        }
        catch( final Exception e )
        {
            final String message =
                "Failed to validate " + m_classname + " due to " + e;
            throw new BuildException( message, e );
        }
    }
}
