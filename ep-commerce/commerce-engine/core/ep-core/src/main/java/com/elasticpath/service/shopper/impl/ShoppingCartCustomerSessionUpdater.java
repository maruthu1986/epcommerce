/**
 * Copyright (c) Elastic Path Software Inc., 2012
 */
package com.elasticpath.service.shopper.impl;

import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.service.customer.CustomerSessionShopperUpdateHandler;

/**
 * Update the shopping cart with the customer session.
 */
public class ShoppingCartCustomerSessionUpdater implements CustomerSessionShopperUpdateHandler {

	@Override
	public void invalidateShopper(final CustomerSession customerSession, final Shopper shopper) {
		customerSession.getShopper().getCurrentShoppingCart().setCustomerSession(customerSession);
	}

}
