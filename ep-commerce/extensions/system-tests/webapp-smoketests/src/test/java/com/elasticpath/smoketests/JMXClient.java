package com.elasticpath.smoketests;

import javax.management.MBeanServerConnection;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Util class for accessing MBeans on remote JVM.
 */

public class JMXClient {

	private static final Logger LOG = LoggerFactory.getLogger(JMXClient.class);
	private MBeanServerConnection mBeanServer;

	/**
	 * Custom constructor.
	 * @param remoteJmxPort Remote JMX port passed from "start.remote.jmx.port" Maven property
	 */
	public JMXClient(final String remoteJmxPort) {

		try {
			final JMXServiceURL jmxServiceURL = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://:" + remoteJmxPort + "/jmxrmi");
			final JMXConnector jmxc = JMXConnectorFactory.connect(jmxServiceURL, null);

			mBeanServer = jmxc.getMBeanServerConnection();

		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Return MBean object instance, if found.
	 *
	 * @param objectNameUri MBean URI
	 * @return Object instance or null
	 */
	public ObjectInstance getObjectInstance(final String objectNameUri) {

		try {

			final ObjectName cacheName = new ObjectName(objectNameUri);
			return mBeanServer.getObjectInstance(cacheName);

		} catch (Exception e) {
			LOG.error("Error occurred while getting object instance", e);
		}

		return null;
	}

}
