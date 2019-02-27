/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.editors;

import java.util.IdentityHashMap;
import java.util.Set;

/**
 * <p>Holds collections (sets) of added/modified/removed items, keeping the
 * three collections in sync by ensuring that a given item can only be in
 * one collection at a time. This class also guarantees that objects cannot
 * appear in a collection more than once. The Objects in the
 * collections are compared using == rather than the equals() method.</p>
 *
 * <p>We cannot use normal HashSets because we need to make sure that an
 * object can only be in one of the three collections at a time.
 * If we inserted these mutable objects into, say, an addedItems
 * <i>HashSet</i>, then mutated the object and tried to add it to the
 * modifiedItems HashSet, we would not necessarily find the mutated item
 * in the AddedItems HashSet if its hashcode had been changed. Therefore,
 * we must use the IdentityHashMap's KeySet to store the items because it
 * uses the == modifier to check for equality.</p>
 *
 * @param <T> the type of object stored within the table
 */
public class TableItems<T> {
	private final IdentityHashMap<T, ? > addedItems = new IdentityHashMap<T, Object>();

	private final IdentityHashMap<T, ? > modifiedItems = new IdentityHashMap<T, Object>();

	private final IdentityHashMap<T, ? > removedItems = new IdentityHashMap<T, Object>();

	/**
	 * Adds an item to the set of AddedItems.
	 *
	 * @param item the item to add
	 */
	public void addAddedItem(final T item) {
		this.addedItems.put(item, null);
	}

	/**
	 * Determines whether the AddedItems collection contains
	 * the given item.
	 * @param item the item to search for
	 * @return true if the item is in the AddedItems collection, false if not.
	 */
	public boolean containsAddedItem(final T item) {
		return this.addedItems.containsKey(item);
	}

	/**
	 * Deletes an item from the set of AddedItems. If the item should
	 * be added to the RemovedItems list as well, should call
	 * {@link addRemovedItem} instead.
	 *
	 * @param item the item to be deleted
	 */
	public void deleteAddedItem(final T item) {
		this.addedItems.remove(item);
	}

	/**
	 * Adds an item to the set of items modified. If the item was previously added, it's not added
	 * to this set, it remains in the added item set.
	 *
	 * @param item the item that modified
	 */
	public void addModifiedItem(final T item) {
		if (!getAddedItems().contains(item)) {
			this.modifiedItems.put(item, null);
		}
	}

	/**
	 * Deletes an item from the set of ModifiedItems. If the item should
	 * be added to the RemovedItems list as well, should call
	 * {@link addRemovedItem} instead.
	 *
	 * @param item the item to be deleted
	 */
	public void deleteModifiedItem(final T item) {
		this.modifiedItems.remove(item);
	}

	/**
	 * Adds an item to the set of items removed.<br>
	 * The item is also removed from the set of items added (if present) and of items modified (if present).<br>
	 *
	 * @param item the item that was removed
	 */
	public void addRemovedItem(final T item) {
		getModifiedItems().remove(item);
		getAddedItems().remove(item);
		this.removedItems.put(item, null);
	}

	/**
	 * Lists items that have been removed.
	 *
	 * @return items that have been removed
	 */
	public Set<T> getAddedItems() {
		return addedItems.keySet();
	}

	/**
	 * Lists items that have been removed.
	 *
	 * @return items that have been removed
	 */
	public Set<T> getRemovedItems() {
		return removedItems.keySet();
	}

	/**
	 * Lists items that have been added or modified (<i>not</i> removed).
	 *
	 * @return items that have been added or modified
	 */
	public Set<T> getModifiedItems() {
		return modifiedItems.keySet();
	}

	/**
	 * Returns true is all 3 lists are empty.
	 * @return true when all 3 lists are empty 
	 */
	public boolean isAllEmpty() {
		return addedItems.isEmpty() && modifiedItems.isEmpty() && removedItems.isEmpty();
	}
}