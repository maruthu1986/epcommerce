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
import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionsIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.ShipmentRepository;

/**
 * Repository that returns existing options.
 *
 * @param <I> extends ShipmentLineItemIdentifier
 * @param <CI> extends ShipmentLineItemOptionsIdentifier
 */
@Component
public class LineItemToOptionsRepositoryImpl<I extends ShipmentLineItemIdentifier, CI extends ShipmentLineItemOptionsIdentifier> implements
		LinksRepository<ShipmentLineItemIdentifier, ShipmentLineItemOptionsIdentifier> {

	private ShipmentRepository shipmentRepository;

	@Override
	public Observable<ShipmentLineItemOptionsIdentifier> getElements(final ShipmentLineItemIdentifier identifier) {
		ShipmentIdentifier shipmentIdentifier = identifier.getShipmentLineItems().getShipment();
		String purchaseId = shipmentIdentifier.getShipments().getPurchase().getPurchaseId().getValue();
		String shipmentId = shipmentIdentifier.getShipmentId().getValue();
		String lineitemId = identifier.getShipmentLineItemId().getValue();
		return shipmentRepository.getProductSku(purchaseId, shipmentId, lineitemId)
				.flatMapObservable(productSku -> productSku.getOptionValueCodes().isEmpty()
						? Observable.empty() : Observable.just(buildShipmentLineItemOptions(identifier)));
	}

	private ShipmentLineItemOptionsIdentifier buildShipmentLineItemOptions(final ShipmentLineItemIdentifier identifier) {
		return ShipmentLineItemOptionsIdentifier.builder()
				.withShipmentLineItem(identifier)
				.build();
	}

	@Reference
	public void setShipmentRepository(final ShipmentRepository shipmentRepository) {
		this.shipmentRepository = shipmentRepository;
	}
}
