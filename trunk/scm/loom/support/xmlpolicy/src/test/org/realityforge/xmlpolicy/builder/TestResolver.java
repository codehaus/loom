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
import org.realityforge.xmlpolicy.runtime.DefaultPolicy;

/**
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003-06-05 09:02:47 $
 */
class TestResolver
    implements PolicyResolver
{
    public URL resolveLocation( String location )
        throws Exception
    {
        return new URL( location );
    }

    public Policy createPolicy( Map grants )
        throws Exception
    {
        return new DefaultPolicy( grants );
    }
}
