/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.tools.profile;

import java.util.Map;
import org.jcontainer.loom.tools.profile.PartitionProfile;

/**
 * Load metadata for an Assembly from some source.
 * The source is usually one or more xml config files.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003-06-29 00:44:44 $
 */
public interface ProfileBuilder
{
    /**
     * Load metadata from a particular source
     * using specified map of parameters. The content
     * of the parameters is left unspecified.
     *
     * @param parameters the parameters indicating method to load meta data source
     * @return the set of components in metadata
     * @throws java.lang.Exception if unable to load or resolve
     *         meta data for any reason
     */
    PartitionProfile buildProfile( Map parameters )
        throws Exception;
}
