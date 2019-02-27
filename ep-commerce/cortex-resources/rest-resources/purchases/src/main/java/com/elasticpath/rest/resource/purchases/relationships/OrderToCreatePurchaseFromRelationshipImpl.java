/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.orders.OrderIdentifier;
import com.elasticpath.rest.definition.purchases.CreatePurchaseFormFromOrderRelationship;
import com.elasticpath.rest.definition.purchases.CreatePurchaseFormIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Orders to create purchase form link.
 */
public class OrderToCreatePurchaseFromRelationshipImpl implements CreatePurchaseFormFromOrderRelationship.LinkTo {

	private final OrderIdentifier orderIdentifier;

	/**
	 * Constructor.
	 *
	 * @param orderIdentifier orderIdentifier
	 */
	@Inject
	public OrderToCreatePurchaseFromRelationshipImpl(@RequestIdentifier final OrderIdentifier orderIdentifier) {
		this.orderIdentifier = orderIdentifier;
	}

	@Override
	public Observable<CreatePurchaseFormIdentifier> onLinkTo() {
		return Observable.just(CreatePurchaseFormIdentifier.builder()
				.withOrder(orderIdentifier)
				.build());
	}
}
