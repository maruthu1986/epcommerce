/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.fulfillment.event;

import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.domain.order.Order;

/**
 * This interface must be implemented by part that need to be notified on search result events.
 */
public interface OrderEventListener {

	/**
	 * The class implementing that interface will receive an event notifying for new results.
	 * 
	 * @param event instance of {@link SearchResultEvent}
	 */
	void searchResultsUpdate(SearchResultEvent<Order> event);

	/**
	 * Notifies for a changed order.
	 * 
	 * @param event order change event
	 */
	void orderChanged(ItemChangeEvent<Order> event);
}
