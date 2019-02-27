/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.impl.lineitems.options;

import io.reactivex.Observable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.shipments.ShipmentIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionsIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.ShipmentRepository;

/**
 * Repository that implements reading options for lineitem.
 * @param <I>	extends ShipmentLineItemIdentifier
 * @param <LI>	extends ShipmentLineItemOptionIdentifier
 */
@Component
public class OptionsForLineItemRepositoryImpl<I extends ShipmentLineItemIdentifier, LI extends ShipmentLineItemOptionIdentifier>
		implements LinksRepository<ShipmentLineItemIdentifier, ShipmentLineItemOptionIdentifier> {

	private ShipmentRepository shipmentRepository;

	@Override
	public Observable<ShipmentLineItemOptionIdentifier> getElements(final ShipmentLineItemIdentifier identifier) {
		ShipmentIdentifier shipmentIdentifier = identifier.getShipmentLineItems().getShipment();
		String purchaseId = shipmentIdentifier.getShipments().getPurchase().getPurchaseId().getValue();
		String shipmentId = shipmentIdentifier.getShipmentId().getValue();
		String lineitemId = identifier.getShipmentLineItemId().getValue();
		return shipmentRepository.getProductSku(purchaseId, shipmentId, lineitemId)
				.flatMapObservable(productSku -> Observable.fromIterable(productSku.getOptionValueCodes()))
				.map(optionId -> buildShipmentLineItemOptionIdentifier(identifier, optionId));
	}

	private ShipmentLineItemOptionIdentifier buildShipmentLineItemOptionIdentifier(final ShipmentLineItemIdentifier identifier, final String
			optionId) {
		return ShipmentLineItemOptionIdentifier.builder()
				.withShipmentLineItemOptionId(StringIdentifier.of(optionId))
				.withShipmentLineItemOptions(buildShipmentLineItemOptionsIdentifier(identifier))
				.build();
	}

	private ShipmentLineItemOptionsIdentifier buildShipmentLineItemOptionsIdentifier(final ShipmentLineItemIdentifier identifier) {
		return ShipmentLineItemOptionsIdentifier.builder()
				.withShipmentLineItem(identifier)
				.build();
	}

	@Reference
	public void setShipmentRepository(final ShipmentRepository shipmentRepository) {
		this.shipmentRepository = shipmentRepository;
	}
}
