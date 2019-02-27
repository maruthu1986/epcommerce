/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.impl.lineitems;

import io.reactivex.Single;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.purchases.PurchaseIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemEntity;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.ShipmentRepository;

/**
 * Repository for reading line item.
 *
 * @param <E> extends ShipmentLineItemEntity
 * @param <I> extends ShipmentIdentifier
 */
@Component
public class LineItemForShipmentRepositoryImpl<E extends ShipmentLineItemEntity, I extends ShipmentLineItemIdentifier>
		implements Repository<ShipmentLineItemEntity, ShipmentLineItemIdentifier> {

	private ShipmentRepository shipmentRepository;

	@Override
	public Single<ShipmentLineItemEntity> findOne(final ShipmentLineItemIdentifier identifier) {
		ShipmentIdentifier shipmentIdentifier = identifier.getShipmentLineItems().getShipment();
		PurchaseIdentifier purchaseIdentifier = shipmentIdentifier.getShipments().getPurchase();
		String scope = purchaseIdentifier.getPurchases().getScope().getValue();
		String purchaseId = purchaseIdentifier.getPurchaseId().getValue();
		String lineItemId = identifier.getShipmentLineItemId().getValue();
		String shipmentId = shipmentIdentifier.getShipmentId().getValue();
		return shipmentRepository.getOrderSkuWithParentId(scope, purchaseId, shipmentId, lineItemId, null)
				.map(orderSku -> buildShipmentLineItemEntity(purchaseId, lineItemId, shipmentId, orderSku));
	}

	private ShipmentLineItemEntity buildShipmentLineItemEntity(final String purchaseId, final String lineItemId, final String shipmentId,
															   final OrderSku orderSku) {
		return ShipmentLineItemEntity.builder()
				.withLineItemId(lineItemId)
				.withPurchaseId(purchaseId)
				.withShipmentId(shipmentId)
				.withName(orderSku.getDisplayName())
				.withQuantity(orderSku.getQuantity())
				.build();
	}

	@Reference
	public void setShipmentRepository(final ShipmentRepository shipmentRepository) {
		this.shipmentRepository = shipmentRepository;
	}
}
