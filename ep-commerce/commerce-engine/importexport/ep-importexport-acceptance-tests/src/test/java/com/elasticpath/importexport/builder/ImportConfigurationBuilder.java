/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.importexport.builder;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.elasticpath.importexport.common.configuration.PackagerConfiguration;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.common.types.PackageType;
import com.elasticpath.importexport.common.types.TransportType;
import com.elasticpath.importexport.importer.configuration.ImportConfiguration;
import com.elasticpath.importexport.importer.configuration.ImporterConfiguration;
import com.elasticpath.importexport.importer.configuration.RetrievalConfiguration;
import com.elasticpath.importexport.importer.types.ImportStrategyType;

/**
 * Builder for {@link com.elasticpath.importexport.importer.configuration.ImportConfiguration}.
 */
public class ImportConfigurationBuilder {

	private TransportType retrievalMethod = TransportType.FILE;
	private String retrievalSource;
	private PackageType packageType = PackageType.NONE;
	private final Map<JobType, ImportStrategyType> importStrategyMap = new HashMap<>();

	/**
	 * Creates a new instance of the builder.
	 *
	 * @return the import configuration builder
	 */
	public static ImportConfigurationBuilder newInstance() {
		return new ImportConfigurationBuilder();
	}

	/**
	 * Builds the import configuration.
	 *
	 * @return the import configuration
	 */
	public ImportConfiguration build() {
		final ImportConfiguration importConfiguration = new ImportConfiguration();

		final Map<JobType, ImporterConfiguration> importerConfigurationMap = new HashMap<>();
		for (Entry<JobType, ImportStrategyType> importStrategy : importStrategyMap.entrySet()) {
			final JobType jobType = importStrategy.getKey();
			final ImportStrategyType strategyType = importStrategy.getValue();

			final ImporterConfiguration importerConfiguration = new ImporterConfiguration();
			importerConfiguration.setJobType(jobType);
			importerConfiguration.setImportStrategyType(strategyType);

			importerConfigurationMap.put(jobType, importerConfiguration);
		}

		final RetrievalConfiguration retrievalConfiguration = new RetrievalConfiguration();
		retrievalConfiguration.setMethod(retrievalMethod);
		retrievalConfiguration.setSource(retrievalSource);

		final PackagerConfiguration packagerConfiguration = new PackagerConfiguration();
		packagerConfiguration.setType(packageType);

		importConfiguration.setImporterConfigurationMap(importerConfigurationMap);
		importConfiguration.setRetrievalConfiguration(retrievalConfiguration);
		importConfiguration.setPackagerConfiguration(packagerConfiguration);
		importConfiguration.setXmlValidation(true);

		return importConfiguration;
	}

	/**
	 * Sets the retrieval method.
	 *
	 * @param retrievalMethod the retrieval method
	 * @return the import configuration builder
	 */
	public ImportConfigurationBuilder setRetrievalMethod(final TransportType retrievalMethod) {
		this.retrievalMethod = retrievalMethod;
		return this;
	}

	/**
	 * Sets the retrieval source.
	 *
	 * @param retrievalSource the retrieval source
	 * @return the import configuration builder
	 */
	public ImportConfigurationBuilder setRetrievalSource(final String retrievalSource) {
		this.retrievalSource = retrievalSource;
		return this;
	}
	
	/**
	 * Sets the package type.
	 *
	 * @param packageType the package type
	 * @return the import configuration builder
	 */
	public ImportConfigurationBuilder setPackageType(final PackageType packageType) {
		this.packageType = packageType;
		return this;
	}

	/**
	 * Adds an importer configuration.
	 *
	 * @param jobType the job type
	 * @param importerStrategyType the importer strategy type
	 * @return the import configuration builder
	 */
	public ImportConfigurationBuilder addImporterConfiguration(
			final JobType jobType, final ImportStrategyType importerStrategyType) {
		importStrategyMap.put(jobType, importerStrategyType);
		return this;
	}
}
