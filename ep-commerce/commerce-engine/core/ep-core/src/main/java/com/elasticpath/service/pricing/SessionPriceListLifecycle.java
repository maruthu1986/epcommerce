/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.pricing;

import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.store.Store;

/**
 * <p>
 * The SessionPriceListLifecycle is responsible for ensuring that
 * the correct PriceListStack is associated with a Shopper/CustomerSession
 * based on their current pricing context.  The pricing context for a
 * Shopper is determined by evaluating their personalisation context (TagSet)
 * against the available price list assignments in a given Store
 * (and more specifically, a catalog within a store).  The pricing context
 * of a Shopper may change when their personalisation context is modified.
 * A change in pricing context makes it necessary to update the PriceListStack
 * for the Shopper.
 * </p>
 */
public interface SessionPriceListLifecycle {

	/**
	 * Refreshes the PriceListStack on the CustomerSession if necessary.
	 * Subsequent calls to refresh the PriceListStack will have no effect
	 * while the pricing context (TagSet) of the CustomerSession remains the constant.
	 * @param customerSession The customer session.
	 * @param store The store that the customer session is in.
	 */
	void refreshPriceListStack(CustomerSession customerSession, Store store);

	/**
	 * Refreshes the PriceListStack on the Shopper if necessary.
	 * Subsequent calls to refresh the PriceListStack will have no effect
	 * while the pricing context (TagSet) of the Shopper remains the constant.
	 * @param shopper The shopper.
	 * @param catalogCode  The catalog that the shopper is in.
	 */
	void refreshPriceListStack(Shopper shopper, String catalogCode);
}
