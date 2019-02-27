/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
/**
 * 
 */
package com.elasticpath.cmclient.reporting.util;

import java.util.ArrayList;
import java.util.List;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.store.StoreService;

/**
 * Provides authorization-related methods needed by reports.
 */
public class ReportAuthorizationUtility {

	/**
	 * Gets a list of store names for which the current user is authorized.  
	 * @return the list of store names for which the current user is authorized
	 */
	public List<String> getAuthorizedStoreNames() {
		List<String> storeNames = new ArrayList<String>();
		for (Store store : getAllStores()) {
			if (isAuthorized(store)) {
				storeNames.add(store.getName());
			}
		}
		return storeNames;
	}

	/**
	 * Gets a list of stores for which the current user is authorized.  
	 * @return the list of stores for which the current user is authorized
	 */
	public List<Store> getAuthorizedStores() {
		List<Store> stores = new ArrayList<Store>();
		for (Store store : getAllStores()) {
			if (isAuthorized(store)) {
				stores.add(store);
			}
		}
		return stores;
	}
	
	/**
	 * Get all the stores known to the system that are not under construction. This implementation uses
	 * the {@link StoreService}.
	 * @return a list of all stores known to the system that are not under construction
	 */
	protected List<Store> getAllStores() {
		StoreService storeService = ServiceLocator.getService(ContextIdNames.STORE_SERVICE);
		return storeService.findAllCompleteStores();
	}
	
	/**
	 * Determines whether the user is authorized for the given store.
	 * This implementation uses the {@link AuthorizationService}.
	 * @param store the store to check
	 * @return true if the current user is authorized for the given store
	 */
	boolean isAuthorized(final Store store) {
		return AuthorizationService.getInstance().isAuthorizedForStore(store);
	}	
	
}
