/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.impl.lineitems.options;

import io.reactivex.Observable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.shipments.ShipmentIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionValueIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.ShipmentRepository;

/**
 * Repository for implementing getting the value of an option.
 *
 * @param <I>	extends ShipmentLineItemOptionIdentifier
 * @param <CI>	extends ShipmentLineItemOptionValueIdentifier
 */
@Component
public class ShipmentLineItemOptionValueForOptionRepositoryImpl<I extends ShipmentLineItemOptionIdentifier,
		CI extends ShipmentLineItemOptionValueIdentifier> implements
		LinksRepository<ShipmentLineItemOptionIdentifier, ShipmentLineItemOptionValueIdentifier> {

	private ShipmentRepository shipmentRepository;

	@Override
	public Observable<ShipmentLineItemOptionValueIdentifier> getElements(final ShipmentLineItemOptionIdentifier identifier) {
		String optionId = identifier.getShipmentLineItemOptionId().getValue();
		ShipmentLineItemIdentifier shipmentLineItemIdentifier = identifier.getShipmentLineItemOptions().getShipmentLineItem();
		ShipmentIdentifier shipmentIdentifier = shipmentLineItemIdentifier.getShipmentLineItems().getShipment();
		String purchaseId = shipmentIdentifier.getShipments().getPurchase().getPurchaseId().getValue();
		String shipmentId = shipmentIdentifier.getShipmentId().getValue();
		String lineitemId = shipmentLineItemIdentifier.getShipmentLineItemId().getValue();
		return shipmentRepository.getSkuOptionValue(purchaseId, shipmentId, lineitemId, optionId)
				.map(skuOptionValue -> buildShipmentLineItemOptionValueIdentifier(identifier, skuOptionValue))
				.toObservable();
	}

	private ShipmentLineItemOptionValueIdentifier buildShipmentLineItemOptionValueIdentifier(final ShipmentLineItemOptionIdentifier identifier,
																							 final SkuOptionValue skuOptionValue) {
		return ShipmentLineItemOptionValueIdentifier.builder()
				.withShipmentLineItemOptionValueId(StringIdentifier.of(skuOptionValue.getGuid()))
				.withShipmentLineItemOption(identifier)
				.build();
	}

	@Reference
	public void setShipmentRepository(final ShipmentRepository shipmentRepository) {
		this.shipmentRepository = shipmentRepository;
	}
}
