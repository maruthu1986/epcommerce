/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.order;

import java.math.BigDecimal;
import java.util.Date;

import com.elasticpath.domain.ListenableObject;
import com.elasticpath.domain.catalog.DigitalAsset;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemTaxSnapshot;
import com.elasticpath.money.Money;
import com.elasticpath.service.catalog.ProductSkuLookup;

/**
 * Represents an order for a quantity of SKUs.
 */
public interface OrderSku extends ShoppingItem, ListenableObject {
	
	/**
	 * @return the shipment that this order sku is part of
	 */
	OrderShipment getShipment();

	/**
	 * @param shipment the shipment to set
	 */
	void setShipment(OrderShipment shipment);

	/**
	 * Get the date that this order was created on.
	 * 
	 * @return the created date
	 */
	Date getCreatedDate();

	/**
	 * Set the date that the order is created.
	 * 
	 * @param createdDate the start date
	 */
	void setCreatedDate(Date createdDate);

	/**
	 * Get the CM user who last modified this order sku.
	 * 
	 * @return the CM user
	 */
	CmUser getLastModifiedBy();

	/**
	 * Set the CM User who last modified this order sku.
	 * 
	 * @param createdBy the CM user
	 */
	void setLastModifiedBy(CmUser createdBy);

	/**
	 * Get the productSku SKU.
	 * 
	 * @return the productSku system name
	 */
	String getSkuCode();

	/**
	 * Set the productSku SKU.
	 * 
	 * @param code the productSku system name
	 */
	void setSkuCode(String code);

	/**
	 * Get the unit price for this sku.
	 * 
	 * @return the price
	 * @deprecated Call getUnitPriceCalc().getAmount() instead.
	 */
	@Deprecated
	BigDecimal getUnitPrice();

	/**
	 * Get the unit price as a <code>Money</code> object.
	 * 
	 * @return a <code>Money</code> object representing the unit price
	 * @deprecated Call getUnitPriceCalc().getMoney() instead.
	 */
	@Deprecated
	Money getUnitPriceMoney();

	/**
	 * Set the unit price for this sku.
	 * 
	 * @param price the price
	 */
	void setUnitPrice(BigDecimal price);
	
	/**
	 *
	 * @param discount the discount to set
	 */
	void setDiscountBigDecimal(BigDecimal discount);
	
	/**
	 *
	 * @return a BigDecimal amount
	 */
	BigDecimal getDiscountBigDecimal();

	/**
	 * Get the product's display name.
	 * 
	 * @return the product's display name.
	 */
	String getDisplayName();

	/**
	 * Set the product's display name.
	 * 
	 * @param displayName the product's display name
	 */
	void setDisplayName(String displayName);

	/**
	 * Get the product's option values for display.
	 * 
	 * @return the product's option values for display.
	 */
	String getDisplaySkuOptions();
	
	/**
	 * Set the product's option values for display.
	 * 
	 * @param displaySkuOptions the product's option values for display
	 */
	void setDisplaySkuOptions(String displaySkuOptions);

	/**
	 * Get the product's image path.
	 * 
	 * @return the product's image path.
	 */
	String getImage();

	/**
	 * Set the product's image path.
	 * 
	 * @param image the product's image path
	 */
	void setImage(String image);

	/**
	 * Returns the shipping weight.
	 * 
	 * @return the shipping weight.
	 */
	int getWeight();

	/**
	 * Sets the shipping weight.
	 * 
	 * @param weight the shipping weight to set.
	 */
	void setWeight(int weight);

	/**
	 * Calculates the <code>Money</code> savings if the price has a discount.
	 * 
	 * @return the price savings as a <code>Money</code>
	 */
	Money getDollarSavingsMoney();

	/**
	 * Calculates the <code>BigDecimal</code> savings if any.
	 * 
	 * @return the price savings as a <code>Money</code>
	 */
	BigDecimal getSavings();

	/**
	 * Gets the digital asset belong to this order SKU.
	 * 
	 * @return the digital asset belong to this order SKU
	 */
	DigitalAsset getDigitalAsset();

	/**
	 * Sets the digital asset.
	 * 
	 * @param digitalAsset the digital asset
	 */
	void setDigitalAsset(DigitalAsset digitalAsset);

	/**
	 * Gets the tax code for this order SKU.
	 * 
	 * @return the tax code for this order SKU.
	 */
	String getTaxCode();

	/**
	 * Sets the tax code for this order SKU.
	 * 
	 * @param taxCode the tax code for this order SKU.
	 */
	void setTaxCode(String taxCode);

	/**
	 * Gets the number of items that can possibly be returned for this order sku.
	 *
	 * @return number of items that can be returned.
	 */
	int getReturnableQuantity();
	
	/**
	 * Sets the number of items that can possibly be returned for this order sku.
	 *
	 * @param returnableQuantity number of items that can be returned.
	 */
	void setReturnableQuantity(int returnableQuantity);	
	
	/**
	 * Copy order sku from another <CODE>OrderSku</CODE>.
	 *
	 * @param orderSku order sku which <CODE>this</CODE> copies from
	 * @param productSkuLookup a product sku lookup
	 * @param taxSnapshot the tax-aware pricing snapshot corresponding to the ordersku being copied
	 */
	void copyFrom(OrderSku orderSku, ProductSkuLookup productSkuLookup, ShoppingItemTaxSnapshot taxSnapshot);

	/**
	 * Copy order sku from another <CODE>OrderSku</CODE>.
	 *
	 * @param orderSku order sku which <CODE>this</CODE> copies from
	 * @param productSkuLookup a product sku lookup
	 * @param taxSnapshot the tax-aware pricing snapshot corresponding to the ordersku being copied
	 * @param setPricingInfo if true, copies the pricing info
	 */
	void copyFrom(OrderSku orderSku, ProductSkuLookup productSkuLookup, ShoppingItemTaxSnapshot taxSnapshot, boolean setPricingInfo);

	/**
	 * Set the amount of inventory allocated to this order sku.
	 * Order is not shippable unless it is allocated its order quantity.
	 *
	 * @param qty of this order allocation
	 */
	void setAllocatedQuantity(int qty);
	
	/**
	 * The amount of inventory allocated to this order sku. 
	 *
	 * @return allocation status
	 */
	int getAllocatedQuantity();
	
	/**
	 * Convenience method to check if system has allocated inventory for this order sku.
	 *
	 * @return whether inventory has been allocated fully to this order
	 */
	boolean isAllocated();
	
	/**
	 * Set the changedQuantityAllocated.
	 * changedQuantityAllocated is the amount of allocated quantity increased/decreased based on quantityAllocated
	 *
	 * @param changedQuantityAllocated the amount of allocated quantity increased/decreased upon quanittyAllocated
	 */
	void setChangedQuantityAllocated(int changedQuantityAllocated);
	
	/**
	 * Get the changedQuantityAllocated.
	 * changedQuantityAllocated is the amount of allocated quantity increased/decreased based on quantityAllocated
	 * 
	 * @return <CODE>changedQuantityAllocated</CODE>
	 */
	int getChangedQuantityAllocated();
	
	/**
	 * Use for splitting shipment.
	 * set preOrBackOrderQuantity
	 *
	 * @param preOrBackOrderQuantity preOrBackOrderQuantity
	 */
	void setPreOrBackOrderQuantity(int preOrBackOrderQuantity);
	
	/**
	 * Use for splitting shipment.
	 * get preOrBackOrderQuantity
	 *
	 * @return preOrBackOrderQuantity
	 */
	int getPreOrBackOrderQuantity();
	
	/**
	 * Sets the quantity. 
	 * This method is to be removed when the PriceTier and Quantity can be set together.
	 * @param quantity the quantity to set
	 */
	void setQuantity(int quantity);
	
	/** 
	 *  @return the parent if this is a dependent item, otherwise null
	*/
	OrderSku getParent();

	/** 
	 *  @return the root if this is a dependent item, otherwise null
	*/
	OrderSku getRoot();
	
	/**
	 * Removes {@link ShoppingItem} item from this {@link ShoppingItem}.
	 * 
	 * @param dependentItem {@link ShoppingItem}
	 */
	void removeChildItem(ShoppingItem dependentItem);
	
	/**
	 * @return The total sum charged in respect of a single invoice item in accordance with the terms of delivery. 
	 * 			This amount is the total for this {@code OrderSku} and is not a unit price.
	 * @deprecated Call getPriceCalc().withCartDiscounts().getAmount() instead.
	 */
	@Deprecated
	BigDecimal getInvoiceItemAmount();

}
