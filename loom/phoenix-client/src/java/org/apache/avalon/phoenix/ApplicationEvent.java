/*
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
package org.apache.avalon.phoenix;

import java.util.EventObject;
import org.apache.avalon.phoenix.metadata.SarMetaData;

/**
 * This is the class that is used to deliver notifications
 * about Application state changes to the
 * <code>ApplicationListener</code>s of a Server Application.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 */
public final class ApplicationEvent
    extends EventObject
{
    private final String m_name;
    private final SarMetaData m_sarMetaData;

    /**
     * Construct the <code>ApplicationEvent</code>.
     *
     * @param name the name of app
     * @param sarMetaData the SarMetaData object for app
     */
    public ApplicationEvent( final String name,
                             final SarMetaData sarMetaData )
    {
        super( name );

        if( null == name )
        {
            throw new NullPointerException( "name" );
        }
        if( null == sarMetaData )
        {
            throw new NullPointerException( "sarMetaData" );
        }

        m_name = name;
        m_sarMetaData = sarMetaData;
    }

    /**
     * Retrieve name of app.
     *
     * @return the name of app
     */
    public String getName()
    {
        return m_name;
    }

    /**
     * Retrieve the SarMetaData for app.
     *
     * @return the SarMetaData for app
     */
    public SarMetaData getSarMetaData()
    {
        return m_sarMetaData;
    }
}
