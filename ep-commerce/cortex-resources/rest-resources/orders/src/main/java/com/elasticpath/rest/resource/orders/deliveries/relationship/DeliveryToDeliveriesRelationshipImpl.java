/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.deliveries.relationship;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.orders.DeliveriesIdentifier;
import com.elasticpath.rest.definition.orders.DeliveryIdentifier;
import com.elasticpath.rest.definition.orders.DeliveryToDeliveriesRelationship;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Delivery to deliveries link.
 */
public class DeliveryToDeliveriesRelationshipImpl implements DeliveryToDeliveriesRelationship.LinkTo {

	private final DeliveryIdentifier deliveryIdentifier;

	/**
	 * Constructor.
	 *
	 * @param deliveryIdentifier deliveryIdentifier
	 */
	@Inject
	public DeliveryToDeliveriesRelationshipImpl(@RequestIdentifier final DeliveryIdentifier deliveryIdentifier) {
		this.deliveryIdentifier = deliveryIdentifier;
	}

	@Override
	public Observable<DeliveriesIdentifier> onLinkTo() {
		return Observable.just(deliveryIdentifier.getDeliveries());
	}
}
