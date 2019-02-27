/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.service;

import java.util.ArrayList;
import java.util.List;

import com.elasticpath.cmclient.core.CmSingletonUtil;
import com.elasticpath.cmclient.core.helpers.PaginationChangeListener;

/**
 * Provides notifications of system-wide UI changes.
 */
public class CoreEventService {
	private final List<PaginationChangeListener> paginationListeners = new ArrayList<PaginationChangeListener>();
	
	/**
	 * Returns the session instance of {@link CoreEventService}.
	 * 
	 * @return CoreEventService instance
	 */
	public static CoreEventService getInstance() {
		return  CmSingletonUtil.getSessionInstance(CoreEventService.class);
	}
	
	/**
	 * Adds a pagination listener.
	 *
	 * @param listener the listener
	 */
	public void addPaginationListener(final PaginationChangeListener listener) {
		paginationListeners.add(listener);
	}
	
	/**
	 * Removes a pagination listener from the list of pagination listeners. Does nothing if the
	 * listener is not apart of the list of pagination listeners.
	 *
	 * @param listener the category listener
	 */
	public void removePaginationListener(final PaginationChangeListener listener) {
		paginationListeners.remove(listener);
	}
	
	/**
	 * Notifies all pagination listeners of pagination changed.
	 *
	 * @param newPagination the new pagination
	 */
	public void notifyPaginationChange(final int newPagination) {
		for (PaginationChangeListener listener : paginationListeners) {
			listener.paginationChange(newPagination);
		}
	}
}
