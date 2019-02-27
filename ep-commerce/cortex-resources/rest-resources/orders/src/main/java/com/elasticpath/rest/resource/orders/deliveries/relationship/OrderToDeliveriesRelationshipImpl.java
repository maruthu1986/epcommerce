/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.deliveries.relationship;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.orders.DeliveriesForOrderRelationship;
import com.elasticpath.rest.definition.orders.DeliveriesIdentifier;
import com.elasticpath.rest.definition.orders.OrderIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Order to deliveries link.
 */
public class OrderToDeliveriesRelationshipImpl implements DeliveriesForOrderRelationship.LinkTo {

	private final OrderIdentifier orderIdentifier;

	/**
	 * Constructor.
	 *
	 * @param orderIdentifier orderIdentifier
	 */
	@Inject
	public OrderToDeliveriesRelationshipImpl(@RequestIdentifier final OrderIdentifier orderIdentifier) {
		this.orderIdentifier = orderIdentifier;
	}

	@Override
	public Observable<DeliveriesIdentifier> onLinkTo() {
		return Observable.just(DeliveriesIdentifier.builder()
				.withOrder(orderIdentifier)
				.build());
	}
}
