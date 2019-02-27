/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.sellingchannel.director.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.elasticpath.common.dto.OrderItemDto;
import com.elasticpath.commons.tree.TreeNode;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.impl.OrderSkuImpl;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.domain.store.Store;
import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.service.catalog.BundleIdentifier;
import com.elasticpath.service.catalog.ProductInventoryManagementService;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.shoppingcart.PricingSnapshotService;

/**
 * Adapter that allows the getChildren/addChild methods to be used on OrderSku even though they exist,
 * with different returns types, in the base class ShoppingItem.
 */
class OrderSkuImplTreeNodeAdapter implements TreeNode<OrderSkuImplTreeNodeAdapter> {

	private final BundleIdentifier bundleIdentifier;
	private final OrderSku orderSku;
	private final OrderShipment shipment;
	private final Store store;
	private final ProductInventoryManagementService productInventoryManagementService;
	private final ProductSkuLookup productSkuLookup;
	private final PricingSnapshotService pricingSnapshotService;

	/**
	 * Normal parameter constructor.
	 *
	 * @param orderSku The sku to wrap.
	 * @param shipment The order shipment to filter by
	 * @param store the order's store
	 * @param bundleIdentifier The BundleIdentifier bean.
	 * @param productInventoryManagementService The Inventory Service.
	 * @param productSkuLookup a product sku lookup
	 * @param pricingSnapshotService the pricing snapshot service
	 */
	@SuppressWarnings("checkstyle:redundantmodifier")
	public OrderSkuImplTreeNodeAdapter(final OrderSku orderSku, final OrderShipment shipment, final Store store,
									   final BundleIdentifier bundleIdentifier,
									   final ProductInventoryManagementService productInventoryManagementService,
									   final ProductSkuLookup productSkuLookup, final PricingSnapshotService pricingSnapshotService) {
		this.bundleIdentifier = bundleIdentifier;
		this.orderSku = orderSku;
		this.store = store;
		this.shipment = shipment;
		this.productInventoryManagementService = productInventoryManagementService;
		this.productSkuLookup = productSkuLookup;
		this.pricingSnapshotService = pricingSnapshotService;
	}

	/**
	 * Copies the fields of the {@code OrderSku} to the {@code destDto}.
	 * @param destDto The dto to copy to.
	 */
	public void copyToParent(final OrderItemDto destDto) {
		copyToChild(destDto);
	}

	/**
	 * Copies the fields of the {@code OrderSku} to the {@code destDto}.
	 * @param destDto The dto to copy to.
	 */
	public void copyToChild(final OrderItemDto destDto) {
		ProductSku productSku = getProductSkuLookup().findByGuid(orderSku.getSkuGuid());

		destDto.setDigitalAsset(orderSku.getDigitalAsset());
		destDto.setDisplayName(orderSku.getDisplayName());
		destDto.setImage(orderSku.getImage());
		destDto.setDisplaySkuOptions(orderSku.getDisplaySkuOptions());
		destDto.setAllocated(orderSku.isAllocated());
		destDto.setProductSku(productSku);
		destDto.setSkuCode(orderSku.getSkuCode());
		destDto.setQuantity(orderSku.getQuantity());
		destDto.setIsBundle(orderSku.isBundle(getProductSkuLookup()));
		destDto.setCalculatedBundle(bundleIdentifier.isCalculatedBundle(productSku));
		if (orderSku.getParent() == null) {
			destDto.setCalculatedBundleItem(false);
		} else {
			final ProductSku parentSku = getProductSkuLookup().findByGuid(orderSku.getParent().getSkuGuid());
			destDto.setCalculatedBundleItem(bundleIdentifier.isCalculatedBundle(parentSku));
		}

		((OrderSkuImpl) orderSku).enableRecalculation();

		final ShoppingItemPricingSnapshot pricingSnapshot = pricingSnapshotService.getPricingSnapshotForOrderSku(orderSku);
		destDto.setListPrice(pricingSnapshot.getListUnitPrice());
		destDto.setUnitPrice(orderSku.getUnitPriceMoney());
		destDto.setPrice(pricingSnapshot.getPrice());
		destDto.setDollarSavings(orderSku.getDollarSavingsMoney());
		destDto.setTotal(pricingSnapshot.getTotal());

		destDto.setInventory(getInventory(productSku, getWarehouseUidPk()));
	}

	private InventoryDto getInventory(final ProductSku productSku, final Long warehouseUid) {
		return productInventoryManagementService.getInventory(productSku, warehouseUid);
	}

	/**
	 * Gets the warehouse uidpk.
	 * @return warehouse uidpk.
	 */
	protected long getWarehouseUidPk() {
		return store.getWarehouse().getUidPk();
	}

	@Override
	public void addChild(final OrderSkuImplTreeNodeAdapter child) {
		orderSku.addChildItem(child.orderSku);
	}

	@Override
	public List<OrderSkuImplTreeNodeAdapter> getChildren() {
		if (!orderSku.getChildren().isEmpty()) {
			List<OrderSkuImplTreeNodeAdapter> orderSkuList = new ArrayList<>(orderSku.getChildren().size());

			for (ShoppingItem shoppingItem : orderSku.getChildren()) {
				OrderSku candidateSku = (OrderSku) shoppingItem;

				if (hasChildrenInShipment(candidateSku, this.shipment)
						|| isInShipment(candidateSku, this.shipment)) {
					orderSkuList.add(new OrderSkuImplTreeNodeAdapter(candidateSku, shipment, store, bundleIdentifier,
							productInventoryManagementService, productSkuLookup, pricingSnapshotService));
				}
			}

			return orderSkuList;
		}

		return Collections.emptyList();
	}

	private boolean hasChildrenInShipment(final ShoppingItem item, final OrderShipment shipment) {
		boolean result = false;

		if (!item.getChildren().isEmpty()) {
			for (ShoppingItem shoppingItem : item.getChildren()) {
				OrderSku candidateSku = (OrderSku) shoppingItem;

				if (candidateSku.getChildren().isEmpty()) {
					result = isInShipment(candidateSku, shipment);
				} else {
					result = hasChildrenInShipment(candidateSku, shipment);
				}

				if (result) {
					break;
				}
			}
		}

		return result;
	}

	private boolean isInShipment(final OrderSku candidateSku, final OrderShipment shipment) {
		return candidateSku.getShipment() != null && candidateSku.getShipment().getUidPk() == shipment.getUidPk();
	}

	protected ProductSkuLookup getProductSkuLookup() {
		return productSkuLookup;
	}
}
