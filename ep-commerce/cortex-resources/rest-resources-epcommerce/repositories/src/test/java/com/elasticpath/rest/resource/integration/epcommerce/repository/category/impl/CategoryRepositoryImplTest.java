/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.category.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Single;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.impl.CategoryImpl;
import com.elasticpath.domain.misc.impl.OrderingComparatorImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.resource.integration.epcommerce.repository.store.StoreRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.impl.ReactiveAdapterImpl;
import com.elasticpath.service.catalog.CategoryLookup;
import com.elasticpath.service.catalog.CategoryService;

/**
 * Tests {@link CategoryRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CategoryRepositoryImplTest {

	private static final String CATALOG_CODE = "CATALOG_CODE";
	private static final String CATEGORY2_CODE = "CATEGORY2_CODE";
	private static final String CATEGORY1_CODE = "CATEGORY1_CODE";
	private static final String CATEGORY_CODE = "CATEGORY_CODE";
	private static final String CATEGORY_GUID = "CATEGORY_GUID";
	private static final String STORE_CODE = "STORE_CODE";
	public static final String NOT_FOUND = "not found";

	@Mock
	private CategoryService categoryService;
	@Mock
	private CategoryLookup categoryLookup;
	@Mock
	private StoreRepository storeRepository;
	@Mock
	private BeanFactory coreBeanFactory;
	@InjectMocks
	private ReactiveAdapterImpl reactiveAdapter;
	@InjectMocks
	private CategoryRepositoryImpl categoryRepository;

	@Before
	public void setUp() {
		categoryRepository = new CategoryRepositoryImpl(categoryLookup, storeRepository, coreBeanFactory, categoryService, reactiveAdapter);
	}
	
	@Test
	public void testFindRootNodesWhenSuccessful() {
		Catalog catalog = createMockCatalog(CATALOG_CODE);
		Store store = createMockStore(catalog);
		Category category = createMockCategory();
		List<Category> categories = Collections.singletonList(category);
		shouldFindStoreWithResult(store);
		shouldListRootCategories(catalog, categories);

		categoryRepository.findRootCategories(STORE_CODE)
				.test()
				.assertValueSequence(categories);
	}

	@Test
	public void testFindRootNodesWhenNotFound() {
		Catalog catalog = createMockCatalog(CATALOG_CODE);
		Store store = createMockStore(catalog);
		List<Category> categories = Collections.emptyList();
		shouldFindStoreWithResult(store);
		shouldListRootCategories(catalog, categories);

		categoryRepository.findRootCategories(STORE_CODE)
				.test()
				.assertValueSequence(categories);
	}

	@Test
	public void testFindRootNodesWhenStoreNotFound() {
		shouldNotFindTheStore();

		categoryRepository.findRootCategories(STORE_CODE)
				.test()
				.assertError(ResourceOperationFailure.notFound(NOT_FOUND));
	}

	@Test
	public void testGetCategoryByIdWhenFound() {
		Catalog catalog = createMockCatalog(CATALOG_CODE);
		Store store = createMockStore(catalog);
		shouldFindStoreWithResult(store);
		Category category = createMockCategory();
		when(categoryLookup.findByCategoryAndCatalogCode(CATEGORY_CODE, CATALOG_CODE)).thenReturn(category);

		categoryRepository.findByStoreAndCategoryCode(STORE_CODE, CATEGORY_CODE)
				.test()
				.assertValue(category);
	}

	@Test
	public void testGetCategoryByGuidWhenFound() {
		Catalog catalog = createMockCatalog(CATALOG_CODE);
		Store store = createMockStore(catalog);
		shouldFindStoreWithResult(store);
		Category category = createMockCategory();
		when(categoryLookup.findByGuid(CATEGORY_GUID)).thenReturn(category);

		categoryRepository.findByGuid(CATEGORY_GUID)
				.test()
				.assertValue(category);
	}

	@Test
	public void testGetCategoryByIdWhenStoreNotFound() {
		shouldNotFindTheStore();

		categoryRepository.findByStoreAndCategoryCode(STORE_CODE, CATEGORY_CODE)
				.test()
				.assertError(ResourceOperationFailure.notFound(NOT_FOUND));
	}

	@Test
	public void testGetCategoryByIdWhenCategoryNotFound() {
		Catalog catalog = createMockCatalog(CATALOG_CODE);
		Store store = createMockStore(catalog);
		shouldFindStoreWithResult(store);
		when(categoryLookup.findByCategoryAndCatalogCode(CATEGORY_CODE, CATALOG_CODE)).thenReturn(null);


		categoryRepository.findByStoreAndCategoryCode(STORE_CODE, CATEGORY_CODE)
				.test()
				.assertError(ResourceOperationFailure.notFound(CategoryRepositoryImpl.NAVIGATION_NODE_WAS_NOT_FOUND));
	}

	@Test
	public void testGettingChildNodesWhenFound() {
		Catalog catalog = createMockCatalog(CATALOG_CODE);
		Store store = createMockStore(catalog);
		shouldFindStoreWithResult(store);
		Category category = createMockCategory();
		when(categoryLookup.findByCategoryAndCatalogCode(CATEGORY_CODE, CATALOG_CODE)).thenReturn(category);
		List<Category> categories = createCategoriesList();
		shouldGetSubCategories(category, categories);
		shouldGetBean();

		categoryRepository.findChildren(STORE_CODE, CATEGORY_CODE)
				.test()
				.assertValueSequence(categories);
	}

	@Test
	public void testGettingChildNodesWhenNoneFound() {
		Catalog catalog = createMockCatalog(CATALOG_CODE);
		Store store = createMockStore(catalog);
		shouldFindStoreWithResult(store);
		Category category = createMockCategory();
		when(categoryLookup.findByCategoryAndCatalogCode(CATEGORY_CODE, CATALOG_CODE)).thenReturn(category);
		List<Category> categories = Collections.emptyList();
		shouldGetSubCategories(category, categories);
		shouldGetBean();

		categoryRepository.findChildren(STORE_CODE, CATEGORY_CODE)
				.test()
				.assertValueSequence(categories);
	}

	private void shouldFindStoreWithResult(final Store store) {
		when(storeRepository.findStoreAsSingle(STORE_CODE)).thenReturn(Single.just(store));
	}

	private void shouldNotFindTheStore() {
		when(storeRepository.findStoreAsSingle(STORE_CODE)).thenReturn(Single.error(ResourceOperationFailure.notFound(NOT_FOUND)));
	}

	private void shouldListRootCategories(final Catalog catalog, final List<Category> categories) {
		when(categoryService.listRootCategories(catalog, true)).thenReturn(categories);
	}

	private void shouldGetSubCategories(final Category category, final List<Category> categories) {
		when(categoryLookup.findChildren(category)).thenReturn(categories);
	}

	private void shouldGetBean() {
		when(coreBeanFactory.getBean(ContextIdNames.ORDERING_COMPARATOR)).thenReturn(new OrderingComparatorImpl());
	}

	private Catalog createMockCatalog(final String catalogCode) {
		Catalog catalog = mock(Catalog.class);
		when(catalog.getCode()).thenReturn(catalogCode);

		return catalog;
	}

	private Store createMockStore(final Catalog catalog) {
		Store store = mock(Store.class);
		when(store.getCatalog()).thenReturn(catalog);

		return store;
	}

	private List<Category> createCategoriesList() {
		List<Category> categories = new ArrayList<>();
		Category category1 = new CategoryImpl();
		Category category2 = new CategoryImpl();

		category1.setCode(CATEGORY1_CODE);
		category2.setCode(CATEGORY2_CODE);

		categories.add(category1);
		categories.add(category2);

		return categories;
	}

	private Category createMockCategory() {
		Category category = mock(Category.class);
		category.setCode(CATEGORY_CODE);

		return category;
	}
}
