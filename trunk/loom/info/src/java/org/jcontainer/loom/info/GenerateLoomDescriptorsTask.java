/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.info;

import org.realityforge.metaclass.tools.tasks.GenerateClassDescriptorsTask;
import org.realityforge.metaclass.tools.tasks.PluginElement;

/**
 * Custom Loom Descriptor creation task. This task replaces
 * the old Lomm metadata generation task and compiles the
 * information into MetaClass descriptors.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003-10-13 05:50:35 $
 */
public class GenerateLoomDescriptorsTask
    extends GenerateClassDescriptorsTask
{
    /**
     * Overide execute to add in custom
     * Loom Filters and interceptors.
     */
    public void execute()
    {
        final PluginElement filter = new PluginElement();
        filter.setName( PhoenixJavaClassFilter.class.getName() );
        addFilter( filter );
        final PluginElement interceptor = new PluginElement();
        interceptor.setName( PhoenixAttributeInterceptor.class.getName() );
        addInterceptor( interceptor );
        super.execute();
    }
}
