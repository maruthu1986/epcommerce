/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.service.store;

import java.util.Collection;
import java.util.List;

import com.elasticpath.domain.store.Store;

/**
 *	Used to retrieve Stores.
 */
public interface StoreRetrieveStrategy {

	/**
	 * Retrieve a <code>store</code> with the given store uid. Return <code>null</code> if
	 * a store with the given uid doesn't exist.
	 * <p>
	 *
	 * @param storeUid a store uid
	 * @return a <code>store</code> with the given store uid.
	 */
	Store retrieveStore(long storeUid);

	/**
	 * Retrieve a list of <code>store</code> of the given store uids.
	 *
	 * @param storeUids a collection of store uids
	 * @return a list of <code>store</code>s.
	 */
	List<Store> retrieveStores(Collection<Long> storeUids);

	/**
	 * Retrieve a <code>store</code> with the given store code. Return <code>null</code> if
	 * a store with the given code doesn't exist.
	 * <p>
	 *
	 * @param storeCode a store code
	 * @return a <code>store</code> with the given store code.
	 */
	Store retrieveStore(String storeCode);

	/**
	 * Adds a Store to the cache.
	 * @param store the Store to cache.
	 */
	void cacheStore(Store store);
}
