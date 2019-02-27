/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.catalog;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.pagination.DirectedSortingField;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.PreOrBackOrderDetails;
import com.elasticpath.persistence.api.LoadTuner;

/**
 * Provides productSku-related business services.
 */
public interface ProductSkuService {

	/**
	 * Deletes the product sku, all of its associations, and all of its {@code BaseAmount}s.
	 *
	 * @param productSkuUid the uid of product sku to remove
	 * @throws EpServiceException in case of any errors
	 */
	void removeProductSkuTree(long productSkuUid) throws EpServiceException;

	/**
	 * Retrieve the list of productSkus, whose specified property contain the given criteria value.
	 *
	 * @param propertyName productSku property to search on.
	 * @param criteriaValue criteria value to be used for searching.
	 * @return list of productSkus matching the given criteria.
	 * @throws EpServiceException in case of any error
	 */
	List<ProductSku> findProductSkuLike(String propertyName, String criteriaValue) throws EpServiceException;

	/**
	 * Retrieve the list of productSkus, whose name matches the given criteria and belongs to direct or indirect subcategory of the specified parent
	 * category.
	 *
	 * @param criteriaValue criteria value to be used for searching.
	 * @param parentCategoryUid Parent Category used to restricted the search results.
	 * @return list of productSkus matching the given criteria.
	 * @throws EpServiceException in case of any error
	 */
	List<ProductSku> findProductSkuCodeLikeWithRestriction(String criteriaValue, long parentCategoryUid) throws EpServiceException;

	/**
	 * Returns a list of <code>ProductSku</code> based on the given product Uid.
	 *
	 * @param productUid the product Uid
	 * @return a list of <code>ProductSku</code>
	 */
	List<ProductSku> findByProductUid(long productUid);

	/**
	 * Save or update the given product sku.
	 *
	 * @param sku the product sku to save or update
	 * @return the updated object instance
	 * @throws EpServiceException - in case of any errors
	 */
	ProductSku saveOrUpdate(ProductSku sku) throws EpServiceException;

	/**
	 * Adds the given product sku.
	 *
	 * @param productSku the product sku to add
	 * @return the persisted instance of product sku
	 * @throws EpServiceException - in case of any errors
	 */
	ProductSku add(ProductSku productSku) throws EpServiceException;

	/**
	 * Checks to see if any of the given SKU codes exist. Will exclude those SKUs for the given
	 * product UID.
	 *
	 * @param skuCodes a {@link Collection} of SKU codes
	 * @param productUid the UID of the product to exclude
	 * @return the list of SKU codes that exist
	 * @throws EpServiceException in case of any errors
	 */
	List<String> skuExists(Collection<String> skuCodes, long productUid) throws EpServiceException;

	/**
	 * Checks whether the product SKU can be deleted or not.
	 * @param productSku the SKU
	 * @return true if it can be deleted
	 */
	boolean canDelete(ProductSku productSku);


	/**
	 * Checks is a given ProductSku is used within any bundles.
	 * @param productSku the ProductSku
	 * @return true if productSku is used by any bundle.
	 */
	boolean isInBundle(ProductSku productSku);

	/**
	 * Finds a collection of ProductBundles that contain the specified ProductSku.
	 * @param productSku the ProductSku.
	 * @return the ProductBundles that contain the ProductSku.
	 */
	Collection<ProductBundle> findProductBundlesContaining(ProductSku productSku);

	/**
	 * Finds SKUs by their product relationship.
	 *
	 * @param productCode the product code
	 * @param startIndex the starting index for the result set
	 * @param maxResults the max returned results
	 * @param sortingFields the fields to order by
	 * @param loadTuner the load tuner
	 * @return a list of product SKUs
	 */
	List<ProductSku> findSkusByProductCode(String productCode, int startIndex, int maxResults,
			DirectedSortingField [] sortingFields, LoadTuner loadTuner);

	/**
	 * Gets the total number of SKUs for a product.
	 *
	 * @param productCode the product code
	 * @return the number of SKUs belonging to the product with the given product code
	 */
	long getProductSkuCount(String productCode);

	/**
	 * Get the sku UID of the given sku identifier. The given sku identifier will first be dealt as a guid to try to find a sku UID.
	 *
	 * @param skuCode the SKU Code or Guid.
	 * @return the sku UID, otherwise 0
	 * @throws EpServiceException - in case of any errors
	 */
	long findUidBySkuCode(String skuCode) throws EpServiceException;

	/**
	 * Find the UIDs of skus whose parent product was modified since the given date.
	 *
	 * @param lastModifiedDate the date to check
	 * @return a list of uids
	 */
	List<Long> findUidsByProductLastModifiedDate(Date lastModifiedDate);

	/**
	 * Find all sku UIDs. <b>NOTE:</b> this may return a very large number of results.
	 *
	 * @return all sku uids.
	 */
	List<Long> findAllUids();

	/**
	 * Retrieves list of sku uids where the deleted date is later than the specified date.
	 *
	 * @param date date to compare with the deleted date
	 * @return list of sku uids whose deleted date is later than the specified date
	 */
	List<Long> findSkuUidsByDeletedDate(Date date);

	/**
	 * Retrieves the PreOrBackOrder details for the given product SKU.
	 *
	 * @param skuCode the code of the product SKU
	 * @return the {@link PreOrBackOrderDetails}
	 */
	PreOrBackOrderDetails getPreOrBackOrderDetails(String skuCode);

	/**
	 * Find {@link com.elasticpath.domain.catalog.ProductSku} for given SKU code when product is not always available.
	 *
	 * @param skuCode SKU code
	 * @return if found, a lightweight ProductSku, with sku Code and product availability criteria, will be returned, otherwise, null
	 */
	ProductSku findNotAlwaysAvailableProductSku(String skuCode);

}
