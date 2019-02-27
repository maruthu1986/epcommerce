/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.persister;

import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.domain.builder.shopper.ShoppingContext;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.service.customer.CustomerSessionService;
import com.elasticpath.service.shopper.ShopperService;

/**
 * Persister that persists an associated {@link com.elasticpath.domain.customer.CustomerSession} and {@link com.elasticpath.domain.shopper.Shopper}.
 */
public class ShoppingContextPersister implements Persister<ShoppingContext> {

	@Autowired
	private CustomerSessionService customerSessionService;

	@Autowired
	private ShopperService shopperService;

	@Override
	public void persist(final ShoppingContext shoppingContext) {
		final Shopper shopper = shoppingContext.getShopper();
		final CustomerSession customerSession = shoppingContext.getCustomerSession();

		shopperService.save(shopper);

		customerSession.setShopper(shopper);

		customerSessionService.add(customerSession);
	}

}
