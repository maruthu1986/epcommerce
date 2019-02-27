/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.catalog.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.util.CategoryGuidUtil;
import com.elasticpath.domain.catalog.ProductCategory;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.service.catalog.ProductCategoryService;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;

/**
 * Service for working with {@link ProductCategory}s.
 */
public class ProductCategoryServiceImpl extends AbstractEpPersistenceServiceImpl implements ProductCategoryService {

	private CategoryService categoryService;
	private CategoryGuidUtil categoryGuidUtil;

	@Override
	public Collection<ProductCategory> findByCategoryUid(final long categoryUid) {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("FIND_BY_CATEGORY_UID", categoryUid);
	}

	@Override
	public Object getObject(final long uid) throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("FIND_BY_PRODUCT_CATEGORY_UID", uid);
	}

	@Override
	public ProductCategory saveOrUpdate(final ProductCategory productCategory) throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().saveOrUpdate(productCategory);
	}

	@Override
	public void remove(final ProductCategory productCategory) throws EpServiceException {
		sanityCheck();
		getPersistenceEngine().delete(productCategory);
	}

	@Override
	public List<ProductCategory> findByCategoryGuid(final String categoryGuid) {
		return getPersistenceEngine().retrieveByNamedQuery(
				"FIND_PRODUCT_CATEGORIES_BY_CATEGORY_GUID", categoryGuid);
	}

	@Override
	public Collection<ProductCategory> findByCategoryAndCatalog(final String catalogCode, final String categoryCode) {
		sanityCheck();
		if (catalogCode == null) {
			throw new EpServiceException("Missing catalog code argument.");
		}
		if (categoryCode == null) {
			throw new EpServiceException("Missing category code argument.");
		}

		Long categoryUid = getCategoryService().findUidByCompoundGuid(getCategoryGuidUtil().get(categoryCode, catalogCode));
		if (categoryUid == null) {
			return Collections.emptyList();
		}
		return findByCategoryUid(categoryUid);
	}

	@Override
	public ProductCategory findByCategoryAndProduct(final String categoryGuid, final String productCode) {
		sanityCheck();
		if (categoryGuid == null) {
			throw new EpServiceException("Missing category guid argument.");
		}
		if (productCode == null) {
			throw new EpServiceException("Missing product code argument.");
		}

		Collection<ProductCategory> result = getPersistenceEngine().retrieveByNamedQuery("FIND_BY_CATEGORY_GUID_AND_PRODUCT_CODE",
				categoryGuid,
				productCode);

		if (result.size() == 1) {
			return result.iterator().next();
		} else if (result.size() > 1) {
			throw new EpServiceException("Inconsistent data. Found [" + result.size() + "] product categories matching category guid: ["
					+ categoryGuid + "], and product code: [" + productCode + "].");
		}
		return null;
	}

	/**
	 * Get the category service.
	 * 
	 * @return The category service.
	 */
	public CategoryService getCategoryService() {
		return categoryService;
	}

	/**
	 * Set the category service.
	 * 
	 * @param categoryService The category service.
	 */
	public void setCategoryService(final CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	protected CategoryGuidUtil getCategoryGuidUtil() {
		return categoryGuidUtil;
	}

	public void setCategoryGuidUtil(final CategoryGuidUtil categoryGuidUtil) {
		this.categoryGuidUtil = categoryGuidUtil;
	}
}