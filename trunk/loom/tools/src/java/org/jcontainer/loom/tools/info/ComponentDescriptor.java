/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.tools.info;

/**
 * This class is used to provide explicit information to assembler
 * and administrator about the Component. It includes information
 * such as;
 *
 * <ul>
 *   <li>a symbolic name</li>
 *   <li>classname</li>
 * </ul>
 *
 * <p>The ComponentDescriptor also includes an arbitrary set
 * of Attribute about the component. Usually these are container
 * specific Attributes that store information relevent to a particular
 * requirement. The Attribute names should be stored with keys based
 * on package name of container. ie You could use the following</p>
 *
 * <pre>
 * public class CocoonKeys
 * {
 *     private final static String PACKAGE =
 *         CocoonKeys.class.getPackage().getName();
 *
 *     //Is object Multi-thread safe, sharable between components
 *     public final static String LIFESTYLE = PACKAGE + ".Lifestyle";
 *
 *     //Is object scoped per-request, per-session, per-page etc
 *     public final static String SCOPE = PACKAGE + ".Scope";
 * }
 *
 * ...
 *
 * ComponentDescriptor cd = ...;
 * Attribute lifestyle = cd.getAttribute( LIFESTYLE );
 * Attribute scope = cd.getAttribute( SCOPE );
 * </pre>
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003-06-26 08:37:43 $
 */
public final class ComponentDescriptor
    extends FeatureDescriptor
{
    /**
     * The implementation key for component (usually classname).
     */
    private final String m_implementationKey;

    public ComponentDescriptor( final String implementationKey,
                                final Attribute[] attribute )
    {
        super( attribute );
        if( null == implementationKey )
        {
            throw new NullPointerException( "implementationKey" );
        }

        m_implementationKey = implementationKey;
    }

    /**
     * Return the implementation key for component (usually classname).
     *
     * @return the implementation key for component (usually classname).
     */
    public String getImplementationKey()
    {
        return m_implementationKey;
    }
}
