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
package org.jcontainer.loom.launcher;

import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;
import org.tanukisoftware.wrapper.WrapperListener;
import org.tanukisoftware.wrapper.WrapperManager;

/**
 * A frontend for Phoenix that starts it as a native service using the Java
 * Service Wrapper at http://wrapper.tanukisoftware.org
 *
 * @author Peter Donald
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 */
public class DaemonLauncher
    implements WrapperListener, Observer
{
    /**
     * In order to avoid calling the Wrapper stop method recursively, we need to
     * keep track of whether or not the Wrapper already knows we are stopping.
     * Necessary because of the way the shutdown process in Phoenix works.
     * Ideally, we would unregister this Observer with CLIMain but we can't do
     * that for security reasons.
     */
    private boolean m_ignoreUpdates = false;

    /*---------------------------------------------------------------
     * WrapperListener Methods
     *-------------------------------------------------------------*/
    /**
     * The start method is called when the WrapperManager is signaled by the
     * native wrapper code that it can start its application.  This method call
     * is expected to return, so a new thread should be launched if necessary.
     *
     * @param args List of arguments used to initialize the application.
     * @return Any error code if the application should exit on completion of
     *         the start method.  If there were no problems then this method
     *         should return null.
     */
    public Integer start( final String[] args )
    {
        Integer exitCodeInteger = null;

        // This startup could take a while, so tell the wrapper to be patient.
        WrapperManager.signalStarting( 45000 );

        final Hashtable data = new Hashtable();
        data.put( Observer.class.getName(), this );

        if( WrapperManager.isDebugEnabled() )
        {
            System.out.println( "DaemonLauncher: starting up." );
        }

        try
        {
            int exitCode = Main.startup( args, data, false );
            if( exitCode != 0 )
            {
                exitCodeInteger = new Integer( exitCode );
            }

            if( WrapperManager.isDebugEnabled() )
            {
                System.out.println( "DaemonLauncher: startup completed." );
            }
        }
        catch( final Exception e )
        {
            e.printStackTrace();
            exitCodeInteger = new Integer( 1 );
        }

        // We are almost up now, so reset the wait time
        WrapperManager.signalStarting( 2000 );

        return exitCodeInteger;
    }

    /**
     * Called when the application is shutting down.  The Wrapper assumes that
     * this method will return fairly quickly.  If the shutdown code code could
     * potentially take a long time, then WrapperManager.stopping() should be
     * called to extend the timeout period.  If for some reason, the stop method
     * can not return, then it must call WrapperManager.stopped() to avoid
     * warning messages from the Wrapper.
     *
     * @param exitCode The suggested exit code that will be returned to the OS
     * when the JVM exits.
     * @return The exit code to actually return to the OS.  In most cases, this
     *         should just be the value of exitCode, however the user code has
     *         the option of changing the exit code if there are any problems
     *         during shutdown.
     */
    public int stop( final int exitCode )
    {
        // To avoid recursive calls, start ignoring updates.
        m_ignoreUpdates = true;

        Main.shutdown();
        return exitCode;
    }

    /**
     * Called whenever the native wrapper code traps a system control signal
     * against the Java process.  It is up to the callback to take any actions
     * necessary.  Possible values are: WrapperManager.WRAPPER_CTRL_C_EVENT,
     * WRAPPER_CTRL_CLOSE_EVENT, WRAPPER_CTRL_LOGOFF_EVENT, or
     * WRAPPER_CTRL_SHUTDOWN_EVENT
     *
     * @param event The system control signal.
     */
    public void controlEvent( final int event )
    {
        if( WrapperManager.isControlledByNativeWrapper() )
        {
            if( WrapperManager.isDebugEnabled() )
            {
                System.out.println(
                    "DaemonLauncher: controlEvent(" + event + ") - Ignored." );
            }

            // This application ignores all incoming control events.
            //  It relies on the wrapper code to handle them.
        }
        else
        {
            if( WrapperManager.isDebugEnabled() )
            {
                System.out.println(
                    "DaemonLauncher: controlEvent(" + event + ") - Stopping." );
            }

            // Not being run under a wrapper, so this isn't an NT service and should always exit.
            //  Handle the event here.
            WrapperManager.stop( 0 );
            // Will not get here.
        }
    }

    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    /**
     * We use an Observer rather than operating on some more meaningful event
     * system as Observer and friends can be loaded from system ClassLoader and
     * thus the Embeddor does not have to share a common classloader ancestor
     * with invoker
     */
    public void update( final Observable observable, final Object arg )
    {
        if( m_ignoreUpdates )
        {
            // Ignore this update
            if( WrapperManager.isDebugEnabled() )
            {
                System.out.println(
                    "DaemonLauncher: " +
                    arg
                    + " request ignored because stop already called." );
                System.out.flush();
            }
        }
        else
        {
            final String command = ( null != arg ) ? arg.toString() : "";
            if( command.equals( "restart" ) )
            {
                if( WrapperManager.isDebugEnabled() )
                {
                    System.out.println( "DaemonLauncher: restart requested." );
                    System.out.flush();
                }

                WrapperManager.restart();

                if( WrapperManager.isDebugEnabled() )
                {
                    //Should never get here???
                    System.out.println( "DaemonLauncher: restart completed." );
                    System.out.flush();
                }
            }
            else if( command.equals( "shutdown" ) )
            {
                if( WrapperManager.isDebugEnabled() )
                {
                    System.out.println( "DaemonLauncher: shutdown requested." );
                    System.out.flush();
                }

                WrapperManager.stop( 0 );

                if( WrapperManager.isDebugEnabled() )
                {
                    //Should never get here???
                    System.out.println( "DaemonLauncher: shutdown completed." );
                    System.out.flush();
                }
            }
            else
            {
                throw new IllegalArgumentException(
                    "Unknown action " + command );
            }
        }
    }

    /*---------------------------------------------------------------
     * Main Method
     *-------------------------------------------------------------*/
    public static void main( final String[] args )
    {
        // Start the application.  If the JVM was launched from the native
        //  Wrapper then the application will wait for the native Wrapper to
        //  call the application's start method.  Otherwise the start method
        //  will be called immediately.
        WrapperManager.start( new DaemonLauncher(), args );
    }
}
