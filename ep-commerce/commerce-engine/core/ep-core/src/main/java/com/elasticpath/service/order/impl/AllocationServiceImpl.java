/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.order.impl;

import java.util.List;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.InventoryEventType;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.order.AllocationEventType;
import com.elasticpath.domain.order.AllocationResult;
import com.elasticpath.domain.order.AllocationStatus;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.store.Store;
import com.elasticpath.inventory.InventoryExecutionResult;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.catalog.ProductInventoryManagementService;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.order.AllocationService;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.service.store.WarehouseService;

/**
 * Allocates and deallocates product quantities dependent on the event type occurring in
 * the system. Also provides information on the already allocated quantities.
 */
public class AllocationServiceImpl implements AllocationService {

	private ProductInventoryManagementService productInventoryManagementService;
	private WarehouseService warehouseService;
	private StoreService storeService;
	private ProductSkuLookup productSkuLookup;
	private PersistenceEngine persistenceEngine;
	private BeanFactory beanFactory;

	/**
	 * Get quantity awaiting allocation for given inventory.
	 *
	 * @param skuCode the product SKU's skuCode
	 * @param warehouseUid the warehouse UID
	 * @return quantity awaiting allocation
	 */
	@Override
	public int getQuantityAwaitingAllocation(final String skuCode, final long warehouseUid) {
		final List<OrderSku> orderSkuList = findOrderSkusWithCodeAndStatus(
				skuCode, warehouseUid, OrderShipmentStatus.AWAITING_INVENTORY);
		int result = 0;
		for (final OrderSku orderSku : orderSkuList) {
			if (!orderSku.isAllocated()) {
				result += orderSku.getQuantity() - orderSku.getAllocatedQuantity();
			}
		}

		return result;
	}

	@Override
	public List<OrderSku> findOrderSkusWithCodeAndStatus(final String skuCode, final long warehouseUid,
															final OrderShipmentStatus status) {

		return getPersistenceEngine().retrieveByNamedQuery("ORDER_SKU_SELECT_BY_CODE_AND_STATUS", skuCode, status, warehouseUid);
	}


	@Override
	public AllocationStatus getAllocationStatus(final ProductSku productSku, final long warehouseUid, final int quantityToAllocate) {

		AllocationStatus status;
		if (hasSufficientUnallocatedQty(productSku, warehouseUid, quantityToAllocate)) {
			if (productInventoryManagementService.hasSufficientInventory(productSku, warehouseUid, quantityToAllocate)) {
				status = AllocationStatus.ALLOCATED_IN_STOCK;
			} else {
				status = AllocationStatus.AWAITING_ALLOCATION;
			}
		} else {
			status = AllocationStatus.NOT_ALLOCATED;
		}
		return status;
	}

	/**
	 * Checks the amount of unallocated pre/back order limit (if applicable) and inventory state of the given product SKU.
	 *
	 * @param productSku the product SKU
	 * @param warehouseUid the warehouse UID
	 * @param quantity the quantity to be checked
	 * @return true if there is enough unallocated quantity
	 */
	@Override
	public boolean hasSufficientUnallocatedQty(final ProductSku productSku, final long warehouseUid, final int quantity) {
		if (quantity <= 0) {
			throw new EpDomainException("Invalid argument: cannot check for zero or negative quantity");
		}
		final Product product = productSku.getProduct();
		return product.getAvailabilityCriteria().hasSufficientUnallocatedQty(productInventoryManagementService, productSku, warehouseUid, quantity);
	}

	@Override
	public AllocationResult processAllocationEvent(
			final OrderSku orderSku,
			final AllocationEventType eventType,
			final String eventOriginator,
			final int quantity,
			final String reason) {

		final AllocationResult allocationResult = getBean(ContextIdNames.ALLOCATION_RESULT);
		if (orderSku == null) {
			return allocationResult;
		}

		final ProductSku productSku = getProductSkuLookup().findByGuid(orderSku.getSkuGuid());
		InventoryExecutionResult inventoryResult;

		// if always available there is nothing to be allocated/deallocated
		if (productSku.getProduct().getAvailabilityCriteria() == AvailabilityCriteria.ALWAYS_AVAILABLE) {
			inventoryResult = getBean(ContextIdNames.INVENTORY_EXECUTION_RESULT);
			inventoryResult.setQuantity(quantity);
			allocationResult.setInventoryResult(inventoryResult);
			return allocationResult;
		}

		final Order order = orderSku.getShipment().getOrder();
		final Store store = getStoreService().findStoreWithCode(order.getStoreCode());
		final InventoryEventType inventoryEventType = eventType.translateAllocationEvent(quantity);
		final long warehouseUid = store.getWarehouse().getUidPk();
		final int inventoryQtyToProcess = inventoryEventType.preProcessInventoryCommand(productInventoryManagementService, productSku,
				warehouseUid, quantity, orderSku.getAllocatedQuantity());

		// process the inventory update
		inventoryResult = productInventoryManagementService.processInventoryUpdate(productSku, warehouseUid,
				inventoryEventType, eventOriginator, inventoryQtyToProcess, order, reason);

		allocationResult.setInventoryResult(inventoryResult);
		inventoryEventType.postProcessInventoryCommand(productInventoryManagementService, productSku, orderSku.getQuantity(),
				allocationResult);

		return allocationResult;
	}

	private int getAllocatedQty(final ProductSku productSku, final String warehouseCode) {
		final List<Integer> allocatedQty = getPersistenceEngine().retrieveByNamedQuery("ALLOCATED_QTY_FOR_SKU_AND_WAREHOUSE",
				productSku.getSkuCode(),
				warehouseCode);
		if (allocatedQty.size() == 1) {
			return allocatedQty.get(0);
		}
		throw new IllegalStateException("Unable to calculate properly the quantity allocated");
	}

	@Override
	public int getUnallocatedQuantityInStock(final ProductSku productSku, final long warehouseUid) {
		final int qtyInStock = productInventoryManagementService.getAvailableInStockQty(productSku, warehouseUid);
		final int allocatedQty = getAllocatedQty(productSku, warehouseService.getWarehouse(warehouseUid).getCode());

		return qtyInStock - allocatedQty;
	}

	private <T> T getBean(final String beanName) {
		return getBeanFactory().getBean(beanName);
	}

	/**
	 * Sets the {@link ProductInventoryManagementService} service.
	 *
	 * @param productInventoryManagementService the service implementation.
	 */
	public void setProductInventoryManagementService(final ProductInventoryManagementService productInventoryManagementService) {
		this.productInventoryManagementService = productInventoryManagementService;
	}

	/**
	 *
	 * @param warehouseService the warehouse service
	 */
	public void setWarehouseService(final WarehouseService warehouseService) {
		this.warehouseService = warehouseService;
	}

	public void setStoreService(final StoreService storeService) {
		this.storeService = storeService;
	}

	protected StoreService getStoreService() {
		return storeService;
	}

	protected ProductSkuLookup getProductSkuLookup() {
		return productSkuLookup;
	}

	public void setProductSkuLookup(final ProductSkuLookup productSkuLookup) {
		this.productSkuLookup = productSkuLookup;
	}

	protected PersistenceEngine getPersistenceEngine() {
		return persistenceEngine;
	}

	public void setPersistenceEngine(final PersistenceEngine persistenceEngine) {
		this.persistenceEngine = persistenceEngine;
	}

	protected BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
}
