/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.importexport.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.elasticpath.importexport.common.configuration.ConfigurationOption;
import com.elasticpath.importexport.common.configuration.PackagerConfiguration;
import com.elasticpath.importexport.common.types.PackageType;
import com.elasticpath.importexport.common.types.RequiredJobType;
import com.elasticpath.importexport.common.types.TransportType;
import com.elasticpath.importexport.exporter.configuration.DeliveryConfiguration;
import com.elasticpath.importexport.exporter.configuration.ExportConfiguration;
import com.elasticpath.importexport.exporter.configuration.ExporterConfiguration;

/**
 * Builder for {@link com.elasticpath.importexport.exporter.configuration.ExportConfiguration}.
 */
public class ExportConfigurationBuilder {

	private TransportType deliveryMethod = TransportType.FILE;
	private String deliveryTarget;
	private PackageType packageType = PackageType.NONE;
	private List<RequiredJobType> exporterTypes = Collections.emptyList();
	private final Map<RequiredJobType, Map<String, String>> exporterOptionMap = new HashMap<>();

	/**
	 * Creates a new instance of the builder.
	 *
	 * @return the export configuration builder
	 */
	public static ExportConfigurationBuilder newInstance() {
		return new ExportConfigurationBuilder();
	}

	/**
	 * Builds the export configuration.
	 *
	 * @return the export configuration
	 */
	public ExportConfiguration build() {
		final ExportConfiguration exportConfiguration = new ExportConfiguration();

		for (RequiredJobType exporterType : exporterTypes) {
			final ExporterConfiguration exporterConfiguration = new ExporterConfiguration();
			exporterConfiguration.setType(exporterType);

			final Map<String, String> exporterOptions = exporterOptionMap.get(exporterType);
			if (exporterOptions != null && !exporterOptions.isEmpty()) {
				final List<ConfigurationOption> configurationOptions = new ArrayList<>();
				for (Entry<String, String> exporterOption : exporterOptions.entrySet()) {
					final ConfigurationOption configurationOption = new ConfigurationOption();
					configurationOption.setKey(exporterOption.getKey());
					configurationOption.setValue(exporterOption.getValue());
					configurationOptions.add(configurationOption);
				}
				exporterConfiguration.setOptions(configurationOptions);
			}

			exportConfiguration.getExporterConfigurations().add(exporterConfiguration);
		}

		final DeliveryConfiguration deliveryConfiguration = new DeliveryConfiguration();
		deliveryConfiguration.setMethod(deliveryMethod);
		deliveryConfiguration.setTarget(deliveryTarget);

		final PackagerConfiguration packagerConfiguration = new PackagerConfiguration();
		packagerConfiguration.setType(packageType);

		exportConfiguration.setDeliveryConfiguration(deliveryConfiguration);
		exportConfiguration.setPackagerConfiguration(packagerConfiguration);

		return exportConfiguration;
	}

	/**
	 * Sets the delivery method.
	 *
	 * @param deliveryMethod the delivery method
	 * @return the export configuration builder
	 */
	public ExportConfigurationBuilder setDeliveryMethod(final TransportType deliveryMethod) {
		this.deliveryMethod = deliveryMethod;
		return this;
	}

	/**
	 * Sets the delivery target.
	 *
	 * @param deliveryTarget the delivery target
	 * @return the export configuration builder
	 */
	public ExportConfigurationBuilder setDeliveryTarget(final String deliveryTarget) {
		this.deliveryTarget = deliveryTarget;
		return this;
	}
	
	/**
	 * Sets the package type.
	 *
	 * @param packageType the package type
	 * @return the export configuration builder
	 */
	public ExportConfigurationBuilder setPackageType(final PackageType packageType) {
		this.packageType = packageType;
		return this;
	}

	/**
	 * Sets the exporter types.
	 *
	 * @param exporterTypes the exporter types
	 * @return the export configuration builder
	 */
	public ExportConfigurationBuilder setExporterTypes(final List<RequiredJobType> exporterTypes) {
		this.exporterTypes = exporterTypes;
		return this;
	}

	/**
	 * Adds an exporter option.
	 *
	 * @param exporterType the exporter type
	 * @param optionKey the option key
	 * @param optionValue the option value
	 * @return the export configuration builder
	 */
	public ExportConfigurationBuilder addExporterOption(final RequiredJobType exporterType,
			final String optionKey, final String optionValue) {
		Map<String, String> options = this.exporterOptionMap.get(exporterType);
		if (options == null) {
			options = new HashMap<>();
			this.exporterOptionMap.put(exporterType, options);
		}
		options.put(optionKey, optionValue);
		return this;
	}
}
