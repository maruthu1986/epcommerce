/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.advisors;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.advise.Message;
import com.elasticpath.rest.definition.orders.OrderIdentifier;
import com.elasticpath.rest.definition.orders.OrderValidationInfoAdvisor;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceService;
import com.elasticpath.rest.resource.integration.epcommerce.repository.purchase.service.OrderPurchasableService;

/**
 * Advisor on the order resource for validation violations.
 */
public class OrderValidationAdvisorImpl implements OrderValidationInfoAdvisor.ReadAdvisor {

	private final OrderIdentifier orderIdentifier;

	private final OrderPurchasableService orderPurchasableService;

	/**
	 * Constructor.
	 *
	 * @param orderIdentifier         orderIdentifier
	 * @param orderPurchasableService orderPurchasableService
	 */
	@Inject
	public OrderValidationAdvisorImpl(
			@RequestIdentifier final OrderIdentifier orderIdentifier,
			@ResourceService final OrderPurchasableService orderPurchasableService) {
		this.orderIdentifier = orderIdentifier;
		this.orderPurchasableService = orderPurchasableService;
	}

	@Override
	public Observable<Message> onAdvise() {
		return orderPurchasableService.validateOrderPurchasable(orderIdentifier);
	}
}
