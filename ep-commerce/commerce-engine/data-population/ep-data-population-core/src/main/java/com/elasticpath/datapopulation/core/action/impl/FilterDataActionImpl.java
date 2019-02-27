/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.core.action.impl;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.elasticpath.datapopulation.core.action.DataPopulationAction;
import com.elasticpath.datapopulation.core.context.DataPopulationContext;
import com.elasticpath.datapopulation.core.context.configurer.FilterActionConfiguration;
import com.elasticpath.datapopulation.core.exceptions.DataPopulationActionException;
import com.elasticpath.datapopulation.core.exceptions.FilterDataActionException;
import com.elasticpath.datapopulation.core.service.FilterService;
import com.elasticpath.datapopulation.core.utils.DpUtils;
import com.elasticpath.datapopulation.core.utils.DynamicDataPropertiesGenerator;
import com.elasticpath.datapopulation.importexport.ImportExportChange;

/**
 * This action calls FilterService to filter all datafiles from the input directory and outputs the
 * results to the output directory defined in the context. If no output is defined in the context, it fall backs to
 * the temporary output path in the working directory.
 */
public class FilterDataActionImpl implements DataPopulationAction {
	private static final Logger LOG = Logger.getLogger(FilterDataActionImpl.class);

	@Autowired
	private FilterService filterService;

	@Autowired
	@Qualifier("filterPropertiesForFilteringData")
	private Properties filterProperties;

	@Autowired
	@Qualifier("tempOutputDirectory")
	private File tempOutputDirectory;

	@Autowired
	@Qualifier("dataInputDirectory")
	private File inputDirectory;

	@Autowired
	private DynamicDataPropertiesGenerator dynamicPropertiesGenerator;

	@Override
	public void execute(final DataPopulationContext context) {
		File outputDirectory = null;
		final Object wrapper = context.getActionConfiguration();
		if (wrapper instanceof FilterActionConfiguration) {
			FilterActionConfiguration filterOutputWrapper = (FilterActionConfiguration) wrapper;
			outputDirectory = filterOutputWrapper.getFilterOutputDirectory();
		}

		if (outputDirectory == null) { // Fallback to the temporary folder
			outputDirectory = tempOutputDirectory;
		}

		// Set the Import/Export system properties that are required since we can't pass them directly
		// due to the CustomChange interface Liquibase designed
		setImportExportSystemProperties(outputDirectory);

		try {
			filterDataDirectory(outputDirectory);
		} catch (final IOException ex) {
			throw new DataPopulationActionException("Error: Unable to filter the data directory to '"
					+ outputDirectory.getAbsolutePath() + "'. " + DpUtils.getNestedExceptionMessage(ex), ex);
		}
	}

	/**
	 * Sets the System Properties required by the {@link ImportExportChange} class.
	 * This involves setting the data directory system property so it can resolve it.
	 */
	private void setImportExportSystemProperties(final File outputDirectory) {
		// We need to pass the data directory to the ImportExportChange class and we can't do that directly
		// since Liquibase's CustomChange interface is not helpful. Therefore we have to use a System property
		System.setProperty(ImportExportChange.DATA_DIRECTORY_PROPERTY, outputDirectory.getAbsolutePath());
	}

	/**
	 * Filters the configured input directory to the given output directory.
	 * This is so that the filtered data can be processed separately at a later date.
	 *
	 * @param outputDirectory the directory to output the filtered data to.
	 * @throws IOException               if there is a problem reading the properties file
	 * @throws FilterDataActionException if there is a problem with the configured values or filtering the data files.
	 */
	public void filterDataDirectory(final File outputDirectory) throws IOException {
		if (inputDirectory == null) {
			throw new FilterDataActionException("Error: No data directory configured to be filtered, so unable to filter it");
		}
		if (filterProperties == null) {
			throw new FilterDataActionException("Error: No filter properties configured, so unable to filter the data directory");
		}
		Properties dynamicProperties = null;

		// Use the configured dynamic properties generator, if there is one, to generate the properties that vary at runtime
		// (timestamps etc)
		if (dynamicPropertiesGenerator != null) {
			dynamicProperties = dynamicPropertiesGenerator.generateDynamicDataProperties();
		}

		// Filter the data with the assembled information
		LOG.info("Filtering input directory '" + inputDirectory + "' to output directory '" + outputDirectory + "'. Filter Properties size: "
				+ DpUtils.size(filterProperties) + "; Dynamic Properties size: " + DpUtils.size(dynamicProperties));
		try {
			filterService.filterDirectory(inputDirectory, outputDirectory, filterProperties, dynamicProperties);
		} catch (final IOException e) {
			throw new FilterDataActionException("Error: A problem occurred filtering the data directory '" + inputDirectory.getAbsolutePath()
					+ "'. " + DpUtils.getNestedExceptionMessage(e), e);
		}
	}
}
