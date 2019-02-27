/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.performancetools.queryanalyzer;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.elasticpath.performancetools.queryanalyzer.beans.QueryStatistics;

/**
 * OSGi bundle activator.
 * <p>
 * Logback TRACE level is automatically enabled upon bundle start, for openjpa,
 * com.elasticpath.rest.resource.dispatch.operator.AnnotatedMethodDispatcher and
 * com.elasticpath.rest.resource.dispatch.linker.ResourceLinkerImpl.
 * <p>
 * Also, reference to cortex.log file is obtained from Logback configuration.
 */
public class QueryAnalyzerBundleActivator implements BundleActivator {

	@Override
	public void start(final BundleContext bundleContext) throws Exception {
		QueryAnalyzerConfigurator.INSTANCE
				.enableTraceLogLevelViaJMX()
				.setLogFileFromLogbackConfiguration()
				.prepareLogFile();
	}

	@Override
	public void stop(final BundleContext bundleContext) throws Exception {
		final QueryAnalyzerConfigurator configurator = QueryAnalyzerConfigurator.INSTANCE
				.restoreLogLevels();

		final LogParser logParser = LogParser.INSTANCE;

		final QueryStatistics statistics = logParser.parse(configurator.getLogFile());
		logParser.generateStatistics(statistics);
	}
}
