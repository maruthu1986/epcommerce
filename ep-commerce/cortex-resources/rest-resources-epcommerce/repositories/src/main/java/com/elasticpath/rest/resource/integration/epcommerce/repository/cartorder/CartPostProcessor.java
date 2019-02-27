/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder;

import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;

/**
 * Performs post processing on carts.
 */
public interface CartPostProcessor {

	/**
	 * Sets the customer session on the cart, and does the after retrieval updates on the cart according to
	 * {@link com.elasticpath.service.shoppingcart.impl.ShoppingCartServiceImpl#findByGuid(String)}.
	 *
	 * @param cart            the cart
	 * @param shopper         the shopper
	 * @param customerSession the customer session
	 */
	void postProcessCart(ShoppingCart cart, Shopper shopper, CustomerSession customerSession);
}
