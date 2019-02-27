/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.shipment;

import io.reactivex.Observable;
import io.reactivex.Single;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;

/**
 * The facade for operations with shipments.
 */
public interface ShipmentRepository {

	/**
	 * Find by shipment GUID.
	 *
	 * @param orderId the order GUID
	 * @param shipmentId the shipment GUID
	 * @return order shipment
	 */
	Single<PhysicalOrderShipment> find(String orderId, String shipmentId);

	/**
	 * Find all shipments by order GUID.
	 *
	 * @param storeCode the store code
	 * @param orderGuid the order GUID
	 * @return Observable with the list of shipments, returns blank list if none found
	 */
	Observable<PhysicalOrderShipment> findAll(String storeCode, String orderGuid);

	/**
	 * Gets the order sku using {@link #getOrderSku} and validates the parent of order sku.
	 *
	 * @param scope The scope.
	 * @param purchaseId The purchase.
	 * @param shipmentId The shipment.
	 * @param lineItemId The line item.
	 * @param parentOrderSkuGuid The parent order sku.
	 * @return The order sku.
	 */
	Single<OrderSku> getOrderSkuWithParentId(
			String scope, String purchaseId, String shipmentId, String lineItemId, String parentOrderSkuGuid);

	/**
	 * Gets the order sku.
	 *
	 * @param purchaseId The purchase.
	 * @param shipmentId The shipment.
	 * @param lineItemId The line item.
	 * @return The order sku.
	 */
	Single<OrderSku> getOrderSku(String purchaseId, String shipmentId, String lineItemId);

	/**
	 * Gets the product sku.
	 *
	 * @param purchaseId purchase id
	 * @param shipmentId shipment id
	 * @param lineItemId line item id
	 * @return the product sku
	 */
	Single<ProductSku> getProductSku(String purchaseId, String shipmentId, String lineItemId);

	/**
	 * Gets the sku option value.
	 *
	 * @param purchaseId purchase id
	 * @param shipmentId shipment id
	 * @param lineItemId line item id
	 * @param optionId option id
	 * @return the sku option value
	 */
	Single<SkuOptionValue> getSkuOptionValue(String purchaseId, String shipmentId, String lineItemId, String optionId);

	/**
	 * Gets the order skus for a shipment.
	 *
	 * @param scope The scope.
	 * @param purchaseId The purchase.
	 * @param shipmentId The shipment.
	 * @return The order skus.
	 */
	Observable<OrderSku> getOrderSkusForShipment(String scope, String purchaseId, String shipmentId);
}
