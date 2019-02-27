/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.search.impl;

import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.Single;
import org.apache.commons.lang3.math.NumberUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elasticpath.domain.store.Store;
import com.elasticpath.repository.PaginationRepository;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.definition.items.ItemsIdentifier;
import com.elasticpath.rest.definition.searches.KeywordSearchResultIdentifier;
import com.elasticpath.rest.definition.searches.SearchKeywordsEntity;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.id.type.CompositeIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.id.util.CompositeIdUtil;
import com.elasticpath.rest.identity.util.SubjectUtil;
import com.elasticpath.rest.pagination.PaginationEntity;
import com.elasticpath.rest.pagination.PagingLink;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.pagination.PaginatedResult;
import com.elasticpath.rest.resource.integration.epcommerce.repository.search.SearchRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.store.StoreRepository;
import com.elasticpath.service.search.query.KeywordSearchCriteria;

/**
 * Repository that provides lookup of item data through indexed keyword search.
 *
 * @param <I>  the self identifier type
 * @param <LI> the linked identifier type
 */
@Component(service = PaginationRepository.class)
public class KeywordSearchResultPaginationRepository<I extends KeywordSearchResultIdentifier, LI extends ItemIdentifier>
		extends PaginationRepository<KeywordSearchResultIdentifier, ItemIdentifier> {

	private static final Logger LOG = LoggerFactory.getLogger(KeywordSearchResultPaginationRepository.class);

	private static final int FIRST_PAGE_ID = 1;

	private ResourceOperationContext resourceOperationContext;
	private StoreRepository storeRepository;
	private SearchRepository searchRepository;

	@Reference
	public void setResourceOperationContext(final ResourceOperationContext resourceOperationContext) {
		this.resourceOperationContext = resourceOperationContext;
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
	public Single<PaginationEntity> getPaginationInfo(final KeywordSearchResultIdentifier keywordSearchResultIdentifier) {
		return validateSearchData(keywordSearchResultIdentifier).flatMap(this::getPaginationInfo);
	}

	@Override
	public Observable<ItemIdentifier> getElements(final KeywordSearchResultIdentifier keywordSearchResultIdentifier) {
		return validateSearchData(keywordSearchResultIdentifier).flatMapObservable(this::getItems);
	}

	@Override
	public Observable<PagingLink<KeywordSearchResultIdentifier>> getPagingLinks(
			final KeywordSearchResultIdentifier keywordSearchResultIdentifier) {
		return validateSearchData(keywordSearchResultIdentifier).flatMapObservable(keywordSearchData ->
				getLinks(keywordSearchData, keywordSearchResultIdentifier));
	}

	@Override
	public KeywordSearchResultIdentifier buildPageIdentifier(
			final KeywordSearchResultIdentifier keywordSearchResultIdentifier, final IdentifierPart<Integer> pageId) {

		return KeywordSearchResultIdentifier
				.builderFrom(keywordSearchResultIdentifier)
				.withPageId(pageId)
				.build();
	}

	/**
	 * validate search data.
	 *
	 * @param keywordSearchResultIdentifier KeywordSearchResultIdentifier
	 * @return KeywordSearchData or validation error
	 */
	public Single<KeywordSearchData> validateSearchData(final KeywordSearchResultIdentifier keywordSearchResultIdentifier) {
		int pageId = keywordSearchResultIdentifier.getPageId().getValue();

		if (pageId < FIRST_PAGE_ID) {
			String unknownPageIdErrorMsg = "Page id " + pageId + "can't be smaller than" + FIRST_PAGE_ID;
			LOG.error(unknownPageIdErrorMsg);
			return Single.error(ResourceOperationFailure.badRequestBody(unknownPageIdErrorMsg));
		}

		String pageSizeString = keywordSearchResultIdentifier.getSearchId().getValue()
				.get(PaginationEntity.PAGE_SIZE_PROPERTY);

		if ("null".equals(pageSizeString)) {
			pageSizeString = "0";
		}
		if (!NumberUtils.isDigits(pageSizeString)) {
			return Single.error(ResourceOperationFailure.badRequestBody("Invalid page size"));
		}
		int pageSize = Integer.parseInt(pageSizeString);

		String searchKeywords = keywordSearchResultIdentifier.getSearchId().getValue().get(SearchKeywordsEntity
				.KEYWORDS_PROPERTY);
		String scope = keywordSearchResultIdentifier.getSearches().getScope().getValue();
		return Single.just(new KeywordSearchData(pageId, pageSize, searchKeywords, scope));
	}

	/**
	 * get pagination information for search data.
	 *
	 * @param keywordSearchData search data
	 * @return PaginationEntity containing pagination information
	 */
	public Single<PaginationEntity> getPaginationInfo(final KeywordSearchData keywordSearchData) {
		return getPaginatedResult(keywordSearchData)
				.map(paginatedResult ->
						PaginationEntity.builder()
								.withCurrent(keywordSearchData.getPageId())
								.withPageSize(keywordSearchData.getPageSize())
								.withPages(paginatedResult.getNumberOfPages())
								.withResultsOnPage(paginatedResult.getResultIds().size())
								.withResults(paginatedResult.getTotalNumberOfResults())
								.build());
	}

	/**
	 * get linked items for search data.
	 *
	 * @param keywordSearchData search data.
	 * @return linked items
	 */
	protected Observable<ItemIdentifier> getItems(final KeywordSearchData keywordSearchData) {
		return getPaginatedResult(keywordSearchData)
				.flatMapObservable(paginatedResult -> Observable.fromIterable(paginatedResult.getResultIds()))
				.map(itemId ->
						ItemIdentifier.builder()
								.withItemId(CompositeIdentifier.of(CompositeIdUtil.decodeCompositeId(itemId)))
								.withItems(ItemsIdentifier.builder()
										.withScope(StringIdentifier.of(keywordSearchData.getScope()))
										.build())
								.build()
				);
	}

	/**
	 * get paging links for search data.
	 *
	 * @param keywordSearchData             search data
	 * @param keywordSearchResultIdentifier current page identifier
	 * @return paging links
	 */
	protected Observable<PagingLink<KeywordSearchResultIdentifier>> getLinks(
			final KeywordSearchData keywordSearchData,
			final KeywordSearchResultIdentifier keywordSearchResultIdentifier) {
		return getPaginatedResult(keywordSearchData)
				.flatMapObservable(paginatedResult ->
						createPagingLinks(keywordSearchData.getPageId(), paginatedResult.getNumberOfPages(), keywordSearchResultIdentifier)
				);
	}

	/**
	 * get paginated result for search data.
	 *
	 * @param keywordSearchData search data
	 * @return paginated result
	 */
	protected Single<PaginatedResult> getPaginatedResult(final KeywordSearchData keywordSearchData) {
		return storeRepository.findStoreAsSingle(keywordSearchData.getScope())
				.flatMap(store -> createSearchCriteria(keywordSearchData.getSearchKeyword(), store))
				.flatMap(keywordSearchCriteria ->
						getPageSizeUsed(keywordSearchData.getScope(), keywordSearchData.getPageSize())
								.flatMap(pageSizeUsed -> search(keywordSearchData.getPageId(), keywordSearchCriteria, pageSizeUsed))
				).flatMap(paginatedResult -> validateSearchResult(keywordSearchData, paginatedResult));
	}

	private Single<PaginatedResult> validateSearchResult(final KeywordSearchData keywordSearchData,
														 final PaginatedResult paginatedResult) {
		int pageCount = paginatedResult.getNumberOfPages();
		if (keywordSearchData.getPageId() > pageCount) {
			LOG.debug("Tried to access page {} which exceeds number of pages: {}", keywordSearchData.getPageId(), pageCount);
			return Single.error(ResourceOperationFailure.notFound(String.format("Page %s does not exist.",
					keywordSearchData.getPageId())));
		}
		return Single.just(paginatedResult);
	}

	private Single<PaginatedResult> search(final int currentPageNumber, final KeywordSearchCriteria keywordSearchCriteria,
										   final Integer pageSizeUsed) {
		return searchRepository.searchForItemIds(currentPageNumber, pageSizeUsed, keywordSearchCriteria);
	}

	/**
	 * get used page size.
	 *
	 * @param storeCode              store code
	 * @param numberOfResultsPerPage input page size
	 * @return page size
	 */
	protected Single<Integer> getPageSizeUsed(final String storeCode, final int numberOfResultsPerPage) {
		return numberOfResultsPerPage == 0 ? searchRepository.getDefaultPageSize(storeCode) : Single.just(numberOfResultsPerPage);
	}

	/**
	 * Create search criteria for keyword search.
	 *
	 * @param searchKeywords search keyword
	 * @param store          store
	 * @return keyword search criteria
	 */
	protected Single<KeywordSearchCriteria> createSearchCriteria(final String searchKeywords, final Store store) {
		Locale locale = SubjectUtil.getLocale(resourceOperationContext.getSubject());
		KeywordSearchCriteria keywordSearchCriteria = new KeywordSearchCriteria();
		keywordSearchCriteria.setStoreCode(store.getCode());
		keywordSearchCriteria.setCatalogCode(store.getCatalog().getCode());
		keywordSearchCriteria.setFuzzySearchDisabled(false);
		keywordSearchCriteria.setKeyword(searchKeywords);
		keywordSearchCriteria.setLocale(locale);
		keywordSearchCriteria.setDisplayableOnly(true);
		keywordSearchCriteria.setActiveOnly(true);
		return Single.just(keywordSearchCriteria);
	}

}
