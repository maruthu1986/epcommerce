/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.domain.shoppingcart;

/**
 * Represents a wish list.
 */
public interface WishList extends ShoppingList {

	/**
	 * Add item into wish list.
	 *
	 * @param item the item to be added
	 * @return the shopping item added
	 */
	ShoppingItem addItem(ShoppingItem item);

	/**
	 * Remove item from wish list by sku <em>guid</em>.
	 *
	 * @param skuGuid the guid (not code) of the sku of the item to be removed
	 */
	void removeItemBySkuGuid(String skuGuid);

	/**
	 * Remove item from wish list by wish list item uidpk.
	 *
	 * @param wishListItemGuid the guid of the wish list item
	 */
	void removeItem(String wishListItemGuid);

	/**
	 * Gets the {@link Store} code for this domain model object.
	 *
	 * @return the unique identifier.
	 */
	String getStoreCode();

	/**
	 * Sets the {@link Store} code for this domain model object.
	 *
	 * @param storeCode the new storeCode.
	 */
	void setStoreCode(String storeCode);

}
