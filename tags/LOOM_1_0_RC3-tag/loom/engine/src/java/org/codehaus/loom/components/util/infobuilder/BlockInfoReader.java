package org.codehaus.loom.components.util.infobuilder;

import org.codehaus.loom.components.util.info.ComponentInfo;

/**
 * @author <a href="mailto:proyal@pace2020.com">peter royal</a>
 */
public interface BlockInfoReader
{
    /**
     * Create a ComponentInfo object for specified classname, in specified ClassLoader.
     *
     * @param type The Components type
     *
     * @return the created ComponentInfo
     */
    ComponentInfo buildComponentInfo( Class type )
        throws Exception;
}