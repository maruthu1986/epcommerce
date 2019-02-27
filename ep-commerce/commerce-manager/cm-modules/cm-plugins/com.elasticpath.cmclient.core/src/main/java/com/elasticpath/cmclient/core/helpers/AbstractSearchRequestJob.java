/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.helpers;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.core.EpUiException;
import com.elasticpath.cmclient.core.pagination.PaginationInfo;
import com.elasticpath.cmclient.core.search.SearchJob;
import com.elasticpath.cmclient.core.search.impl.SolrSearchJobImpl;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.search.query.SearchCriteria;

/**
 * An abstract search request job class from which all search request job classes should inherit.
 * 
 * @param <T> The type of search this job is implementing
 */
public abstract class AbstractSearchRequestJob<T extends Persistable> implements SearchItemsLocator<T> {

	private SearchCriteria searchCriteria;

	/**
	 * Default constructor.
	 * 
	 * @throws EpUiException if pageSize < 1
	 */
	public AbstractSearchRequestJob() throws EpUiException {
		super();
		if (PaginationInfo.getInstance().getPagination() < 1) {
			throw new EpUiException("Must have at least 1 result per page", null); //$NON-NLS-1$
		}
	}

	/**
	 * Sets the {@link SearchCriteria} for this search jobs.
	 * 
	 * @param searchCriteria the {@link SearchCriteria} for this search job
	 */
	public void setSearchCriteria(final SearchCriteria searchCriteria) {
		this.searchCriteria = searchCriteria;
	}

	/**
	 * Returns the {@link SearchCriteria} for this search jobs.
	 * 
	 * @return the searchCriteria
	 */
	public SearchCriteria getSearchCriteria() {
		return searchCriteria;
	}
	
	/**
	 * Executes a query to the database for searching customers.
	 * 
	 * @param activeShell the Eclipse shell used to view the dialog
	 */
	public void executeSearch(final Shell activeShell) {
		executeSearchFromIndex(activeShell, 0);
	}

	/**
	 * Executes a search for items starting at <code>startIndex</code>.
	 * 
	 * @param activeShell the Eclipse shell used to view the dialog
	 * @param startIndex the start index
	 */
	public void executeSearchFromIndex(final Shell activeShell, final int startIndex) {
		SearchJob searchJob = getSearchJob(Display.getDefault());
		searchJob.getStartIndexQueue().offer(startIndex);
		searchJob.getSearchCriteriaQueue().offer(getSearchCriteria());
		searchJob.schedule();
	}


	/**
	 * Get the search job for this request.
	 * 
	 * @return a {@code SearchJob}
	 * @param display the display
	 */
	protected SearchJob getSearchJob(final Display display) {
		return new SolrSearchJobImpl(this, display);
	}

}
