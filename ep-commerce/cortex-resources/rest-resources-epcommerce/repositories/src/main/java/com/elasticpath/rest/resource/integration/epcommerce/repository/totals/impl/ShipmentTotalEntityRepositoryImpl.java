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
import com.elasticpath.rest.definition.totals.ShipmentTotalIdentifier;
import com.elasticpath.rest.definition.totals.TotalEntity;
import com.elasticpath.rest.resource.integration.epcommerce.repository.calc.ShipmentTotalsCalculator;

/**
 * Shipment Total Entity Repository.
 *
 * @param <E> the entity type
 * @param <I> the identifier type
 */
@Component
public class ShipmentTotalEntityRepositoryImpl<E extends TotalEntity, I extends ShipmentTotalIdentifier>
		implements Repository<TotalEntity, ShipmentTotalIdentifier> {

	private ShipmentTotalsCalculator shipmentTotalsCalculator;
	private ConversionService conversionService;

	@Override
	public Single<TotalEntity> findOne(final ShipmentTotalIdentifier shipmentTotalIdentifier) {
		final ShipmentIdentifier shipmentIdentifier = shipmentTotalIdentifier.getShipment();

		String shipmentId = shipmentIdentifier.getShipmentId().getValue();
		String purchaseId = shipmentIdentifier.getShipments().getPurchase().getPurchaseId().getValue();

		return shipmentTotalsCalculator.calculateTotalForShipment(purchaseId, shipmentId)
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
