/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.service.catalog;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;

/**
 * Helper interface for identifying bundles and calculated bundles.
 */
public interface BundleIdentifier {

	/**
	 * Determines if the passed product is a bundle.
	 *
	 * @param product the product to be checked
	 * @return <code>true</code> iff the product is a bundle
	 */
	boolean isBundle(Product product);

	/**
	 * Determines if the passed product is a calculated bundle.
	 *
	 * @param product the product to be checked
	 * @return <code>true</code> iff the product is a calculated bundle
	 */
	boolean isCalculatedBundle(Product product);

	/**
	 * Determines if the passed product sku is a calculated bundle.
	 *
	 * @param productSku the sku to be checked
	 * @return <code>true</code> iff the sku is a calculated bundle
	 */
	boolean isCalculatedBundle(ProductSku productSku);

	/**
	 * Determines if the passed product is an assigned bundle.
	 *
	 * @param product the product to be checked
	 * @return <code>true</code> iff the product is an assigned bundle
	 */
	boolean isAssignedBundle(Product product);

	/**
	 * Determines if the passed product sku is an assigned bundle.
	 *
	 * @param productSku the sku to be checked
	 * @return <code>true</code> iff the sku is an assigned bundle
	 */
	boolean isAssignedBundle(ProductSku productSku);

	/**
	 * Casts the passed product to a ProductBundle, or throws an exception if the product is not a bundle.
	 *
	 * @param product the product to be casted
	 * @return a ProductBundle
	 * @throws ClassCastException if the product is not a bundle
	 */
	ProductBundle asProductBundle(Product product);
}
