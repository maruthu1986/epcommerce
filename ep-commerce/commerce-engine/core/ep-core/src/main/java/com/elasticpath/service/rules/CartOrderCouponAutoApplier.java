/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.rules;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.store.Store;
/**
 * Adapter interface to filter and auto apply coupons to a cart order.
 */
public interface CartOrderCouponAutoApplier {
	/**
	 * Filter existing coupons on cart order and auto apply new coupons.
	 *
	 * @param cartOrder cart order.
	 * @param store the store
	 * @param customerEmailAddress the customer email address
	 * @return true if cart order is updated, false otherwise.
	 */
	boolean filterAndAutoApplyCoupons(CartOrder cartOrder, Store store, String customerEmailAddress);
	
}
