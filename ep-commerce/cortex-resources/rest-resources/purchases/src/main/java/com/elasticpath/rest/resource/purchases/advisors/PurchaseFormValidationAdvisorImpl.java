/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.advisors;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.advise.Message;
import com.elasticpath.rest.definition.purchases.CreatePurchaseFormIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseFormValidationInfoAdvisor;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceService;
import com.elasticpath.rest.resource.integration.epcommerce.repository.purchase.service.OrderPurchasableService;

/**
 * Advisor on the purchase form for validation violations.
 */
public class PurchaseFormValidationAdvisorImpl implements PurchaseFormValidationInfoAdvisor.FormAdvisor {

	private final CreatePurchaseFormIdentifier createPurchaseFormIdentifier;

	private final OrderPurchasableService orderPurchasableService;

	/**
	 * Constructor.
	 *
	 * @param createPurchaseFormIdentifier createPurchaseFormIdentifier
	 * @param orderPurchasableService      orderPurchasableService
	 */
	@Inject
	public PurchaseFormValidationAdvisorImpl(
			@RequestIdentifier final CreatePurchaseFormIdentifier createPurchaseFormIdentifier,
			@ResourceService final OrderPurchasableService orderPurchasableService) {
		this.createPurchaseFormIdentifier = createPurchaseFormIdentifier;
		this.orderPurchasableService = orderPurchasableService;
	}

	@Override
	public Observable<Message> onAdvise() {
		return orderPurchasableService.validateOrderPurchasable(createPurchaseFormIdentifier.getOrder());
	}
}
