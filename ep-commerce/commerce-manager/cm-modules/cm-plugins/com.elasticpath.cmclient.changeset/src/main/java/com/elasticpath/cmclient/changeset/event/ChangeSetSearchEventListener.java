/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.changeset.event;

import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.domain.changeset.ChangeSet;

/**
 * This interface must be implemented by part that needs to be notified on change set searches.
 */
public interface ChangeSetSearchEventListener {	
	
	/**
	 * Change set search update.
	 * @param event is the search result with change set return type.
	 */
	void changeSetSearchUpdated(SearchResultEvent<ChangeSet> event);
}
