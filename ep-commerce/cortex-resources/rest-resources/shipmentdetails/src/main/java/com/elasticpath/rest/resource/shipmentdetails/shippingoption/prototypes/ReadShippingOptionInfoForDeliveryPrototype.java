/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.shippingoption.prototypes;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.rest.definition.controls.InfoEntity;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionInfoIdentifier;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionInfoResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipmentdetails.ShipmentDetailsConstants;

/**
 * Read shipping option info for a delivery.
 */
public class ReadShippingOptionInfoForDeliveryPrototype implements ShippingOptionInfoResource.Read {

	private final String orderId;

	/**
	 * Constructor.
	 *
	 * @param shippingOptionInfoIdentifier	identifier
	 */
	@Inject
	public ReadShippingOptionInfoForDeliveryPrototype(@RequestIdentifier final ShippingOptionInfoIdentifier shippingOptionInfoIdentifier) {
		this.orderId = shippingOptionInfoIdentifier.getShipmentDetailsId().getValue().get(ShipmentDetailsConstants.ORDER_ID);
	}

	@Override
	public Single<InfoEntity> onRead() {
		return Single.just(InfoEntity.builder()
				.withName(ShipmentDetailsConstants.SHIPPING_OPTION_INFO_NAME)
				.withInfoId(orderId)
				.build());
	}
}
