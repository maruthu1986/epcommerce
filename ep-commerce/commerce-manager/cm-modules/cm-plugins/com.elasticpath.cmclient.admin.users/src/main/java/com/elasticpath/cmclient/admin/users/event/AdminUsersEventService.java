/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.admin.users.event;

import java.util.ArrayList;
import java.util.List;

import com.elasticpath.cmclient.core.CmSingletonUtil;
import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.domain.cmuser.CmUser;

/**
 * Event service for sending notifications on occurring events.
 */
public final class AdminUsersEventService {

	private final List<AdminUsersEventListener> adminUsersEventListeners = new ArrayList<>();

	/**
	 * Private constructor following the singleton pattern.
	 */
	private AdminUsersEventService() {
		super();
	}

	/**
	 * Gets a session instance of <code>AdminUsersEventService</code>.
	 * 
	 * @return session instance of <code>AdminUsersEventService</code>
	 */
	public static AdminUsersEventService getInstance() {
		return CmSingletonUtil.getSessionInstance(AdminUsersEventService.class);
	}

	/**
	 * Registers a <code>AdminUsersEventListener</code> listener.
	 * 
	 * @param listener the admin user event listener
	 */
	public void registerUserEventListener(final AdminUsersEventListener listener) {
		if (!adminUsersEventListeners.contains(listener)) {
			adminUsersEventListeners.add(listener);
		}
	}

	/**
	 * Unregisters a <code>AdminUsersEventListener</code> listener.
	 * 
	 * @param listener the admin user event listener
	 */
	public void unregisterUserEventListener(final AdminUsersEventListener listener) {
		if (adminUsersEventListeners.contains(listener)) {
			adminUsersEventListeners.remove(listener);
		}
	}

	/**
	 * Notifies all the listeners with a <code>SearchResultEvent</code> event.
	 * 
	 * @param event the search result event
	 */
	public void fireUsersSearchResultEvent(final SearchResultEvent<CmUser> event) {
		for (final AdminUsersEventListener eventListener : adminUsersEventListeners) {
			eventListener.searchResultsUpdate(event);
		}
	}
	
}
