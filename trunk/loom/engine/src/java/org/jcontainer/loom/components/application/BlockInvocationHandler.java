/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 *
 * This product includes software developed by the
 * Apache Software Foundation (http://www.apache.org/).
 */
package org.jcontainer.loom.components.application;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * This makes a dynamic proxy for an object.  The object can be represented
 * by one, some or all of it's interfaces.
 *
 * <p>Amongst other things, it's an anti hackinge measure.  Suitable armed code
 * could have case an interface for a thing back to it's impl and used methods
 * and properties that were not it's authors intention.  Reflection too allows
 * some powerful introspection things and some traversal even more things
 * including private member vars by a serialisation trick... hence the transient.</p>
 *
 * <p>This proxy also allows itself to be invalidated thus making it
 * impossible to call methods on a Block after it has been shutdown.</p>
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @author <a href="mailto:Paul_Hammant@yahoo.com">Paul Hammant</a>
 * @version CVS $Revision: 1.1 $ $Date: 2003-06-29 04:38:21 $
 */
final class BlockInvocationHandler
    implements InvocationHandler
{
    private transient Object m_object;
    private transient Object m_proxy;

    /**
     * Create a proxy object that has specified interfaces implemented by proxy.
     *
     * @param object the underlying object
     * @param interfaces the interfaces to proxy
     */
    BlockInvocationHandler( final Object object, final Class[] interfaces )
    {
        final ClassLoader classLoader = object.getClass().getClassLoader();

        m_object = object;
        m_proxy = Proxy.newProxyInstance( classLoader, interfaces, this );
    }

    /**
     * Invalidate Proxy making it impossible to call methods
     * of real-object.
     */
    public void invalidate()
    {
        m_object = null;
        m_proxy = null;
    }

    /**
     * Return the proxy object.
     *
     * @return the proxy object
     */
    public Object getProxy()
    {
        return m_proxy;
    }

    /**
     * Invoke the specified method on underlying object.
     * This is called by proxy object.
     *
     * @param proxy the proxy object
     * @param method the method invoked on proxy object
     * @param args the arguments supplied to method
     * @return the return value of method
     * @throws Throwable if an error occurs
     */
    public Object invoke( final Object proxy,
                          final Method method,
                          final Object[] args )
        throws Throwable
    {
        if( null != m_object )
        {
            try
            {
                return method.invoke( m_object, args );
            }
            catch( final InvocationTargetException ite )
            {
                throw ite.getTargetException();
            }
        }
        else
        {
            throw new IllegalStateException( "Using a stale object reference "
                                             + "to call a disposed Block." );
        }
    }
}
