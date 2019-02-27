/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.impl.purchases;

import io.reactivex.Single;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.shipments.ShipmentEntity;
import com.elasticpath.rest.definition.shipments.ShipmentIdentifier;
import com.elasticpath.rest.definition.shipments.StatusEntity;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.ShipmentRepository;

/**
 * Repository For reading shipment.
 * @param <E>	extends ShipmentEntity
 * @param <I>	extends ShipmentIdentifier
 */
@Component
public class ShipmentForPurchaseRepositoryImpl<E extends ShipmentEntity, I extends ShipmentIdentifier>
		implements Repository<ShipmentEntity, ShipmentIdentifier> {

	private ShipmentRepository shipmentRepository;

	@Override
	public Single<ShipmentEntity> findOne(final ShipmentIdentifier identifier) {
		String purchaseId = identifier.getShipments().getPurchase().getPurchaseId().getValue();
		String shipmentId = identifier.getShipmentId().getValue();
		return shipmentRepository.find(purchaseId, shipmentId)
				.map(orderShipment -> buildShipmentEntity(purchaseId, shipmentId, orderShipment));
	}

	private ShipmentEntity buildShipmentEntity(final String purchaseId, final String shipmentId, final PhysicalOrderShipment orderShipment) {
		return ShipmentEntity.builder()
				.withStatus(buildStatusEntity(orderShipment))
				.withPurchaseId(purchaseId)
				.withShipmentId(shipmentId)
				.build();
	}

	private StatusEntity buildStatusEntity(final PhysicalOrderShipment orderShipment) {
		return StatusEntity.builder()
				.withCode(orderShipment.getShipmentStatus().getName())
				.build();
	}

	@Reference
	public void setShipmentRepository(final ShipmentRepository shipmentRepository) {
		this.shipmentRepository = shipmentRepository;
	}
}
