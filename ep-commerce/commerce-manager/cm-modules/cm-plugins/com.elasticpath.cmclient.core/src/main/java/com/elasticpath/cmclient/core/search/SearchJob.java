/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.search;

import java.util.Queue;

import com.elasticpath.service.search.query.SearchCriteria;

/**
 * Defines a search job.
 */
public interface SearchJob {

	/**
	 * Get the start index queue.
	 * 
	 * @return the start index queue.
	 */
	Queue<Integer> getStartIndexQueue();

	/**
	 * Get the search criteria queue.
	 * 
	 * @return the search criteria queue
	 */
	Queue<SearchCriteria> getSearchCriteriaQueue();
	
	/**
	 * Schedule the search job.
	 */
	void schedule();

}