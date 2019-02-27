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
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.definition.shipments.ShipmentIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionValueEntity;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionValueIdentifier;
import com.elasticpath.rest.identity.util.SubjectUtil;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.ShipmentRepository;

/**
 * Read value of shipment lineitem option.
 *
 * @param <E>	extends ShipmentLineItemOptionValueEntity
 * @param <I>	extends ShipmentLineItemOptionValueIdentifier
 */
@Component
public class ValueForShipmentLineItemOptionRepositoryImpl<E extends ShipmentLineItemOptionValueEntity,
		I extends ShipmentLineItemOptionValueIdentifier> implements
		Repository<ShipmentLineItemOptionValueEntity, ShipmentLineItemOptionValueIdentifier> {

	private static final String OPTION_VALUE_NOT_FOUND = "Option value not found.";
	private ShipmentRepository shipmentRepository;
	private ResourceOperationContext resourceOperationContext;

	@Override
	public Single<ShipmentLineItemOptionValueEntity> findOne(final ShipmentLineItemOptionValueIdentifier identifier) {
		String valueId = identifier.getShipmentLineItemOptionValueId().getValue();
		ShipmentLineItemOptionIdentifier shipmentLineItemOptionIdentifier = identifier.getShipmentLineItemOption();
		ShipmentLineItemIdentifier shipmentLineItemIdentifier = shipmentLineItemOptionIdentifier.getShipmentLineItemOptions().getShipmentLineItem();
		ShipmentIdentifier shipmentIdentifier = shipmentLineItemIdentifier.getShipmentLineItems().getShipment();
		String optionId = shipmentLineItemOptionIdentifier.getShipmentLineItemOptionId().getValue();
		String purchaseId = shipmentIdentifier.getShipments().getPurchase().getPurchaseId().getValue();
		String shipmentId = shipmentIdentifier.getShipmentId().getValue();
		String lineitemId = shipmentLineItemIdentifier.getShipmentLineItemId().getValue();
		Locale locale = SubjectUtil.getLocale(resourceOperationContext.getSubject());
		return shipmentRepository.getSkuOptionValue(purchaseId, shipmentId, lineitemId, optionId)
				.flatMap(skuOptionValue -> getValidatedSkuOptionValue(valueId, skuOptionValue))
				.map(skuOptionValue -> buildShipmentLineItemOptionValueEntity(locale, skuOptionValue));
	}

	private ShipmentLineItemOptionValueEntity buildShipmentLineItemOptionValueEntity(final Locale locale, final SkuOptionValue skuOptionValue) {
		return ShipmentLineItemOptionValueEntity.builder()
				.withDisplayName(skuOptionValue.getDisplayName(locale, true))
				.withName(skuOptionValue.getOptionValueKey())
				.build();
	}

	private Single<SkuOptionValue> getValidatedSkuOptionValue(final String valueId, final SkuOptionValue skuOptionValue) {
		return skuOptionValue.getGuid().equals(valueId)
				? Single.just(skuOptionValue) : Single.error(ResourceOperationFailure.notFound(OPTION_VALUE_NOT_FOUND));
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
