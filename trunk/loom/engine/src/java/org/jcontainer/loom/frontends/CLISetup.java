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
package org.jcontainer.loom.frontends;

import java.util.List;
import java.util.Properties;
import org.realityforge.cli.CLArgsParser;
import org.realityforge.cli.CLOption;
import org.realityforge.cli.CLOptionDescriptor;
import org.realityforge.cli.CLUtil;
import org.realityforge.salt.i18n.ResourceManager;
import org.realityforge.salt.i18n.Resources;

/**
 * The class prepare parameters based on input options.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @author <a href="mail@leosimons.com">Leo Simons</a>
 */
class CLISetup
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( CLISetup.class );

    private static final int DEBUG_LOG_OPT = 'd';
    private static final int HELP_OPT = 'h';
    private static final int LOG_FILE_OPT = 'l';
    private static final int APPS_PATH_OPT = 'a';
    private static final int PERSISTENT_OPT = 'p';
    private static final int CONFIGFILE_OPT = 'f';
    private static final int DISABLE_HOOK_OPT = 2;

    ///Parameters created by parsing CLI options
    private final Properties m_parameters = new Properties();

    ///Command used to execute program
    private final String m_command;

    public CLISetup( final String command )
    {
        m_command = command;
    }

    /**
     * Display usage report.
     */
    private void usage( final CLOptionDescriptor[] options )
    {
        System.err.println( m_command );
        System.err.println( "\t" + REZ.getString( "cli.desc.available.header" ) );
        System.err.println( CLUtil.describeOptions( options ) );
    }

    /**
     * Initialise the options for command line parser.
     */
    private CLOptionDescriptor[] createCLOptions()
    {
        final CLOptionDescriptor options[] = new CLOptionDescriptor[ 7 ];
        options[ 0 ] =
            new CLOptionDescriptor( "help",
                                    CLOptionDescriptor.ARGUMENT_DISALLOWED,
                                    HELP_OPT,
                                    REZ.getString( "cli.opt.help.desc" ) );
        options[ 1 ] =
            new CLOptionDescriptor( "log-file",
                                    CLOptionDescriptor.ARGUMENT_REQUIRED,
                                    LOG_FILE_OPT,
                                    REZ.getString( "cli.opt.log-file.desc" ) );
        options[ 2 ] =
            new CLOptionDescriptor( "apps-path",
                                    CLOptionDescriptor.ARGUMENT_REQUIRED,
                                    APPS_PATH_OPT,
                                    REZ.getString( "cli.opt.apps-path.desc" ) );
        options[ 3 ] =
            new CLOptionDescriptor( "debug-init",
                                    CLOptionDescriptor.ARGUMENT_DISALLOWED,
                                    DEBUG_LOG_OPT,
                                    REZ.getString( "cli.opt.debug-init.desc" ) );
        options[ 4 ] =
            new CLOptionDescriptor( "disable-hook",
                                    CLOptionDescriptor.ARGUMENT_DISALLOWED,
                                    DISABLE_HOOK_OPT,
                                    REZ.getString( "cli.opt.disable-hook.desc" ) );
        options[ 5 ] =
            new CLOptionDescriptor( "persistent",
                                    CLOptionDescriptor.ARGUMENT_DISALLOWED,
                                    PERSISTENT_OPT,
                                    REZ.getString( "cli.opt.persistent.desc" ) );
        options[ 6 ] =
            new CLOptionDescriptor( "configfile",
                                    CLOptionDescriptor.ARGUMENT_REQUIRED,
                                    CONFIGFILE_OPT,
                                    REZ.getString( "cli.opt.configfile.desc" ) );
        return options;
    }

    public Properties getParameters()
    {
        return m_parameters;
    }

    public boolean parseCommandLineOptions( final String[] args )
    {
        final CLOptionDescriptor[] options = createCLOptions();
        final CLArgsParser parser = new CLArgsParser( args, options );

        if( null != parser.getErrorString() )
        {
            final String message = REZ.getString( "cli.error.parser", parser.getErrorString() );
            System.err.println( message );
            return false;
        }

        final List clOptions = parser.getArguments();
        final int size = clOptions.size();

        for( int i = 0; i < size; i++ )
        {
            final CLOption option = (CLOption)clOptions.get( i );

            switch( option.getDescriptor().getId() )
            {
                case 0:
                    {
                        final String message =
                            REZ.getString( "cli.error.unknown.arg", option.getArgument() );
                        System.err.println( message );
                    }
                    return false;

                case HELP_OPT:
                    usage( options );
                    return false;

                case DEBUG_LOG_OPT:
                    m_parameters.setProperty( "log-priority", "DEBUG" );
                    break;

                case LOG_FILE_OPT:
                    m_parameters.setProperty( "log-destination", option.getArgument() );
                    break;

                case APPS_PATH_OPT:
                    m_parameters.setProperty( CLIMain.APPS_DIR,
                                              option.getArgument() );
                    break;

                case PERSISTENT_OPT:
                    m_parameters.setProperty( CLIMain.PERSISTENT, "true" );
                    break;

                case DISABLE_HOOK_OPT:
                    m_parameters.setProperty( CLIMain.DISABLE_HOOK, "true" );
                    break;

                case CONFIGFILE_OPT:
                    m_parameters.setProperty( CLIMain.CONFIGFILE, option.getArgument() );
                    break;
            }
        }

        return true;
    }
}
