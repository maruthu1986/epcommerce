/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.common.dto;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.service.catalogview.impl.InventoryMessage;


/**
 * Data Transfer Object (DTO) for communicating the details of inventory for a root shopping item.
 */
@SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName") //Field name seems the best to me
public class SkuInventoryDetails implements Serializable {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;
	
	private int availableQuantityInStock;

	private boolean hasSufficientUnallocatedQty = true;

	private InventoryMessage messageCode;

	private AvailabilityCriteria availabilityCriteria;

	private Date stockDate;

	/**
	 * Get physical available quantity currently in stock and can be shipped out right away. <br/>
	 * Formula: quantityOnHand - quantityReserved - quantityAllocated <br/>
	 * When provided for a bundle, this method returns the availableQuantityInStock that can be shipped for the bundle.
	 * This is the smallest availableQuantityInStock with allowance for bundles with multiple quantities of an item.
	 *
	 * @return the available quantity in stock.
	 */
	public int getAvailableQuantityInStock() {
		return availableQuantityInStock;
	}

	/**
	 * @param availableQuantityInStock the new available quantity in stock.
	 */
	public void setAvailableQuantityInStock(final int availableQuantityInStock) {
		this.availableQuantityInStock = availableQuantityInStock;
	}

	/**
	 * @param hasSufficientUnallocatedQty the hasSufficientUnallocatedQty to set
	 */
	public void setHasSufficientUnallocatedQty(final boolean hasSufficientUnallocatedQty) {
		this.hasSufficientUnallocatedQty = hasSufficientUnallocatedQty;
	}

	/**
	 * @return true if there is enough inventory to fulfil the request.
	 */
	public boolean hasSufficientUnallocatedQty() {
		return hasSufficientUnallocatedQty;
	}

	/**
	 * @param messageCode the messageCode to set
	 */
	public void setMessageCode(final InventoryMessage messageCode) {
		this.messageCode = messageCode;
	}

	/**
	 * @return the messageCode
	 */
	public InventoryMessage getMessageCode() {
		return messageCode;
	}

	/**
	 * @param availabilityCriteria the availabilityCriteria to set
	 */
	public void setAvailabilityCriteria(final AvailabilityCriteria availabilityCriteria) {
		this.availabilityCriteria = availabilityCriteria;
	}

	/**
	 * @return the availabilityCriteria
	 */
	public AvailabilityCriteria getAvailabilityCriteria() {
		return availabilityCriteria;
	}

	/**
	 * @param stockDate the restockDate to set
	 */
	public void setStockDate(final Date stockDate) {
		this.stockDate = stockDate;
	}

	/**
	 * @return the restockDate
	 */
	public Date getStockDate() {
		return stockDate;
	}

	@SuppressWarnings("PMD.PrematureDeclaration")
	private int getOrdinal() {
		final int outOfStockOrdinal = 5;
		final int outOfStockWithRestockDateOrdinal = 4;
		final int availableForPreorderOrdinal = 3;
		final int availableForBackorderOrdinal = 2;
		final int instockNotAlwaysAvailableOrdinal = 1;
		final int instockAlwaysAvailableOrdinal = 0;

		InventoryMessage message = getMessageCode();
		if (message == InventoryMessage.OUT_OF_STOCK) {
			return outOfStockOrdinal;
		} else if (message == InventoryMessage.OUT_OF_STOCK_WITH_RESTOCK_DATE) {
			return outOfStockWithRestockDateOrdinal;
		} else if (message == InventoryMessage.AVAILABLE_FOR_PREORDER) {
			return availableForPreorderOrdinal;
		} else if (message == InventoryMessage.AVAILABLE_FOR_BACKORDER) {
			return availableForBackorderOrdinal;
		} else if (message == InventoryMessage.IN_STOCK
				&& !getAvailabilityCriteria().equals(AvailabilityCriteria.ALWAYS_AVAILABLE)) {
			return instockNotAlwaysAvailableOrdinal;
		}
		return instockAlwaysAvailableOrdinal;
	}

	/**
	 * Worse comparison.
	 *
	 * For all availablities,
	 * OUT_OF_STOCK worse than
	 * OUT_OF_STOCK_WITH_RESTOCK_DATE worse than
	 * AVAILABLE_FOR_PREORDER worse than
	 * AVAILABLE_FOR_BACKORDER worse than
	 * IN_STOCK worse than
	 * ALWAYS_AVAILABLE
	 *
	 * When in stock, sku with less availableQuantityInStock is worse.
	 *
	 * When out of stock, later stockDate is worse.
	 *
	 * This operation is not commutative!  If both SkuInventoryDetails are equal, other will be considered worse.
	 *
	 * @param other other SkuInventoryDetails
	 * @return the worst of this or other
	 */
	public boolean worseThan(final SkuInventoryDetails other) {
		if (getOrdinal() == other.getOrdinal()) {
			if (getMessageCode() == InventoryMessage.IN_STOCK) {
				//worst case is smallest inventory
				return getAvailableQuantityInStock() < other.getAvailableQuantityInStock();
			}
			//worst case is latest restock date
			return !(getStockDate() != null && other.getStockDate() != null
					&& getStockDate().compareTo(other.getStockDate()) < 0);
		} else if (getOrdinal() > other.getOrdinal()) {
			return true;
		}

		return false;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.reflectionToString(this);
	}
}
