/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.destinationinfo.prototypes;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.rest.definition.controls.InfoEntity;
import com.elasticpath.rest.definition.shipmentdetails.DestinationInfoIdentifier;
import com.elasticpath.rest.definition.shipmentdetails.DestinationInfoResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipmentdetails.ShipmentDetailsConstants;

/**
 * Read destination info of a delivery.
 */
public class ReadDestinationInfoForDeliveryPrototype implements DestinationInfoResource.Read {

	private final String orderId;

	/**
	 * Constructor.
	 *
	 * @param destinationInfoIdentifier	identifier
	 */
	@Inject
	public ReadDestinationInfoForDeliveryPrototype(@RequestIdentifier final DestinationInfoIdentifier destinationInfoIdentifier) {
		this.orderId = destinationInfoIdentifier.getShipmentDetailsId().getValue().get(ShipmentDetailsConstants.ORDER_ID);
	}

	@Override
	public Single<InfoEntity> onRead() {
		return Single.just(InfoEntity.builder()
				.withName(ShipmentDetailsConstants.DESTINATION_INFO_NAME)
				.withInfoId(orderId)
				.build());
	}
}
