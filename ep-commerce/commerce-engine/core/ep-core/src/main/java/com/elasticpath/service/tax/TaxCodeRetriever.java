/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.service.tax;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.tax.TaxCode;

/**
 * Service for obtaining the appropriate a {@link TaxCode} to use with a particular {@Product} or {@link ProductSku}.
 */
public interface TaxCodeRetriever {

	/**
	 * Determines the effective tax code for the given product sku.
	 * 
	 * @param productSku sku for which to determine the tax code
	 * @return a tax code
	 */
	TaxCode getEffectiveTaxCode(ProductSku productSku);

	/**
	 * Determines the effective tax code for the given product.
	 *
	 * @param product the product for which to determine the tax code
	 * @return a tax code
	 */
	TaxCode getEffectiveTaxCode(Product product);

}
