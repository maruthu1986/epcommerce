/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.domain.catalog;

import java.io.Serializable;


/**
 * Methods to describe the characteristics of a product.
 */
public interface ProductCharacteristics extends Serializable {

	/**
	 * Checks whether the product requires selection choice(s) from the user (i.e. if the bundle is a dynamic bundle or has multiple skus).
	 *
	 * @return true if the product requires further selection action from the user
	 */
	boolean offerRequiresSelection();
	
	/**
	 * @return <code>true</code> iff the product is a bundle
	 */
	boolean isBundle();
	
	/**
	 * @return <code>true</code> iff the product is a calculated bundle
	 */
	boolean isCalculatedBundle();
	
	/**
	 * @return <code>true</code> iff the product is a dynamic bundle
	 */
	boolean isDynamicBundle();
	
	/**
	 * Gets the uid for the bundle.
	 *
	 * @return the bundle uid, or null if not a bundle.
	 */
	Long getBundleUid();

	/**
	 * @return <code>true</code> iff the product has multiple SKU options.
	 */
	boolean hasMultipleSkus();
	
}
