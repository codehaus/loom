/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
 package org.jcontainer.loom.tools.metadata;

import java.util.Map;

/**
 * Load metadata for an Assembly from some source.
 * The source is usually one or more xml config files.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.3 $ $Date: 2003-06-29 01:07:36 $
 */
public interface MetaDataBuilder
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
    PartitionMetaData buildAssembly( Map parameters )
        throws Exception;
}
