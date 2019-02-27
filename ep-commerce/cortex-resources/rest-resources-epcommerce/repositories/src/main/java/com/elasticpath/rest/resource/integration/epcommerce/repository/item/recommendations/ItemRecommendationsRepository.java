/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.item.recommendations;

import io.reactivex.Single;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.store.Store;
import com.elasticpath.rest.resource.integration.epcommerce.repository.pagination.PaginatedResult;

/**
 * Recommendation repository for items.
 */
public interface ItemRecommendationsRepository {

	/**
	 * Get a collection of recommendation groups for an item.
	 *
	 * @param store the store
	 * @param sourceProduct the source product for the recommendations
	 * @param recommendationGroup the name of the recommendation group
	 * @param pageNumber current page number
	 * @return Collection of paginated recommendation groups for an item.
	 */
	Single<PaginatedResult> getRecommendedItemsFromGroup(Store store, Product sourceProduct, String recommendationGroup, int pageNumber);

}
