/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.service.tax.impl;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.service.tax.TaxCodeRetriever;

/**
 * Default implementation of {@link TaxCodeRetriever}.
 */
public class TaxCodeRetrieverImpl implements TaxCodeRetriever {

	@Override
	public TaxCode getEffectiveTaxCode(final ProductSku productSku) {
		TaxCode skuTaxCode = productSku.getTaxCodeOverride();
		if (skuTaxCode == null) {
			return fallBackToProduct(productSku);
		} else {
			return skuTaxCode;
		}
	}

	@Override
	public TaxCode getEffectiveTaxCode(final Product product) {
		TaxCode taxCode = product.getTaxCodeOverride();
		if (taxCode == null) {
			return fallBackToProductType(product);
		} else {
			return taxCode;
		}
	}

	private TaxCode fallBackToProduct(final ProductSku productSku) {
		Product product = productSku.getProduct();
		TaxCode productTaxCode = product.getTaxCodeOverride();
		if (productTaxCode == null) {
			return fallBackToProductType(product);
		} else {
			return productTaxCode;
		}
	}

	private TaxCode fallBackToProductType(final Product product) {
		return product.getProductType().getTaxCode();
	}

}
