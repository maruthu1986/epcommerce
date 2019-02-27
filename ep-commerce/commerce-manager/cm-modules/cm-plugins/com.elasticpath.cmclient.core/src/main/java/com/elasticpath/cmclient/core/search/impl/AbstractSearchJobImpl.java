/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.core.search.impl;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.jobs.Job;

import com.elasticpath.cmclient.core.pagination.PaginationInfo;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.helpers.SearchItemsLocator;
import com.elasticpath.cmclient.core.search.SearchJob;
import com.elasticpath.service.search.query.SearchCriteria;

/**
 * Functionality common to all search jobs.
 */
public abstract class AbstractSearchJobImpl extends Job implements SearchJob {

	private final Queue<Integer> startIndexQueue = new LinkedBlockingQueue<Integer>();
	
	private final Queue<SearchCriteria> searchCriteriaQueue = new LinkedBlockingQueue<SearchCriteria>();
	
	private final SearchItemsLocator< ? > locator;

	private final int pagination;

	private SearchCriteria lastCriteria;
	

	/**
	 * Constructor that takes a search items locator.
	 * 
	 * @param locator locator for items 
	 */
	public AbstractSearchJobImpl(final SearchItemsLocator< ? > locator) {
		super(CoreMessages.get().SearchProgress_JobTitle);
		this.locator = locator;
		pagination = PaginationInfo.getInstance().getPagination();
	}
	
	@Override
	public Queue<Integer> getStartIndexQueue() {
		return startIndexQueue;
	}

	@Override
	public Queue<SearchCriteria> getSearchCriteriaQueue() {
		return searchCriteriaQueue;
	}
	
	/**
	 * Get the Search items locator.
	 * 
	 * @return a {@link SearchItemsLocator}
	 */
	public SearchItemsLocator< ? > getLocator() {
		return locator;
	}

	/**
	 * Check if the job progress monitor is cancelled. 
	 * 
	 * @param monitor the progress monitor
	 */
	protected void checkMonitorCancelled(final IProgressMonitor monitor) {
		if (monitor.isCanceled()) {
			throw new OperationCanceledException();
		}
	}
	
	/**
	 * Get the search criteria from the queue.
	 * 
	 * @return the search criteria
	 */
	protected SearchCriteria getCriteria() {
		synchronized (getSearchCriteriaQueue()) {
			// make sure that we don't run off the end of the queue by wanting another search
			// from the same search criteria
			if (getSearchCriteriaQueue().peek() != null) {
				lastCriteria = getSearchCriteriaQueue().poll();
			}
			return lastCriteria;
		}
	}

	/**
	 * Get the pagination value.
	 *
	 * @return the pagination value
	 */
	public int getPagination() {
		return pagination;
	}
}
