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
package org.jcontainer.loom.interfaces;

import java.util.Date;

/**
 * This is the interface via which the Management interface interacts
 * with the Embeddor.
 *
 * @phoenix:mx-topic name="Embeddor"
 *
 * @author <a href="peter at realityforge.org">Peter Donald</a>
 */
public interface EmbeddorMBean
{
    String ROLE = EmbeddorMBean.class.getName();

    /**
     * Get name by which the server is known.
     * Usually this defaults to {@link ContainerConstants.SOFTWARE} but the admin
     * may assign another name. This is useful when you
     * are managing a cluster of servers.
     *
     * @phoenix:mx-attribute
     * @phoenix:mx-description Name by which this server is known.
     *
     * @return the name of server
     */
    String getName();

    /**
     * Get location of container installation
     *
     * @phoenix:mx-attribute
     *
     * @return the home directory of container
     */
    String getHomeDirectory();

    /**
     * Get the date at which this server started.
     *
     * @phoenix:mx-attribute
     *
     * @return the date at which this server started
     */
    Date getStartTime();

    /**
     * Retrieve the number of millisecond
     * the server has been up.
     *
     * @phoenix:mx-attribute
     *
     * @return the the number of millisecond the server has been up
     */
    long getUpTimeInMillis();

    /**
     * Retrieve a string identifying version of server.
     * Usually looks like "x.y.z".
     *
     * @phoenix:mx-attribute
     * @phoenix:mx-description Retrieve a string identifying version of server.
     *
     * @return version string of server.
     */
    String getVersion();

    /**
     * Get a string defining the build.
     * Possibly the date on which it was built, where it was built,
     * with what features it was built and so forth.
     *
     * @phoenix:mx-attribute
     *
     * @return the string describing build
     */
    String getBuild();

    /**
     * Request the Embeddor shutsdown.
     *
     * @phoenix:mx-operation
     */
    void shutdown();

    /**
     * Request the embeddor to restart.
     *
     * @phoenix:mx-operation
     *
     * @throws UnsupportedOperationException if restart not a supported operation
     */
    void restart()
        throws UnsupportedOperationException;
}
