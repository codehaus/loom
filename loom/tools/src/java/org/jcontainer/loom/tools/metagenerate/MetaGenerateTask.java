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
package org.jcontainer.loom.tools.metagenerate;

import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import java.io.File;
import java.io.IOException;
import org.apache.avalon.phoenix.framework.tools.ant.FormatEnum;
import org.apache.tools.ant.BuildException;

/**
 * MetaInfo Generation Ant Taskdef
 * @author Paul Hammant
 */
public class MetaGenerateTask
    extends org.apache.avalon.phoenix.framework.tools.ant.MetaGenerateTask
{
    //private boolean m_inheritance = true;

    /**
     * Inheritence : should parent classes of blocks be queried too?
     * @param inheritance
     */
    public void setInheritance( final boolean inheritance )
    {
        log( "Inheritance should not be specified at the task level" );
    }

    public void setDest( final File destDir )
    {
        super.setDestDir( destDir );
    }

    public void execute()
        throws BuildException
    {
        final FormatEnum format = new FormatEnum();
        format.setValue( "legacy" );
        setFormat( format );
        super.execute();
        outputClasses();
    }

    /**
     * Output the classes
     *
     * @throws org.apache.tools.ant.BuildException If a problem writing output
     */
    private void outputClasses()
        throws BuildException
    {
        final int size = allClasses.size();
        for( int i = 0; i < size; i++ )
        {
            final JavaClass javaClass = (JavaClass)allClasses.get( i );
            final DocletTag topic = javaClass.getTagByName( "phoenix:mx-topic" );
            if( topic != null )
            {
                final MxinfoFactory factory =
                    new MxinfoFactory( getDestDir(), javaClass );
                try
                {
                    factory.generate();
                }
                catch( final IOException ioe )
                {
                    throw new BuildException( ioe.getMessage(), ioe );
                }
            }
        }
    }
}
