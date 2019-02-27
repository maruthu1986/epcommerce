/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.domain.catalog.impl;

import java.util.Map;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import com.elasticpath.common.dto.InventoryDetails;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.ConstituentItem;
import com.elasticpath.domain.catalog.InventoryCalculator;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.service.catalog.ProductInventoryManagementService;

/**
 * Implements {@code InventoryCalculator}.
 */
public class InventoryCalculatorImpl implements InventoryCalculator {
	
	private BeanFactory beanFactory;
	
	@Override
	public InventoryDetails getInventoryDetails(
			final ProductInventoryManagementService productInventoryManagementService,
			final ProductSku productSku, final long warehouseUid) {
		InventoryDetails inventoryDetails = createInventoryDetails();
		
		int availableQuantityInStock = calculateAvailableQuantityInStock(
				productInventoryManagementService, productSku, warehouseUid); 
		
		inventoryDetails.setAvailableQuantityInStock(availableQuantityInStock);
		
		return inventoryDetails;
	}

	/**
	 * Creates an InventoryDetails object.
	 * @return the object.
	 */
	protected InventoryDetails createInventoryDetails() {
		return beanFactory.getBean(ContextIdNames.INVENTORY_DETAILS);
	}

	/**
	 * Calculate the inventory for a normal product and a bundle.
	 */
	private int calculateAvailableQuantityInStock(
			final ProductInventoryManagementService productInventoryManagementService, 
			final ProductSku productSku,
			final long warehouseUid) {
		
			Multiset<String> inventoryRequirementsMultiset = HashMultiset.create();
			addInventoryRequirementsToMap(productSku, inventoryRequirementsMultiset, 1);
			
			int rootInventoryAvailable = Integer.MAX_VALUE;
			if (inventoryRequirementsMultiset.isEmpty()) {
				return rootInventoryAvailable;
			}
			
			Map<String, InventoryDto> inventoryMap = productInventoryManagementService.getInventoriesForSkusInWarehouse(
					inventoryRequirementsMultiset.elementSet(), warehouseUid);
			for (Multiset.Entry<String> mapEntry : inventoryRequirementsMultiset.entrySet()) {
				String skuCode = mapEntry.getElement();
				int inventoryRequired = mapEntry.getCount();
				InventoryDto skuInventory = inventoryMap.get(skuCode);
				int itemInventoryAvailable = 0;
				if (skuInventory != null) {
					int skuInventoryAvailable = skuInventory.getAvailableQuantityInStock();
				
					// Note that Java will round towards zero in this implicit conversion.
					itemInventoryAvailable = skuInventoryAvailable / inventoryRequired;
				}
				rootInventoryAvailable = Math.min(rootInventoryAvailable, itemInventoryAvailable);
			}
			
			return rootInventoryAvailable;
		}

	/**
	 * Adds the inventory requirements for {@productSku} and children to {@code inventoryRequirementsMap}.
	 * @param quantityPerUnit The quantity of this item per unit of the parent. This number is multiplied down the tree.
	 */
	private void addInventoryRequirementsToMap(final ProductSku productSku,
			final Multiset<String> inventoryRequirementsMultiset, final int quantityPerUnit) {
		if (productSku.getProduct() instanceof ProductBundle) {
			ProductBundle bundle = (ProductBundle) productSku.getProduct();
			for (BundleConstituent constituent : bundle.getConstituents()) {
				ConstituentItem child = constituent.getConstituent();
				if (child.getProduct().getAvailabilityCriteria() != AvailabilityCriteria.ALWAYS_AVAILABLE) {
					addInventoryRequirementsToMap(child.getProductSku(), inventoryRequirementsMultiset, quantityPerUnit * constituent.getQuantity());
				}
			}
		} else {
			if (productSku.getProduct().getAvailabilityCriteria() != AvailabilityCriteria.ALWAYS_AVAILABLE) {
				inventoryRequirementsMultiset.add(productSku.getSkuCode(), quantityPerUnit);
			}
		}
	}
	
	/**
	 * @param beanFactory the beanFactory to set
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

}
