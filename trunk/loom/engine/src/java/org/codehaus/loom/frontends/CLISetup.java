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
package org.codehaus.loom.frontends;

import java.util.Properties;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.codehaus.spice.salt.i18n.ResourceManager;
import org.codehaus.spice.salt.i18n.Resources;

/**
 * The class prepare parameters based on input options.
 */
class CLISetup
{
    private static final Resources REZ = ResourceManager.getPackageResources(
        CLISetup.class );

    private static final String DEBUG_LOG_OPT = "d";
    private static final String HELP_OPT = "h";
    private static final String LOG_FILE_OPT = "l";
    private static final String PERSISTENT_OPT = "p";
    private static final String CONFIGFILE_OPT = "f";
    private static final String STDOUT_OPT = "s";

    ///Parameters created by parsing CLI options
    private final Properties m_parameters = new Properties();

    ///Command used to execute program
    private final String m_command;

    public CLISetup( final String command )
    {
        m_command = command;
    }

    /**
     * Initialise the options for command line parser.
     */
    private Options createCLOptions()
    {
        final Options options = new Options();
        options.addOption( STDOUT_OPT,
                           "std-out",
                           false,
                           REZ.getString( "cli.opt.configfile.desc" ) );
        options.addOption( HELP_OPT,
                           "help",
                           false,
                           REZ.getString( "cli.opt.help.desc" ) );
        options.addOption( CONFIGFILE_OPT,
                           "configfile",
                           true,
                           REZ.getString( "cli.opt.configfile.desc" ) );
        options.addOption( LOG_FILE_OPT,
                           "log-file",
                           true,
                           REZ.getString( "cli.opt.log-file.desc" ) );
        options.addOption( DEBUG_LOG_OPT,
                           "debug-init",
                           false,
                           REZ.getString( "cli.opt.debug-init.desc" ) );
        options.addOption( PERSISTENT_OPT,
                           "persistent",
                           false,
                           REZ.getString( "cli.opt.persistent.desc" ) );
        return options;
    }

    public Properties getParameters()
    {
        return m_parameters;
    }

    public boolean parseCommandLineOptions( final String[] args )
    {
        // create the command line parser
        final CommandLineParser parser = new PosixParser();

        // create the Options
        final Options options = createCLOptions();
        try
        {
            // parse the command line arguments
            final CommandLine line = parser.parse( options, args );

            if( line.hasOption( HELP_OPT ) )
            {
                final HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp( m_command, options );
                return false;
            }
            if( line.getArgList().size() > 0 )
            {
                final String message = REZ.getString( "cli.error.unknown.arg" );
                System.err.println( message );
                return false;
            }
            if( line.hasOption( DEBUG_LOG_OPT ) )
            {
                m_parameters.setProperty( "log-priority", "DEBUG" );
            }
            if( line.hasOption( PERSISTENT_OPT ) )
            {
                m_parameters.setProperty( CLIMain.PERSISTENT, "true" );
            }
            if( line.hasOption( CONFIGFILE_OPT ) )
            {
                final String file = line.getOptionValue( CONFIGFILE_OPT );
                m_parameters.setProperty( CLIMain.CONFIGFILE, file );
            }
            if( line.hasOption( LOG_FILE_OPT ) )
            {
                final String file = line.getOptionValue( LOG_FILE_OPT );
                m_parameters.setProperty( "log-destination", file );
            }
            if( line.hasOption( STDOUT_OPT ) )
            {
                m_parameters.setProperty( "log-stdout", "true" );
            }
            return true;
        }
        catch( final ParseException pe )
        {
            final String message = REZ.format( "cli.error.parser",
                                               pe.getMessage() );
            System.err.println( message );
            return false;
        }
    }
}
