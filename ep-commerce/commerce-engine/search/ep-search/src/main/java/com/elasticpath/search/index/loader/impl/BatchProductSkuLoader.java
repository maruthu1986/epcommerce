/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.search.index.loader.impl;

import java.util.Collection;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.service.catalog.ProductSkuLookup;

/**
 * Fetches a batch of {@link ProductSku}s.
 */
public class BatchProductSkuLoader extends AbstractEntityLoader<ProductSku> {

	private ProductSkuLookup skuLookup;

	/**
	 * Loads the {@link ProductSku}s for the batched ids and loads each batch in bulk.
	 * 
	 * @return the loaded {@link ProductSku}s
	 */
	@Override
	public Collection<ProductSku> loadBatch() {
		return getSkuLookup().findByUids(getUidsToLoad());
	}

	public void setSkuLookup(final ProductSkuLookup skuLookup) {
		this.skuLookup = skuLookup;
	}

	protected ProductSkuLookup getSkuLookup() {
		return skuLookup;
	}
}
