/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.domain.builder.shopper;

import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shopper.Shopper;

/**
 * Container for an associated {@link com.elasticpath.domain.customer.CustomerSession} and {@link com.elasticpath.domain.shopper.Shopper}.
 */
public class ShoppingContext {

	private final CustomerSession customerSession;
	private final Shopper shopper;

	/**
	 * Constructor.
	 *
	 * @param customerSession the CustomerSession
	 * @param shopper the Shopper
	 */
	public ShoppingContext(final CustomerSession customerSession, final Shopper shopper) {
		this.customerSession = customerSession;
		this.shopper = shopper;
	}

	public CustomerSession getCustomerSession() {
		return customerSession;
	}

	public Shopper getShopper() {
		return shopper;
	}

}
