/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.core.action.impl;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.elasticpath.datapopulation.core.action.DataPopulationAction;
import com.elasticpath.datapopulation.core.context.DataPopulationContext;
import com.elasticpath.datapopulation.core.exceptions.DataPopulationActionException;

/**
 * This action cleans the working directory to blank state. Data population fails if this action
 * cannot be achieved.
 */
public class CleanWorkDirectoryActionImpl implements DataPopulationAction {

	private File workingDirectory;

	@Override
	public void execute(final DataPopulationContext context) {
		if (workingDirectory == null) {
			throw new DataPopulationActionException("Aborted: the working directory is not specified.");
		}

		deleteDirectory(workingDirectory);

		File tmpDir = new File(workingDirectory, "config/on-classpath");
		tmpDir.mkdirs();
	}

	private void deleteDirectory(final File workDirectory) {
		if (workDirectory.exists()) {
			try {
				FileUtils.deleteDirectory(workDirectory);
			} catch (IOException e) {
				throw new DataPopulationActionException("Aborted: the working directory cannot be deleted at this moment. " + e.getMessage(), e);
			}
		}
	}

	public File getWorkingDirectory() {
		return workingDirectory;
	}

	public void setWorkingDirectory(final File workingDirectory) {
		this.workingDirectory = workingDirectory;
	}
}
