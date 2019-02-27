/**
 * Copyright (c) Elastic Path Software Inc., 2012
 */
package com.elasticpath.domain;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import com.elasticpath.domain.shipping.ShipmentType;
import com.elasticpath.domain.shoppingcart.ShoppingItem;

/**
 * Operations that can be performed on a ShoppingItemContainer.
 *
 * @param <T> the type of ShoppingItem this container contains
 */
public interface ShoppingItemContainer<T extends ShoppingItem> {

	/**
	 * Returns the shopping item which matches the given GUID.
	 *
	 * @param itemGuid The guid of the shopping item to find.
	 * @return The requested shopping item, or null if it cannot be found.
	 */
	T getShoppingItemByGuid(String itemGuid);

	/**
	 * <p>Returns an list of the root shopping items, ordered by {@link ShoppingItem#getOrdering()}.</p>
	 * <p>A shopping cart contains a collection of items, each of which may contain child shopping items. Root items are those at the top level,
	 * that have no parent.</p>
	 * <p>Child shopping items may be navigated to by inspecting each member of this collection</p>
	 * @return an ordered list of the root shopping items
	 * @see ShoppingItem#getChildren()
	 */
	List<T> getRootShoppingItems();

	/**
	 * Returns a collection of all shopping items from all depths of the shopping item tree, including dependent items, bundles, and bundle
	 * constituents.
	 *
	 * @return a collection of shopping items
	 * @see #getRootShoppingItems()
	 */
	Collection<T> getAllShoppingItems();

	/**
	 * Returns a collection of all shopping items from all depths of the shopping item tree, filtered by the given predicate.
	 *
	 * @param shoppingItemPredicate the predicate with which to filter the shopping items
	 * @return all matching shopping items
	 * @see #getAllShoppingItems()
	 * @see #getRootShoppingItems()
	 */
	Collection<T> getShoppingItems(Predicate<T> shoppingItemPredicate);

	/**
	 * Return the shipment types that occur on items within the container.
	 *
	 * @return the shipment types
	 */
	Set<ShipmentType> getShipmentTypes();

}
