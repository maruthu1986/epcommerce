/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.impl.addresses;

import io.reactivex.Single;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.definition.shipments.ShipmentIdentifier;
import com.elasticpath.rest.resource.integration.commons.addresses.transform.AddressTransformer;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.ShipmentRepository;

/**
 * Repository that implements reading an address for a shipment.
 *
 * @param <E>	extends AddressEntity
 * @param <I>	extends ShipmentIdentifier
 */
@Component
public class DestinationForShipmentRepositoryImpl<E extends AddressEntity, I extends ShipmentIdentifier> implements
		Repository<AddressEntity, ShipmentIdentifier> {

	private ShipmentRepository shipmentRepository;
	private AddressTransformer addressTransformer;

	@Override
	public Single<AddressEntity> findOne(final ShipmentIdentifier identifier) {
		String shipmentId = identifier.getShipmentId().getValue();
		String purchaseId = identifier.getShipments().getPurchase().getPurchaseId().getValue();
		return shipmentRepository.find(purchaseId, shipmentId)
				.map(orderShipment -> addressTransformer.transformAddressToEntity(orderShipment.getShipmentAddress()));
	}

	@Reference
	public void setShipmentRepository(final ShipmentRepository shipmentRepository) {
		this.shipmentRepository = shipmentRepository;
	}

	@Reference
	public void setAddressTransformer(final AddressTransformer addressTransformer) {
		this.addressTransformer = addressTransformer;
	}
}
