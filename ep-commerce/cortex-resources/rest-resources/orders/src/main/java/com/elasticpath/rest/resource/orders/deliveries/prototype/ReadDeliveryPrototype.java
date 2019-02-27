/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.deliveries.prototype;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.rest.definition.orders.DeliveryEntity;
import com.elasticpath.rest.definition.orders.DeliveryIdentifier;
import com.elasticpath.rest.definition.orders.DeliveryResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Read operation for the delivery.
 */
public class ReadDeliveryPrototype implements DeliveryResource.Read {

	private final DeliveryIdentifier deliveryidentifier;

	/**
	 * Constructor.
	 *
	 * @param deliveryIdentifier deliveryIdentifier
	 */
	@Inject
	public ReadDeliveryPrototype(@RequestIdentifier final DeliveryIdentifier deliveryIdentifier) {
		this.deliveryidentifier = deliveryIdentifier;
	}

	@Override
	public Single<DeliveryEntity> onRead() {
		String orderId = deliveryidentifier.getDeliveries().getOrder().getOrderId().getValue();
		String deliveryId = deliveryidentifier.getDeliveryId().getValue();

		//Delivery id and type are the same things
		return Single.just(DeliveryEntity.builder()
				.withDeliveryId(deliveryId)
				.withDeliveryType(deliveryId)
				.withOrderId(orderId)
				.build());
	}
}
