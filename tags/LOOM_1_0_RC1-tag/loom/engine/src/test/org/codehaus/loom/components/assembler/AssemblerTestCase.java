/*
 * Copyright (C) The Loom Group. All rights reserved.
 *
 * This software is published under the terms of the Loom
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.codehaus.loom.components.assembler;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import org.codehaus.dna.Configuration;
import org.codehaus.dna.ConfigurationException;
import org.codehaus.dna.impl.ConsoleLogger;
import org.codehaus.dna.impl.DefaultConfiguration;
import org.codehaus.loom.components.assembler.Assembler;
import org.codehaus.loom.components.assembler.data.Component1;
import org.codehaus.loom.components.assembler.data.Component2;
import org.codehaus.loom.components.assembler.data.Component3;
import org.codehaus.loom.components.assembler.data.Listener1;
import org.codehaus.loom.components.assembler.data.Service1;
import org.codehaus.loom.components.deployer.PhoenixProfileBuilder;
import org.codehaus.loom.components.util.ConfigurationBuilder;
import org.codehaus.loom.components.util.metadata.DependencyDirective;
import org.codehaus.loom.components.util.profile.ComponentProfile;
import org.codehaus.loom.components.util.profile.PartitionProfile;
import org.codehaus.loom.interfaces.ContainerConstants;
import org.codehaus.loom.interfaces.LoomException;
import org.xml.sax.InputSource;

/**
 * An basic test case for the LogManager.
 *
 * @author Peter Donald
 * @author <a href="mailto:peter.royal@pobox.com">Peter Royal</a>
 * @version $Revision: 1.2 $ $Date: 2004-05-01 12:48:35 $
 */
public class AssemblerTestCase
    extends TestCase
{
    public void testBasic()
        throws Exception
    {
        final PartitionProfile partitionProfile = assembleSar( "assembly1.xml" );
        final ComponentProfile[] blocks =
            partitionProfile.getPartition( ContainerConstants.BLOCK_PARTITION ).
            getComponents();
        assertEquals( "Block Count", 2, blocks.length );

        final ComponentProfile block1 = blocks[0];
        final ComponentProfile block2 = blocks[1];
        final DependencyDirective[] dependencies1 = block1.getTemplate()
            .getDependencies();
        final DependencyDirective[] dependencies2 = block2.getTemplate()
            .getDependencies();

        assertEquals( "Block1 getImplementationKey",
                      Component1.class.getName(),
                      block1.getTemplate().getImplementationKey() );
        assertEquals( "Block1 getName", "c1", block1.getTemplate().getName() );
        assertEquals( "Block1 getDependencies count",
                      1, dependencies1.length );
        assertEquals( "Block1 dep1 name",
                      "c2",
                      dependencies1[0].getProviderName() );
        assertEquals( "Block1 dep1 role",
                      Service1.class.getName(), dependencies1[0].getKey() );
        assertTrue( "Block1 getBlockInfo non null",
                    null != block1.getInfo() );
        assertEquals( "Block1 isDisableProxy",
                      false,
                      isProxyDisabled( block1 ) );

        assertEquals( "Block2 getImplementationKey",
                      Component2.class.getName(),
                      block2.getTemplate().getImplementationKey() );
        assertEquals( "Block2 getName", "c2", block2.getTemplate().getName() );
        assertEquals( "Block2 getDependencies count",
                      0, dependencies2.length );
        assertTrue( "Block2 getBlockInfo non null",
                    null != block2.getInfo() );
        assertEquals( "Block2 isDisableProxy",
                      true,
                      isProxyDisabled( block2 ) );

        final ComponentProfile[] listeners =
            partitionProfile.getPartition( ContainerConstants.LISTENER_PARTITION ).
            getComponents();
        final ComponentProfile listener = listeners[0];

        assertEquals( "Listener Count", 1, listeners.length );
        assertEquals( "Listener1 getImplementationKey",
                      Listener1.class.getName(),
                      listener.getTemplate().getImplementationKey() );
        assertEquals( "Listener1 getName",
                      "l1",
                      listener.getTemplate().getName() );
    }

    private boolean isProxyDisabled( final ComponentProfile block2 )
    {
        return block2.getTemplate().isDisableProxy();
    }

    public void testComplex()
        throws Exception
    {
        final PartitionProfile partitionProfile = assembleSar( "assembly2.xml" );
        final ComponentProfile[] blocks =
            partitionProfile.getPartition( ContainerConstants.BLOCK_PARTITION )
            .getComponents();
        assertEquals( "Block Count", 4, blocks.length );

        final ComponentProfile block1 = blocks[0];
        final ComponentProfile block2 = blocks[1];
        final ComponentProfile block3 = blocks[2];
        final ComponentProfile block4 = blocks[3];
        final DependencyDirective[] dependencies1 = block1.getTemplate()
            .getDependencies();
        final DependencyDirective[] dependencies2 = block2.getTemplate()
            .getDependencies();
        final DependencyDirective[] dependencies3 = block3.getTemplate()
            .getDependencies();
        final DependencyDirective[] dependencies4 = block4.getTemplate()
            .getDependencies();

        assertEquals( "Block1 getImplementationKey",
                      Component2.class.getName(),
                      block1.getTemplate().getImplementationKey() );
        assertEquals( "Block1 getName",
                      "c2a",
                      block1.getTemplate().getName() );
        assertEquals( "Block1 getDependencies count",
                      0, dependencies1.length );
        assertTrue( "Block2 getBlockInfo non null",
                    null != block1.getInfo() );
        assertEquals( "Block1 isDisableProxy",
                      false,
                      isProxyDisabled( block1 ) );

        assertEquals( "Block2 getImplementationKey",
                      Component2.class.getName(),
                      block2.getTemplate().getImplementationKey() );
        assertEquals( "Block2 getName",
                      "c2b",
                      block2.getTemplate().getName() );
        assertEquals( "Block2 getDependencies count",
                      0, dependencies2.length );
        assertTrue( "Block2 getBlockInfo non null",
                    null != block2.getInfo() );
        assertEquals( "Block2 isDisableProxy",
                      false,
                      isProxyDisabled( block2 ) );

        assertEquals( "Block3 getImplementationKey",
                      Component2.class.getName(),
                      block3.getTemplate().getImplementationKey() );
        assertEquals( "Block3 getName",
                      "c2c",
                      block3.getTemplate().getName() );
        assertEquals( "Block3 getDependencies count",
                      0, dependencies3.length );
        assertTrue( "Block3 getBlockInfo non null",
                    null != block3.getInfo() );
        assertEquals( "Block3 isDisableProxy",
                      false,
                      isProxyDisabled( block3 ) );

        assertEquals( "Block4 getImplementationKey",
                      Component3.class.getName(),
                      block4.getTemplate().getImplementationKey() );
        assertEquals( "Block4 getName", "c3", block4.getTemplate().getName() );
        assertEquals( "Block4 getDependencies count",
                      3, dependencies4.length );
        final DependencyDirective dependency1 = dependencies4[0];
        final DependencyDirective dependency2 = dependencies4[1];
        final DependencyDirective dependency3 = dependencies4[2];
        assertEquals( "Block4 dep1 name",
                      "c2a",
                      dependency1.getProviderName() );
        assertEquals( "Block4 dep1 role",
                      Service1.class.getName(), dependency1.getKey() );
        assertEquals( "Block4 dep1 name",
                      "c2b",
                      dependency2.getProviderName() );
        assertEquals( "Block4 dep1 role",
                      Service1.class.getName(), dependency2.getKey() );
        assertEquals( "Block4 dep1 name",
                      "c2c",
                      dependency3.getProviderName() );
        assertEquals( "Block4 dep1 role",
                      Service1.class.getName(), dependency3.getKey() );
        assertTrue( "Block4 getBlockInfo non null",
                    null != block4.getInfo() );
        assertEquals( "Block4 isDisableProxy",
                      false,
                      isProxyDisabled( block4 ) );
    }

    public void testBuildDependencies()
        throws Exception
    {
        final DefaultConfiguration[] provides = new DefaultConfiguration[2];
        final String name0 = "Cheese";
        final String role0 = "market.Cheddar";
        final String name1 = "Bleu";
        final String role1 = "market.blue";

        provides[0] = new DefaultConfiguration( "provide", "", "" );
        provides[0].setAttribute( "name", name0 );
        provides[0].setAttribute( "role", role0 );
        provides[1] = new DefaultConfiguration( "provide", "", "" );
        provides[1].setAttribute( "name", name1 );
        provides[1].setAttribute( "role", role1 );

        final Assembler assembler = new Assembler();
        final DependencyDirective[] directives = assembler.buildDependencies( provides );

        assertEquals( provides.length, directives.length );
        assertEquals( name0, directives[0].getProviderName() );
        assertEquals( name1, directives[1].getProviderName() );
        assertEquals( role0, directives[0].getKey() );
        assertEquals( role1, directives[1].getKey() );
    }

    public void testBuildDependency()
        throws Exception
    {
        final String name = "Blah";
        final String alias = name;
        final String role = "com.biz.Service";
        final Assembler assembler = new Assembler();
        final DefaultConfiguration provide = new DefaultConfiguration( "provide", "", "" );
        provide.setAttribute( "name", name );
        provide.setAttribute( "role", role );
        final DependencyDirective directive =
            assembler.buildDependency( provide );
        assertEquals( "name", name, directive.getProviderName() );
        assertEquals( "role", role, directive.getKey() );
        assertEquals( "alias", alias, directive.getAlias() );
    }

    public void testBuildDependencyThatSpecifiesAlias()
        throws Exception
    {
        final String name = "Blah";
        final String alias = "Blee";
        final String role = "com.biz.Service";
        final Assembler assembler = new Assembler();
        final DefaultConfiguration provide = new DefaultConfiguration( "provide", "", "" );
        provide.setAttribute( "name", name );
        provide.setAttribute( "role", role );
        provide.setAttribute( "alias", alias );
        final DependencyDirective directive =
            assembler.buildDependency( provide );
        assertEquals( "name", name, directive.getProviderName() );
        assertEquals( "role", role, directive.getKey() );
        assertEquals( "alias", alias, directive.getAlias() );
    }

    public void testBuildDependencyThatMissesName()
        throws Exception
    {
        final Assembler assembler = new Assembler();
        final DefaultConfiguration provide = new DefaultConfiguration( "provide", "", "" );
        provide.setAttribute( "role", "com.biz.Service" );
        try
        {
            assembler.buildDependency( provide );
        }
        catch( final ConfigurationException ce )
        {
            return;
        }
        fail( "Expected ConfigurationException due to missing name" );
    }

    public void testBuildDependencyThatMissesRole()
        throws Exception
    {
        final Assembler assembler = new Assembler();
        final DefaultConfiguration provide = new DefaultConfiguration( "provide", "", "" );
        provide.setAttribute( "name", "MyService" );
        try
        {
            assembler.buildDependency( provide );
        }
        catch( final ConfigurationException ce )
        {
            return;
        }
        fail( "Expected ConfigurationException due to missing role" );
    }

    protected PartitionProfile assembleSar( final String config )
        throws Exception
    {
        final PhoenixProfileBuilder assembler = new PhoenixProfileBuilder();
        final URL resource = getClass().getResource( config );
        assertNotNull( "Config resource: " + config, resource );
        final ConsoleLogger logger = new ConsoleLogger();
        final Configuration assembly =
            ConfigurationBuilder.build( new InputSource( resource.toExternalForm() ),
                                        ConfigurationBuilder.ASSEMBLY_SCHEMA,
                                        logger );
        final Map parameters = new HashMap();
        parameters.put( ContainerConstants.ASSEMBLY_NAME, "test" );
        parameters.put( ContainerConstants.ASSEMBLY_DESCRIPTOR, assembly );
        parameters.put( ContainerConstants.CONFIG_DESCRIPTOR,
                        new DefaultConfiguration( "config", "", "" ) );
        parameters.put( ContainerConstants.ASSEMBLY_CLASSLOADER,
                        getClass().getClassLoader() );

        assembler.enableLogging( logger );

        return assembler.buildProfile( parameters );
    }

    public void testMalformedBlockListener()
        throws Exception
    {
        final DefaultConfiguration listener = new DefaultConfiguration( "listner", "", "" );
        final DefaultConfiguration config = new DefaultConfiguration( "config",
                                                                      "",
                                                                      "" );
        final Assembler assembler = new Assembler();

        try
        {
            assembler.buildBlockListener( listener, config );
            fail( "buildBlockListner allowed no 'name' attribute" );
        }
        catch( LoomException e )
        {
            assertEquals( "Malformed listener entry in assembly.xml at \"\". " +
                          "(Reason: Attribute named name not specified.).",
                          e.getMessage() );
        }

        listener.setAttribute( "name", "foo" );

        try
        {
            assembler.buildBlockListener( listener, config );
            fail( "buildBlockListner allowed no 'class' attribute" );
        }
        catch( LoomException e )
        {
            assertEquals( "Malformed listener entry in assembly.xml at \"\". " +
                          "(Reason: Attribute named class not specified.).",
                          e.getMessage() );
        }
    }
}
