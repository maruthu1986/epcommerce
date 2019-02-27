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
 * Deliveries to order link.
 */
public class DeliveriesToOrderRelationshipImpl implements DeliveriesForOrderRelationship.LinkFrom {

	private final DeliveriesIdentifier deliveriesIdentifier;

	/**
	 * Constructor.
	 *
	 * @param deliveriesIdentifier deliveriesIdentifier
	 */
	@Inject
	public DeliveriesToOrderRelationshipImpl(@RequestIdentifier final DeliveriesIdentifier deliveriesIdentifier) {
		this.deliveriesIdentifier = deliveriesIdentifier;
	}

	@Override
	public Observable<OrderIdentifier> onLinkFrom() {
		return Observable.just(deliveriesIdentifier.getOrder());
	}
}
