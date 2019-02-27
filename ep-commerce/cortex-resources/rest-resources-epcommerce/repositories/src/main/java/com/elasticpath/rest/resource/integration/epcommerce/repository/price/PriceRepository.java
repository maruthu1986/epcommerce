/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.price;

import java.util.Set;

import io.reactivex.Single;

import com.elasticpath.domain.catalog.Price;

/**
 * Repository for working with the Price domain.
 */
public interface PriceRepository {

	/**
	 * Returns the price for the sku in the given store.
	 *
	 * @param storeCode the store code
	 * @param skuCode   the product sku code
	 * @return the price
	 */
	Single<Price> getPrice(String storeCode, String skuCode);

	/**
	 * Returns the lowest price of all skus of the given product in the given store.
	 *
	 * @param skuCode the item ID
	 * @return the lowest price
	 */
	Single<Price> getLowestPrice(String skuCode);

	/**
	 * Returns the lowest price of all skus of the given product in the given store. This method will modify the passed in ruleTracker and add
	 * all rules that contributed to the price calculation.
	 *
	 * @param storeCode the store code
	 * @param itemId    the item id
	 * @return the promotion rules
	 */
	Single<Set<Long>> getLowestPriceRules(String storeCode, String itemId);

	/**
	 * Checks if a price exists for the sku in the given store.
	 *
	 * @param storeCode the store code
	 * @param skuCode   the item id
	 * @return true if a price exists for the sku in the store, false otherwise
	 */
	Single<Boolean> priceExists(String storeCode, String skuCode);
}
