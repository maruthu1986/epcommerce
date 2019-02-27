/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.performancetools.queryanalyzer;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.LoggerFactory;

import com.elasticpath.performancetools.queryanalyzer.utils.SystemProperties;

/**
 * Configurator class.
 * It invokes {@link JMXClient} to toggle log levels on
 * bundle start/stop, as well as to obtain the reference to cortex log file.
 */

public enum QueryAnalyzerConfigurator {
	/**
	 * Configurator instance.
	 */
	INSTANCE;

	private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(QueryAnalyzerConfigurator.class);

	private static final String DEFAULT_LOG_FILE_PATH = "/ep/logs/ep-cortex.log";
	private static final String DEFAULT_JMX_PORT = "6969";
	private static final int MAIN_LOG_FILES_NUMBER_OF_EXTENSIONS = 1;

	private File logFile;
	private JMXClient jmxClient;

	/**
	 * Enable trace log level for target classes via JMX.
	 *
	 * @return this class instance.
	 */
	public QueryAnalyzerConfigurator enableTraceLogLevelViaJMX() {
		final String jmxPort = resolveJmxPort();

		this.jmxClient = new JMXClient(jmxPort);
		this.jmxClient.toggleLogLevel(JMXClient.TRACE_LOG_LEVEL);

		return this;
	}

	/**
	 * Clear ehcache via JMX.
	 *
	 * @return this class instance.
	 */
	public QueryAnalyzerConfigurator clearEhCache() {
		this.jmxClient.clearEhCache();
		return this;
	}

	/**
	 * Set a reference to a Cortex log file from logback.xml.
	 *
	 * @return this class instance.
	 */
	public QueryAnalyzerConfigurator setLogFileFromLogbackConfiguration() {
		LOG.debug("Getting log file path from system property {}", SystemProperties.LOG_FILE_PATH_SYSTEM_PROPERTY);

		String logFilePath = System.getProperty(SystemProperties.LOG_FILE_PATH_SYSTEM_PROPERTY);
		if (StringUtils.isBlank(logFilePath)) {
			//Default to USER_HOME/ep/logs/ep
			logFilePath = System.getProperty("user.home") + DEFAULT_LOG_FILE_PATH;

			LOG.debug("System property {} is not specified or it's empty. Using defaults {}",
					SystemProperties.LOG_FILE_PATH_SYSTEM_PROPERTY, logFilePath);
		}

		LOG.debug("Log file path: {}", logFilePath);

		logFile = new File(logFilePath);

		if (!logFile.exists()) {
			throw new IllegalStateException("Log file not found! "
					+ "Ensure that system property [-D" + SystemProperties.LOG_FILE_PATH_SYSTEM_PROPERTY + "] is specified"
					+ "or file exists under USER_HOME/ep/logs dir");
		}

		return this;
	}

	/**
	 * Delete all existing log files (e.g. ep-cortex.log.1, ep-cortex.log.2 etc) and clears the main log
	 * (because it can't be deleted - at least not on Windows) before starting a new analysis
	 *
	 * @return {@link QueryAnalyzerConfigurator}
	 * @throws IOException an IO exception
	 */
	public QueryAnalyzerConfigurator prepareLogFile() throws IOException {
		final String logFileNamePrefix = logFile.getName().split("[.]")[0];
		final File logFolder = logFile.getParentFile();
		final File[] logFiles = logFolder.listFiles((file, fileName) -> fileName.startsWith(logFileNamePrefix));

		if (logFiles == null) {
			throw new IllegalStateException("Log files not found! "
					+ "Ensure that system property [-D" + SystemProperties.LOG_FILE_PATH_SYSTEM_PROPERTY + "] is specified"
					+ "or file exists under USER_HOME/ep/logs dir");
		}

		for (File logFile : logFiles) {
			LOG.debug("Deleting log file {}", logFile.getAbsolutePath());

			//look for main log file - must have a single extension e.g. ".log"
			if (StringUtils.countMatches(logFile.getName(), ".") == MAIN_LOG_FILES_NUMBER_OF_EXTENSIONS) {
				try (Writer writer = Files.newBufferedWriter(logFile.toPath(), StandardCharsets.UTF_8)) {
					LOG.debug("Clearing log file {}", logFile.getAbsolutePath());

					IOUtils.write("", writer);

					LOG.debug("Log file cleared successfully");
				}
			} else if (logFile.delete()) { //delete all other log files, with multiple extensions (e.g. .log.1, log.2 etc) - if possible
				LOG.debug("Log file deleted successfully");
			}
		}
		return this;
	}

	/**
	 * Get Log file.
	 *
	 * @return file object.
	 */
	public File getLogFile() {
		return logFile;
	}

	/**
	 * Set ERROR log level for target classes.
	 *
	 * @return this class INSTANCE.
	 */
	public QueryAnalyzerConfigurator restoreLogLevels() {
		jmxClient.toggleLogLevel(JMXClient.ERROR_LOG_LEVEL);
		return this;
	}

	private String resolveJmxPort() {
		final String jmxPort = System.getProperty(SystemProperties.JMX_PORT_SYSTEM_PROPERTY);
		if (!NumberUtils.isCreatable(jmxPort)) {
			LOG.warn("JMX port is not set via -D" + SystemProperties.JMX_PORT_SYSTEM_PROPERTY + " property OR is not a number.");
			LOG.warn("Default port will be used.");
			return DEFAULT_JMX_PORT;
		}
		return jmxPort;
	}
}
