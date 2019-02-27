/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.integration.epcommerce.repository.purchase.service;

import io.reactivex.Observable;

import com.elasticpath.rest.advise.Message;
import com.elasticpath.rest.definition.orders.OrderIdentifier;

/**
 * Service for determining whether a purchase can be completed.
 */
public interface OrderPurchasableService {

	/**
	 * Validates if order is purchasable returning error messages.
	 *
	 * @param order the order
	 * @return error messages as observable
	 */
	Observable<Message> validateOrderPurchasable(OrderIdentifier order);
}
