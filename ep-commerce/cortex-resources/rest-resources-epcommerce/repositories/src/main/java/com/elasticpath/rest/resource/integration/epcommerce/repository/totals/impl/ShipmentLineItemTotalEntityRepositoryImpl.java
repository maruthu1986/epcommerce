/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.totals.impl;

import io.reactivex.Single;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.springframework.core.convert.ConversionService;

import com.elasticpath.money.Money;
import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.shipments.ShipmentIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemIdentifier;
import com.elasticpath.rest.definition.totals.ShipmentLineItemTotalIdentifier;
import com.elasticpath.rest.definition.totals.TotalEntity;
import com.elasticpath.rest.resource.integration.epcommerce.repository.calc.ShipmentTotalsCalculator;

/**
 * Shipment Line Item Total Entity Repository.
 *
 * @param <E> the entity type
 * @param <I> the identifier type
 */
@Component
public class ShipmentLineItemTotalEntityRepositoryImpl<E extends TotalEntity, I extends ShipmentLineItemTotalIdentifier>
		implements Repository<TotalEntity, ShipmentLineItemTotalIdentifier> {

	private ShipmentTotalsCalculator shipmentTotalsCalculator;
	private ConversionService conversionService;

	@Override
	public Single<TotalEntity> findOne(final ShipmentLineItemTotalIdentifier shipmentLineItemTotalIdentifier) {
		final ShipmentLineItemIdentifier shipmentLineItemIdentifier = shipmentLineItemTotalIdentifier.getShipmentLineItem();
		final ShipmentIdentifier shipmentIdentifier = shipmentLineItemIdentifier.getShipmentLineItems().getShipment();

		String lineItemId = shipmentLineItemIdentifier.getShipmentLineItemId().getValue();
		String shipmentId = shipmentIdentifier.getShipmentId().getValue();
		String purchaseId = shipmentIdentifier.getShipments().getPurchase().getPurchaseId().getValue();

		return shipmentTotalsCalculator.calculateTotalForLineItem(purchaseId, shipmentId, lineItemId)
				.map(this::convertMoneyToTotalEntity);
	}

	/**
	 * Converts given Money to TotalEntity.
	 *
	 * @param money money to convert
	 * @return the converted total entity
	 */
	protected TotalEntity convertMoneyToTotalEntity(final Money money) {
		return conversionService.convert(money, TotalEntity.class);
	}

	@Reference
	public void setShipmentTotalsCalculator(final ShipmentTotalsCalculator shipmentTotalsCalculator) {
		this.shipmentTotalsCalculator = shipmentTotalsCalculator;
	}


	@Reference
	public void setConversionService(final ConversionService conversionService) {
		this.conversionService = conversionService;
	}
}
