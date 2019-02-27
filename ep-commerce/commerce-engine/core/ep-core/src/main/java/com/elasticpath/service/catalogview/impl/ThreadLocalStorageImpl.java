/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.service.catalogview.impl;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.cache.SimpleTimeoutCache;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.catalogview.StoreConfig;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.domain.SettingValue;

/**
 * A store configuration that provides a store based on the store previously
 * selected for the current thread.
 */
public class ThreadLocalStorageImpl implements StoreConfig {
	
	private final ThreadLocal<String> tlStoreCode = new ThreadLocal<>();

	//injected via Spring
	private SimpleTimeoutCache<String, Store> storeCache;
	private SettingsReader settingsService;
	private StoreService storeService;
		
	/**
	 * Returns the store for the current thread.
	 * 
	 * This implementation only retrieves the store once from the storeService
	 * and then returns a local copy of it on subsequent calls.
	 * Calls {@link #getStoreCode()}.
	 * 
	 * @return The appropriate store for the current application.
	 * @throws EpServiceException if a store cannot be found or the store code has not been
	 *         set for this thread.
	 */
	@Override
	public Store getStore() {
		
		String storeCode = getStoreCode();
		if (storeCode == null) {
			throw new EpServiceException("Store code has not been set");
		}
		
		Store store = storeCache.get(storeCode);

		if (store == null) {
			store = storeService.findStoreWithCode(tlStoreCode.get());
			storeCache.put(storeCode, store);
		}
		
		if (store == null) {
			throw new EpServiceException("Store not found.");
		}
		
		return store;
	}
	
	/**
	 * Returns the code for the Store associated with this configuration.
	 * 
	 * @return the code for the store associated with this store configuration
	 */
	@Override
	public String getStoreCode() {
		return tlStoreCode.get();
	}

	/**
	 * Sets the storeCode for the current thread to be used by getStore() to return the store.
	 * @param storeCode the storeCode for the current thread.
	 */
	public void setStoreCode(final String storeCode) {
		tlStoreCode.set(storeCode);
	}
	
	/**
	 * @param storeService the service from which to retrieve the store.
	 */
	public void setStoreService(final StoreService storeService) {
		this.storeService = storeService;
	}	
	
	/**
	 * @param settingsService the service from which to retrieve settings for the threadlocal store.
	 */
	public void setSettingsService(final SettingsReader settingsService) {
		this.settingsService = settingsService;
	}

	@Override
	@Deprecated
	public SettingValue getSetting(final String path) {
		if (getStoreCode() == null) {
			throw new EpServiceException("StoreCode has not been set in this thread.");
		}
		return settingsService.getSettingValue(path, getStoreCode());
	}

	public SimpleTimeoutCache<String, Store> getStoreCache() {
		return storeCache;
	}

	public void setStoreCache(final SimpleTimeoutCache<String, Store> storeCache) {
		this.storeCache = storeCache;
	}
}
