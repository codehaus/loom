/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.realityforge.xmlpolicy.builder;

import java.security.KeyStore;
import java.util.HashMap;

/**
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003-06-05 09:40:52 $
 */
class MockKeyStore
    extends KeyStore
{
    MockKeyStore( final HashMap certs )
    {
        super( new MockKeyStoreSpi( certs ), null, null );
    }
}
