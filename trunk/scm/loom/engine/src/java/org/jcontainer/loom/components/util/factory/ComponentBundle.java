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
 * @version $Revision: 1.2 $ $Date: 2003-11-03 06:11:26 $
 */
public interface ComponentBundle
{
    /**
     * Return the ComponentInfo that describes the
     * component.
     *
     * @return the ComponentInfo that describes the component.
     */
    ComponentInfo getComponentInfo();
}
