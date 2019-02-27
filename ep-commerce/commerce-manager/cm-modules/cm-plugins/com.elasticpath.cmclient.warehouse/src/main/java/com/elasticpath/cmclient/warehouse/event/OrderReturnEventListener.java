/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.warehouse.event;

import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.domain.order.OrderReturn;

/**
 * This interface must be implemented by part that need to be notified on search result events.
 */
public interface OrderReturnEventListener {

	/**
	 * The class implementing that interface will receive an event notifying for new results.
	 * 
	 * @param event instance of {@link SearchResultEvent}
	 */
	void searchResultsUpdate(SearchResultEvent<OrderReturn> event);

	/**
	 * Notifies for a changed order return.
	 * 
	 * @param event order change event
	 */
	void orderReturnChanged(OrderReturnChangeEvent event);
}
