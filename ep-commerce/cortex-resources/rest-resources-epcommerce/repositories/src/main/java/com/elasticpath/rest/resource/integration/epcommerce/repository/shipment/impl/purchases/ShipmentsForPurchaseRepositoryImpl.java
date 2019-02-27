/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.impl.purchases;

import io.reactivex.Observable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.purchases.PurchaseIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentsIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.ShipmentRepository;

/**
 * Repository for finding all shipments for a purchase.
 *
 * @param <I>	extends PurchaseIdentifier
 * @param <IE>	extends ShipmentIdentifier
 */
@Component
public class ShipmentsForPurchaseRepositoryImpl<I extends PurchaseIdentifier, IE extends ShipmentIdentifier>
		implements LinksRepository<PurchaseIdentifier, ShipmentIdentifier> {

	private ShipmentRepository shipmentRepository;

	@Override
	public Observable<ShipmentIdentifier> getElements(final PurchaseIdentifier identifier) {
		String scope = identifier.getPurchases().getScope().getValue();
		String purchaseId = identifier.getPurchaseId().getValue();
		return shipmentRepository.findAll(scope, purchaseId)
				.map(orderShipment -> buildShipmentIdentifier(identifier, orderShipment));
	}

	private ShipmentIdentifier buildShipmentIdentifier(final PurchaseIdentifier identifier, final PhysicalOrderShipment orderShipment) {
		return ShipmentIdentifier.builder()
				.withShipments(ShipmentsIdentifier.builder().withPurchase(identifier).build())
				.withShipmentId(StringIdentifier.of(orderShipment.getShipmentNumber()))
				.build();
	}

	@Reference
	public void setShipmentRepository(final ShipmentRepository shipmentRepository) {
		this.shipmentRepository = shipmentRepository;
	}
}
