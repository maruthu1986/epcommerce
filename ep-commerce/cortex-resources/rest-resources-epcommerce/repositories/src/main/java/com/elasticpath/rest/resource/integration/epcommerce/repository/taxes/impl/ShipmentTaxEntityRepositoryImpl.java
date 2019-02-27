/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.taxes.impl;

import io.reactivex.Single;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.springframework.core.convert.ConversionService;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.taxes.ShipmentTaxIdentifier;
import com.elasticpath.rest.definition.taxes.TaxesEntity;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.ShipmentRepository;

/**
 * Repository for Shipment Tax Entity.
 * @param <E> the entity type
 * @param <I> the identifier type
 */
@Component
public class ShipmentTaxEntityRepositoryImpl<E extends TaxesEntity, I extends ShipmentTaxIdentifier>
		implements Repository<TaxesEntity, ShipmentTaxIdentifier> {

	private ConversionService conversionService;
	private ShipmentRepository shipmentRepository;

	/**
	 * Finds Taxes Entity.
	 *
	 * @param identifier shipment tax identifier
	 * @return taxes entity
	 */
	@Override
	public Single<TaxesEntity> findOne(final ShipmentTaxIdentifier identifier) {
		String shipmentId = identifier.getShipment().getShipmentId().getValue();
		String purchaseId = identifier.getShipment().getShipments().getPurchase().getPurchaseId().getValue();

		 return shipmentRepository.find(purchaseId, shipmentId)
				.map(physicalOrderShipment -> conversionService.convert(physicalOrderShipment, TaxesEntity.class));
	}

	@Reference
	public void setShipmentRepository(final ShipmentRepository shipmentRepository) {
		this.shipmentRepository = shipmentRepository;
	}

	@Reference
	public void setConversionService(final ConversionService conversionService) {
		this.conversionService = conversionService;
	}
}
