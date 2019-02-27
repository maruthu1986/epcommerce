/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.store.shipping.events;

import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.domain.shipping.ShippingServiceLevel;

/**
 * This interface must be implemented by part that need to be notified on shipping service level search result events.
 */
public interface ShippingLevelsEventListener {

	/**
	 * The class implementing that interface will receive an event notifying for new results.
	 * 
	 * @param event instance of {@link SearchResultEvent}
	 */
	void searchResultsUpdate(SearchResultEvent<ShippingServiceLevel> event);
}
