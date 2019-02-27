/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.plugin.tax.builder;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.plugin.tax.domain.TaxExemption;
import com.elasticpath.plugin.tax.domain.impl.TaxExemptionImpl;

/**
 * Builder for {@link com.elasticpath.plugin.tax.domain.TaxExemption}.
 */
public class TaxExemptionBuilder {

	/**
	 * The Tax Exemption prefix used to persist data on the Order Data table.
	 */
	public static final String PREFIX = "taxExemption.";

	private final TaxExemption taxExemption;

	/**
	 * Constructor.
	 */
	public TaxExemptionBuilder() {
		taxExemption = new TaxExemptionImpl();
	}

	/**
	 * Gets a new builder.
	 *
	 * @return a new builder.
	 */
	public static TaxExemptionBuilder newBuilder() {
		return new TaxExemptionBuilder();
	}

	/**
	 * Gets the instance built by the builder.
	 *
	 * @return the built instance
	 */
	public TaxExemption build() {
		return taxExemption;
	}

	/**
	 * Sets the tax exemption id.
	 *
	 * @param taxExemptionId the given tax exemption id
	 * @return the builder
	 */
	public TaxExemptionBuilder withTaxExemptionId(final String taxExemptionId) {
		taxExemption.setExemptionId(taxExemptionId);
		return this;
	}

	/**
	 * Helper method to set the data fields from order data. Data which is related TaxExemption will
	 * contain a prefix. The builder will remove the prefix from the key.
	 *
	 * @param dataFields the data fields
	 * @return the builder
	 */
	public TaxExemptionBuilder withDataFields(final Map<String, String> dataFields) {
		for (Map.Entry<String, String> dataEntry : dataFields.entrySet()) {
			if (dataEntry.getKey().startsWith(PREFIX)) {
				// The order data is related to tax exemption
				// Remove the prefix when building a tax exemption entity
				String dataKey = StringUtils.stripStart(dataEntry.getKey(), PREFIX);
				taxExemption.addData(dataKey, dataEntry.getValue());
			}
		}
		return this;
	}
}
