/*
 * Created on Jan 30, 2005
 */
package org.codehaus.loom.apps.apacheds;

import java.util.Properties;

import javax.naming.directory.InitialDirContext;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;


/**
 * @dna.component
 * 
 * @author Laszlo Hornyak
 */
public class ApacheDSService implements Configurable, Initializable, Startable, LogEnabled{

	private Properties properties = null;
	private Logger logger = null;

	/* (non-Javadoc)
	 * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
	 */
	public void configure(Configuration arg0) throws ConfigurationException {
		logger.info("Configuring direcory service...");
		properties = new Properties();
		Configuration[] confs = arg0.getChild("properties").getChildren();
		for(int i=0; i<confs.length; i++){
			properties.put(confs[i].getName(), confs[i].getValue());
		}
		logger.info("Direcory service configured.");
	}

	/* (non-Javadoc)
	 * @see org.apache.avalon.framework.activity.Initializable#initialize()
	 */
	public void initialize() throws Exception {
		logger.info("Initializing directory service...");
		new InitialDirContext(properties);
		logger.info("Initialized directory service.");
	}

	/* (non-Javadoc)
	 * @see org.apache.avalon.framework.activity.Startable#start()
	 */
	public void start() throws Exception {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.apache.avalon.framework.activity.Startable#stop()
	 */
	public void stop() throws Exception {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.apache.avalon.framework.logger.LogEnabled#enableLogging(org.apache.avalon.framework.logger.Logger)
	 */
	public void enableLogging(Logger arg0) {
		logger =arg0;
	}

}
