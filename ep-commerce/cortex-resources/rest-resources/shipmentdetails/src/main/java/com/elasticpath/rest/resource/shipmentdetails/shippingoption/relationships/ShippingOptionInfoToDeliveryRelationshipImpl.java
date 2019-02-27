/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.shippingoption.relationships;

import java.util.Map;
import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.orders.DeliveriesIdentifier;
import com.elasticpath.rest.definition.orders.DeliveryIdentifier;
import com.elasticpath.rest.definition.orders.OrderIdentifier;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionInfoForDeliveryRelationship;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionInfoIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipmentdetails.ShipmentDetailsConstants;

/**
 * Adds a shipping option info link in delivery.
 */
public class ShippingOptionInfoToDeliveryRelationshipImpl implements ShippingOptionInfoForDeliveryRelationship.LinkFrom {

	private final ShippingOptionInfoIdentifier shippingOptionInfoIdentifier;

	/**
	 * Constructor.
	 *
	 * @param shippingOptionInfoIdentifier	identifier.
	 */
	@Inject
	public ShippingOptionInfoToDeliveryRelationshipImpl(@RequestIdentifier final ShippingOptionInfoIdentifier shippingOptionInfoIdentifier) {
		this.shippingOptionInfoIdentifier = shippingOptionInfoIdentifier;
	}

	@Override
	public Observable<DeliveryIdentifier> onLinkFrom() {
		Map<String, String> shipmentDetailsId = shippingOptionInfoIdentifier.getShipmentDetailsId().getValue();
		IdentifierPart<String> scope = shippingOptionInfoIdentifier.getScope();
		String deliveryId = shipmentDetailsId.get(ShipmentDetailsConstants.DELIVERY_ID);
		String orderId = shipmentDetailsId.get(ShipmentDetailsConstants.ORDER_ID);
		OrderIdentifier orderIdentifier = OrderIdentifier.builder()
				.withScope(scope)
				.withOrderId(StringIdentifier.of(orderId))
				.build();
		DeliveriesIdentifier deliveriesIdentifier = DeliveriesIdentifier.builder()
				.withOrder(orderIdentifier)
				.build();
		return Observable.just(DeliveryIdentifier.builder()
				.withDeliveryId(StringIdentifier.of(deliveryId))
				.withDeliveries(deliveriesIdentifier)
				.build());
	}
}
