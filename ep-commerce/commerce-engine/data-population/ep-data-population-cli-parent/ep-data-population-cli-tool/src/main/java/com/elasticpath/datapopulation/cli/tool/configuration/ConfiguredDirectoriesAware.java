/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.cli.tool.configuration;

/**
 * An interface which allows implementing classes to be configured based on the
 * locations of the configured data and config directories.
 */
public interface ConfiguredDirectoriesAware {
	/**
	 * A callback method which passes the locations of the data directory and configuration directories.
	 * These are urls to the data and configuration directories, and are very likely to refer to the filesystem (file: urls)
	 * but this isn't guaranteed. In the future this configuration may be read from the classpath, or even from a remote url
	 * for remote configuration.
	 *
	 * @param dataDirectoryLocation        the data directory location in use.
	 * @param environmentDirectoryLocation the config directory location in use.
	 */
	void directoriesConfigured(String dataDirectoryLocation, String environmentDirectoryLocation);
}
