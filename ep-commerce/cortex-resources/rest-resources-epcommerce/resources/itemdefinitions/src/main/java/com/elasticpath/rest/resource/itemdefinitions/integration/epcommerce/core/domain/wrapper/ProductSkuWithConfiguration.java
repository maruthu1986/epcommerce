/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemdefinitions.integration.epcommerce.core.domain.wrapper;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.elasticpath.domain.catalog.ProductSku;

/**
 * Wraps ProductSku to carry a configurationCode in and out of EpDomainTransformer.
 */
public class ProductSkuWithConfiguration {

	private final ProductSku productSku;
	private final String configurationCode;

	/**
	 * Instantiates a new ProductSku With Configuration.
	 *
	 * @param productSku the product sku
	 * @param configurationCode the configuration code
	 */
	public ProductSkuWithConfiguration(final ProductSku productSku, final String configurationCode) {
		this.productSku = productSku;
		this.configurationCode = configurationCode;
	}

	/**
	 * Gets the {@link ProductSku}.
	 *
	 * @return the ProductSku
	 */
	public ProductSku getProductSku() {
		return productSku;
	}

	/**
	 * Gets the configuration code.
	 *
	 * @return the configuration code
	 */
	public String getConfigurationCode() {
		return configurationCode;
	}

	@Override
	public boolean equals(final Object obj) {
		final boolean result;
		if (super.equals(obj)) {
			result = true;
		} else if (obj instanceof ProductSkuWithConfiguration) {
			ProductSkuWithConfiguration pswc = (ProductSkuWithConfiguration) obj;
			EqualsBuilder builder = new EqualsBuilder()
					.append(configurationCode, pswc.configurationCode)
					.append(productSku, pswc.productSku);
			result = builder.build();
		} else {
			result = false;
		}
		return result;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(productSku)
				.append(configurationCode)
				.build();
	}
}
