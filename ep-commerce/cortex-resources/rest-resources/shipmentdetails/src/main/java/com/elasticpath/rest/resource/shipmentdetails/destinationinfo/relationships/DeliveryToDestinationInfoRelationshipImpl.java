/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.destinationinfo.relationships;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.shipmentdetails.ShipmentDetailsUtil.createShipmentDetailsId;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.orders.DeliveryIdentifier;
import com.elasticpath.rest.definition.orders.OrderIdentifier;
import com.elasticpath.rest.definition.shipmentdetails.DestinationInfoForDeliveryRelationship;
import com.elasticpath.rest.definition.shipmentdetails.DestinationInfoIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.id.type.CompositeIdentifier;

/**
 * Adds a destinationinfo link in delivery.
 */
public class DeliveryToDestinationInfoRelationshipImpl implements DestinationInfoForDeliveryRelationship.LinkTo {

	private final DeliveryIdentifier deliveryIdentifier;

	/**
	 * Constructor.
	 *
	 * @param deliveryIdentifier	identifier
	 */
	@Inject
	public DeliveryToDestinationInfoRelationshipImpl(@RequestIdentifier final DeliveryIdentifier deliveryIdentifier) {
		this.deliveryIdentifier = deliveryIdentifier;
	}

	@Override
	public Observable<DestinationInfoIdentifier> onLinkTo() {
		OrderIdentifier orderIdentifier = deliveryIdentifier.getDeliveries().getOrder();
		IdentifierPart<String> scope = orderIdentifier.getScope();
		String orderId = orderIdentifier.getOrderId().getValue();
		String shipmentId = deliveryIdentifier.getDeliveryId().getValue();
		return Observable.just(DestinationInfoIdentifier.builder()
				.withScope(scope)
				.withShipmentDetailsId(CompositeIdentifier.of(createShipmentDetailsId(orderId, shipmentId)))
				.build());
	}
}
