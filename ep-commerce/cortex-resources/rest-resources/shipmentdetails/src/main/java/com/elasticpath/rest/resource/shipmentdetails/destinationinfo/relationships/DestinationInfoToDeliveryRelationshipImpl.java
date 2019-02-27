/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.destinationinfo.relationships;

import java.util.Map;
import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.orders.DeliveriesIdentifier;
import com.elasticpath.rest.definition.orders.DeliveryIdentifier;
import com.elasticpath.rest.definition.orders.OrderIdentifier;
import com.elasticpath.rest.definition.shipmentdetails.DestinationInfoForDeliveryRelationship;
import com.elasticpath.rest.definition.shipmentdetails.DestinationInfoIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipmentdetails.ShipmentDetailsConstants;

/**
 * Adds a delivery link in destination info.
 */
public class DestinationInfoToDeliveryRelationshipImpl implements DestinationInfoForDeliveryRelationship.LinkFrom {

	private final DestinationInfoIdentifier destinationInfoIdentifier;

	/**
	 * Constructor.
	 *
	 * @param destinationInfoIdentifier	identifier.
	 */
	@Inject
	public DestinationInfoToDeliveryRelationshipImpl(@RequestIdentifier final DestinationInfoIdentifier destinationInfoIdentifier) {
		this.destinationInfoIdentifier = destinationInfoIdentifier;
	}

	@Override
	public Observable<DeliveryIdentifier> onLinkFrom() {
		Map<String, String> shipmentDetailsId = destinationInfoIdentifier.getShipmentDetailsId().getValue();
		OrderIdentifier orderIdentifier = OrderIdentifier.builder()
				.withOrderId(StringIdentifier.of(shipmentDetailsId.get(ShipmentDetailsConstants.ORDER_ID)))
				.withScope(destinationInfoIdentifier.getScope())
				.build();
		DeliveriesIdentifier deliveriesIdentifier = DeliveriesIdentifier.builder()
				.withOrder(orderIdentifier)
				.build();
		String deliveryId = shipmentDetailsId.get(ShipmentDetailsConstants.DELIVERY_ID);
		return Observable.just(DeliveryIdentifier.builder()
				.withDeliveries(deliveriesIdentifier)
				.withDeliveryId(StringIdentifier.of(deliveryId))
				.build());
	}
}
