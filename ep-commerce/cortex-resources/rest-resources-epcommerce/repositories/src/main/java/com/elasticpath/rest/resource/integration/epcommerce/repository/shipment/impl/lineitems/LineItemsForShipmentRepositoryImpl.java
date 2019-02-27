/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.impl.lineitems;

import io.reactivex.Observable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.purchases.PurchaseIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemsIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.ShipmentRepository;

/**
 * Repository for reading lineitems of a shipment.
 * @param <I>	extends ShipmentIdentifier
 * @param <LI>	extends ShipmentLineItemIdentifier
 */
@Component
public class LineItemsForShipmentRepositoryImpl<I extends ShipmentIdentifier, LI extends ShipmentLineItemIdentifier>
		implements LinksRepository<ShipmentIdentifier, ShipmentLineItemIdentifier> {

	private ShipmentRepository shipmentRepository;

	@Override
	public Observable<ShipmentLineItemIdentifier> getElements(final ShipmentIdentifier identifier) {
		PurchaseIdentifier purchaseIdentifier = identifier.getShipments().getPurchase();
		String purchaseId = purchaseIdentifier.getPurchaseId().getValue();
		String shipmentId = identifier.getShipmentId().getValue();
		String scope = purchaseIdentifier.getPurchases().getScope().getValue();
		return shipmentRepository.getOrderSkusForShipment(scope, purchaseId, shipmentId)
				.map(orderSku -> buildShipmentLineItemIdentifier(identifier, orderSku));
	}

	private ShipmentLineItemIdentifier buildShipmentLineItemIdentifier(final ShipmentIdentifier identifier, final OrderSku orderSku) {
		return ShipmentLineItemIdentifier.builder()
				.withShipmentLineItems(ShipmentLineItemsIdentifier.builder().withShipment(identifier).build())
				.withShipmentLineItemId(StringIdentifier.of(orderSku.getGuid()))
				.build();
	}

	@Reference
	public void setShipmentRepository(final ShipmentRepository shipmentRepository) {
		this.shipmentRepository = shipmentRepository;
	}
}
