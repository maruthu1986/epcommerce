/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.tools.sync.merge.configuration.impl;

import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.catalog.ProductBundleService;
import com.elasticpath.service.catalog.ProductLookup;

/**
 * Product bundle locator class.
 */
public class ProductBundleLocatorImpl extends AbstractEntityLocator {

	private ProductBundleService productBundleService;
	private ProductLookup productLookup;
	private FetchGroupLoadTuner productBundleSortLoadTuner;

	@Override
	public Persistable locatePersistence(final String guid, final Class<?> clazz) {
		return getProductLookup().findByGuid(guid);
	}

	@Override
	public boolean isResponsibleFor(final Class<?> clazz) {
		return ProductBundle.class.isAssignableFrom(clazz);
	}

	@Override
	public boolean entityExists(final String guid, final Class<?> clazz) {
		return productBundleService.guidExists(guid);
	}

	@Override
	public Persistable locatePersistenceForSorting(final String guid, final Class<?> clazz) {
		return productBundleService.findByGuidWithFetchGroupLoadTuner(guid, productBundleSortLoadTuner);
	}

	@Override
	public Persistable locatePersistentReference(final String guid, final Class<?> clazz) {
		return productBundleService.findByGuidWithFetchGroupLoadTuner(guid, getEmptyFetchGroupLoadTuner());
	}

	/**
	 * Set product bundle service.
	 *
	 * @param productBundleService the product bundle service
	 */
	public void setProductBundleService(final ProductBundleService productBundleService) {
		this.productBundleService = productBundleService;
	}

	protected ProductLookup getProductLookup() {
		return productLookup;
	}

	public void setProductLookup(final ProductLookup productLookup) {
		this.productLookup = productLookup;
	}

	/**
	 * Sets the product bundle load tuner.
	 *
	 * @param productBundleSortLoadTuner the new product bundle load tuner
	 */
	public void setProductBundleSortLoadTuner(final FetchGroupLoadTuner productBundleSortLoadTuner) {
		this.productBundleSortLoadTuner = productBundleSortLoadTuner;
	}
}
