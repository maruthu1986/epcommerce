/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.search.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import io.reactivex.Single;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.cache.CacheResult;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.pagination.PaginatedResult;
import com.elasticpath.rest.resource.integration.epcommerce.repository.product.StoreProductRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.search.SearchRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.settings.SettingsRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ReactiveAdapter;
import com.elasticpath.rest.util.math.NumberUtil;
import com.elasticpath.service.search.ProductCategorySearchCriteria;
import com.elasticpath.service.search.index.IndexSearchResult;
import com.elasticpath.service.search.index.IndexSearchService;
import com.elasticpath.service.search.solr.IndexUtility;

/**
 * Repository class for general search.
 */
@Component
public class SearchRepositoryImpl implements SearchRepository {

	private static final String DEFAULT_PAGINATION_VALUE_NO_SETTING_FOUND_ERROR =
			"Error encountered while retrieving default pagination setting with context '%s'";
	private static final String DEFAULT_PAGINATION_VALUE_NO_VALUE_ERROR = "No default pagination value for '%s' is defined";
	private static final String DEFAULT_PAGINATION_VALUE_INVALID_VALUE_ERROR = "Default pagination value for '%s' is invalid: '%s'";
	private static final String PAGINATION_SETTING = "COMMERCE/STORE/listPagination";

	private SettingsRepository settingsRepository;
	private IndexSearchService indexSearchService;
	private StoreProductRepository storeProductRepository;
	private ItemRepository itemRepository;
	private IndexUtility indexUtility;
	private ReactiveAdapter reactiveAdapter;

	@Reference
	public void setSettingsRepository(final SettingsRepository settingsRepository) {
		this.settingsRepository = settingsRepository;
	}

	@Reference
	public void setIndexSearchService(final IndexSearchService indexSearchService) {
		this.indexSearchService = indexSearchService;
	}

	@Reference
	public void setStoreProductRepository(final StoreProductRepository storeProductRepository) {
		this.storeProductRepository = storeProductRepository;
	}

	@Reference
	public void setItemRepository(final ItemRepository itemRepository) {
		this.itemRepository = itemRepository;
	}

	@Reference
	public void setIndexUtility(final IndexUtility indexUtility) {
		this.indexUtility = indexUtility;
	}

	@Reference
	public void setReactiveAdapter(final ReactiveAdapter reactiveAdapter) {
		this.reactiveAdapter = reactiveAdapter;
	}

	@Override
	@CacheResult
	public Single<Integer> getDefaultPageSize(final String storeCode) {
		return settingsRepository.<Integer>getSetting(PAGINATION_SETTING, storeCode)
				.toSingle()
				.onErrorResumeNext(throwable -> {
					if (throwable instanceof NoSuchElementException) {
						return Single.error(ResourceOperationFailure.notFound(String.format(DEFAULT_PAGINATION_VALUE_NO_VALUE_ERROR, storeCode)));
					} else {
						return Single.error(ResourceOperationFailure.serverError(
								String.format(DEFAULT_PAGINATION_VALUE_NO_SETTING_FOUND_ERROR, storeCode)));
					}
				})
				.flatMap(paginationSetting -> getPaginationValue(storeCode, paginationSetting));
	}

	private Single<Integer> getPaginationValue(final String storeCode, final Integer paginationValue) {
		if (paginationValue == null || !NumberUtil.isPositive(paginationValue)) {
			return Single.error(ResourceOperationFailure.serverError(
					String.format(DEFAULT_PAGINATION_VALUE_INVALID_VALUE_ERROR, storeCode, paginationValue)));
		}
		return Single.just(paginationValue);
	}

	@Override
	@CacheResult
	public Single<PaginatedResult> searchForItemIds(final int startPageNumber, final int numberOfResultsPerPage,
													final ProductCategorySearchCriteria productSearchCriteria) {
		return reactiveAdapter.fromServiceAsSingle(() -> indexSearchService.search(productSearchCriteria))
				.flatMap(productSearchResult ->
						createPaginatedSearchResult(startPageNumber, numberOfResultsPerPage, productSearchResult));
	}

	private Single<PaginatedResult> createPaginatedSearchResult(final int startPageNumber, final int numberOfResultsPerPage,
																final IndexSearchResult productSearchResult) {
		return Single.just(productSearchResult.getResults((startPageNumber - 1) * numberOfResultsPerPage, numberOfResultsPerPage))
				.flatMap(this::getSortedProducts)
				.map(this::getItemIds)
				.map(itemIds -> new PaginatedResult(itemIds, startPageNumber, numberOfResultsPerPage, productSearchResult
						.getLastNumFound()));
	}

	private List<String> getItemIds(final List<Product> sortedProducts) {
		// translate product IDs to item IDs in the same order
		List<String> itemIds = new ArrayList<>();
		for (Product product : sortedProducts) {
			String defaultItemId = itemRepository.getDefaultItemIdForProduct(product).toSingle().blockingGet();
			itemIds.add(defaultItemId);
		}
		return itemIds;
	}

	private Single<List<Product>> getSortedProducts(final List<Long> productUids) {
		return reactiveAdapter.fromServiceAsSingle(() -> storeProductRepository.findByUids(productUids))
				.flatMap(products -> reactiveAdapter.fromServiceAsSingle(() -> indexUtility.sortDomainList(productUids,
						products)));
	}

}
