/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.search.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.reactivex.Single;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.store.Store;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.definition.collections.PaginationEntity;
import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.definition.searches.KeywordSearchResultIdentifier;
import com.elasticpath.rest.definition.searches.SearchKeywordsEntity;
import com.elasticpath.rest.definition.searches.SearchesIdentifier;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.id.type.CompositeIdentifier;
import com.elasticpath.rest.id.type.IntegerIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.identity.Subject;
import com.elasticpath.rest.identity.TestSubjectFactory;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.pagination.PaginatedResult;
import com.elasticpath.rest.resource.integration.epcommerce.repository.search.SearchRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.store.StoreRepository;
import com.elasticpath.service.search.ProductCategorySearchCriteria;

/**
 * Test class for {@link KeywordSearchResultPaginationRepository}.
 */
@RunWith(MockitoJUnitRunner.class)
public class KeywordSearchResultPaginationRepositoryTest {

	private static final String ITEM_ID = "item id";
	private static final int TOTAL_RESULTS = 12842;
	private static final int TOTAL_PAGES = 2569;
	private static final String CATALOG_CODE = "catalog_code";
	private static final String STORE_CODE = "store_code";
	private static final String USERID = "userid";
	private static final String SEARCH_KEYWORDS = "search_keywords";
	private static final int PAGE = 1;
	private static final int RESULTS_PER_PAGE = 5;
	private static final int THREE = 3;
	private static final String SEARCHTERM = "term";
	private static final IdentifierPart<String> SCOPE = StringIdentifier.of("scope");

	@Mock
	private ResourceOperationContext resourceOperationContext;
	@Mock
	private StoreRepository storeRepository;
	@Mock
	private SearchRepository searchRepository;

	@InjectMocks
	private KeywordSearchResultPaginationRepository<KeywordSearchResultIdentifier, ItemIdentifier> paginationRepository;

	@Test
	public void validateSearchData() {
		Map<String, String> searchId = new HashMap<>();
		searchId.put(PaginationEntity.PAGE_SIZE_PROPERTY, "2");
		searchId.put(SearchKeywordsEntity.KEYWORDS_PROPERTY, SEARCHTERM);
		KeywordSearchResultIdentifier keywordSearchResultIdentifier = KeywordSearchResultIdentifier.builder()
				.withPageId(IntegerIdentifier.of(1))
				.withSearchId(CompositeIdentifier.of(searchId))
				.withSearches(
						SearchesIdentifier.builder()
								.withScope(SCOPE)
								.build())
				.build();

		paginationRepository.validateSearchData(keywordSearchResultIdentifier)
				.test()
				.assertValue(keywordSearchData -> 1 == keywordSearchData.getPageId())
				.assertValue(keywordSearchData -> 2 == keywordSearchData.getPageSize())
				.assertValue(keywordSearchData -> "scope".equals(keywordSearchData.getScope()))
				.assertValue(keywordSearchData -> SEARCHTERM.equals(keywordSearchData.getSearchKeyword()));
	}

	@Test
	public void validateSearchDataNormalizeNullPageSize() {
		Map<String, String> searchId = new HashMap<>();
		searchId.put(PaginationEntity.PAGE_SIZE_PROPERTY, "null");
		searchId.put(SearchKeywordsEntity.KEYWORDS_PROPERTY, SEARCHTERM);
		KeywordSearchResultIdentifier keywordSearchResultIdentifier = KeywordSearchResultIdentifier.builder()
				.withPageId(IntegerIdentifier.of(1))
				.withSearchId(CompositeIdentifier.of(searchId))
				.withSearches(
						SearchesIdentifier.builder()
								.withScope(SCOPE)
								.build())
				.build();

		paginationRepository.validateSearchData(keywordSearchResultIdentifier)
				.test()
				.assertValue(keywordSearchData -> 1 == keywordSearchData.getPageId())
				.assertValue(keywordSearchData -> 0 == keywordSearchData.getPageSize())
				.assertValue(keywordSearchData -> "scope".equals(keywordSearchData.getScope()))
				.assertValue(keywordSearchData -> SEARCHTERM.equals(keywordSearchData.getSearchKeyword()));
	}

	@Test
	public void validateSearchDataBlankPageSizeNegativePageSize() {
		Map<String, String> searchId = new HashMap<>();
		searchId.put(PaginationEntity.PAGE_SIZE_PROPERTY, "-1");
		searchId.put(SearchKeywordsEntity.KEYWORDS_PROPERTY, SEARCHTERM);
		KeywordSearchResultIdentifier keywordSearchResultIdentifier = KeywordSearchResultIdentifier.builder()
				.withPageId(IntegerIdentifier.of(1))
				.withSearchId(CompositeIdentifier.of(searchId))
				.withSearches(
						SearchesIdentifier.builder()
								.withScope(SCOPE)
								.build())
				.build();

		paginationRepository.validateSearchData(keywordSearchResultIdentifier)
				.test()
				.assertFailure(ResourceOperationFailure.class)
				.assertFailure(
						throwable -> ResourceStatus.BAD_REQUEST_BODY
								.equals(((ResourceOperationFailure) throwable).getResourceStatus())
				);
	}

	@Test
	public void validateSearchDataBlankPageSizeIsString() {
		Map<String, String> searchId = new HashMap<>();
		searchId.put(PaginationEntity.PAGE_SIZE_PROPERTY, "spaksd");
		searchId.put(SearchKeywordsEntity.KEYWORDS_PROPERTY, SEARCHTERM);
		KeywordSearchResultIdentifier keywordSearchResultIdentifier = KeywordSearchResultIdentifier.builder()
				.withPageId(IntegerIdentifier.of(1))
				.withSearchId(CompositeIdentifier.of(searchId))
				.withSearches(
						SearchesIdentifier.builder()
								.withScope(SCOPE)
								.build())
				.build();

		paginationRepository.validateSearchData(keywordSearchResultIdentifier)
				.test()
				.assertFailure(ResourceOperationFailure.class)
				.assertFailure(
						throwable -> ResourceStatus.BAD_REQUEST_BODY
								.equals(((ResourceOperationFailure) throwable).getResourceStatus())
				);
	}


	/**
	 * Test a valid keyword search.
	 */
	@Test
	public void testKeywordSearch() {
		Catalog catalog = createMockCatalog();
		Store store = createMockStore(catalog);
		Collection<String> itemIds = Collections.singleton(ITEM_ID);
		PaginatedResult searchResult = new PaginatedResult(itemIds, PAGE, RESULTS_PER_PAGE, TOTAL_RESULTS);

		shouldFindSubject();
		shouldFindStoreWithResult(Single.just(store));
		shouldGetDefaultPageSizeWithResult(Single.just(RESULTS_PER_PAGE));
		shouldSearchItemIdsWithResult(Single.just(searchResult));

		paginationRepository.getPaginationInfo(new KeywordSearchData(PAGE, RESULTS_PER_PAGE, SEARCH_KEYWORDS, STORE_CODE))
				.test()
				.assertComplete()
				.assertValue(paginationEntity -> RESULTS_PER_PAGE == paginationEntity.getPageSize())
				.assertValue(paginationEntity -> TOTAL_PAGES == paginationEntity.getPages())
				.assertValue(paginationEntity -> PAGE == paginationEntity.getCurrent())
				.assertValue(paginationEntity -> TOTAL_RESULTS == paginationEntity.getResults())
				.assertValue(paginationEntity -> 1 == paginationEntity.getResultsOnPage());
	}

	/**
	 * Test a valid keyword search with a custom page size.
	 */
	@Test
	public void testKeywordSearchWithCustomPageSize() {
		int pageSize = THREE;
		Catalog catalog = createMockCatalog();
		Store store = createMockStore(catalog);
		Collection<String> itemIds = Collections.singleton(ITEM_ID);
		PaginatedResult searchResult = new PaginatedResult(itemIds, PAGE, RESULTS_PER_PAGE, pageSize);

		shouldFindSubject();
		shouldFindStoreWithResult(Single.just(store));
		shouldSearchItemIdsWithResult(Single.just(searchResult));

		paginationRepository.getPaginationInfo(new KeywordSearchData(PAGE, pageSize, SEARCH_KEYWORDS, STORE_CODE))
				.test()
				.assertComplete()
				.assertValue(paginationEntity -> THREE == paginationEntity.getPageSize())
				.assertValue(paginationEntity -> 1 == paginationEntity.getPages())
				.assertValue(paginationEntity -> PAGE == paginationEntity.getCurrent())
				.assertValue(paginationEntity -> THREE == paginationEntity.getResults())
				.assertValue(paginationEntity -> 1 == paginationEntity.getResultsOnPage());
	}

	/**
	 * Test keyword search when no store is found.
	 */
	@Test
	public void testKeywordSearchWithStoreNotFound() {
		shouldFindSubject();
		shouldFindStoreWithResult(Single.error(ResourceOperationFailure.notFound("not found")));

		paginationRepository.getPaginationInfo(new KeywordSearchData(PAGE, RESULTS_PER_PAGE, SEARCH_KEYWORDS, STORE_CODE))
				.test()
				.assertFailure(ResourceOperationFailure.class)
				.assertFailure(
						throwable -> ResourceStatus.NOT_FOUND
								.equals(((ResourceOperationFailure) throwable).getResourceStatus())
				);
	}


	/**
	 * Test keyword search when trying to access a page greater than the number of resulting pages.
	 */
	@Test
	public void testKeywordSearchWithPageGreaterThanResultPages() {
		Catalog catalog = createMockCatalog();
		Store store = createMockStore(catalog);
		Collection<String> emptyItemIds = Collections.emptyList();
		PaginatedResult searchResult = new PaginatedResult(emptyItemIds, PAGE, RESULTS_PER_PAGE,
				RESULTS_PER_PAGE);

		shouldFindSubject();
		shouldFindStoreWithResult(Single.just(store));
		shouldGetDefaultPageSizeWithResult(Single.just(RESULTS_PER_PAGE));
		shouldSearchItemIdsWithResult(Single.just(searchResult));

		paginationRepository.getPaginationInfo(new KeywordSearchData(PAGE + 1, RESULTS_PER_PAGE, SEARCH_KEYWORDS, STORE_CODE))
				.test()
				.assertFailure(ResourceOperationFailure.class)
				.assertFailure(
						throwable -> ResourceStatus.NOT_FOUND
								.equals(((ResourceOperationFailure) throwable).getResourceStatus())
				);
	}

	/**
	 * Test keyword search with invalid pagination setting result returned.
	 */
	@Test
	public void testKeywordSearchWithInvalidPageSizeReturnedFromSettingsRepository() {
		Catalog catalog = createMockCatalog();
		Store store = createMockStore(catalog);

		shouldFindSubject();
		shouldFindStoreWithResult(Single.just(store));
		shouldGetDefaultPageSizeWithResult(Single.error(ResourceOperationFailure.serverError("Invalid pagination setting.")));

		paginationRepository.getPaginationInfo(new KeywordSearchData(PAGE, 0, SEARCH_KEYWORDS, STORE_CODE))
				.test()
				.assertFailure(ResourceOperationFailure.class)
				.assertFailure(
						throwable -> ResourceStatus.SERVER_ERROR
								.equals(((ResourceOperationFailure) throwable).getResourceStatus())
				);
	}

	/**
	 * Test keyword search when pagination setting is invalid.
	 */
	@Test
	public void testKeywordSearchWithPaginationSettingOfZero() {
		Catalog catalog = createMockCatalog();
		Store store = createMockStore(catalog);

		shouldFindSubject();
		shouldFindStoreWithResult(Single.just(store));
		shouldGetDefaultPageSizeWithResult(Single.error(ResourceOperationFailure.serverError("Zero size pagination setting")));

		paginationRepository.getPaginationInfo(new KeywordSearchData(PAGE, 0, SEARCH_KEYWORDS, STORE_CODE))
				.test()
				.assertFailure(ResourceOperationFailure.class)
				.assertFailure(
						throwable -> ResourceStatus.SERVER_ERROR
								.equals(((ResourceOperationFailure) throwable).getResourceStatus())
				);
	}

	/**
	 * Test the behaviour of keyword search with search result failure.
	 */
	@Test
	public void testKeywordSearchWithSearchResultFailure() {
		Catalog catalog = createMockCatalog();
		Store store = createMockStore(catalog);

		shouldFindSubject();
		shouldFindStoreWithResult(Single.just(store));
		shouldSearchItemIdsWithResult(Single.error(ResourceOperationFailure.serverError("Server error during search")));
		shouldGetDefaultPageSizeWithResult(Single.just(RESULTS_PER_PAGE));

		paginationRepository.getPaginationInfo(new KeywordSearchData(PAGE, RESULTS_PER_PAGE, SEARCH_KEYWORDS, STORE_CODE))
				.test()
				.assertFailure(ResourceOperationFailure.class)
				.assertFailure(
						throwable -> ResourceStatus.SERVER_ERROR
								.equals(((ResourceOperationFailure) throwable).getResourceStatus())
				);
	}


	private void shouldFindSubject() {
		Subject subject = TestSubjectFactory.createWithScopeAndUserIdAndLocale(STORE_CODE, USERID, Locale.ENGLISH);
		when(resourceOperationContext.getSubject())
				.thenReturn(subject);
	}

	private void shouldFindStoreWithResult(final Single<Store> result) {
		when(storeRepository.findStoreAsSingle(STORE_CODE)).thenReturn(result);
	}

	private void shouldGetDefaultPageSizeWithResult(final Single<Integer> result) {
		when(searchRepository.getDefaultPageSize(STORE_CODE)).thenReturn(result);
	}

	private void shouldSearchItemIdsWithResult(final Single<PaginatedResult> result) {
		when(searchRepository.searchForItemIds(anyInt(), anyInt(), any(ProductCategorySearchCriteria.class))).thenReturn(result);
	}

	private Catalog createMockCatalog() {
		Catalog catalog = mock(Catalog.class);
		when(catalog.getCode()).thenReturn(CATALOG_CODE);

		return catalog;
	}

	private Store createMockStore(final Catalog catalog) {
		Store store = mock(Store.class);
		when(store.getCatalog()).thenReturn(catalog);

		return store;
	}
}
