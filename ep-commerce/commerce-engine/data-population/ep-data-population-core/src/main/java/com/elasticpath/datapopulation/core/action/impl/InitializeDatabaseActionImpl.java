/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.core.action.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.elasticpath.datapopulation.core.action.DataPopulationAction;
import com.elasticpath.datapopulation.core.context.DataPopulationContext;
import com.elasticpath.datapopulation.core.context.DatabaseConnectionProperties;
import com.elasticpath.datapopulation.core.context.configurer.SqlActionConfiguration;
import com.elasticpath.datapopulation.core.exceptions.DatabaseInitializerActionException;
import com.elasticpath.datapopulation.core.service.FilterService;
import com.elasticpath.datapopulation.core.utils.ClasspathResourceResolverUtil;
import com.elasticpath.datapopulation.core.utils.DpUtils;
import com.elasticpath.datapopulation.core.utils.SqlInputStream;

/**
 * This action initializes (dropping and creating) the database.
 * First, it finds a compatible sql file according to the database type. The list of candidate sql files are on classpath
 * and can be extended.
 * After finding a match, it generates a filtered copy of that sql file.
 * Lastly, it calls RunSqlActionImpl to execute it.
 */
public class InitializeDatabaseActionImpl implements DataPopulationAction {

	private static final Logger LOG = Logger.getLogger(InitializeDatabaseActionImpl.class);

	@Autowired
	private FilterService filterService;

	@Autowired
	private RunSqlActionImpl sqlAction;

	@Autowired
	@Qualifier("filteredSqlFile")
	private File filteredSqlFile;

	@Autowired
	@Qualifier("filteredPropertiesForDatabaseInitialization")
	private Properties filterProperties;

	@Autowired
	private ClasspathResourceResolverUtil classpathResolver;

	@Override
	public void execute(final DataPopulationContext context) {
		LOG.info("Database initialization: validating the sql files are ready for consumption");

		// Load filtering properties from file
		if (filterProperties == null) {
			throw new DatabaseInitializerActionException("Error: No filter properties were configured for this command. "
					+ "Cannot initialize database.");
		}

		// Filter the sql file and output to filterSqlFile location
		if (filteredSqlFile == null) {
			throw new DatabaseInitializerActionException("Error: No filtered sql file was configured for this command. Cannot initialize database.");
		}
		if (filteredSqlFile.exists() && !filteredSqlFile.delete()) {
			throw new DatabaseInitializerActionException("Error: Cannot delete existing filtered SQL file: " + filteredSqlFile.getAbsolutePath());
		}

		String dbType = filterProperties.getProperty(DatabaseConnectionProperties.DATA_POPULATION_DATABASE_TYPE_KEY);
		if (StringUtils.isEmpty(dbType)) {
			throw new DatabaseInitializerActionException("Error: No database type detected.");
		}

		SqlInputStream unfilteredSqlFile = getUnfilteredSqlFile(dbType);

		try {
			filterFile(unfilteredSqlFile.getStream(), filteredSqlFile, filterProperties);
			unfilteredSqlFile.getStream().close();
		} catch (final IOException e) {
			throw new DatabaseInitializerActionException("Error: Unable to filter SQL file (Input file: " + unfilteredSqlFile
					+ ". Output file: " + filteredSqlFile.getAbsolutePath() + "). " + DpUtils.getNestedExceptionMessage(e), e);
		}

		// Run the filtered sql file
		SqlActionConfiguration actionConfiguration = new SqlActionConfiguration();
		actionConfiguration.setSqlFile(filteredSqlFile);
		actionConfiguration.setReadFullScript(unfilteredSqlFile.isStoredProcedure());
		actionConfiguration.setUsingCreatedConnection(true);
		context.setActionConfiguration(actionConfiguration);

		LOG.info("Initializing the database using SQL file '" + filteredSqlFile);
		sqlAction.execute(context);
	}

	/**
	 * Returns the unfiltered sql file used by the database-initializer.
	 * Employs the naming fall back strategy to resolve the filename.
	 *
	 * @param dbType the database type which serves as the name of the sql file
	 * @return unfilteredSqlFile the unfiltered sql file
	 */
	private SqlInputStream getUnfilteredSqlFile(final String dbType) {
		return classpathResolver.getSqlInputStreamWithFallback(dbType, "database-reset-sql/%s"); //make sql -> *
	}

	/**
	 * Filters the given input {@link File} and writes it to the output {@link File} using the given filter properties.
	 *
	 * @param unfilteredFile   the file to filter.
	 * @param outputFile       the output destination.
	 * @param filterProperties the properties to filter the file with.
	 * @throws IOException if there is a problem filtering the file.
	 */
	protected void filterFile(final InputStream unfilteredFile, final File outputFile, final Properties filterProperties) throws IOException {
		filterService.filter(unfilteredFile, outputFile, filterProperties);
	}
}
