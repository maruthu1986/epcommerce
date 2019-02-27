/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.item.recommendations.impl;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.Single;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductAssociation;
import com.elasticpath.domain.catalog.ProductAssociationType;
import com.elasticpath.domain.store.Store;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.recommendations.ItemRecommendationsRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.pagination.PaginatedResult;
import com.elasticpath.service.catalog.ProductAssociationService;
import com.elasticpath.service.search.query.ProductAssociationSearchCriteria;

/**
 * Repository for gathering sourceProduct associations.
 */
@Singleton
@Named("itemRecommendationsRepository")
public class ItemRecommendationsRepositoryImpl implements ItemRecommendationsRepository {

	/**
	 * Error that indicated the failure when searching for recommendation item ids.
	 */
	static final String ITEM_ID_SEARCH_FAILURE = "Server error when searching for item ids";
	private static final Logger LOG = LoggerFactory.getLogger(ItemRecommendationsRepositoryImpl.class);
	private final ProductAssociationService productAssociationService;
	private final ItemRepository itemRepository;
	private final RecommendedItemsPageSizeResolver pageSizeResolver;

	/**
	 * Creates instance with needed services wired in.
	 *
	 * @param productAssociationService the product association service
	 * @param itemRepository the item repository
	 * @param pageSizeResolver the resolver for page size
	 */
	@Inject
	public ItemRecommendationsRepositoryImpl(
			@Named("productAssociationService")
			final ProductAssociationService productAssociationService,
			@Named("itemRepository")
			final ItemRepository itemRepository,
			@Named("recommendedItemsPageSizeResolver")
			final RecommendedItemsPageSizeResolver pageSizeResolver) {
		this.productAssociationService = productAssociationService;
		this.itemRepository = itemRepository;
		this.pageSizeResolver = pageSizeResolver;
	}

	@Override
	public Single<PaginatedResult> getRecommendedItemsFromGroup(final Store store, final Product sourceProduct,
																final String recommendationGroup, final int pageNumber) {
		Single<PaginatedResult> result;
		try {
			ProductAssociationType productAssociationType = ProductAssociationType.fromName(recommendationGroup);

			ProductAssociationSearchCriteria criteria = new ProductAssociationSearchCriteria();
			criteria.setAssociationType(productAssociationType);
			criteria.setSourceProduct(sourceProduct);
			criteria.setCatalogCode(store.getCatalog().getCode());
			criteria.setWithinCatalogOnly(true);
			Date now = new Date();
			criteria.setStartDateBefore(now);
			criteria.setEndDateAfter(now);

			int pageSize = pageSizeResolver.getPageSize();
			int startIndex = (pageNumber - 1) * pageSize;

			int totalResultCount = productAssociationService.findCountForCriteria(criteria).intValue();
			Single<List<String>> recommendedItemIds = Single.just(Collections.emptyList());
			if (totalResultCount > 0) {
				recommendedItemIds = extractRecommendedItemIds(criteria, pageSize, startIndex);
			}
			result = recommendedItemIds.map(itemIds -> new PaginatedResult(itemIds, pageNumber, pageSize, totalResultCount));
		} catch (Exception exception) {
			LOG.error("Error when searching for item associations", exception);
			result = Single.error(ResourceOperationFailure.serverError(ITEM_ID_SEARCH_FAILURE));
		}
		return result;
	}

	/**
	 * Extract recommendation ids as list.
	 *
	 * @param criteria search criteria
	 * @param pageSize page size
	 * @param startIndex start index
	 * @return single list of ids
	 */
	protected Single<List<String>> extractRecommendedItemIds(final ProductAssociationSearchCriteria criteria,
															 final int pageSize, final int startIndex) {

		return Observable.fromIterable(productAssociationService.findByCriteria(criteria, startIndex, pageSize))
				.map(ProductAssociation::getTargetProduct)
				.flatMapSingle(itemRepository::getDefaultItemIdForProductSingle)
				.toList();
	}
}
