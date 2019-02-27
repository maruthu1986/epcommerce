/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.integration.epcommerce.repository.taxes.impl;

import java.util.Set;

import io.reactivex.Single;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.repository.Repository;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemIdentifier;
import com.elasticpath.rest.definition.taxes.ShipmentLineItemTaxIdentifier;
import com.elasticpath.rest.definition.taxes.TaxesEntity;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.ShipmentRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.taxdocument.TaxDocumentRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.taxes.transform.ShipmentLineItemTaxesEntityTransformer;

/**
 * Repository for Shipment Line Items Tax Entity.
 * @param <E> the entity type
 * @param <I> the identifier type
 */
@Component
public class ShipmentLineItemsTaxEntityRepositoryImpl<E extends TaxesEntity, I extends ShipmentLineItemTaxIdentifier>
		implements Repository<TaxesEntity, ShipmentLineItemTaxIdentifier> {

	/**
	 * Error message when line item not found.
	 */
	static final String LINE_ITEM_NOT_FOUND = "Line item not found";

	private ShipmentRepository shipmentRepository;
	private TaxDocumentRepository taxDocumentRepository;
	private ShipmentLineItemTaxesEntityTransformer transformer;

	@Override
	public Single<TaxesEntity> findOne(final ShipmentLineItemTaxIdentifier identifier) {
		ShipmentLineItemIdentifier lineItem = identifier.getShipmentLineItem();

		String shipmentId = lineItem.getShipmentLineItems().getShipment().getShipmentId().getValue();
		String purchaseId = lineItem.getShipmentLineItems().getShipment().getShipments().getPurchase().getPurchaseId().getValue();
		String lineItemId = lineItem.getShipmentLineItemId().getValue();

		return shipmentRepository.find(purchaseId, shipmentId)
				.flatMap(shipment -> getTaxesEntity(lineItemId, shipment));
	}

	/**
	 * Getter for the SKU.
	 *
	 * @param orderSkus order SKUs
	 * @param skuGuid SKU guid
	 * @return SKU for the order
	 */
	protected Single<OrderSku> getShoppingItemByGuid(final Set<OrderSku> orderSkus, final String skuGuid) {
		for (OrderSku orderSku : orderSkus) {
			if (orderSku.getGuid().equals(skuGuid)) {
				return Single.just(orderSku);
			}
		}
		return Single.error(ResourceOperationFailure.notFound(LINE_ITEM_NOT_FOUND));
	}

	/**
	 * Get taxes entity.
	 *
	 * @param lineItemId id of the line item
	 * @param shipment physical shipment for the order
	 * @return taxes entity
	 */
	protected Single<TaxesEntity> getTaxesEntity(final String lineItemId, final PhysicalOrderShipment shipment) {
		return getShoppingItemByGuid(shipment.getShipmentOrderSkus(), lineItemId)
				.flatMap(sku -> getTaxesBySkuAndShipment(shipment, sku));
	}

	/**
	 * Get taxes given SKU and shipment.
	 *
	 * @param shipment shipment
	 * @param sku sku
	 * @return taxes entity
	 */
	protected Single<TaxesEntity> getTaxesBySkuAndShipment(final PhysicalOrderShipment shipment, final OrderSku sku) {
		return taxDocumentRepository.getTaxDocument(shipment.getTaxDocumentId(), sku.getSkuCode())
				.flatMap(taxRecords -> transformer.transform(sku, taxRecords));
	}

	@Reference
	public void setShipmentRepository(final ShipmentRepository shipmentRepository) {
		this.shipmentRepository = shipmentRepository;
	}

	@Reference
	public void setTaxDocumentRepository(final TaxDocumentRepository taxDocumentRepository) {
		this.taxDocumentRepository = taxDocumentRepository;
	}

	@Reference
	public void setShipmentLineItemTaxesEntityTransformer(final ShipmentLineItemTaxesEntityTransformer transformer) {
		this.transformer = transformer;
	}
}
