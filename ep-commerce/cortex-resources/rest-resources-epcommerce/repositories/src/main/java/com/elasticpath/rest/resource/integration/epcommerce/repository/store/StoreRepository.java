/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.store;

import io.reactivex.Single;

import com.elasticpath.domain.store.Store;
import com.elasticpath.rest.command.ExecutionResult;

/**
 * A repository for {@link Store}s.
 */
public interface StoreRepository {

	/**
	 * Find store by store code.
	 *
	 * @param storeCode store code
	 * @return store
	 */
	ExecutionResult<Store> findStore(String storeCode);

	/**
	 * Find store by store code.
	 *
	 * @param storeCode store code
	 * @return store
	 */
	Single<Store> findStoreAsSingle(String storeCode);
	
	/**
	 * Checks if storecode is enabled.
	 *
	 * @param storeCode a storeCode.
	 * @return true if store is enabled.
	 */
	ExecutionResult<Boolean> isStoreCodeEnabled(String storeCode);
}
