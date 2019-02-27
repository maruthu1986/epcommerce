/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.store.promotions.event;

import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.domain.rules.Rule;

/**
 * This interface must be implemented by part that need to be notified on promotion search result events.
 */
public interface PromotionsEventListener {

	/**
	 * The class implementing that interface will receive an event notifying for new results.
	 * 
	 * @param event instance of {@link SearchResultEvent}
	 */
	void searchResultsUpdate(SearchResultEvent<Rule> event);
	
	/**
	 * The class implementing that interface will receive an event notifying for new results.
	 * 
	 * @param event instance of {@link SearchResultEvent}
	 */
	void couponSearchResultsUpdate(SearchResultEvent<Coupon> event);

	/**
	 * Notifies for a changed promotion.
	 * 
	 * @param event promotions change event
	 */
	void promotionChanged(PromotionsChangeEvent event);
}
