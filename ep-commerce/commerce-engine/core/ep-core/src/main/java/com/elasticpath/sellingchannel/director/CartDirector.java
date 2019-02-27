/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.sellingchannel.director;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.shoppingcart.ShoppingCartRefresher;

/**
 * Business domain delegate of the functionality required to manage cart items within a shopping cart.
 *
 * Note that CartDirector contains many methods that should normally only be run within a transaction,
 * but CartDirector itself is <em>not</em> transactional and does not persist the cart.
 * To access CartDirector functionality with persistence and container managed transactions, see {@link CartDirectorService}.
 *
 * @see com.elasticpath.sellingchannel.director.CartDirectorService
 */
public interface CartDirector extends ShoppingCartRefresher {

	/**
	 * Creates a ShoppingItem with given sku and quantity.  Also verifies that the sku is purchasable in the store.
	 *
	 * @param skuCode the sku code
	 * @param store the store, for validation purposes
	 * @param quantity the quantity to purchase
	 *
	 * @return the shopping item
	 * @throws com.elasticpath.base.exception.EpServiceException if the sku is not purchasable
	 */
	ShoppingItem createShoppingItem(String skuCode, Store store, int quantity) throws EpServiceException;

	/**
	 * @param shoppingCart {@code ShoppingCart}
	 * @param dto of new item
	 * @return the {@link ShoppingItem} that's added to the cart
	 */
	ShoppingItem addItemToCart(ShoppingCart shoppingCart, ShoppingItemDto dto);

	/**
	 * @param shoppingCart {@code ShoppingCart}
	 * @param dto of new item
	 * @param parentItem whether the item is a dependent or not
	 * @return the {@link ShoppingItem} that's added to the cart
	 */
	ShoppingItem addItemToCart(ShoppingCart shoppingCart, ShoppingItemDto dto, ShoppingItem parentItem);

	/**
	 * @param shoppingCart {@code ShoppingCart}
	 * @param itemId id of cart item for update
	 * @param dto of new item
	 * @return the ShoppingItem that's updated as a result of the given dto
	 */
	ShoppingItem updateCartItem(ShoppingCart shoppingCart, long itemId, ShoppingItemDto dto);

	/**
	 * Finds the parent of a given shopping item, if present.
	 *
	 * @param cartItems cart items
	 * @param child shopping child to get parents for
	 * @return parent item
	 */
	Optional<ShoppingItem> getParent(Collection<ShoppingItem> cartItems, ShoppingItem child);

	/**
	 * Check if the specified child is dependent on an element in the list.
	 *
	 * @param cartItems list to check for the parent
	 * @param child the child
	 * @return true if there is a parent in the list
	 */
	boolean isDependent(List<ShoppingItem> cartItems, ShoppingItem child);

	/**
	 * Reorders and renumbers the shopping cart items in the given shopping cart.
	 * After completion, should be in order, and the order numbers should be
	 * monotonically increasing.
	 *
	 * @param shoppingCart the shopping cart to reorder
	 */
	void reorderItems(ShoppingCart shoppingCart);

	/**
	 * Clears all items from the shopping cart.
	 *
	 * @param shoppingCart shopping cart to clear
	 */
	void clearItems(ShoppingCart shoppingCart);

	/**
	 * Checks for equivalency between two shopping items.
	 * If the items are single sku, return check for sku guid equivalency.
	 * For multi skus, identify whether they are the same multi sku items.
	 *
	 * @param shoppingItem a shopping item.
	 * @param existingItem a shopping item to compare with.
	 * @return boolean representing whether the items are equivalent
	 */
	boolean itemsAreEqual(ShoppingItem shoppingItem, ShoppingItem existingItem);

}