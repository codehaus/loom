/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.xmlpolicy.builder;

import java.net.URL;
import java.security.Policy;
import java.util.Map;
import org.jcontainer.loom.xmlpolicy.runtime.DefaultPolicy;

/**
 *
 * @author Peter Donald
 * @version $Revision: 1.1 $ $Date: 2004-03-31 03:27:58 $
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
