/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.store.promotions.event;

import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.domain.rules.CouponUsage;

/**
 * Interface to listen to changes to coupons.
 */
public interface CouponEventListener {

	/**
	 * The class implementing that interface will receive an event notifying for new results.
	 * 
	 * @param event instance of {@link SearchResultEvent}
	 */
	void searchResultsUpdate(SearchResultEvent<CouponUsage> event);
}
