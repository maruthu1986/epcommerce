/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.warehouse.editors.inventory;

import com.elasticpath.domain.catalog.InventoryAudit;
import com.elasticpath.inventory.InventoryDto;


/** Class holder for <code>Inventory</code> and <code>InventoryAudit</code>. */
public class InventoryModel {

	private InventoryDto inventoryDto;

	private InventoryAudit inventoryAudit;

	/**
	 * Get Inventory.
	 * 
	 * @return the inventory
	 */
	public InventoryDto getInventory() {
		return inventoryDto;
	}

	/**
	 * Set Inventory.
	 * 
	 * @param inventoryDto the inventory to set
	 */
	public void setInventory(final InventoryDto inventoryDto) {
		this.inventoryDto = inventoryDto;
	}

	/**
	 * Get InventoryAudit.
	 * 
	 * @return the inventoryAudit
	 */
	public InventoryAudit getInventoryAudit() {
		return inventoryAudit;
	}

	/**
	 * Set InventoryAudit.
	 * 
	 * @param inventoryAudit the inventoryAudit to set
	 */
	public void setInventoryAudit(final InventoryAudit inventoryAudit) {
		this.inventoryAudit = inventoryAudit;
	}
}
