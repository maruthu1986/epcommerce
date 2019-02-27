/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.advancedsearch.service;

import java.util.HashMap;
import java.util.Map;

import com.elasticpath.cmclient.advancedsearch.helpers.AdvancedSearchProductListener;
import com.elasticpath.cmclient.core.CmSingletonUtil;
import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.domain.catalog.Product;

/**
 * The <code>AdvancedSearchEventService</code> provides event notification services for the advanced search functionality.
 */
public final class AdvancedSearchEventService {
	
	private final Map<String, AdvancedSearchProductListener> advancedSearchProductListeners = new HashMap<>();
	
	private AdvancedSearchEventService() {
		//private empty constructor
	}
	
	/**
	 * Returns the session instance of {@link AdvancedSearchEventService}.
	 * 
	 * @return AdvancedSearchEventService instance
	 */
	public static AdvancedSearchEventService getInstance() {
		return CmSingletonUtil.getSessionInstance(AdvancedSearchEventService.class);
	}
	
	/**
	 * Adds advanced search product listener.
	 * 
	 * @param listenerId the unique id for product listener, this id uses for sending notification to specific listener.
	 * @param listener the listener
	 */
	public void addAdvancedSearchProductListener(final String listenerId, final AdvancedSearchProductListener listener) {
		advancedSearchProductListeners.put(listenerId, listener);
	}
	
	/**
	 * Removes advanced search product listener by id.
	 * 
	 * @param listenerId the unique product listener id
	 */
	public void removeAdvancedSearchProductListener(final String listenerId) {
		advancedSearchProductListeners.remove(listenerId);
	}
	
	/**
	 * Notifies about advanced search result.
	 *
	 * @param listenerId the listener id that should be notified, in case of null argument all listeners will be notified
	 * @param searchResultsReturnedEvent the event
	 */
	public void notifyProductSearchResultReturned(final String listenerId, final SearchResultEvent<Product> searchResultsReturnedEvent) {
		if (listenerId == null) {
			for (final AdvancedSearchProductListener currListener : advancedSearchProductListeners.values()) {
				currListener.productAdvancedSearchResultReturned(searchResultsReturnedEvent);
			}
			return;
		}
		
		AdvancedSearchProductListener searchProductListener = advancedSearchProductListeners.get(listenerId);
		if (searchProductListener != null) {
			searchProductListener.productAdvancedSearchResultReturned(searchResultsReturnedEvent);
		}
	}
}
