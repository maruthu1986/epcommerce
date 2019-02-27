/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.service.shoppingcart.actions.impl;

import java.util.HashMap;
import java.util.Map;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.PreOrBackOrderDetails;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.inventory.InventoryCapabilities;
import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.sellingchannel.inventory.ProductInventoryShoppingService;

/**
 * Tracks total deductions from inventory for all cart items rather than
 * checking each item individually, ensuring there is inventory for all the
 * items in the cart together.  If each item was checked individually then
 * if 2 entries for the same low-stocked SKU existed, one stand-alone and one
 * as a dependent item, then they could both be cleared for inventory when in
 * fact there may only be enough inventory for one entry.
 */
class TemporaryInventory {
	/**
	 * Temporary inventory map. Maps SKU code to the number of items available in inventory.
	 */
	private final Map<String, Integer> temporaryInventoryMap = new HashMap<>();

	/** The warehouse that will be fulfilling the inventory requests. */
	private final Warehouse fulfillingWarehouse;

	private final ProductInventoryShoppingService productInventoryShoppingService;

	/**
	 * Create an instance that will fulfill the inventory using the specified
	 * warehouse.
	 *
	 * @param warehouse the warehouse that will fulfill the inventory.
	 * @param productInventoryShoppingService The inventory service to use for inventory retrieval.
	 */
	@SuppressWarnings("checkstyle:redundantmodifier")
	public TemporaryInventory(final Warehouse warehouse, final ProductInventoryShoppingService productInventoryShoppingService) {
		this.fulfillingWarehouse = warehouse;
		this.productInventoryShoppingService = productInventoryShoppingService;
	}

	/**
	 * @param shoppingItem the shopping item
	 * @param productSku - the product sku whose inventory to check
	 * @return true if there is enough inventory, false otherwise
	 */
	@SuppressWarnings("fallthrough")
	public boolean hasSufficient(final ShoppingItem shoppingItem, final ProductSku productSku) {
		final Product product = productSku.getProduct();
		int preOrBackOrderQtyLeft = 0;

		switch (product.getAvailabilityCriteria()) {
		case ALWAYS_AVAILABLE:
			return true;
		case AVAILABLE_FOR_BACK_ORDER:
		case AVAILABLE_FOR_PRE_ORDER:
			preOrBackOrderQtyLeft = getPreOrBackOrderQtyLeft(shoppingItem, productSku);
			break;
		default:
			// do nothing
		}

		Integer inventoryQty = this.temporaryInventoryMap.get(productSku.getSkuCode());
		if (inventoryQty == null) {
			final InventoryDto newInventory = productInventoryShoppingService.getInventory(productSku, fulfillingWarehouse.getUidPk());
			if (newInventory != null) {
				inventoryQty = newInventory.getAvailableQuantityInStock();
				this.temporaryInventoryMap.put(productSku.getSkuCode(), inventoryQty);
			}
		}

		if (inventoryQty == null) {
			inventoryQty = Integer.valueOf(0);
		}

		inventoryQty += preOrBackOrderQtyLeft;

		this.temporaryInventoryMap.put(productSku.getSkuCode(), inventoryQty - shoppingItem.getQuantity());

		return this.temporaryInventoryMap.get(productSku.getSkuCode()) >= 0;
	}

	/**
	 * Gets the quantity available for pre or back order.
	 *
	 * @param shoppingItem the shopping item to check
	 * @param productSku the product sku
	 * @return the amount of pre or back order quantity left
	 */
	protected int getPreOrBackOrderQtyLeft(final ShoppingItem shoppingItem, final ProductSku productSku) {
		int preOrBackOrderQtyLeft;
		final boolean supportsPreOrBackOrderLimit = productInventoryShoppingService.getInventoryCapabilities().supports(
				InventoryCapabilities.PRE_OR_BACK_ORDER_LIMIT);

		if (supportsPreOrBackOrderLimit && productSku.getProduct().getPreOrBackOrderLimit() > 0) {
			final PreOrBackOrderDetails preOrBackOrderDetails = productInventoryShoppingService.getPreOrBackOrderDetails(productSku);
			preOrBackOrderQtyLeft = preOrBackOrderDetails.getLimit() - preOrBackOrderDetails.getQuantity();

		} else {
			// unlimited qty and therefore we can set the amount left to the qty of the cart item
			// in order to get in stock result from the temp inventory check
			preOrBackOrderQtyLeft = shoppingItem.getQuantity();
		}
		return preOrBackOrderQtyLeft;
	}
}
