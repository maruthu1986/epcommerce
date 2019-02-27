/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.tools.sync.merge.configuration.impl;

import org.apache.log4j.Logger;

import com.elasticpath.domain.catalog.ProductCategory;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.catalog.ProductCategoryService;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;

/**
 * Locator for <code>ProductCategory</code> objects.
 */
public class ProductCategoryLocator extends AbstractEntityLocator {
	
	private static final Logger LOG = Logger.getLogger(ProductCategoryLocator.class);
	
	private ProductCategoryService productCategoryService;

	/**
	 * Delimiter for decoding the product/category guid.
	 */
	public static final String GUID_SEPARATOR = "|";
	
	@Override
	public Persistable locatePersistence(final String guid, final Class<?> clazz) throws SyncToolConfigurationException {
		
		final String categoryGuid = guid.substring(0, guid.lastIndexOf(GUID_SEPARATOR));
		final String productCode = guid.substring(guid.lastIndexOf(GUID_SEPARATOR) + GUID_SEPARATOR.length());

		if (productCode.contains(GUID_SEPARATOR)) {
			LOG.warn("One or more identifiers contained the delimiter: " + guid);
		}

		return productCategoryService.findByCategoryAndProduct(categoryGuid, productCode);
	}

	@Override
	public boolean isResponsibleFor(final Class<?> clazz) {
		return ProductCategory.class.isAssignableFrom(clazz);
	}

	/**
	 * Gets the ProductCategoryService.
	 * 
	 * @return the ProductCategoryService.
	 */
	public ProductCategoryService getProductCategoryService() {
		return productCategoryService;
	}

	/**
	 * Sets the ProductCategoryService.
	 * 
	 * @param productCategoryService The ProductCategoryService.
	 */
	public void setProductCategoryService(final ProductCategoryService productCategoryService) {
		this.productCategoryService = productCategoryService;
	}
}