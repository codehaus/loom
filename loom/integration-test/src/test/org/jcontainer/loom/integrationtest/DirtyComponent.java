package org.jcontainer.loom.integrationtest;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.DefaultServiceManager;
import junit.framework.TestCase;

public class DirtyComponent extends TestCase
    implements Serviceable, Configurable {

    public void service(ServiceManager serviceManager) {
        try {
            serviceManager.lookup("ShouldNotExist");
            fail("Should have barfed");
        }
        catch(ServiceException e) {
            // expected
        }

        try {
            DefaultServiceManager dsm = (DefaultServiceManager) serviceManager;
            fail("Should have barfed");
        } catch (Exception e) {
            // expected
        }
        assertFalse("Should not be DefaultServiceManager", serviceManager.getClass().getName().equals(DefaultServiceManager.class.getName()));
    }

    public void configure(Configuration configuration) {

        try {
            String foo = configuration.getAttribute("ShouldNotExist");
            fail("Should have barfed");
        } catch (ConfigurationException e) {
            // expected
        }

        try {
            DefaultConfiguration dc = (DefaultConfiguration) configuration;
            fail("Should have barfed");
        } catch (Exception e) {
            // expected
        }
        assertFalse("Should not be DefaultConfiguration", configuration.getClass().getName().equals(DefaultConfiguration.class.getName()));
    }

    public void testNothing() {
    }
}
