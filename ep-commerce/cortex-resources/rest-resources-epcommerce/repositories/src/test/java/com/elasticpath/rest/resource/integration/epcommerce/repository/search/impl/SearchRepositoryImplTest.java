/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.search.impl;

import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Single;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.persistence.api.EpPersistenceException;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.pagination.PaginatedResult;
import com.elasticpath.rest.resource.integration.epcommerce.repository.product.StoreProductRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.settings.SettingsRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ExceptionTransformer;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.impl.ReactiveAdapterImpl;
import com.elasticpath.service.search.ProductCategorySearchCriteria;
import com.elasticpath.service.search.index.IndexSearchResult;
import com.elasticpath.service.search.index.IndexSearchService;
import com.elasticpath.service.search.query.KeywordSearchCriteria;
import com.elasticpath.service.search.solr.IndexUtility;

/**
 * Test class for search lookup strategy.
 */
@RunWith(MockitoJUnitRunner.class)
public class SearchRepositoryImplTest {
	private static final String STORE_CODE = "store";
	private static final String PAGINATION_SETTING = "COMMERCE/STORE/listPagination";
	private static final Integer DEFAULT_PAGE_SIZE = 10;

	@Mock
	private IndexSearchService indexSearchService;
	@Mock
	private SettingsRepository settingsRepository;
	@Mock
	private StoreProductRepository storeProductRepository;
	@Mock
	private ItemRepository itemRepository;
	@Mock
	private IndexUtility indexUtility;
	@Mock
	private IndexSearchResult indexSearchResult;
	@SuppressWarnings({"PMD.UnusedPrivateField"})
	@Mock
	private ExceptionTransformer exceptionTransformer;
	@InjectMocks
	private ReactiveAdapterImpl reactiveAdapter;

	private SearchRepositoryImpl repository;

	@Before
	public void setUp() {
		repository = new SearchRepositoryImpl();
		repository.setIndexSearchService(indexSearchService);
		repository.setIndexUtility(indexUtility);
		repository.setItemRepository(itemRepository);
		repository.setReactiveAdapter(reactiveAdapter);
		repository.setSettingsRepository(settingsRepository);
		repository.setStoreProductRepository(storeProductRepository);
	}

	@Test
	public void ensureSearchForMultipleItemIdsLessThanPageSizeReturnsThemAll() {
		final int page = 1;
		final int pageSize = 10;

		final ProductCategorySearchCriteria searchCriteria = new KeywordSearchCriteria();
		final Collection<String> expectedItemIds = Arrays.asList("id1", "id2", "id3");

		final List<Long> resultUids = mockIndexSearch(page, pageSize, searchCriteria, expectedItemIds.size());
		mockProductAndItemCalls(resultUids, expectedItemIds);

		Single<PaginatedResult> itemIdsSearchExecutionResult = repository.searchForItemIds(page, pageSize,
				searchCriteria);

		itemIdsSearchExecutionResult.test()
				.assertComplete()
				.assertValue(paginatedResult -> page == paginatedResult.getCurrentPage())
				.assertValue(paginatedResult -> 1 == paginatedResult.getNumberOfPages())
				.assertValue(paginatedResult -> pageSize == paginatedResult.getResultsPerPage())
				.assertValue(paginatedResult -> expectedItemIds.size() == paginatedResult.getTotalNumberOfResults());
	}

	private List<Long> mockIndexSearch(final int page, final int pageSize, final ProductCategorySearchCriteria searchCriteria,
									   final int numberOfSearchResultsToReturn) {
		final List<Long> resultUids = Collections.emptyList();

		when(indexSearchService.search(searchCriteria)).thenReturn(indexSearchResult);
		when(indexSearchResult.getResults(page - 1, pageSize)).thenReturn(resultUids);
		when(indexSearchResult.getLastNumFound()).thenReturn(numberOfSearchResultsToReturn);

		return resultUids;
	}

	private void mockProductAndItemCalls(final List<Long> productUids, final Collection<String> expectedItemIds) {
		final Product product1 = new ProductImpl();
		product1.setCode("one");
		final Product product2 = new ProductImpl();
		product2.setCode("two");
		final Product product3 = new ProductImpl();
		product3.setCode("three");
		final List<Product> products = Arrays.asList(product1, product2, product3);

		when(storeProductRepository.findByUids(productUids)).thenReturn(products);
		when(indexUtility.sortDomainList(productUids, products)).thenReturn(products);

		Iterator<Product> productsIterator = products.iterator();
		Iterator<String> expectedItemIdsIterator = expectedItemIds.iterator();
		while (productsIterator.hasNext() && expectedItemIdsIterator.hasNext()) {
			Product product = productsIterator.next();
			String itemId = expectedItemIdsIterator.next();

			when(itemRepository.getDefaultItemIdForProduct(product)).thenReturn(ExecutionResultFactory.createReadOK(itemId));
		}
	}

	@Test
	public void ensureSearchForItemIdsWhereGetDefaultItemIdFailsReturnsServerError() {
		final int page = 1;
		final int pageSize = 10;
		final ProductCategorySearchCriteria searchCriteria = new KeywordSearchCriteria();
		final Collection<String> expectedItemIds = Arrays.asList("id1", "id2", "id3");
		final List<Long> resultUids = mockIndexSearch(page, pageSize, searchCriteria, 2);

		mockProductAndItemCallWithAssertionError(resultUids, expectedItemIds);

		repository.searchForItemIds(page, pageSize, searchCriteria)
				.test()
				.assertFailure(AssertionError.class);
	}

	private void mockProductAndItemCallWithAssertionError(final List<Long> productUids, final Collection<String> expectedItemIds) {
		final Product product1 = new ProductImpl();
		final List<Product> products = Arrays.asList(product1);

		when(storeProductRepository.findByUids(productUids)).thenReturn(products);
		when(indexUtility.sortDomainList(productUids, products)).thenReturn(products);

		Iterator<Product> productsIterator = products.iterator();
		Iterator<String> expectedItemIdsIterator = expectedItemIds.iterator();
		while (productsIterator.hasNext() && expectedItemIdsIterator.hasNext()) {
			Product product = productsIterator.next();

			when(itemRepository.getDefaultItemIdForProduct(product)).thenThrow(new AssertionError());
		}
	}

	@Test
	public void ensureSearchItemIdsWithException() {
		final int page = 1;
		final int pageSize = 10;
		final ProductCategorySearchCriteria searchCriteria = new KeywordSearchCriteria();

		when(indexSearchService.search(searchCriteria)).thenReturn(indexSearchResult);
		when(indexSearchResult.getResults(page - 1, pageSize))
				.thenThrow(new EpPersistenceException("persistence exception during search"));

		repository.searchForItemIds(page, pageSize, searchCriteria)
				.test()
				.assertFailure(EpPersistenceException.class);
	}

	@Test
	public void ensureGetDefaultPageSize() {
		when(settingsRepository.getSetting(PAGINATION_SETTING, STORE_CODE))
				.thenReturn(Maybe.just(DEFAULT_PAGE_SIZE));

		Single<Integer> result = repository.getDefaultPageSize(STORE_CODE);
		result.test().assertComplete().assertNoErrors().assertValue(DEFAULT_PAGE_SIZE);
	}

	@Test
	public void ensureGetDefaultPageSizeWithInvalidStoreCode() {
		when(settingsRepository.getSetting(PAGINATION_SETTING, STORE_CODE))
				.thenReturn(Maybe.empty());

		Single<Integer> result = repository.getDefaultPageSize(STORE_CODE);
		result.test()
				.assertFailure(ResourceOperationFailure.class)
				.assertFailure(
						throwable -> ResourceStatus.NOT_FOUND
								.equals(((ResourceOperationFailure) throwable).getResourceStatus())
				);
	}

	@Test
	public void ensureGetDefaultPageSizeWithMissingSetting() {
		when(settingsRepository.getSetting(PAGINATION_SETTING, STORE_CODE))
				.thenReturn(Maybe.error(ResourceOperationFailure.serverError("Error reading setting")));

		Single<Integer> result = repository.getDefaultPageSize(STORE_CODE);
		result.test()
				.assertFailure(ResourceOperationFailure.class)
				.assertFailure(
						throwable -> ResourceStatus.SERVER_ERROR
								.equals(((ResourceOperationFailure) throwable).getResourceStatus())
				);
	}

	@Test
	public void ensureGetDefaultPageSizeWithNegativePageSize() {
		when(settingsRepository.getSetting(PAGINATION_SETTING, STORE_CODE))
				.thenReturn(Maybe.just(-1));

		Single<Integer> result = repository.getDefaultPageSize(STORE_CODE);
		result.test()
				.assertFailure(ResourceOperationFailure.class)
				.assertFailure(
						throwable -> ResourceStatus.SERVER_ERROR
								.equals(((ResourceOperationFailure) throwable).getResourceStatus())
				);
	}

}
