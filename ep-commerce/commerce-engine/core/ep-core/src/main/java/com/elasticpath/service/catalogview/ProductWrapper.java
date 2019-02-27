/**
 * Copyright (c) Elastic Path Software Inc., 2011
 */
package com.elasticpath.service.catalogview;

import com.elasticpath.domain.catalog.Product;

/**
 * Interface for classes that decorate a product.
 */
public interface ProductWrapper {

	/**
	 * Gets the wrapped product.
	 * 
	 * @return the product
	 */
	Product getWrappedProduct();

}