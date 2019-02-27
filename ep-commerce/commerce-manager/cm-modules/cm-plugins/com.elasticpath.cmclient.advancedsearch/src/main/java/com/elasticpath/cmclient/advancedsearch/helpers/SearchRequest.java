/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.advancedsearch.helpers;

/**
 * The search request for EPQL search.
 */
public class SearchRequest {
	
	private String searchQuery;
	
	private int startIndex;
	
	/**
	 * Constructs empty search request.
	 */
	public SearchRequest() {
		this("", 0); //$NON-NLS-1$
	}

	/**
	 * Constructs the request to EPQL search.
	 * 
	 * @param searchQuery the search query in EPQL form.
	 * @param startIndex the start index for searching
	 */
	public SearchRequest(final String searchQuery, final int startIndex) {
		this.searchQuery = searchQuery;
		this.startIndex = startIndex;
	}

	/**
	 * The search query in EPQL form.
	 * 
	 * @return the searchQuery
	 */
	public String getSearchQuery() {
		return searchQuery;
	}

	/**
	 * The start index for finding objects.
	 * 
	 * @return the startIndex
	 */
	public int getStartIndex() {
		return startIndex;
	}

	/**
	 * Sets the search query in EPQL form.
	 * 
	 * @param searchQuery the searchQuery to set
	 */
	public void setSearchQuery(final String searchQuery) {
		this.searchQuery = searchQuery;
	}

	/**
	 * Sets the start index.
	 * 
	 * @param startIndex the startIndex to set
	 */
	public void setStartIndex(final int startIndex) {
		this.startIndex = startIndex;
	}
}
