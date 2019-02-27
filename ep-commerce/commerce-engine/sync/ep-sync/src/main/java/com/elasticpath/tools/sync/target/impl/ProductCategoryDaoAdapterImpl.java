/*
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.tools.sync.target.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductCategory;
import com.elasticpath.service.catalog.ProductCategoryService;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;
import com.elasticpath.tools.sync.exception.SyncToolRuntimeException;
import com.elasticpath.tools.sync.merge.configuration.impl.ProductCategoryLocator;
import com.elasticpath.tools.sync.target.AssociatedDaoAdapter;

/**
 * ProductCategory dao adapter.
 */
// ---- DOCProductCategoryDaoApadterImpl
public class ProductCategoryDaoAdapterImpl extends AbstractDaoAdapter<ProductCategory> implements AssociatedDaoAdapter<ProductCategory> {

	private static final Logger LOG = Logger.getLogger(ProductCategoryDaoAdapterImpl.class);

	private ProductCategoryService productCategoryService;

	@Override
	public ProductCategory update(final ProductCategory mergedPersistence) throws SyncToolRuntimeException {
		return productCategoryService.saveOrUpdate(mergedPersistence);
	}

	@Override
	public void add(final ProductCategory newPersistence) throws SyncToolRuntimeException {
		productCategoryService.saveOrUpdate(newPersistence);
	}

	@Override
	public boolean remove(final String guid) throws SyncToolRuntimeException {
		final ProductCategory productCategory = get(guid);
		if (productCategory == null) {
			LOG.warn("Attempt to remove unknown ProductCategory with guid: " + guid);
			return false;
		}
		productCategoryService.remove(productCategory);
		return true;
	}

	@Override
	public ProductCategory get(final String guid) {
		try {
			return (ProductCategory) getEntityLocator().locatePersistence(guid, ProductCategory.class);
		} catch (SyncToolConfigurationException e) {
			throw new SyncToolRuntimeException("Unable to locate persistence", e);
		}
	}

	@Override
	public ProductCategory createBean(final ProductCategory bean) {
		if (bean == null) {
			throw new EpServiceException("Cannot create ProductCategory bean without a source model.");
		}

		try {
			return bean.getClass().newInstance();
		} catch (Exception ex) {
			throw new EpServiceException("Failed to create ProductCategory bean", ex);
		}
	}

	@Override
	public List<String> getAssociatedGuids(final Class<?> clazz, final String categoryGuid) {
		if (Category.class.isAssignableFrom(clazz)) {

			Collection<ProductCategory> productCategories = productCategoryService.findByCategoryGuid(categoryGuid);

			List<String> guids = new ArrayList<>();
			for (ProductCategory productCategory : productCategories) {
				Product product = productCategory.getProduct();
				String productCode = product.getCode();
				String productCategoryGuid = categoryGuid + ProductCategoryLocator.GUID_SEPARATOR + productCode;
				guids.add(productCategoryGuid);
			}
			return guids;
		}
		return Collections.emptyList();
	}

	@Override
	public Class<?> getType() {
		return ProductCategory.class;
	}

	/**
	 * @param productCategoryService The product category service
	 */
	public void setProductCategoryService(final ProductCategoryService productCategoryService) {
		this.productCategoryService = productCategoryService;
	}
}

// ---- DOCProductCategoryDaoApadterImpl