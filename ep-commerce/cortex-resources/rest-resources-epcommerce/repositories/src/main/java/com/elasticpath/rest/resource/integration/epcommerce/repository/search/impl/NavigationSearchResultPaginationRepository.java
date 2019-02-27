/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.search.impl;

import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.Single;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.store.Store;
import com.elasticpath.repository.PaginationRepository;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.definition.items.ItemsIdentifier;
import com.elasticpath.rest.definition.searches.NavigationSearchResultIdentifier;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.id.type.CompositeIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.id.util.CompositeIdUtil;
import com.elasticpath.rest.identity.util.SubjectUtil;
import com.elasticpath.rest.pagination.PaginationEntity;
import com.elasticpath.rest.pagination.PagingLink;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.category.CategoryRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.pagination.PaginatedResult;
import com.elasticpath.rest.resource.integration.epcommerce.repository.search.SearchRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.store.StoreRepository;
import com.elasticpath.service.search.query.ProductSearchCriteria;
import com.elasticpath.service.search.query.SortOrder;
import com.elasticpath.service.search.query.StandardSortBy;

/**
 * Repository that provides lookup of item data through indexed navigation-id search.
 *
 * @param <I>  the self identifier type
 * @param <LI> the linked identifier type
 */
@Component(service = PaginationRepository.class)
public class NavigationSearchResultPaginationRepository<I extends NavigationSearchResultIdentifier, LI extends ItemIdentifier>
		extends PaginationRepository<NavigationSearchResultIdentifier, ItemIdentifier> {

	private static final Logger LOG = LoggerFactory.getLogger(NavigationSearchResultPaginationRepository.class);

	private static final int FIRST_PAGE_ID = 1;

	private ResourceOperationContext resourceOperationContext;
	private CategoryRepository categoryRepository;
	private StoreRepository storeRepository;
	private SearchRepository searchRepository;

	@Reference
	public void setResourceOperationContext(final ResourceOperationContext resourceOperationContext) {
		this.resourceOperationContext = resourceOperationContext;
	}

	@Reference
	public void setCategoryRepository(final CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	@Reference
	public void setStoreRepository(final StoreRepository storeRepository) {
		this.storeRepository = storeRepository;
	}

	@Reference
	public void setSearchRepository(final SearchRepository searchRepository) {
		this.searchRepository = searchRepository;
	}

	@Override
	public Single<PaginationEntity> getPaginationInfo(final NavigationSearchResultIdentifier navigationSearchResultIdentifier) {
		return validateSearchData(navigationSearchResultIdentifier).flatMap(this::getPaginationInfo);
	}

	@Override
	public Observable<ItemIdentifier> getElements(final NavigationSearchResultIdentifier navigationSearchResultIdentifier) {
		return validateSearchData(navigationSearchResultIdentifier).flatMapObservable(this::getItems);
	}

	@Override
	public Observable<PagingLink<NavigationSearchResultIdentifier>> getPagingLinks(final NavigationSearchResultIdentifier
																						   navigationSearchResultIdentifier) {
		return validateSearchData(navigationSearchResultIdentifier).flatMapObservable(keywordSearchData ->
				getLinks(keywordSearchData, navigationSearchResultIdentifier));
	}

	@Override
	protected NavigationSearchResultIdentifier buildPageIdentifier(
			final NavigationSearchResultIdentifier identifier, final IdentifierPart<Integer> pageId) {
		return NavigationSearchResultIdentifier.builderFrom(identifier)
				.withPageId(pageId)
				.build();
	}

	/**
	 * validate search data.
	 *
	 * @param navigationSearchResultIdentifier NavigationSearchResultIdentifier
	 * @return KeywordSearchData or validation error
	 */
	protected Single<NavigationSearchData> validateSearchData(final NavigationSearchResultIdentifier navigationSearchResultIdentifier) {
		int pageId = navigationSearchResultIdentifier.getPageId().getValue();
		if (pageId < FIRST_PAGE_ID) {
			String unknownPageIdErrorMsg = "Page id " + pageId + "can't be smaller than" + FIRST_PAGE_ID;
			LOG.error(unknownPageIdErrorMsg);
			return Single.error(ResourceOperationFailure.badRequestBody(unknownPageIdErrorMsg));
		}

		String searchKeyword = navigationSearchResultIdentifier.getNavigation().getNodeId().getValue();
		String scope = String.valueOf(navigationSearchResultIdentifier.getNavigation().getNavigations().getScope().getValue());
		return Single.just(new NavigationSearchData(pageId, searchKeyword, scope));
	}

	/**
	 * get paginated result for search data.
	 *
	 * @param navigationSearchData search data
	 * @return paginated result
	 */
	protected Single<PaginatedResult> getPaginatedResult(final NavigationSearchData navigationSearchData) {
		return storeRepository.findStoreAsSingle(navigationSearchData.getScope())
				.flatMap(store -> categoryRepository
						.findByStoreAndCategoryCode(navigationSearchData.getScope(), navigationSearchData.getSearchKeyword())
						.flatMap(category -> searchByCategory(navigationSearchData, store, category))
				)
				.flatMap(paginatedResult -> {
					if (navigationSearchData.getPageId() > paginatedResult.getNumberOfPages()) {
						return Single.error(ResourceOperationFailure.notFound(String.format("Page %s does not exist.", navigationSearchData
								.getPageId())));
					}
					return Single.just(paginatedResult);
				});
	}

	/**
	 * Search by category.
	 *
	 * @param navigationSearchData search data
	 * @param store                store
	 * @param category             category
	 * @return paginated result
	 */
	protected Single<PaginatedResult> searchByCategory(final NavigationSearchData navigationSearchData, final Store store,
													   final Category category) {
		ProductSearchCriteria searchCriteria = createSearchCriteria(category, store.getCode());
		return searchRepository.getDefaultPageSize(store.getCode())
				.flatMap(pageSize -> searchRepository.searchForItemIds(navigationSearchData.getPageId(), pageSize,
						searchCriteria));
	}

	/**
	 * get pagination information for search data.
	 *
	 * @param navigationSearchData search data
	 * @return PaginationEntity containing pagination information
	 */
	protected Single<PaginationEntity> getPaginationInfo(final NavigationSearchData navigationSearchData) {
		return getPaginatedResult(navigationSearchData)
				.map(paginatedResult ->
						PaginationEntity.builder()
								.withCurrent(navigationSearchData.getPageId())
								.withPageSize(paginatedResult.getResultsPerPage())
								.withPages(paginatedResult.getNumberOfPages())
								.withResultsOnPage(paginatedResult.getResultIds().size())
								.withResults(paginatedResult.getTotalNumberOfResults())
								.build());
	}

	/**
	 * get linked items for search data.
	 *
	 * @param navigationSearchData search data.
	 * @return linked items
	 */
	protected Observable<ItemIdentifier> getItems(final NavigationSearchData navigationSearchData) {
		return getPaginatedResult(navigationSearchData).
				flatMapObservable(paginatedResult -> Observable.fromIterable(paginatedResult.getResultIds())).
				map(itemId ->
						ItemIdentifier.builder()
								.withItemId(CompositeIdentifier.of(CompositeIdUtil.decodeCompositeId(itemId)))
								.withItems(ItemsIdentifier.builder()
										.withScope(StringIdentifier.of(navigationSearchData.getScope()))
										.build())
								.build()
				);
	}

	/**
	 * get paging links for search data.
	 *
	 * @param navigationSearchData             search data
	 * @param navigationSearchResultIdentifier current page identifier
	 * @return paging links
	 */
	protected Observable<PagingLink<NavigationSearchResultIdentifier>> getLinks(
			final NavigationSearchData navigationSearchData,
			final NavigationSearchResultIdentifier navigationSearchResultIdentifier) {
		return getPaginatedResult(navigationSearchData)
				.flatMapObservable(paginatedResult ->
						createPagingLinks(navigationSearchData.getPageId(), paginatedResult.getNumberOfPages(), navigationSearchResultIdentifier)
				);
	}

	/**
	 * Creates new search criteria and populates it with category id and default search values.
	 *
	 * @param category  category
	 * @param storeCode store code
	 * @return product search criteria
	 */
	protected ProductSearchCriteria createSearchCriteria(final Category category, final String storeCode) {
		Locale locale = SubjectUtil.getLocale(resourceOperationContext.getSubject());
		ProductSearchCriteria criteria = new ProductSearchCriteria();
		criteria.setFuzzySearchDisabled(true);
		criteria.setOnlyWithinDirectCategory(true);
		criteria.setDisplayableOnly(true);
		criteria.setActiveOnly(true);
		criteria.setDirectCategoryUid(category.getUidPk());
		criteria.setCatalogCode(category.getCatalog().getCode());
		criteria.setStoreCode(storeCode);
		criteria.setLocale(locale);
		criteria.setSortingType(StandardSortBy.FEATURED_CATEGORY);
		criteria.setSortingOrder(SortOrder.DESCENDING);
		return criteria;
	}
}
