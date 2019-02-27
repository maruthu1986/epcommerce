/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.search.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Locale;

import io.reactivex.Single;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.store.Store;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.definition.navigations.NavigationIdentifier;
import com.elasticpath.rest.definition.navigations.NavigationsIdentifier;
import com.elasticpath.rest.definition.searches.NavigationSearchResultIdentifier;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.id.type.IntegerIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.identity.Subject;
import com.elasticpath.rest.identity.TestSubjectFactory;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.category.CategoryRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.pagination.PaginatedResult;
import com.elasticpath.rest.resource.integration.epcommerce.repository.search.SearchRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.store.StoreRepository;

/**
 * Test class for {@link NavigationSearchResultPaginationRepository}.
 */
@RunWith(MockitoJUnitRunner.class)
public class NavigationSearchResultPaginationRepositoryTest {

	private static final String ITEM_ID1 = "itemId1";
	private static final int PAGE_SIZE = 10;
	private static final String USERID = "userid";
	private static final String CATALOG_CODE = "catalog_code";
	private static final String STORE_CODE = "store_code";
	private static final String CATEGORY_CODE = "category_code";
	private static final IdentifierPart<String> SCOPE = StringIdentifier.of("scope");
	private static final String NOT_FOUND = "not found";
	public static final ResourceOperationFailure OPERATION_FAILURE = ResourceOperationFailure.notFound(NOT_FOUND);

	@Mock
	private ResourceOperationContext resourceOperationContext;
	@Mock
	private CategoryRepository categoryRepository;
	@Mock
	private SearchRepository searchRepository;
	@Mock
	private StoreRepository storeRepository;

	@InjectMocks
	private NavigationSearchResultPaginationRepository<NavigationSearchResultIdentifier, ItemIdentifier> paginationRepository;

	@Test
	public void testValidateSearchData() {
		NavigationSearchResultIdentifier navigationSearchResultIdentifier = NavigationSearchResultIdentifier
				.builder()
				.withNavigation(NavigationIdentifier.builder()
						.withNodeId(StringIdentifier.of("node"))
						.withNavigations(
								NavigationsIdentifier.builder().withScope(SCOPE).build()
						)
						.build()
				)
				.withPageId(IntegerIdentifier.of(1))
				.build();

		paginationRepository.validateSearchData(navigationSearchResultIdentifier)
				.test()
				.assertValue(navigationSearchData -> 1 == navigationSearchData.getPageId())
				.assertValue(navigationSearchData -> "node".equals(navigationSearchData.getSearchKeyword()))
				.assertValue(navigationSearchData -> "scope".equals(navigationSearchData.getScope()));
	}

	@Test
	public void testValidateSearchDataInvalidPageId() {
		NavigationSearchResultIdentifier navigationSearchResultIdentifier = NavigationSearchResultIdentifier
				.builder()
				.withNavigation(NavigationIdentifier.builder()
						.withNodeId(StringIdentifier.of("node"))
						.withNavigations(NavigationsIdentifier.builder().withScope(
								SCOPE

								).build()
						)
						.build()
				)
				.withPageId(IntegerIdentifier.of(-1))
				.build();

		paginationRepository.validateSearchData(navigationSearchResultIdentifier)
				.test()
				.assertFailure(ResourceOperationFailure.class)
				.assertFailure(
						throwable -> ResourceStatus.BAD_REQUEST_BODY
								.equals(((ResourceOperationFailure) throwable).getResourceStatus())
				);
	}

	/**
	 * Test find item by category.
	 */
	@Test
	public void testFindItemByCategory() {
		Catalog catalog = createMockCatalog();
		Store store = createMockStore();
		Category category = createMockCategory(catalog);
		PaginatedResult searchResult = new PaginatedResult(Collections.singleton(ITEM_ID1), 1, PAGE_SIZE, PAGE_SIZE);

		shouldFindSubject();
		shouldFindStoreWithResult(Single.just(store));
		shouldFindByGuid(category);
		shouldGetDefaultPageSizeWithResult(Single.just(PAGE_SIZE));
		shouldSearchItemIdsWithResult(Single.just(searchResult));

		paginationRepository.getPaginatedResult(new NavigationSearchData(1, CATEGORY_CODE, STORE_CODE))
				.test()
				.assertComplete()
				.assertValue(paginatedResult -> 1 == paginatedResult.getNumberOfPages())
				.assertValue(paginatedResult -> 1 == paginatedResult.getCurrentPage())
				.assertValue(paginatedResult -> PAGE_SIZE == paginatedResult.getTotalNumberOfResults())
				.assertValue(paginatedResult -> 1 == paginatedResult.getResultIds().size());
	}

	/**
	 * Test find item by category.
	 */
	@Test
	public void testPaginationInfo() {
		NavigationSearchResultIdentifier navigationSearchResultIdentifier = NavigationSearchResultIdentifier
				.builder()
				.withNavigation(NavigationIdentifier.builder()
						.withNodeId(StringIdentifier.of(CATEGORY_CODE))
						.withNavigations(
								NavigationsIdentifier.builder().withScope(
										StringIdentifier.of(STORE_CODE)
								).build()
						)
						.build()
				)
				.withPageId(IntegerIdentifier.of(1))
				.build();

		Catalog catalog = createMockCatalog();
		Store store = createMockStore();
		Category category = createMockCategory(catalog);
		PaginatedResult searchResult = new PaginatedResult(Collections.singleton(ITEM_ID1), 1, PAGE_SIZE, PAGE_SIZE);

		shouldFindSubject();
		shouldFindStoreWithResult(Single.just(store));
		shouldFindByGuid(category);
		shouldGetDefaultPageSizeWithResult(Single.just(PAGE_SIZE));
		shouldSearchItemIdsWithResult(Single.just(searchResult));

		paginationRepository.getPaginationInfo(navigationSearchResultIdentifier)
				.test()
				.assertComplete()
				.assertValue(paginationEntity -> 1 == paginationEntity.getPages())
				.assertValue(paginationEntity -> 1 == paginationEntity.getCurrent())
				.assertValue(paginationEntity -> PAGE_SIZE == paginationEntity.getResults())
				.assertValue(paginationEntity -> 1 == paginationEntity.getResultsOnPage());
	}

	/**
	 * Test find item by category.
	 */
	@Test
	public void testPaginationLinks() {
		NavigationSearchResultIdentifier navigationSearchResultIdentifier = NavigationSearchResultIdentifier
				.builder()
				.withNavigation(NavigationIdentifier.builder()
						.withNodeId(StringIdentifier.of(CATEGORY_CODE))
						.withNavigations(
								NavigationsIdentifier.builder().withScope(
										StringIdentifier.of(STORE_CODE)
								).build()
						)
						.build()
				)
				.withPageId(IntegerIdentifier.of(1))
				.build();

		Catalog catalog = createMockCatalog();
		Store store = createMockStore();
		Category category = createMockCategory(catalog);
		PaginatedResult searchResult = new PaginatedResult(Collections.singleton(ITEM_ID1), 1, PAGE_SIZE, PAGE_SIZE);

		shouldFindSubject();
		shouldFindStoreWithResult(Single.just(store));
		shouldFindByGuid(category);
		shouldGetDefaultPageSizeWithResult(Single.just(PAGE_SIZE));
		shouldSearchItemIdsWithResult(Single.just(searchResult));

		paginationRepository.getPagingLinks(navigationSearchResultIdentifier)
				.test()
				.assertComplete()
				.assertNoErrors();
	}


	/**
	 * Test find item by category with category not found.
	 */
	@Test
	public void testFindItemByCategoryWithCategoryNotFound() {
		Store store = createMockStore();

		shouldFindSubject();
		shouldFindStoreWithResult(Single.just(store));
		shouldGetDefaultPageSizeWithResult(Single.just(PAGE_SIZE));
		when(categoryRepository.findByStoreAndCategoryCode(STORE_CODE, CATEGORY_CODE)).thenReturn(Single.error(OPERATION_FAILURE));

		paginationRepository.getPaginatedResult(new NavigationSearchData(0, CATEGORY_CODE, STORE_CODE))
				.test()
				.assertError(OPERATION_FAILURE);
	}

	/**
	 * Test find item by category with store not found.
	 */
	@Test
	public void testFindItemByCategoryWithStoreNotFound() {
		shouldFindSubject();
		shouldFindStoreWithResult(Single.error(ResourceOperationFailure.notFound("Store not found.")));

		paginationRepository.getPaginatedResult(new NavigationSearchData(0, CATEGORY_CODE, STORE_CODE)).test()
				.assertFailure(ResourceOperationFailure.class)
				.assertFailure(
						throwable -> ResourceStatus.NOT_FOUND
								.equals(((ResourceOperationFailure) throwable).getResourceStatus())
				);
	}

	/**
	 * Test find item by category with an invalid pagination setting result returned.
	 */
	@Test
	public void testFindItemByCategoryWithInvalidPageSizeFromSettingsRepository() {
		Catalog catalog = createMockCatalog();
		Store store = createMockStore();
		Category category = createMockCategory(catalog);

		shouldFindSubject();
		shouldFindStoreWithResult(Single.just(store));
		shouldFindByGuid(category);
		shouldGetDefaultPageSizeWithResult(Single.error(ResourceOperationFailure.serverError("Invalid pagination setting")));

		paginationRepository.getPaginatedResult(new NavigationSearchData(0, CATEGORY_CODE, STORE_CODE)).test()
				.assertFailure(ResourceOperationFailure.class)
				.assertFailure(
						throwable -> ResourceStatus.SERVER_ERROR
								.equals(((ResourceOperationFailure) throwable).getResourceStatus())
				);
	}

	/**
	 * Test the behaviour of find item by category with search failure.
	 */
	@Test
	public void testFindItemByCategoryWithSearchFailure() {
		Catalog catalog = createMockCatalog();
		Store store = createMockStore();
		Category category = createMockCategory(catalog);

		shouldFindSubject();
		shouldFindStoreWithResult(Single.just(store));
		shouldFindByGuid(category);
		shouldGetDefaultPageSizeWithResult(Single.just(PAGE_SIZE));
		shouldSearchItemIdsWithResult(Single.error(ResourceOperationFailure.notFound()));

		paginationRepository.getPaginatedResult(new NavigationSearchData(1, CATEGORY_CODE, STORE_CODE)).test()
				.assertFailure(ResourceOperationFailure.class)
				.assertFailure(
						throwable -> ResourceStatus.NOT_FOUND
								.equals(((ResourceOperationFailure) throwable).getResourceStatus())
				);
	}

	/**
	 * Test the behaviour of find item by category with invalid page.
	 */
	@Test
	public void testFindItemByCategoryWithInvalidPage() {
		Catalog catalog = createMockCatalog();
		Store store = createMockStore();
		Category category = createMockCategory(catalog);
		PaginatedResult searchResult = new PaginatedResult(Collections.singleton(ITEM_ID1), 1, PAGE_SIZE, PAGE_SIZE);

		shouldFindSubject();
		shouldFindStoreWithResult(Single.just(store));
		shouldFindByGuid(category);
		shouldGetDefaultPageSizeWithResult(Single.just(PAGE_SIZE));
		shouldSearchItemIdsWithResult(Single.just(searchResult));

		paginationRepository.getPaginatedResult(new NavigationSearchData(2, CATEGORY_CODE, STORE_CODE)).test()
				.assertFailure(ResourceOperationFailure.class)
				.assertFailure(
						throwable -> ResourceStatus.NOT_FOUND
								.equals(((ResourceOperationFailure) throwable).getResourceStatus())
				);
	}

	private void shouldFindSubject() {
		Subject subject = TestSubjectFactory.createWithScopeAndUserIdAndLocale(STORE_CODE, USERID, Locale.ENGLISH);
		when(resourceOperationContext.getSubject())
				.thenReturn(subject);
	}

	private void shouldGetDefaultPageSizeWithResult(final Single<Integer> result) {
		when(searchRepository.getDefaultPageSize(STORE_CODE)).thenReturn(result);
	}

	private void shouldFindStoreWithResult(final Single<Store> result) {
		when(storeRepository.findStoreAsSingle(STORE_CODE)).thenReturn(result);
	}

	private void shouldSearchItemIdsWithResult(final Single<PaginatedResult> executionResult) {
		when(searchRepository.searchForItemIds(anyInt(), anyInt(), any())).thenReturn(executionResult);
	}

	private void shouldFindByGuid(final Category category) {
		when(categoryRepository.findByStoreAndCategoryCode(STORE_CODE, CATEGORY_CODE)).thenReturn(Single.just(category));
	}

	private Catalog createMockCatalog() {
		final Catalog catalog = mock(Catalog.class);
		catalog.setCode(CATALOG_CODE);

		return catalog;
	}

	private Store createMockStore() {
		final Store store = mock(Store.class);
		when(store.getCode()).thenReturn(STORE_CODE);

		return store;
	}

	private Category createMockCategory(final Catalog catalog) {
		final Category category = mock(Category.class);
		when(category.getCatalog()).thenReturn(catalog);

		return category;
	}
}
