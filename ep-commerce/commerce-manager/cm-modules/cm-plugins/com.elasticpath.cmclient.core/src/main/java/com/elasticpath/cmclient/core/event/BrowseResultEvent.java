/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.event;

import java.util.EventObject;
import java.util.List;

/**
 * Search result event that holds information on search results.
 * 
 * @param <T> The type of search this event is implementing
 */
public class BrowseResultEvent<T> extends EventObject {
	private static final long serialVersionUID = 8027986998565103955L;

	private final List<T> itemList;

	private final int startIndex;

	private final int totalNumberFound;

	/**
	 * Default constructor.
	 * 
	 * @param source the event source
	 * @param itemList the list of {@link T} items
	 * @param startIndex the index where the given list starts
	 * @param totalItemsFound the total number of item found
	 */
	public BrowseResultEvent(final Object source, final List<T> itemList, final int startIndex, final int totalItemsFound) {
		super(source);
		this.itemList = itemList;
		this.startIndex = startIndex;
		this.totalNumberFound = totalItemsFound;
	}

	/**
	 * Gets the list of {@link T}s.
	 * 
	 * @return the list of {@link T}s
	 */
	public List<T> getItems() {
		return itemList;
	}

	/**
	 * Gets the index where the list given by {@link #getItems()} starts.
	 * 
	 * @return the index where the list given by {@link #getItems()} starts
	 */
	public int getStartIndex() {
		return startIndex;
	}

	/**
	 * Gets the total number of items found. This may not be the same as size of the list returned
	 * by {@link #getItems()}.
	 * 
	 * @return the total number of items found
	 */
	public int getTotalNumberFound() {
		return totalNumberFound;
	}
}
