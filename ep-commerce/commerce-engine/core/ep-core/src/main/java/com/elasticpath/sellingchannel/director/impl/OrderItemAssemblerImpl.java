/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.sellingchannel.director.impl;

import com.elasticpath.common.dto.OrderItemDto;
import com.elasticpath.commons.tree.Functor;
import com.elasticpath.commons.tree.impl.PreOrderTreeTraverser;
import com.elasticpath.commons.tree.impl.TreeNodeMemento;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.store.Store;
import com.elasticpath.sellingchannel.director.OrderItemAssembler;
import com.elasticpath.service.catalog.BundleIdentifier;
import com.elasticpath.service.catalog.ProductInventoryManagementService;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.shoppingcart.PricingSnapshotService;
import com.elasticpath.service.store.StoreService;

/**
 * Default implementation of {@link OrderItemAssembler}.
 */
public class OrderItemAssemblerImpl implements OrderItemAssembler {

	private BundleIdentifier bundleIdentifier;
	
	private final PreOrderTreeTraverser<OrderSkuImplTreeNodeAdapter, TreeNodeMemento<OrderItemDto>> traverser 
		= new PreOrderTreeTraverser<>();

	private ProductInventoryManagementService productInventoryManagementService;
	private StoreService storeService;
	private ProductSkuLookup productSkuLookup;
	private PricingSnapshotService pricingSnapshotService;

	@Override
	public OrderItemDto createOrderItemDto(final OrderSku orderSku, final OrderShipment shipment) {
		Store store = getStoreService().findStoreWithCode(shipment.getOrder().getStoreCode());

		final OrderSkuImplTreeNodeAdapter sourceNode = new OrderSkuImplTreeNodeAdapter(orderSku, shipment, store, bundleIdentifier,
				productInventoryManagementService, getProductSkuLookup(), pricingSnapshotService);
		TreeNodeMemento<OrderItemDto> rootMemento = traverser
				.traverseTree(sourceNode, null, null, new CopyFunctor(), 0);
		return rootMemento.getTreeNode();
	}
	
	/**
	 * 
	 * @param bundleIdentifier The BundleIdentifier bean.
	 */
	public void setBundleIdentifier(final BundleIdentifier bundleIdentifier) {
		this.bundleIdentifier = bundleIdentifier;
	}
	
	/**
	 * 
	 * @param productInventoryManagementService to set.
	 */
	public void setProductInventoryManagementService(
			final ProductInventoryManagementService productInventoryManagementService) {
		this.productInventoryManagementService = productInventoryManagementService;
	}

	protected StoreService getStoreService() {
		return storeService;
	}

	public void setStoreService(final StoreService storeService) {
		this.storeService = storeService;
	}

	protected ProductSkuLookup getProductSkuLookup() {
		return productSkuLookup;
	}

	public void setProductSkuLookup(final ProductSkuLookup productSkuLookup) {
		this.productSkuLookup = productSkuLookup;
	}

	protected PricingSnapshotService getPricingSnapshotService() {
		return pricingSnapshotService;
	}

	public void setPricingSnapshotService(final PricingSnapshotService pricingSnapshotService) {
		this.pricingSnapshotService = pricingSnapshotService;
	}

	/**
	 * Functor for use with {@code PreOrderTreeTraverser}. Copies the {@code OrderSku} tree to an {@code OrderItemDto} tree.
	 */
	static class CopyFunctor implements Functor<OrderSkuImplTreeNodeAdapter, TreeNodeMemento<OrderItemDto>> {
		@Override
		public TreeNodeMemento<OrderItemDto> processNode(final OrderSkuImplTreeNodeAdapter sourceNode, final OrderSkuImplTreeNodeAdapter parentNode,
				final TreeNodeMemento<OrderItemDto> parentStackMemento, final int level) {
			OrderItemDto destDto = new OrderItemDto();
			
			if (parentStackMemento == null) {
				sourceNode.copyToParent(destDto);
			} else {
				sourceNode.copyToChild(destDto);
				parentStackMemento.getTreeNode().addChild(destDto);
			}
			return new TreeNodeMemento<>(destDto);
		}
	}

}
