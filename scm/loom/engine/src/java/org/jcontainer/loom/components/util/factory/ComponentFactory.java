/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.components.util.factory;

import org.jcontainer.loom.components.util.info.ComponentInfo;

/**
 * This interface defines the mechanism via which a
 * component or its associated ComponentInfo can
 * be created.
 *
 * <p>Usually the component or ComponentBundle will just be loaded
 * from a particular ClassLoader. However if a developer wanted
 * to dynamically assemble applications they could implement
 * a custom factory that created components via non-standard
 * mechanisms (say by wrapping remote, CORBA, or other style
 * objects).</p>
 *
 * <p>The methods take a <code>implementationKey</code> parameter
 * and usually this represents the class name of the component.
 * However in alternative component systems this may designate
 * objects via different mechanisms.</p>
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.2 $ $Date: 2003-11-03 06:17:58 $
 */
public interface ComponentFactory
{
    /**
     * Create a ComponentInfo for component specified by implementationKey.
     *
     * @param implementationKey the key indicating type of component (usually classname)
     * @return the ComponentInfo for component
     * @throws Exception if unable to create Info object
     */
    ComponentInfo createInfo( String implementationKey )
        throws Exception;

    /**
     * Create an instance of component with specified
     * implementationKey.
     *
     * @param implementationKey the key indicating type of component (usually classname)
     * @return an instance of component
     * @throws java.lang.Exception if unable to create component
     */
    Object createComponent( String implementationKey )
        throws Exception;
}
