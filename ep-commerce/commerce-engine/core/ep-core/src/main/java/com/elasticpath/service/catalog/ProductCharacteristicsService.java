/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.service.catalog;

import java.util.Collection;
import java.util.Map;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductCharacteristics;
import com.elasticpath.domain.catalog.ProductSku;

/**
 * Service methods for getting the characteristics of a product.
 */
public interface ProductCharacteristicsService {

	/**
	 * Gets the product characteristics.
	 *
	 * @param product the product
	 * @return the product characteristics
	 */
	ProductCharacteristics getProductCharacteristics(Product product);
	
	/**
	 * Gets the product characteristics.
	 *
	 * @param productSku the product sku
	 * @return the product characteristics
	 */
	ProductCharacteristics getProductCharacteristics(ProductSku productSku);
	
	/**
	 * Gets the product characteristics for the product whose sku has the given sku code.
	 *
	 * @param skuCode the sku code
	 * @return the product characteristics
	 */
	ProductCharacteristics getProductCharacteristicsForSkuCode(String skuCode);
	
	/**
	 * Gets a map of product code to product characteristics for the given collection of products.
	 *
	 * @param products the products
	 * @return the product characteristics map
	 */
	Map<String, ProductCharacteristics> getProductCharacteristicsMap(Collection<? extends Product> products);

	/**
	 * Checks whether a given product has multiple SKU options.
	 *
	 * @param product the Product to be checked.
	 * @return <code>true</code> iff the Product has multiple SKU options or contains at least one multi-sku constituent.
	 */
	boolean hasMultipleSkus(Product product);

	/**
	 * Checks whether the product requires selection from the user (either a dynamic bundle or multi sku product).
	 *
	 * @param product the product
	 * @return <code>true</code>, iff it requires selection.
	 */
	boolean offerRequiresSelection(Product product);

	/**
	 * Checks if a product is configurable.
	 *
	 * @param product the Product
	 * @return true iff product is configurable or, in the case of bundles, if the bundle contains a configurable constituent
	 */
	boolean isConfigurable(Product product);

}
