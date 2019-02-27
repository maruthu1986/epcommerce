/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.purchase.service;

import io.reactivex.Observable;
import io.reactivex.Single;

import com.elasticpath.rest.advise.Message;
import com.elasticpath.rest.definition.orders.OrderIdentifier;

/**
 * Service that checks if the Order can be purchased.
 */
public interface CartHasItemsService {

	/**
	 * Creates Message which tells if the form should be blocked for the given order.
	 * @param order order
	 * @return message
	 */
	Observable<Message> validateCartHasItems(OrderIdentifier order);

	/**
	 * Checks whether the cart is empty.
	 * @param order order
	 * @return boolean corresponding to whether the cart is empty
	 */
	Single<Boolean> isCartEmpty(OrderIdentifier order);
}
