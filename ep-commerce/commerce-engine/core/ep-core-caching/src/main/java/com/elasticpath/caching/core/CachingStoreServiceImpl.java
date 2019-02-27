/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.caching.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.StoreState;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.store.StoreRetrieveStrategy;
import com.elasticpath.service.store.StoreService;

/**
 * Extension of the StoreService that allows caching.
 */
public class CachingStoreServiceImpl implements StoreService {

	private StoreRetrieveStrategy storeCache;
	private StoreService fallbackStoreService;

	/**
	 * When caching is involved, ignore the load tuner and cache the whole thing.
	 *
	 * @param storeUid the <code>Store</code> UID
	 * @param loadTuner the load tuner to be ignored
	 * @return the store
	 * @throws EpServiceException in case of error
	 */
	@Override
	public Store getTunedStore(final long storeUid,  final FetchGroupLoadTuner loadTuner) throws EpServiceException {
		return getStore(storeUid);
	}

	/**
	 * When caching is involved, ignore the load tuner and cache the whole thing.
	 *
	 * @param storeCode the <code>Store</code> code
	 * @param loadTuner the load tuner to be ignored
	 * @return the store
	 * @throws EpServiceException in case of error
	 */
	@Override
	public Store getTunedStore(final String storeCode, final FetchGroupLoadTuner loadTuner) throws EpServiceException {
		return findStoreWithCode(storeCode);
	}

	/**
	 * When caching is involved, ignore the load tuner and cache the whole thing.
	 *
	 * @param storeUids a collection of store UIDs
	 * @param loadTuner the load tuner to ignore
	 * @return the collection of stores
	 * @throws EpServiceException
	 */
	@Override
	public Collection<Store> getTunedStores(final Collection<Long> storeUids, final FetchGroupLoadTuner loadTuner) throws EpServiceException {
		final List<Long> uidsToFind = new ArrayList<>();
		uidsToFind.addAll(storeUids);

		final Collection<Store> stores = getStoreCache().retrieveStores(storeUids);
		for (final Store store : stores) {
			uidsToFind.remove(store.getUidPk());
		}

		if (!uidsToFind.isEmpty()) {
			final Collection<Store> storesFromDb = getFallbackStoreService().getTunedStores(uidsToFind, null);
			for (final Store store : storesFromDb) {
				final Store cached = cacheStore(store);
				stores.add(cached);
			}
		}

		return stores;
	}

	@Override
	public Collection<String> findAllSupportedCreditCardTypes() {
		return getFallbackStoreService().findAllSupportedCreditCardTypes();
	}

	@Override
	public String findValidStoreCode(final String storeCode) {
		return getFallbackStoreService().findValidStoreCode(storeCode);
	}

	@Override
	public Store saveOrUpdate(final Store store) throws EpServiceException {
		return getFallbackStoreService().saveOrUpdate(store);
	}

	@Override
	public void remove(final Store store) throws EpServiceException {
		getFallbackStoreService().remove(store);
	}

	@Override
	public Store getStore(final long storeUid) throws EpServiceException {
		Store store = getStoreCache().retrieveStore(storeUid);
		if (store == null) {
			store = getFallbackStoreService().getStore(storeUid);
			store = cacheStore(store);
		}
		return store;
	}

	@Override
	public boolean isStoreUrlUniqueForState(final Store store, final StoreState state) throws EpServiceException {
		return getFallbackStoreService().isStoreUrlUniqueForState(store, state);
	}

	@Override
	public Store findStoreWithCode(final String storeCode) throws EpServiceException {
		Store store = getStoreCache().retrieveStore(storeCode);
		if (store == null) {
			store = getFallbackStoreService().findStoreWithCode(storeCode);
			store = cacheStore(store);
		}
		return store;
	}

	@Override
	public String getCatalogCodeForStore(final String storeCode) throws EpServiceException {
		return getFallbackStoreService().getCatalogCodeForStore(storeCode);
	}

	@Override
	public List<Long> findAllCompleteStoreUids() throws EpServiceException {
		return getFallbackStoreService().findAllCompleteStoreUids();
	}

	@Override
	public List<Store> findAllCompleteStores() throws EpServiceException {
		return getFallbackStoreService().findAllCompleteStores();
	}

	@Override
	public List<Store> findAllCompleteStores(final FetchGroupLoadTuner loadTuner) throws EpServiceException {
		return getFallbackStoreService().findAllCompleteStores(loadTuner);
	}

	@Override
	public List<Long> findAllStoreUids() throws EpServiceException {
		return getFallbackStoreService().findAllStoreUids();
	}

	@Override
	public List<Store> findAllStores() throws EpServiceException {
		return getFallbackStoreService().findAllStores();
	}

	@Override
	public List<Store> findAllStores(final FetchGroupLoadTuner loadTuner) throws EpServiceException {
		return getFallbackStoreService().findAllStores(loadTuner);
	}

	@Override
	public List<Store> findAllStores(final CmUser user) throws EpServiceException {
		return getFallbackStoreService().findAllStores(user);
	}

	@Override
	public boolean storeInUse(final long storeUidPk) throws EpServiceException {
		return getFallbackStoreService().storeInUse(storeUidPk);
	}

	@Override
	public Collection<Store> findStoresWithCatalogUids(final Collection<Long> catalogUids) {
		return getFallbackStoreService().findStoresWithCatalogUids(catalogUids);
	}

	@Override
	public Collection<Store> findStoresWithCatalogCode(final String catalogCode) {
		return getFallbackStoreService().findStoresWithCatalogCode(catalogCode);
	}

	private Store cacheStore(final Store store) {
		Store detached = getPersistenceEngine().detach(store);
		getStoreCache().cacheStore(detached);

		return detached;
	}

	public void setStoreCache(final StoreRetrieveStrategy storeCache) {
		this.storeCache = storeCache;
	}

	protected StoreRetrieveStrategy getStoreCache() {
		return storeCache;
	}

	public StoreService getFallbackStoreService() {
		return fallbackStoreService;
	}

	public void setFallbackStoreService(final StoreService fallbackStoreService) {
		this.fallbackStoreService = fallbackStoreService;
	}

	@Override
	public void setPersistenceEngine(final PersistenceEngine persistenceEngine) {
		getFallbackStoreService().setPersistenceEngine(persistenceEngine);
	}

	@Override
	public PersistenceEngine getPersistenceEngine() {
		return getFallbackStoreService().getPersistenceEngine();
	}

	@Override
	public Object getObject(final long uid) throws EpServiceException {
		return getStore(uid);
	}

	@Override
	public Object getObject(final long uid, final Collection<String> fieldsToLoad) throws EpServiceException {
		return getStore(uid);
	}
}
