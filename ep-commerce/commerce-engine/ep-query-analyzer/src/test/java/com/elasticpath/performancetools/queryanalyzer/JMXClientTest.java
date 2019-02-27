/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.performancetools.queryanalyzer;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.jmx.JMXConfigurator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;

/**
 * Test class for {@link JMXClientTest}.
 */
@RunWith(MockitoJUnitRunner.class)
public class JMXClientTest {

	@Test
	public void shouldConnectToMBeanServerWhenJMXPortIsSpecified() throws Exception {
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		ObjectName name = new ObjectName("ch.qos.logback.classic:Name=default,Type=ch.qos.logback.classic.jmx.JMXConfigurator");
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		JMXConfigurator jmxConfigurator = new JMXConfigurator(loggerContext, mbs, name);

		mbs.registerMBean(jmxConfigurator, name);

		JMXClient jmxClient = new JMXClient(mbs);
		jmxClient.toggleLogLevel(JMXClient.TRACE_LOG_LEVEL);

		List<String> loggerLevels = new ArrayList<>();

		for (String logger : JMXClient.TRACE_LOGGERS) {
			final String loggerLevel = (String) mbs.invoke(name,
					"getLoggerLevel",
					new Object[]{logger},
					new String[]{String.class.getName()});

			loggerLevels.add(loggerLevel);
		}

		assertThat(loggerLevels)
				.allMatch(level -> level.equalsIgnoreCase(JMXClient.TRACE_LOG_LEVEL));
	}
}
