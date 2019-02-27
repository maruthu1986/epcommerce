/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.performancetools.queryanalyzer;

import java.util.Arrays;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JMX client used to toggle Logback log level.
 */
public class JMXClient {

	/**
	 * List of loggers to toggle levels to.
	 */
	static final String[] TRACE_LOGGERS = {"openjpa",
			"com.elasticpath.rest.resource.dispatch.operator.AnnotatedMethodDispatcher",
			"com.elasticpath.rest.resource.dispatch.linker.ResourceLinkerImpl",
			"com.elasticpath.rest.helix.runtime.server.PrototypeDispatcherResourceServer"};

	/**
	 * Error log level constant.
	 */
	public static final String ERROR_LOG_LEVEL = "ERROR";
	/**
	 * Trace log level constant.
	 */
	public static final String TRACE_LOG_LEVEL = "TRACE";
	private static final Logger LOG = LoggerFactory.getLogger(JMXClient.class);
	private static final String LOGBACK_MBEAN_NAME = "ch.qos.logback.classic:Name=default,Type=ch.qos.logback.classic.jmx.JMXConfigurator";
	private static final String EHCACHE_MBEAN_NAME = "net.sf.ehcache:type=CacheManager,name=CoreOSGiBundle-CacheManager";
	private MBeanServerConnection mBeanServer;

	/**
	 * Custom constructor.
	 *
	 * @param remoteJmxPort Remote JMX port.
	 */
	public JMXClient(final String remoteJmxPort) {
		try {
			final JMXServiceURL jmxServiceURL = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://:" + remoteJmxPort + "/jmxrmi");

			LOG.debug("Connecting to JMX server using url {}", jmxServiceURL);

			final JMXConnector jmxConnector = JMXConnectorFactory.connect(jmxServiceURL, null);

			mBeanServer = jmxConnector.getMBeanServerConnection();

			LOG.debug("JMX connection established successfully");
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Custom constructor.
	 *
	 * @param mBeanServer the MBean server to connect to.
	 */
	public JMXClient(final MBeanServer mBeanServer) {
		this.mBeanServer = mBeanServer;
	}

	/**
	 * Toggle log level via JMX.
	 *
	 * @param logLevel log level to toggle.
	 */
	public void toggleLogLevel(final String logLevel) {
		try {
			LOG.debug("Enabling {} level for loggers: {}", logLevel, Arrays.asList(TRACE_LOGGERS));

			final ObjectName logbackConfigurer = new ObjectName(LOGBACK_MBEAN_NAME);

			for (String traceLogger : TRACE_LOGGERS) {

				LOG.debug("Enabling {} level for logger: {}", logLevel, traceLogger);

				mBeanServer.invoke(logbackConfigurer, "setLoggerLevel",
						new Object[]{traceLogger, logLevel},
						new String[]{String.class.getName(), String.class.getName()});

				LOG.debug("{} level enabled for logger: {}", logLevel, traceLogger);
			}
		} catch (Exception e) {
			LOG.error("Error occurred while enabling {} level for loggers", logLevel, e);
		}
	}

	/**
	 * Clear ehcache.
	 */
	public void clearEhCache() {
		try {
			LOG.debug("Clearing ehcache");
			final ObjectName ehCache = new ObjectName(EHCACHE_MBEAN_NAME);
			mBeanServer.invoke(ehCache, "clearAll", new Object[]{}, new String[]{});
		} catch (Exception e) {
			LOG.error("Error occurred while clearing caches");
		}
	}
}
