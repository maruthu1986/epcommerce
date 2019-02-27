/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.inventory;

import java.io.Serializable;
import java.util.Objects;

import com.elasticpath.base.exception.EpSystemException;

/**
 * Identifies an Inventory item by it's SKU code and Warehouse ID.
 */
public class InventoryKey implements Serializable, Cloneable {
	
	/**
	 * Serial version uid.
	 */
	private static final long serialVersionUID = 1L;
	
	/** The sku code. */
	private String skuCode;
	
	/** The warehouse uid. */
	private long warehouseUid;

	/**
	 * Instantiates a new inventory key.
	 *
	 * @param skuCode the sku code
	 * @param warehouseUid the warehouse uid
	 */
	public InventoryKey(final String skuCode, final Long warehouseUid) {
		this.skuCode = skuCode;
		this.warehouseUid = warehouseUid;
	}

	/**
	 * Default constructor.
	 */
	public InventoryKey() {
		super();
	}

	@Override
	public int hashCode() {
		return Objects.hash(skuCode, warehouseUid);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}	
		if (obj == null) {
			return false;
		}	
		if (getClass() != obj.getClass()) {
			return false;
		}	
		final InventoryKey other = (InventoryKey) obj;

		return Objects.equals(skuCode, other.skuCode)
			&& warehouseUid == other.warehouseUid;
	}

	/**
	 * Gets the sku code.
	 *
	 * @return the sku code
	 */
	public String getSkuCode() {
		return skuCode;
	}

	/**
	 * Gets the warehouse uid.
	 *
	 * @return the warehouse uid
	 */
	public long getWarehouseUid() {
		return warehouseUid;
	}

	public void setSkuCode(final String skuCode) {
		this.skuCode = skuCode;
	}

	public void setWarehouseUid(final long warehouseUid) {
		this.warehouseUid = warehouseUid;
	}

	@Override
	public String toString() {
		return "InventoryKey [skuCode=" + skuCode + ", warehouseUid="
				+ warehouseUid + "]";
	}
	
	@Override
	@SuppressWarnings("PMD.CloneThrowsCloneNotSupportedException")
	public InventoryKey clone() {
		try {
			return (InventoryKey) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new EpSystemException("can't clone InventoryKey", e);
		}
	}
}
