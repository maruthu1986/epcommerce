/*
 * Copyright © 2015 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.category.impl;

import java.util.Comparator;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.Single;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.store.Store;
import com.elasticpath.rest.cache.CacheResult;
import com.elasticpath.rest.resource.integration.epcommerce.repository.category.CategoryRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.store.StoreRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ReactiveAdapter;
import com.elasticpath.service.catalog.CategoryLookup;
import com.elasticpath.service.catalog.CategoryService;

/**
 * Repository class for general search.
 */
@Singleton
@Named("categoryRepository")
public class CategoryRepositoryImpl implements CategoryRepository {

	/**
	 * Navigation node not found error.
	 */
	static final String NAVIGATION_NODE_WAS_NOT_FOUND = "Navigation node was not found.";
	private final CategoryService categoryService;
	private final CategoryLookup categoryLookup;
	private final StoreRepository storeRepository;
	private final BeanFactory coreBeanFactory;
	private final ReactiveAdapter reactiveAdapter;

	/**
	 * Default constructor.
	 *
	 * @param categoryLookup the category lookup.
	 * @param storeRepository the store repository.
	 * @param coreBeanFactory the core bean factory.
	 * @param categoryService the category service.
	 * @param reactiveAdapter reactive adapter.
	 */
	@Inject
	CategoryRepositoryImpl(
			@Named("categoryLookup")
			final CategoryLookup categoryLookup,
			@Named("storeRepository")
			final StoreRepository storeRepository,
			@Named("coreBeanFactory")
			final BeanFactory coreBeanFactory,
			@Named("categoryService")
			final CategoryService categoryService,
			@Named("reactiveAdapter")
			final ReactiveAdapter reactiveAdapter) {

		this.categoryLookup = categoryLookup;
		this.categoryService = categoryService;
		this.coreBeanFactory = coreBeanFactory;
		this.storeRepository = storeRepository;
		this.reactiveAdapter = reactiveAdapter;

	}

	@Override
	public Observable<Category> findRootCategories(final String storeCode) {
		return storeRepository.findStoreAsSingle(storeCode)
				.map(Store::getCatalog)
				.flatMap(this::getListRootCategories)
				.flatMapObservable(Observable::fromIterable);
	}

	@CacheResult
	private Single<List<Category>> getListRootCategories(final Catalog storeCatalog) {
		return reactiveAdapter.fromServiceAsSingle(() -> categoryService.listRootCategories(storeCatalog, true));
	}

	@Override
	public Single<Category> findByStoreAndCategoryCode(final String storeCode, final String categoryCode) {
		return storeRepository.findStoreAsSingle(storeCode)
				.map(Store::getCatalog)
				.flatMap(catalog -> getByCategoryAndCatalogCode(categoryCode, catalog));
	}

	@CacheResult
	private Single<Category> getByCategoryAndCatalogCode(final String categoryCode, final Catalog catalog) {
		return reactiveAdapter.fromServiceAsSingle(() ->
				categoryLookup.findByCategoryAndCatalogCode(categoryCode, catalog.getCode()),
				NAVIGATION_NODE_WAS_NOT_FOUND);
	}

	@Override
	public Observable<Category> findChildren(final String storeCode, final String parentCategoryCode) {
		Comparator<Category> comparator = coreBeanFactory.getBean(ContextIdNames.ORDERING_COMPARATOR);

		return findByStoreAndCategoryCode(storeCode, parentCategoryCode)
				.flatMap(this::getChildren)
				.flatMapObservable(Observable::fromIterable)
				.sorted(comparator);
	}

	@Override
	@CacheResult
	public Single<Category> findByGuid(final String categoryGuid) {
		return reactiveAdapter.fromServiceAsSingle(() -> categoryLookup.findByGuid(categoryGuid), NAVIGATION_NODE_WAS_NOT_FOUND);
	}

	@CacheResult
	private Single<List<Category>> getChildren(final Category parent) {
		return reactiveAdapter.fromServiceAsSingle(() -> categoryLookup.findChildren(parent));
	}


}
