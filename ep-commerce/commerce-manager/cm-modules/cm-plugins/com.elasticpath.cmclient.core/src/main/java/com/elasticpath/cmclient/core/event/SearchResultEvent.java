/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.event;

import java.util.EventObject;
import java.util.List;

/**
 * Search result event that holds information on search results.
 * 
 * @param <T> The return type of the search.
 */
public class SearchResultEvent<T> extends EventObject {
	private static final long serialVersionUID = 585314675337734063L;

	private final List<T> itemList;

	private final int startIndex;

	private final int totalNumberFound;
	
	private boolean startFromFirstPage = true;

	private final EventType eventType;
	
	/**
	 * Default constructor.
	 * 
	 * @param source the event source
	 * @param itemList the list of {@link T} items
	 * @param startIndex the index where the given list starts
	 * @param totalItemsFound the total number of item found
	 * @param startFromFirstPage indicate start show results from first page.
	 * @param eventType event type for event
	 */
	public SearchResultEvent(final Object source, 
			final List<T> itemList, 
			final int startIndex, 
			final int totalItemsFound,
			final boolean startFromFirstPage,
			final EventType eventType
			) {
		super(source);
		this.itemList = itemList;
		this.startIndex = startIndex;
		this.totalNumberFound = totalItemsFound;
		this.startFromFirstPage = startFromFirstPage;
		this.eventType = eventType;
	}
	
	/**
	 * Flag, that indicate start show results from first page.
	 * Default true.
	 * @return true if need start show results from first page
	 */
	public boolean isStartFromFirstPage() {
		return startFromFirstPage;
	}

	/**
	 * Default constructor.
	 * 
	 * @param source the event source
	 * @param itemList the list of {@link T} items
	 * @param startIndex the index where the given list starts
	 * @param totalItemsFound the total number of item found
	 * @param eventType event type for event
	 */
	public SearchResultEvent(final Object source, 
			final List<T> itemList, 
			final int startIndex, 
			final int totalItemsFound,
			final EventType eventType) {
		super(source);
		this.itemList = itemList;
		this.startIndex = startIndex;
		this.totalNumberFound = totalItemsFound;
		this.eventType = eventType;
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

	/**
	 * @return the eventType
	 */
	public EventType getEventType() {
		return eventType;
	}
}
