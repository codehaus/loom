/*
 * Copyright (C) The Spice Group. All rights reserved.
 *
 * This software is published under the terms of the Spice
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.info;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import junit.framework.TestCase;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
import org.realityforge.metaclass.Attributes;
import org.realityforge.metaclass.introspector.DefaultMetaClassAccessor;
import org.realityforge.metaclass.io.MetaClassIOBinary;
import org.realityforge.metaclass.model.Attribute;
import org.realityforge.metaclass.model.ClassDescriptor;

/**
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.3 $ $Date: 2003-10-25 22:54:36 $
 */
public class GenerateLoomDescriptorsTaskTestCase
    extends TestCase
{
    public void testSourceFile()
        throws Exception
    {
        final String source =
            "package com.biz;\n" +
            "\n" +
            "/**\n" +
            " * @phoenix:block\n" +
            " */\n" +
            "public class MyClass\n" +
            "{\n" +
            "}\n";

        final File sourceDirectory = generateDirectory();
        final File destDirectory = generateDirectory();
        final FileSet fileSet = new FileSet();
        fileSet.setDir( sourceDirectory );
        fileSet.setIncludes( "**/*.java" );

        final String sourceFilename =
            sourceDirectory + File.separator + "com" + File.separator + "biz" + File.separator + "MyClass.java";
        final File sourceFile = new File( sourceFilename );
        sourceFile.getParentFile().mkdirs();
        final FileOutputStream output = new FileOutputStream( sourceFile );
        output.write( source.getBytes() );
        output.close();

        final GenerateLoomDescriptorsTask task = new GenerateLoomDescriptorsTask();
        final Project project = new Project();
        project.setBaseDir( getBaseDirectory() );
        task.setProject( project );
        task.setDestDir( destDirectory );
        task.addFileset( fileSet );
        task.execute();
        final String destFilename =
            destDirectory + File.separator + "com" + File.separator + "biz" + File.separator + "MyClass" + DefaultMetaClassAccessor.BINARY_EXT;
        final File destFile = new File( destFilename );

        assertTrue( "destFile.exists()", destFile.exists() );
        final MetaClassIOBinary io = new MetaClassIOBinary();
        final FileInputStream input = new FileInputStream( destFile );
        final ClassDescriptor descriptor = io.deserializeClass( input );
        assertEquals( "descriptor.name", "com.biz.MyClass", descriptor.getName() );
        assertEquals( "descriptor.attributes.length", 1, descriptor.getAttributes().length );
        assertEquals( "descriptor.attributes[0].name", "dna.component", descriptor.getAttributes()[ 0 ].getName() );
        assertEquals( "descriptor.methods.length", 0, descriptor.getMethods().length );
        assertEquals( "descriptor.fields.length", 0, descriptor.getFields().length );
    }

    public void testSourceFileWithTypeResolutionAndDefaultsAddition()
        throws Exception
    {
        final String source =
            "package com.biz;\n" +
            "\n" +
            "/**\n" +
            " * @phoenix:block\n" +
            " */\n" +
            "public class MyClass\n" +
            "{\n" +
            "  /**\n" +
            "   * @dna.configuration\n" +
            "   * @dna.dependency type=\"ClassLoader\"\n" +
            "   */\n" +
            "  public void myMethod()\n" +
            "  {\n" +
            "  }\n" +
            "}\n";

        final File sourceDirectory = generateDirectory();
        final File destDirectory = generateDirectory();
        final FileSet fileSet = new FileSet();
        fileSet.setDir( sourceDirectory );
        fileSet.setIncludes( "**/*.java" );

        final String sourceFilename =
            sourceDirectory + File.separator + "com" + File.separator + "biz" + File.separator + "MyClass.java";
        final File sourceFile = new File( sourceFilename );
        sourceFile.getParentFile().mkdirs();
        final FileOutputStream output = new FileOutputStream( sourceFile );
        output.write( source.getBytes() );
        output.close();

        final GenerateLoomDescriptorsTask task = new GenerateLoomDescriptorsTask();
        final Project project = new Project();
        project.setBaseDir( getBaseDirectory() );
        task.setProject( project );
        task.setDestDir( destDirectory );
        task.addFileset( fileSet );
        task.execute();
        final String destFilename =
            destDirectory + File.separator + "com" + File.separator + "biz" + File.separator + "MyClass" + DefaultMetaClassAccessor.BINARY_EXT;
        final File destFile = new File( destFilename );

        assertTrue( "destFile.exists()", destFile.exists() );
        final MetaClassIOBinary io = new MetaClassIOBinary();
        final FileInputStream input = new FileInputStream( destFile );
        final ClassDescriptor descriptor = io.deserializeClass( input );
        assertEquals( "descriptor.name", "com.biz.MyClass", descriptor.getName() );
        final Attribute[] classAttributes = descriptor.getAttributes();
        assertEquals( "descriptor.attributes.length", 1, classAttributes.length );
        final Attribute componentAttribute =
            Attributes.getAttributeByName( classAttributes, "dna.component" );
        assertNotNull( "dna.component", componentAttribute );

        assertEquals( "descriptor.methods.length", 1, descriptor.getMethods().length );
        final Attribute[] methodAttributes = descriptor.getMethods()[ 0 ].getAttributes();
        assertEquals( "descriptor.methods[0].attributes.length",
                      2, methodAttributes.length );

        final Attribute configAttribute =
            Attributes.getAttributeByName( methodAttributes, "dna.configuration" );
        assertNotNull( "dna.configuration", configAttribute );
        assertEquals( "dna.configuration location",
                      "MyClass-schema.xml",
                      configAttribute.getParameter( "location" ) );
        final Attribute dependencyAttribute =
            Attributes.getAttributeByName( methodAttributes, "dna.dependency" );
        assertNotNull( "dna.dependency", dependencyAttribute );
        assertEquals( "dna.dependency type",
                      "java.lang.ClassLoader",
                      dependencyAttribute.getParameter( "type" ) );

        assertEquals( "descriptor.fields.length", 0, descriptor.getFields().length );
    }

    private static final File generateDirectory()
        throws IOException
    {
        final File baseDirectory = getBaseDirectory();
        final File dir =
            File.createTempFile( "mgtest", ".tmp", baseDirectory ).getCanonicalFile();
        dir.delete();
        dir.mkdirs();
        assertTrue( "dir.exists()", dir.exists() );
        return dir;
    }

    private static final File getBaseDirectory()
    {
        final String tempDir = System.getProperty( "java.io.tmpdir" );
        final String baseDir = System.getProperty( "basedir", tempDir );

        final File base = new File( baseDir ).getAbsoluteFile();
        final String pathname =
            base + File.separator + "target" + File.separator + "test-data";
        final File dir = new File( pathname );
        dir.mkdirs();
        return dir;
    }
}
