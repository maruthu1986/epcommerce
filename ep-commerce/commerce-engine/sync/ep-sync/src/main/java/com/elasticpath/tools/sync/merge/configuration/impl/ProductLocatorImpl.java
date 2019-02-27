/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.tools.sync.merge.configuration.impl;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.catalog.ProductLookup;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;

/**
 *
 * The product locator class.
 *
 */
public class ProductLocatorImpl extends AbstractEntityLocator {

	private ProductService productService;
	private ProductLookup productLookup;

	/**
	 * @param productService the productService to set
	 */
	public void setProductService(final ProductService productService) {
		this.productService = productService;
	}

	@Override
	public boolean isResponsibleFor(final Class<?> clazz) {
		return Product.class.isAssignableFrom(clazz);
	}

	@Override
	public Persistable locatePersistence(final String guid, final Class<?> clazz)
			throws SyncToolConfigurationException {
		return getProductLookup().findByGuid(guid);
	}

	/**
	 * Determines whether the given persistent object exists by querying the persistence layer.
	 * @param guid the guid of the object to check.
	 * @param clazz the class of the object.
	 * @return true if the object exists, false otherwise.
	 */
	@Override
	public boolean entityExists(final String guid, final Class<?> clazz) {
		return productService.guidExists(guid);
	}

	protected ProductLookup getProductLookup() {
		return productLookup;
	}

	public void setProductLookup(final ProductLookup productLookup) {
		this.productLookup = productLookup;
	}
}
