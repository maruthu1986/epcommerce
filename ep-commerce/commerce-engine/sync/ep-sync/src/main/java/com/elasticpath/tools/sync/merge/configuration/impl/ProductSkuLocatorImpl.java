/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.tools.sync.merge.configuration.impl;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;

/**
 * 
 * The product sku locator class.
 *
 */
public class ProductSkuLocatorImpl extends AbstractEntityLocator {
	
	private ProductSkuLookup productSkuLookup;

	@Override
	public boolean isResponsibleFor(final Class<?> clazz) {
		return ProductSku.class.isAssignableFrom(clazz);
	}

	@Override
	public Persistable locatePersistence(final String guid, final Class<?> clazz)
			throws SyncToolConfigurationException {
		return getProductSkuLookup().findByGuid(guid);
	}

	protected ProductSkuLookup getProductSkuLookup() {
		return productSkuLookup;
	}

	public void setProductSkuLookup(final ProductSkuLookup productSkuLookup) {
		this.productSkuLookup = productSkuLookup;
	}
}
