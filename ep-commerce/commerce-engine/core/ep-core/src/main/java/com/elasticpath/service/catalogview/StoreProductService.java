/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.service.catalogview;

import java.util.Collection;
import java.util.List;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.store.Store;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;

/**
 * A Storefront use-case based product service.  Returns read-mostly
 * StoreProducts more suitable for use in Storefronts than the core
 * domain Product object.
 */
public interface StoreProductService {

	/**
	 * Gets a store product for the product with the given UID.
	 *
	 * @param uidPk the product UID
	 * @param store the store the product belongs to
	 * @param loadProductAssociations true if product associations should be loaded for each product
	 * @return the StoreProduct instance
	 */
	StoreProduct getProductForStore(long uidPk, Store store, boolean loadProductAssociations);

	/**
	 * Get a list of store products for a specified store.
	 *
	 * @param uidPks the uids of the products.
	 * @param store the store the products belong to.
	 * @param loadProductAssociations true if product associations should be loaded for each product
	 * @return the store product
	 */
	List<StoreProduct> getProductsForStore(List<Long> uidPks, Store store, boolean loadProductAssociations);

	/**
	 * Get a store product for the specified product.
	 *
	 * @param product the product to get the store product for.
	 * @param store the store the product belongs to.
	 * @return the store product
	 */
	StoreProduct getProductForStore(Product product, Store store);

	/**
	 * Retrieves a list of {@link Product}s from a list of {@link Product} uids and returns them as {@link IndexProduct}s.
	 * @param productUids the list of product uids to load
	 * @param stores the list of stores
	 * @param fetchGroupLoadTuner the {@link FetchGroupLoadTuner}
	 * @return a list of {@link IndexProduct}s
	 */
	Collection<IndexProduct> getIndexProducts(Collection<Long> productUids, Collection<Store> stores,
			FetchGroupLoadTuner fetchGroupLoadTuner);

	/**
	 * Gets the index product.
	 *
	 * @param uidPk the product UID
	 * @param loadTuner the load tuner
	 * @param stores a collection of stores for which the product is being indexed
	 * @return the IndexProduct instance
	 */
	IndexProduct getIndexProduct(long uidPk, FetchGroupLoadTuner loadTuner, Collection<Store> stores);


	/**
	 * Create an index product from the given product.
	 *
	 * @param product the product to wrap as an index product
	 * @param stores a collection of store for which the product is going to be indexed
	 * @return an {@link IndexProduct}
	 */
	IndexProduct createIndexProduct(Product product, Collection<Store> stores);

}
