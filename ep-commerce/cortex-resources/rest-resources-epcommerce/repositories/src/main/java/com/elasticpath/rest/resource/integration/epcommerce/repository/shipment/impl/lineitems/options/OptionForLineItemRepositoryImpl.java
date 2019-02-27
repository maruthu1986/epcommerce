/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.impl.lineitems.options;

import java.util.Locale;

import io.reactivex.Single;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.shipments.ShipmentIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionEntity;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionIdentifier;
import com.elasticpath.rest.identity.util.SubjectUtil;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.ShipmentRepository;

/**
 * Repository that reads an option for a lineitem.
 * @param <E>	extends ShipmentLineItemOptionEntity
 * @param <I>	extends ShipmentLineItemOptionIdentifier
 */
@Component
public class OptionForLineItemRepositoryImpl<E extends ShipmentLineItemOptionEntity, I extends ShipmentLineItemOptionIdentifier>
		implements Repository<ShipmentLineItemOptionEntity, ShipmentLineItemOptionIdentifier> {

	private ShipmentRepository shipmentRepository;
	private ResourceOperationContext resourceOperationContext;

	@Override
	public Single<ShipmentLineItemOptionEntity> findOne(final ShipmentLineItemOptionIdentifier identifier) {
		ShipmentLineItemIdentifier shipmentLineItemIdentifier = identifier.getShipmentLineItemOptions().getShipmentLineItem();
		ShipmentIdentifier shipmentIdentifier = shipmentLineItemIdentifier.getShipmentLineItems().getShipment();
		String shipmentId = shipmentIdentifier.getShipmentId().getValue();
		String purchaseId = shipmentIdentifier.getShipments().getPurchase().getPurchaseId().getValue();
		String lineitemId = shipmentLineItemIdentifier.getShipmentLineItemId().getValue();
		String optionId = identifier.getShipmentLineItemOptionId().getValue();
		Locale locale = SubjectUtil.getLocale(resourceOperationContext.getSubject());
		return shipmentRepository.getSkuOptionValue(purchaseId, shipmentId, lineitemId, optionId)
				.map(skuOptionValue -> buildShipmentLineItemOptionEntity(shipmentId, purchaseId, lineitemId, locale, skuOptionValue));
	}

	private ShipmentLineItemOptionEntity buildShipmentLineItemOptionEntity(final String shipmentId, final String purchaseId,
																		   final String lineitemId, final Locale locale,
																		   final SkuOptionValue skuOptionValue) {
		return ShipmentLineItemOptionEntity.builder()
				.withLineItemId(lineitemId)
				.withShipmentId(shipmentId)
				.withPurchaseId(purchaseId)
				.withName(skuOptionValue.getSkuOption().getOptionKey())
				.withDisplayName(skuOptionValue.getSkuOption().getDisplayName(locale, true))
				.withLineItemOptionId(skuOptionValue.getGuid())
				.withLineItemOptionValueId(skuOptionValue.getOptionValueKey())
				.build();
	}

	@Reference
	public void setShipmentRepository(final ShipmentRepository shipmentRepository) {
		this.shipmentRepository = shipmentRepository;
	}

	@Reference
	public void setResourceOperationContext(final ResourceOperationContext resourceOperationContext) {
		this.resourceOperationContext = resourceOperationContext;
	}
}
