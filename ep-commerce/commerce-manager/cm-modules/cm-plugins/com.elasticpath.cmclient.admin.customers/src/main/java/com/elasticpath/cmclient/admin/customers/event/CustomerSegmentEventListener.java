/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.admin.customers.event;

import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.domain.customer.CustomerGroup;

/**
 * This interface must be implemented by part that need to be notified on search result events.
 */
public interface CustomerSegmentEventListener {

	/**
	 * Notifies for a changed customer segment.
	 * 
	 * @param event customer segment change event
	 */
	void customerSegmentChanged(ItemChangeEvent<CustomerGroup> event);
}
