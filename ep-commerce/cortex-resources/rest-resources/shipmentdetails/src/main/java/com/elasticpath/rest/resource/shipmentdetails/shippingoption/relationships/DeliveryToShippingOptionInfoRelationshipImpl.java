/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.shippingoption.relationships;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.shipmentdetails.ShipmentDetailsUtil.createShipmentDetailsId;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.orders.DeliveryIdentifier;
import com.elasticpath.rest.definition.orders.OrderIdentifier;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionInfoForDeliveryRelationship;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionInfoIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.id.type.CompositeIdentifier;

/**
 * Adds a shippingoptioninfo link in delivery.
 */
public class DeliveryToShippingOptionInfoRelationshipImpl implements ShippingOptionInfoForDeliveryRelationship.LinkTo {

	private final DeliveryIdentifier deliveryIdentifier;

	/**
	 * Constructor.
	 *
	 * @param deliveryIdentifier	identifier
	 */
	@Inject
	public DeliveryToShippingOptionInfoRelationshipImpl(@RequestIdentifier final DeliveryIdentifier deliveryIdentifier) {
		this.deliveryIdentifier = deliveryIdentifier;
	}

	@Override
	public Observable<ShippingOptionInfoIdentifier> onLinkTo() {
		OrderIdentifier orderIdentifier = deliveryIdentifier.getDeliveries().getOrder();
		IdentifierPart<String> scope = orderIdentifier.getScope();
		String orderId = orderIdentifier.getOrderId().getValue();
		String shipmentId = deliveryIdentifier.getDeliveryId().getValue();
		return Observable.just(ShippingOptionInfoIdentifier.builder()
				.withScope(scope)
				.withShipmentDetailsId(CompositeIdentifier.of(createShipmentDetailsId(orderId, shipmentId)))
				.build());
	}
}
