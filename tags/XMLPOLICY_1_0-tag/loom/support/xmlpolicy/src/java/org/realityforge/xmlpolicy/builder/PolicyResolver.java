/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.realityforge.xmlpolicy.builder;

import java.net.URL;
import java.security.Policy;
import java.util.Map;

/**
 * This is the interface via which elements of Policy are resolved.
 * For example it is possible for the Policy file to use abstract URLs
 * such as "sar:/SAR-INF/lib/" which need to be mapped to a concrete
 * URL. It is also necessary for the target values of permissions
 * to be "resolved" using a pseuedo expression language.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003-04-16 11:45:59 $
 */
public interface PolicyResolver
{
    /**
     * Resolve a location to a URL.
     *
     * @param location the location
     * @return the URL
     * @throws Exception if unable to resolve URL
     */
    URL resolveLocation( String location )
        throws Exception;

    /**
     * Create a Policy object from a grant map.
     *
     * @param grants the grants map
     * @return the Policy object
     */
    Policy createPolicy( Map grants )
        throws Exception;
}
