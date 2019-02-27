/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.core.action.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.elasticpath.datapopulation.core.action.DataPopulationAction;
import com.elasticpath.datapopulation.core.context.DataPopulationContext;
import com.elasticpath.datapopulation.core.exceptions.DataPopulationActionException;
import com.elasticpath.datapopulation.core.service.FilterService;
import com.elasticpath.datapopulation.core.utils.ClasspathResourceResolverUtil;
import com.elasticpath.datapopulation.core.utils.DpUtils;

/**
 * This action generates the importexporttool.config file based on the database connection properties configured.
 * A template of importexporttool.config is on classpath and the filtered copy is generated to the working directory
 * to be consumed at a later point.
 */
public class GenerateImportExportFileActionImpl implements DataPopulationAction {

	private static final Logger LOG = Logger.getLogger(GenerateImportExportFileActionImpl.class);

	@Autowired
	private FilterService filterService;

	@Autowired
	@Qualifier("filteredPropertiesForImportExport")
	private Properties filterProperties;

	private File output;

	@Autowired
	private ClasspathResourceResolverUtil classpathResolver;

	@Override
	public void execute(final DataPopulationContext context) {
		if (getOutput() == null) {
			throw new DataPopulationActionException("Error: No file configured for where the filtered Import/Export "
					+ "configuration should be written.");
		}

		// Delete output file first just to make sure we generate a new one
		if (getOutput().exists() && !getOutput().delete()) {
			throw new DataPopulationActionException("Error: Unable to delete output file before generating it: " + getOutput().getAbsolutePath());
		}

		final InputStream input = getUnfilteredImportExportConfig();

		if (filterProperties.isEmpty()) {
			LOG.warn("No filter properties specified to filter the Import/Export configuration file, so will copy it as is to the destination: "
					+ getOutput());
		}

		try {
			filterService.filter(input, getOutput(), filterProperties);
		} catch (final IOException e) {
			throw new DataPopulationActionException("Unable to filter Import/Export file. Input: " + input + "; output: " + getOutput()
					+ ". " + DpUtils.getNestedExceptionMessage(e), e);
		}

		LOG.info("Import/Export configuration file generated at: " + getOutput());

		//This property must be set for ImportExportChange to locate the correct filepath.
		//This configLocation property is read by the importexportservice.xml when starting the application context.
		System.setProperty("configLocation", "file:///" + getOutput().getAbsolutePath());
	}

	/**
	 * Returns the unfiltered configuration file consumed by the import export generator.
	 * The file resides on classpath.
	 *
	 * @return configFile the config file as a resource
	 */
	private InputStream getUnfilteredImportExportConfig() {
		InputStream configFile = classpathResolver.getFileResourceStream("importexporttool.config");
		if (configFile == null) {
			throw new DataPopulationActionException("Error: No unfiltered Import/Export configuration file configured.");
		}
		return configFile;
	}

	public File getOutput() {
		return output;
	}

	public void setOutput(final File output) {
		this.output = output;
	}
}
