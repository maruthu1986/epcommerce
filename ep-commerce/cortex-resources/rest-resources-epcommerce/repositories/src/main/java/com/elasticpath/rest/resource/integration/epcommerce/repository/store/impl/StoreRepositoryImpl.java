/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.store.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import io.reactivex.Single;

import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.StoreState;
import com.elasticpath.rest.cache.CacheResult;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.ExecutionResultChain;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.store.StoreRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ReactiveAdapter;
import com.elasticpath.service.store.StoreService;

/**
 * A repository for {@link Store}s.
 */
@Singleton
@Named("storeRepository")
public class StoreRepositoryImpl implements StoreRepository {

	private static final String STORE_NOT_FOUND = "Store with code %s does not exist";

	private final StoreService storeService;
	private final ReactiveAdapter reactiveAdapter;

	/**
	 * Default constructor.
	 *
	 * @param storeService    the storeService
	 * @param reactiveAdapter the reactive adapter
	 */
	@Inject
	public StoreRepositoryImpl(
			@Named("storeService") final StoreService storeService,
			@Named("reactiveAdapter") final ReactiveAdapter reactiveAdapter) {

		this.storeService = storeService;
		this.reactiveAdapter = reactiveAdapter;
	}

	@Override
	public ExecutionResult<Boolean> isStoreCodeEnabled(final String storeCode) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				Store store = Assign.ifSuccessful(findStore(storeCode));
				Boolean enabled = store.isEnabled() && StoreState.OPEN.equals(store.getStoreState());
				return ExecutionResultFactory.createReadOK(enabled);
			}
		}.execute();
	}

	@Override
	@CacheResult
	public ExecutionResult<Store> findStore(final String storeCode) {

		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				Store store = Assign.ifNotNull(storeService.findStoreWithCode(storeCode),
						OnFailure.returnNotFound(STORE_NOT_FOUND, storeCode));
				return ExecutionResultFactory.createReadOK(store);
			}
		}.execute();
	}

	@Override
	@CacheResult
	public Single<Store> findStoreAsSingle(final String storeCode) {
		return reactiveAdapter.fromServiceAsSingle(() -> storeService.findStoreWithCode(storeCode), String.format(STORE_NOT_FOUND, storeCode));
	}
}
