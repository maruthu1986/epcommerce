/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.performancetools.queryanalyzer.utils;

/**
 * Holder for system properties.
 */
public final class SystemProperties {

	/**
	 * JMX system property.
	 */
	public static final String JMX_PORT_SYSTEM_PROPERTY = "com.sun.management.jmxremote.port";
	/**
	 * External Cortex log file location system property.
	 */
	public static final String LOG_FILE_PATH_SYSTEM_PROPERTY = "log.file.path";
	/**
	 * Should output JSON to console only.
	 */
	public static final String PRINT_JSON_TO_CONSOLE_ONLY_SYSTEM_PROPERTY = "print.json.to.console.only";
	/**
	 * Custom location for output JSON file.
	 */
	public static final String OUTPUT_JSON_FILE_PATH_SYSTEM_PROPERTY = "output.json.file.path";

	private SystemProperties() {
		//constant holder
	}
}
