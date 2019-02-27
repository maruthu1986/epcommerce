/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.core.context.configurer;

import java.io.File;

/**
 * Stores the filter output directory path for the Filter Data Action pipeline to consume.
 */
public class FilterActionConfiguration {

	private File filterOutputDirectory;

	public File getFilterOutputDirectory() {
		return filterOutputDirectory;
	}

	public void setFilterOutputDirectory(final File filterOutputDirectory) {
		this.filterOutputDirectory = filterOutputDirectory;
	}
}
