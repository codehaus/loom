/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.components.util.factory;

import java.io.InputStream;
import org.jcontainer.loom.components.util.info.ComponentInfo;

/**
 * The ComponentBundle gives access to the sum total of all the
 * metadata and resources about a component. This includes all
 * the resources associated with a particular component and the
 * associated {@link org.jcontainer.loom.components.util.info.ComponentInfo}.
 *
 * <p>Additional resources that may be associated with a component
 * include but are not limited to;</p>
 *
 * <ul>
 *   <li>Resource property files for i18n of {@link org.jcontainer.loom.components.util.info.ComponentInfo}</li>
 *   <li>XML schema or DTD that is used when validating a components
 *       configuration, such as in Phoenix.</li>
 *   <li>Descriptor used to define management interface of
 *       component.</li>
 *   <li>Prototype used to define a component profile.</li>
 * </ul>
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003-10-16 14:45:46 $
 */
public interface ComponentBundle
{
    /**
     * Return the {@link org.jcontainer.loom.components.util.info.ComponentInfo} that describes the
     * component.
     *
     * @return the {@link org.jcontainer.loom.components.util.info.ComponentInfo} that describes the component.
     */
    ComponentInfo getComponentInfo();

    /**
     * Return an input stream for a resource associated with
     * component. The resource name can be relative or absolute.
     * Absolute names being with a '/' character.
     *
     * <p>When the component is implemented via a Java class (as
     * opposed to a remote SOAP/RMI/JMX/other service), the resources
     * are loaded from the same <code>ClassLoader</code> as
     * implementation. The resources are loaded relative to the
     * implementaion classes package and are often named after the
     * classname of the component.</p>
     *
     * <p>For example, a component <code>com.biz.Foo</code> may have
     * resources such as <code>com/biz/Foo-schema.xsd</code>,
     * <code>com/biz/Foo.mxinfo</code> or
     * <code>com/biz/Foo-profile.xml</code>.</p>
     *
     * @return the input stream for associated resource, or null
     *         if no such resource
     */
    InputStream getResourceAsStream( String resource );
}
