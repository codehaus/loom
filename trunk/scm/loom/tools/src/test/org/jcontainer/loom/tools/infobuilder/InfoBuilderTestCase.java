/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.jcontainer.loom.tools.infobuilder;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import junit.framework.TestCase;
import org.jcontainer.dna.impl.ConsoleLogger;
import org.jcontainer.dna.impl.ContainerUtil;
import org.jcontainer.loom.tools.info.ComponentDescriptor;
import org.jcontainer.loom.tools.info.ComponentInfo;
import org.jcontainer.loom.tools.info.ContextDescriptor;
import org.jcontainer.loom.tools.info.DependencyDescriptor;
import org.jcontainer.loom.tools.info.EntryDescriptor;
import org.jcontainer.loom.tools.info.SchemaDescriptor;
import org.jcontainer.loom.tools.info.ServiceDescriptor;
import org.jcontainer.loom.tools.qdox.DefaultInfoBuilder;
import org.jcontainer.loom.tools.qdox.LegacyInfoBuilder;
import org.realityforge.metaclass.model.Attribute;

/**
 * Abstract class which TestCases can extend.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.12 $ $Date: 2003-10-06 12:51:25 $
 */
public class InfoBuilderTestCase
    extends TestCase
{
    private static final String BASE_PACKAGE =
        "org.jcontainer.loom.tools.infobuilder.data.";

    private static final String BASE_DIR = '/' + BASE_PACKAGE.replace( '.', '/' );

    private static final String COMPONENT2 = BASE_PACKAGE + "component2";

    private static final String SOURCE1 = BASE_DIR + "QDoxComponent1.java";
    private static final String SOURCE1_INFO = BASE_PACKAGE + "QDoxComponent1";

    private static final String LSOURCE1 = BASE_DIR + "QDoxLegacyComponent1.java";
    private static final String LSOURCE1_INFO = BASE_PACKAGE + "QDoxLegacyComponent1";

    public InfoBuilderTestCase( String name )
    {
        super( name );
    }

    public void testLoadLegacyComponent()
        throws Exception
    {
        final ComponentInfo actual = loadComponentInfo( COMPONENT2 );
        final ComponentInfo expected = createDummyComponentInfo();

        InfoAssert.assertEqualStructure( COMPONENT2 + " should be identical to Dummy",
                                         expected,
                                         actual );
    }

    public void testWriteLegacyXMLComponent1()
        throws Exception
    {
        final ComponentInfo info = loadComponentInfo( COMPONENT2 );
        final LegacyBlockInfoWriter writer = new LegacyBlockInfoWriter();
        final LegacyBlockInfoReader reader = new LegacyBlockInfoReader();
        ContainerUtil.enableLogging( writer, new ConsoleLogger() );
        final File output = File.createTempFile( "info-test", ".xml" );
        final FileOutputStream outputStream = new FileOutputStream( output );
        writer.writeComponentInfo( info, outputStream );
        outputStream.close();

        ContainerUtil.enableLogging( reader, new ConsoleLogger() );
        final String implementationKey = info.getDescriptor().getImplementationKey();
        final FileInputStream inputStream = new FileInputStream( output );
        final ComponentInfo actual = reader.createComponentInfo( implementationKey, inputStream );
        inputStream.close();
        //output.deleteOnExit();
        //output.delete();

        InfoAssert.assertEqualInfos( " Dummy ComponentInfo written out and read back " +
                                     "in again should be equal",
                                     info,
                                     actual );
    }

    public void testQDoxScan()
        throws Exception
    {
        final ComponentInfo expected = loadComponentInfo( SOURCE1_INFO );
        final JavaClass javaClass = loadJavaSource( SOURCE1 );
        final DefaultInfoBuilder infoBuilder = new DefaultInfoBuilder();
        final ComponentInfo actual = infoBuilder.buildComponentInfo( javaClass );

        InfoAssert.assertEqualInfos( " ComponentInfo generated from source file",
                                     expected,
                                     actual );
    }

    public void testLegacyQDoxScan()
        throws Exception
    {
        final ComponentInfo expected = loadComponentInfo( LSOURCE1_INFO );
        final JavaClass javaClass = loadJavaSource( LSOURCE1 );
        final LegacyInfoBuilder infoBuilder = new LegacyInfoBuilder();
        final ComponentInfo actual = infoBuilder.buildComponentInfo( javaClass );

        InfoAssert.assertEqualInfos( " ComponentInfo generated from source file",
                                     expected,
                                     actual );
    }

    private JavaClass loadJavaSource( final String resource )
    {
        final JavaDocBuilder builder = new JavaDocBuilder();
        final InputStream inputStream = getClass().getResourceAsStream( resource );
        assertNotNull( "resource " + resource + " not null", inputStream );
        final Reader reader = new InputStreamReader( inputStream );
        builder.addSource( reader );

        final JavaSource[] sources = builder.getSources();
        assertEquals( "sources.length", 1, sources.length );
        final JavaSource source = sources[ 0 ];
        final JavaClass[] classes = source.getClasses();
        assertEquals( "source.getClasses()", 1, classes.length );
        return classes[ 0 ];
    }

    private ComponentInfo createDummyComponentInfo()
    {
        final ComponentDescriptor component =
            new ComponentDescriptor( "org.jcontainer.loom.tools.infobuilder.data.component2",
                                     Attribute.EMPTY_SET );

        final EntryDescriptor[] entrys = new EntryDescriptor[]{};
        final ContextDescriptor context =
            new ContextDescriptor( "org.apache.avalon.phoenix.BlockContext",
                                   entrys,
                                   Attribute.EMPTY_SET );

        final ServiceDescriptor service1 =
            new ServiceDescriptor( "org.apache.avalon.cornerstone.services.scheduler.TimeScheduler",
                                   Attribute.EMPTY_SET );
        final ServiceDescriptor service2 =
            new ServiceDescriptor( "org.apache.avalon.cornerstone.services.scheduler.TimeScheduler2",
                                   new Attribute[]{LegacyUtil.MX_ATTRIBUTE} );

        final ServiceDescriptor[] services = new ServiceDescriptor[]{service1, service2};
        final DependencyDescriptor dependency1 =
            new DependencyDescriptor( "org.apache.avalon.cornerstone.services.threads.ThreadManager",
                                      "org.apache.avalon.cornerstone.services.threads.ThreadManager",
                                      false,
                                      Attribute.EMPTY_SET );
        final DependencyDescriptor[] deps =
            new DependencyDescriptor[]{dependency1};

        final SchemaDescriptor schema =
            new SchemaDescriptor( "component2-schema.xml",
                                  "http://relaxng.org/ns/structure/1.0",
                                  Attribute.EMPTY_SET );

        return new ComponentInfo( component, services,
                                  context, deps, schema );
    }

    protected ComponentInfo loadComponentInfo( final String classname )
        throws Exception
    {
        final ClassLoader classLoader = getClass().getClassLoader();
        return createInfoBuilder().buildComponentInfo( classname, classLoader );
    }

    private InfoBuilder createInfoBuilder()
    {
        final InfoBuilder builder = new InfoBuilder();
        builder.enableLogging( new ConsoleLogger() );
        return builder;
    }
}
