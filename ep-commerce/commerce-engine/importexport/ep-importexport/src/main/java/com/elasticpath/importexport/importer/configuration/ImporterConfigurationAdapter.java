/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.importexport.importer.configuration;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elasticpath.importexport.common.types.JobType;

/**
 * The xml adapter for data transformation between <code>ImportStrategyConfiguration</code> and
 * <code>Map&lt;JobType, ImporterConfiguration></code>.
 */
public class ImporterConfigurationAdapter extends XmlAdapter<ImportStrategyConfiguration, Map<JobType, ImporterConfiguration>> {

	@Override
	public ImportStrategyConfiguration marshal(final Map<JobType, ImporterConfiguration> configurationMap) throws Exception {
		ImportStrategyConfiguration importStrategyConfiguration = new ImportStrategyConfiguration();
		importStrategyConfiguration.setImporterConfigurationList(new ArrayList<>(configurationMap.values()));
		return importStrategyConfiguration;
	}

	@Override
	public Map<JobType, ImporterConfiguration> unmarshal(final ImportStrategyConfiguration importStrategyConfiguration) throws Exception {
		Map<JobType, ImporterConfiguration> configurationMap = new EnumMap<>(JobType.class);
		for (ImporterConfiguration configuration : importStrategyConfiguration.getImporterConfigurationList()) {
			configurationMap.put(configuration.getJobType(), configuration);
		}
		return configurationMap;
	}

}
