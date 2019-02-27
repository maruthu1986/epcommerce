/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.catalog;

import java.util.Collection;
import java.util.List;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.catalog.ProductCategory;
import com.elasticpath.service.EpPersistenceService;

/**
 * Service for working with {@link ProductCategory}s. These are encapsulated inside {@link Product} and not intended for use from application code.
 */
public interface ProductCategoryService extends EpPersistenceService {

	/**
	 * Saves or updates a given <code>ProductCategoryService</code>.
	 *
	 * @param productCategory catalog the <code>ProductCategoryService</code> to save or update.
	 * @return the updated object instance
	 * @throws EpServiceException in case of any errors.
	 */
	ProductCategory saveOrUpdate(ProductCategory productCategory) throws EpServiceException;

	/**
	 * Deletes the productCategory.
	 *
	 * @param productCategory The ProductCategoryService to remove
	 * @throws EpServiceException in case of any errors
	 */
	void remove(ProductCategory productCategory) throws EpServiceException;

	/**
	 * Lookup of {@link ProductCategory} by a category UID.
	 *
	 * @param categoryUid The category UID
	 * @return The ProductCategory for the given category UID.
	 */
	Collection<ProductCategory> findByCategoryUid(long categoryUid);

	/**
	 * Lookup of {@link ProductCategory} by a category guid.
	 *
	 * @param categoryGuid The category guid
	 * @return The ProductCategories for the given category guid.
	 */
	List<ProductCategory> findByCategoryGuid(String categoryGuid);

	/**
	 * Lookup of {@link ProductCategory} by a category code and catalog code.
	 *
	 * @param categoryCode The category code
	 * @param catalogCode The category code
	 * @return The ProductCategory for the given category in the given catalog.
	 */
	Collection<ProductCategory> findByCategoryAndCatalog(String catalogCode, String categoryCode);

	/**
	 * Lookup of {@link ProductCategory} by a category and product.
	 *
	 * @param categoryGuid The category guid
	 * @param productCode The product code
	 * @return The ProductCategory.
	 */
	ProductCategory findByCategoryAndProduct(String categoryGuid, String productCode);
}