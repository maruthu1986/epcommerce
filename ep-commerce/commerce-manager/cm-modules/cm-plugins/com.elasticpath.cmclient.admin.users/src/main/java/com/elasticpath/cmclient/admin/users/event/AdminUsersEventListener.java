/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.users.event;

import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.domain.cmuser.CmUser;

/**
 * This interface must be implemented by part that need to be notified on user search result events.
 */
public interface AdminUsersEventListener {

	/**
	 * The class implementing that interface will receive an event notifying for new results.
	 * 
	 * @param event instance of {@link SearchResultEvent}
	 */
	void searchResultsUpdate(SearchResultEvent<CmUser> event);
}
