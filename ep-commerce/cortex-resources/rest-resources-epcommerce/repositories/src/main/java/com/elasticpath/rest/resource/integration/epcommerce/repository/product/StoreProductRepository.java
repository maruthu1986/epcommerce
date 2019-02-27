/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.product;

import java.util.List;

import io.reactivex.Single;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.rest.command.ExecutionResult;


/**
 * Repository for {@link StoreProduct}.
 */
public interface StoreProductRepository {

	/**
	 * Find store product with attributes.
	 *
	 * @param storeCode the store code
	 * @param productGuid the product guid
	 * @return the store product
	 */
	ExecutionResult<StoreProduct> findDisplayableStoreProductWithAttributesByProductGuid(String storeCode, String productGuid);

	/**
	 * Find store product with attributes.
	 *
	 * @param storeCode   the store code
	 * @param productGuid the product guid
	 * @return the store product
	 */
	Single<StoreProduct> findDisplayableStoreProductWithAttributesByProductGuidAsSingle(String storeCode, String productGuid);
	
	/**
	 * Find store product with attributes for given sku guid.
	 *
	 * @param storeCode the store code
	 * @param skuGuid   the sku guid
	 * @return the store product
	 */
	Single<StoreProduct> findDisplayableStoreProductWithAttributesBySkuGuid(String storeCode, String skuGuid);

	/**
	 * Find a product based on its guid.
	 * @param productGuid a product guid
	 * @return a product
	 */
	Product findByGuid(String productGuid);

	/**
	 * Find a list of products by their uids.
	 * @param productUids list of product uids
	 * @return a list of products
	 */
	List<Product> findByUids(List<Long> productUids);
}