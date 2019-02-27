/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.shoppingcart;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Map;

import com.elasticpath.commons.tree.TreeNode;
import com.elasticpath.domain.DatabaseLastModifiedDate;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.persistence.api.Entity;
import com.elasticpath.service.catalog.ProductSkuLookup;

/**
 * Represents a quantity of SKUs in a shopping cart, in progress cart, wish list, etc.
 */
public interface ShoppingItem extends Entity, TreeNode<ShoppingItem>, DatabaseLastModifiedDate {
	/**
	 * Returns the guid of the product sku being purchased by this item.
	 * @return the guid of the product sku being purchased by this item.
	 */
	String getSkuGuid();

	/**
	 * Sets the guid of the product sku being purchased by this item.
	 * @param skuGuid the guid of the product sku being purchased by this item.
	 */
	void setSkuGuid(String skuGuid);

	/**
	 * Get the quantity of this item.
	 * 
	 * @return the quantity
	 */
	int getQuantity();
    
	/**
	 * Get the currency of this item.
	 * 
	 * @return the currency
	 */
	Currency getCurrency();

	/**
	 * Set the price details on the shopping item.
	 * 
	 * @param quantity - the new quantity
	 * @param price - the new price (contains Currency)
	 */
	void setPrice(int quantity, Price price);

	/**
	 * Apply a discount to this shopping item.
	 *
	 * @param discount - the discount amount to apply
	 * @param productSkuLookup a product sku lookup
	 */
	void applyDiscount(BigDecimal discount, ProductSkuLookup productSkuLookup);
	
	/**
	 * Clear discount.
	 */
	void clearDiscount();
	
	/**
	 * Set the amount of tax for this item.
	 * 
	 * @param tax - the tax amount to set
	 */
	void setTaxAmount(BigDecimal tax);

	/**
	 * Returns true if this {@code ShoppingItem} has other items that depend on it such that 
	 * the dependent items cannot exist on their own.
	 * 
	 * @return true if this {@code ShoppingItem} has other items that depend on it.
	 * @param productSkuLookup a product sku lookup
	 */
	boolean hasBundleItems(ProductSkuLookup productSkuLookup);

	/**
	 * Specify that another {@code ShoppingItem} depends on this {@code ShoppingItem}.
	 * 
	 * @param dependentShoppingItem the other, dependent item
	 */
	void addChildItem(ShoppingItem dependentShoppingItem);

	/**
	 * Gets this item's dependent {@code ShoppingItem}s.
	 * 
	 * @return the set of dependent {@code ShoppingItem}s.
	 * @param productSkuLookup a product sku lookup
	 */
	List<ShoppingItem> getBundleItems(ProductSkuLookup productSkuLookup);

	/**
	 * Sets this item's dependent {@code ShoppingItem}s.
	 * 
	 * @param dependentShoppingItems the set of dependent items.
	 */
	void setBundleItems(List<ShoppingItem> dependentShoppingItems);
	
	/**
	 * Assigns {@code value} to {@code name}. Any previous value is replaced.
	 *
	 * @param name The name of the field to assign.
	 * @param value The value to assign to the field.
	 */
	void setFieldValue(String name, String value);
	
	/**
	 * Accesses the field for {@code name} and returns the current value. If the field has not been set
	 * then will return null.
	 *
	 * @param name The name of the field.
	 * @return The current value of the field or null.
	 */
	String getFieldValue(String name);
	
	/**
	 * @return unmodifiable map of all key/value data field pairs
	 */
	Map<String, String> getFields();

	/**
	 * Returns the error message for this item.
	 * 
	 * @return the error message
	 */
	String getErrorMessage();

	/**
	 * Sets the error message for this item.
	 * 
	 * @param message the error message
	 */
	void setErrorMessage(String message);
	
	/**
	 * Returns true if the Product this ShoppingItem represents is
	 * configured by the customer. i.e. this instance is dissimilar to another
	 * instance of this ShoppingItem even if they have the same SKU. For
	 * example, a gift certificate is dissimilar to any other gift certificate
	 * even though they have the same product code, product type and sku. This
	 * dissimilarity is because of customer configuration - i.e. the recipient
	 * email address for a gift certificate.
	 * 
	 * Another example of 'configurable' item is bundle.
	 *  
	 * Two configurable items with the same sku will be added as separate items,
	 * i.e. 
	 *     1 item of Camera and a bag and
	 *     1 item of Camera and a bag
	 * rather than 
	 *     2 items of Camera and a bag
	 *
	 * @param productSkuLookup a ProductSkuLookup
	 * @return True if the ShoppingItem is configurable.
	 */
	boolean isConfigurable(ProductSkuLookup productSkuLookup);

	/**
	 * Returns true if the shoppingItem is a gift certificate.
	 *
	 * @param productSkuLookup a ProductSkuLookup
	 * @return True if the ShoppingItem is a gift certificate
	 */
	boolean isGiftCertificate(ProductSkuLookup productSkuLookup);

	/**
	 * Returns true if the shoppingItem has multiple sku options.
	 *
	 * @param productSkuLookup a ProductSkuLookup
	 * @return True if the ShoppingItem has multiple sku options
	 */
	boolean isMultiSku(ProductSkuLookup productSkuLookup);

	/**
	 * Returns true if the item is multi-sku and is equivalent to a given item with the same sku.
	 * Returns false if the item is not multi-sku, the items are not equal, or the item fields are not equivalent (i.e. they are different items).
	 * @param productSkuLookup a ProductSkuLookup to get the item we will compare.
	 * @param comparisonItem the item to check against.
	 * @return true if the two given multi-sku items are equivalent.
	 */
	boolean isSameMultiSkuItem(ProductSkuLookup productSkuLookup, ShoppingItem comparisonItem);

	/**
	 * Returns true if the item is configurable and is equivalent to a given item with the same sku.
	 * Returns false if the item is not configurable, the items are not equal, or the item fields are not equivalent (i.e. they are different items).
	 * @param productSkuLookup a ProductSkuLookup to get the item we will compare.
	 * @param comparisonItem the item to check against.
	 * @return true if the two items are configurable and equivalent.
	 */
	boolean isSameConfigurableItem(ProductSkuLookup productSkuLookup, ShoppingItem comparisonItem);
	
	/**
	 * Sets field values.
	 * 
	 * @param itemFields item fields
	 */
	void mergeFieldValues(Map<String, String> itemFields);

	/**
	 * Gets the ordering of the this shopping item in the tree hierarchy.
	 * 
	 * @return the ordering
	 */
	int getOrdering();

	/**
	 * Sets the ordering of this shopping item.
	 * 
	 * @param ordering the ordering
	 */
	void setOrdering(int ordering);
	
	/**
	 * Returns flag that shows if any type of discount (e.g. price adjustment, cart/catalog promotions) can be applied to this item.
	 * If the flag is <code>false</code>, the item will not contribute to the eligibility of the cart for promotions.
	 *
	 * @return <code>true</code> if discount can be applied, false otherwise
	 * @param productSkuLookup a product sku lookup
	 */
	boolean isDiscountable(ProductSkuLookup productSkuLookup);

	/**
	 * Returns flag that shows if this item is shippable vs. electronic
	 *
	 * @return <code>true</code> if this item is shippable, false otherwise
	 * @param skuLookup a product sku lookup
	 */
	boolean isShippable(ProductSkuLookup skuLookup);
	
	/**
	 * Returns flag that shows if this item is a ProductBundle.
	 *
	 * @return <code>true</code> if this item is a bundle, false otherwise
	 * @param productSkuLookup a product sku lookup
	 */
	boolean isBundle(ProductSkuLookup productSkuLookup);

	/**
	 * <p>Returns true if this item is a constituent of a bundle.</p>
	 * <p>Note that this method will return {@code false} when this shopping item is a dependent item, even if the parent item is a bundle.</p>
	 *
	 * @return if this shopping item is a bundle constituent
	 */
	boolean isBundleConstituent();

	/**
	 * <p>Indicate whether or not this item is a constituent of a bundle.</p>
	 * <p>Note that the parameter should be {@code false} when this shopping item is a dependent item, even if the parent item is a bundle.</p>
	 *
	 * @param isBundleConstituent indicates if this shopping item is a bundle constituent
	 */
	void setBundleConstituent(boolean isBundleConstituent);

	/**
	 * Accepts a ShoppingCartVisitor. If this item is a bundle then it is visited first and then each of its children accept the visitor.
	 * 
	 * @param visitor The visitor.
	 * @param productSkuLookup a product sku lookup
	 */
	void accept(ShoppingCartVisitor visitor, ProductSkuLookup productSkuLookup);

	/**
	 * Returns {@code true} if this shopping item has a price; otherwise {@code false}.  Note that a price of $0.00 is a valid price and would result
	 * in a return value of {code true}.
	 *
	 * @return {@code true} if this shopping item has a price
	 */
	boolean hasPrice();

}
