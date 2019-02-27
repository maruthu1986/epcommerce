/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository;

import java.util.List;

import com.google.common.collect.ImmutableMap;

import com.elasticpath.rest.definition.carts.CartIdentifier;
import com.elasticpath.rest.definition.carts.LineItemIdentifier;
import com.elasticpath.rest.definition.carts.LineItemsIdentifier;
import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionIdentifier;
import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.definition.items.ItemsIdentifier;
import com.elasticpath.rest.definition.orders.OrderIdentifier;
import com.elasticpath.rest.definition.prices.PriceForCartLineItemIdentifier;
import com.elasticpath.rest.definition.prices.PriceForItemIdentifier;
import com.elasticpath.rest.definition.prices.PriceForItemdefinitionIdentifier;
import com.elasticpath.rest.definition.prices.PriceForShipmentLineItemIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemsIdentifier;
import com.elasticpath.rest.definition.purchases.PurchasesIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionsIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemsIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentsIdentifier;
import com.elasticpath.rest.id.type.CompositeIdentifier;
import com.elasticpath.rest.id.type.PathIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;

/**
 * Factory methods for building identifiers.
 */
public final class IdentifierTestFactory {

	private IdentifierTestFactory() {

	}

	/**
	 * Builds a PurchasesIdentifier with test data.
	 *
	 * @param scope the scope
	 * @return PurchasesIdentifier
	 */
	public static PurchasesIdentifier buildPurchasesIdentifier(final String scope) {
		return PurchasesIdentifier.builder().withScope(StringIdentifier.of(scope)).build();
	}

	/**
	 * Builds a PurchaseIdentifier with test data.
	 *
	 * @param scope      the scope
	 * @param purchaseId the purchase id
	 * @return PurchaseIdentifier
	 */
	public static PurchaseIdentifier buildPurchaseIdentifier(final String scope, final String purchaseId) {
		return PurchaseIdentifier.builder()
				.withPurchases(buildPurchasesIdentifier(scope))
				.withPurchaseId(StringIdentifier.of(purchaseId))
				.build();
	}

	/**
	 * Builds a ShipmentsIdentifier with test data.
	 *
	 * @param scope      the scope
	 * @param purchaseId the purchase id
	 * @return ShipmentsIdentifier
	 */
	public static ShipmentsIdentifier buildShipmentsIdentifier(final String scope, final String purchaseId) {
		return ShipmentsIdentifier.builder().withPurchase(buildPurchaseIdentifier(scope, purchaseId)).build();
	}

	/**
	 * Builds a ShipmentIdentifier with test data.
	 *
	 * @param scope      the scope
	 * @param purchaseId the purchase id
	 * @param shipmentId the shipment id
	 * @return ShipmentIdentifier
	 */
	public static ShipmentIdentifier buildShipmentIdentifier(final String scope, final String purchaseId, final String shipmentId) {
		return ShipmentIdentifier.builder()
				.withShipmentId(StringIdentifier.of(shipmentId))
				.withShipments(buildShipmentsIdentifier(scope, purchaseId))
				.build();
	}

	/**
	 * Builds a ShipmentLineItemsIdentifier with test data.
	 *
	 * @param scope      the scope
	 * @param purchaseId the purchase id
	 * @param shipmentId the shipment id
	 * @return ShipmentLineItemsIdentifier
	 */
	public static ShipmentLineItemsIdentifier buildShipmentLineItemsIdentifier(final String scope, final String purchaseId,
																			   final String shipmentId) {
		return ShipmentLineItemsIdentifier.builder().withShipment(buildShipmentIdentifier(scope, purchaseId, shipmentId)).build();
	}

	/**
	 * Builds a ShipmentLineItemIdentifier with test data.
	 *
	 * @param scope              the scope
	 * @param purchaseId         the purchase id
	 * @param shipmentId         the shipment id
	 * @param shipmentLineItemId the shipment line item id
	 * @return ShipmentLineItemIdentifier
	 */
	public static ShipmentLineItemIdentifier buildShipmentLineItemIdentifier(final String scope, final String purchaseId,
																			 final String shipmentId, final String shipmentLineItemId) {
		return ShipmentLineItemIdentifier.builder()
				.withShipmentLineItems(buildShipmentLineItemsIdentifier(scope, purchaseId, shipmentId))
				.withShipmentLineItemId(StringIdentifier.of(shipmentLineItemId))
				.build();
	}

	/**
	 * Builds a ShipmentLineItemOptionsIdentifier with test data.
	 *
	 * @param scope              the scope
	 * @param purchaseId         the purchase id
	 * @param shipmentId         the shipment id
	 * @param shipmentLineItemId the shipment line item id
	 * @return ShipmentLineItemOptionsIdentifier
	 */
	public static ShipmentLineItemOptionsIdentifier buildShipmentLineItemOptionsIdentifier(final String scope, final String purchaseId,
																						   final String shipmentId, final String
																								   shipmentLineItemId) {
		return ShipmentLineItemOptionsIdentifier.builder()
				.withShipmentLineItem(buildShipmentLineItemIdentifier(scope, purchaseId, shipmentId, shipmentLineItemId))
				.build();
	}

	/**
	 * Builds a ShipmentLineItemOptionIdentifier with test data.
	 *
	 * @param scope              the scope
	 * @param purchaseId         the purchase id
	 * @param shipmentId         the shipment id
	 * @param shipmentLineItemId the shipment line item id
	 * @param optionId           the option id
	 * @return ShipmentLineItemOptionIdentifier
	 */
	public static ShipmentLineItemOptionIdentifier buildShipmentLineItemOptionIdentifier(final String scope, final String purchaseId,
																						 final String shipmentId, final String shipmentLineItemId,
																						 final String optionId) {
		return ShipmentLineItemOptionIdentifier.builder()
				.withShipmentLineItemOptions(buildShipmentLineItemOptionsIdentifier(scope, purchaseId, shipmentId, shipmentLineItemId))
				.withShipmentLineItemOptionId(StringIdentifier.of(optionId))
				.build();
	}

	/**
	 * Builds an ItemsIdentifier with test data.
	 *
	 * @param scope the scope
	 * @return ItemsIdentifier
	 */
	public static ItemsIdentifier buildItemsIdentifier(final String scope) {
		return ItemsIdentifier.builder()
				.withScope(StringIdentifier.of(scope))
				.build();
	}

	/**
	 * Builds an ItemIdentifier with test data.
	 *
	 * @param scope  the scope
	 * @param skuCode the item sku code
	 * @return ItemIdentifier
	 */
	public static ItemIdentifier buildItemIdentifier(final String scope, final String skuCode) {
		return ItemIdentifier.builder()
				.withItemId(CompositeIdentifier.of(ImmutableMap.of(ItemRepository.SKU_CODE_KEY, skuCode)))
				.withItems(buildItemsIdentifier(scope))
				.build();
	}

	/**
	 * Builds a PriceForItemIdentifier with test data.
	 *
	 * @param scope  the scope
	 * @param skuCode the item sku code
	 * @return PriceForItemIdentifier
	 */
	public static PriceForItemIdentifier buildPriceForItemIdentifier(final String scope, final String skuCode) {
		return PriceForItemIdentifier.builder()
				.withItem(buildItemIdentifier(scope, skuCode))
				.build();
	}

	/**
	 * Builds a CartIdentifier with test data.
	 *
	 * @param scope  the scope
	 * @param cartId the cart id
	 * @return CartIdentifier
	 */
	public static CartIdentifier buildCartIdentifier(final String scope, final String cartId) {
		return CartIdentifier.builder()
				.withCartId(StringIdentifier.of(cartId))
				.withScope(StringIdentifier.of(scope))
				.build();
	}

	/**
	 * Builds a LineItemsIdentifier with test data.
	 *
	 * @param scope  the scope
	 * @param cartId the cart id
	 * @return LineItemsIdentifier
	 */
	public static LineItemsIdentifier buildLineItemsIdentifier(final String scope, final String cartId) {
		return LineItemsIdentifier.builder()
				.withCart(buildCartIdentifier(scope, cartId))
				.build();
	}

	/**
	 * Builds a LineItemIdentifier with test data.
	 *
	 * @param scope      the scope
	 * @param cartId     the cart id
	 * @param lineItemId the line item id
	 * @return LineItemIdentifier
	 */
	public static LineItemIdentifier buildLineItemIdentifier(final String scope, final String cartId, final String lineItemId) {
		return LineItemIdentifier.builder()
				.withLineItems(buildLineItemsIdentifier(scope, cartId))
				.withLineItemId(StringIdentifier.of(lineItemId))
				.build();
	}

	/**
	 * Builds a PriceForCartLineItemIdentifier with test data.
	 *
	 * @param scope      the scope
	 * @param cartId     the cart id
	 * @param lineItemId the line item id
	 * @return PriceForCartLineItemIdentifier
	 */
	public static PriceForCartLineItemIdentifier buildPriceForCartLineItemIdentifier(final String scope,
																					 final String cartId, final String lineItemId) {
		return PriceForCartLineItemIdentifier.builder()
				.withLineItem(buildLineItemIdentifier(scope, cartId, lineItemId))
				.build();
	}

	/**
	 * Builds an ItemDefinitionIdentifier with test data.
	 *
	 * @param scope  the scope
	 * @param skuCode the item sku code
	 * @return ItemDefinitionIdentifier
	 */
	public static ItemDefinitionIdentifier buildItemDefinitionIdentifier(final String scope, final String skuCode) {
		return ItemDefinitionIdentifier.builder()
				.withScope(StringIdentifier.of(scope))
				.withItemId(CompositeIdentifier.of(ImmutableMap.of(ItemRepository.SKU_CODE_KEY, skuCode)))
				.build();
	}

	/**
	 * Builds a PriceForItemdefinitionIdentifier with test data.
	 *
	 * @param scope  the scope
	 * @param skuCode the item sku code
	 * @return PriceForItemdefinitionIdentifier
	 */
	public static PriceForItemdefinitionIdentifier buildPriceForItemdefinitionIdentifier(final String scope, final String skuCode) {
		return PriceForItemdefinitionIdentifier.builder()
				.withItemDefinition(buildItemDefinitionIdentifier(scope, skuCode))
				.build();
	}

	/**
	 * Builds a PriceForShipmentLineItemIdentifier with test data.
	 *
	 * @param scope              the scope
	 * @param purchaseId         the purchase id
	 * @param shipmentId         the shipment id
	 * @param shipmentLineItemId the shipment line item id
	 * @return PriceForShipmentLineItemIdentifier
	 */
	public static PriceForShipmentLineItemIdentifier buildPriceForShipmentLineItemIdentifier(final String scope, final String purchaseId,
																							 final String shipmentId,
																							 final String shipmentLineItemId) {
		return PriceForShipmentLineItemIdentifier.builder()
				.withShipmentLineItem(buildShipmentLineItemIdentifier(scope, purchaseId, shipmentId, shipmentLineItemId))
				.build();
	}

	/**
	 * Builds an OrderIdentifier with test data.
	 *
	 * @param scope   the scope
	 * @param orderId the order id
	 * @return OrderIdentifier
	 */
	public static OrderIdentifier buildOrderIdentifier(final String scope, final String orderId) {
		return OrderIdentifier.builder()
				.withOrderId(StringIdentifier.of(orderId))
				.withScope(StringIdentifier.of(scope))
				.build();
	}

	/**
	 * Builds a PurchaseLineItemsIdentifier.
	 * @param scope scope
	 * @param purchaseId purchase id
	 * @return PurchaseLineItemsIdentifier
	 */
	public static PurchaseLineItemsIdentifier buildPurchaseLineItemsIdentifier(final String scope, final String purchaseId) {
		return PurchaseLineItemsIdentifier.builder()
				.withPurchase(buildPurchaseIdentifier(scope, purchaseId))
				.build();
	}

	/**
	 * Builds a PurchaseLineItemIdentifier.
	 * @param scope scope
	 * @param purchaseId purchase id
	 * @param lineItemIds line item ids
	 * @return PurchaseLineItemIdentifier
	 */
	public static PurchaseLineItemIdentifier buildPurchaseLineItemIdentifier(final String scope, final String purchaseId,
																			 final List<String> lineItemIds) {
		return PurchaseLineItemIdentifier.builder()
				.withLineItemId(PathIdentifier.of(lineItemIds))
				.withPurchaseLineItems(buildPurchaseLineItemsIdentifier(scope, purchaseId))
				.build();
	}

}
