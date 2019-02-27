/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.advancedsearch.helpers;

import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;

import com.elasticpath.cmclient.advancedsearch.AdvancedSearchPlugin;
import com.elasticpath.cmclient.advancedsearch.service.EPQLSearchService;
import com.elasticpath.cmclient.advancedsearch.service.impl.EPQLSearchServiceImpl;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.EpUiException;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.ql.parser.SearchExecutionException;
import com.elasticpath.search.searchengine.EpQlSearchResult;
import com.elasticpath.service.search.solr.IndexUtility;

/**
 * This class represents a job responsible for retrieving products from the database.
 * 
 * @param <T> The type of search this job is implementing
 */
public abstract class AbstractAdvancedSearchRequestJob<T extends Persistable> {

	private static final Logger LOG = Logger.getLogger(AbstractAdvancedSearchRequestJob.class);

	private final AdvancedSearchJob advancedSearchJob = new AdvancedSearchJob(Display.getDefault());

	private final SearchRequest searchRequest;

	private String listenerId;

	private int pageSize;

	/**
	 * Constructs the advanced search request job.
	 * 
	 * @param pageSize the page size for displaying objects.
	 */
	AbstractAdvancedSearchRequestJob(final int pageSize) {
		searchRequest = new SearchRequest();
		setPageSize(pageSize);
	}

	/**
	 * Gets the list of items corresponding to the given <code>uidList</code>.
	 * 
	 * @param uidList a list of UIDs
	 * @return a list of items corresponding to the given UIDs
	 */
	protected abstract List<T> getItems(final List<Long> uidList);

	/**
	 * Fires an event that the given <code>itemList</code> are results returned from a search.
	 * 
	 * @param itemList the list of items representing returned results
	 * @param startIndex the start index used to get the items
	 * @param totalFound the total number of items found
	 */
	protected abstract void fireItemsUpdated(final List<T> itemList, final int startIndex, final int totalFound);

	/**
	 * Executes a query to the database for searching objects.
	 */
	public void executeSearch() {
		executeSearch(0);
	}

	/**
	 * Executes search from <code>startIndex</code>.
	 * 
	 * @param startIndex the start index
	 */
	public void executeSearch(final int startIndex) {
		searchRequest.setStartIndex(startIndex);
		advancedSearchJob.getAdvancedSearchRequestQueue().offer(searchRequest);
		advancedSearchJob.schedule();
	}

	/**
	 * Sets the page size to use for this request job.
	 * 
	 * @param pageSize the page size to use for this request job
	 */
	public final void setPageSize(final int pageSize) {
		if (pageSize < 1) {
			throw new EpUiException("Must have at least 1 result per page", null); //$NON-NLS-1$
		}
		this.pageSize = pageSize;
	}

	/**
	 * Gets the page size for this request job.
	 * 
	 * @return the page size
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * Gets the listener id that should be notified about search results.
	 * 
	 * @return the listenerId or null if all listeners should be notified
	 */
	public String getListenerId() {
		return listenerId;
	}

	/**
	 * Sets the listener id that should be notified about search results, in case of null argument all listeners will be notified.
	 * 
	 * @param listenerId the listenerId to set
	 */
	public void setListenerId(final String listenerId) {
		this.listenerId = listenerId;
	}

	/**
	 * Gets the advanced search criteria in EPQL format.
	 * 
	 * @return the advanced search criteria.
	 */
	public String getAdvancedSearchCriteria() {
		return searchRequest.getSearchQuery();
	}

	/**
	 * Sets the advanced search criteria.
	 * 
	 * @param advancedSearchCriteria the criteria in EPQL format
	 */
	public void setAdvancedSearchCriteria(final String advancedSearchCriteria) {
		searchRequest.setSearchQuery(advancedSearchCriteria);
	}

	/**
	 * The search job for advanced search.
	 */
	private class AdvancedSearchJob extends Job {

		private static final int SEARCH_UNITS_WORK = 3;

		private static final int DISPLAY_UNITS_WORK = 5;

		private static final int FIRE_ITEMS_WORK = 1;

		private static final int WORK_UNITS = SEARCH_UNITS_WORK + DISPLAY_UNITS_WORK + FIRE_ITEMS_WORK;

		private final IndexUtility indexUtility = (IndexUtility) ServiceLocator.getService("indexUtility"); //$NON-NLS-1$

		private final EPQLSearchService searchService = new EPQLSearchServiceImpl();

		private final Queue<SearchRequest> advancedSearchRequestQueue = new LinkedBlockingQueue<>();

		private EpQlSearchResult searchResult;

		private SearchRequest searchRequestInternal;

		private final Display display;

		private final String statusBarMessageStartSearch = CoreMessages.get().SearchProgress_StatusBarMessage_StartSearch;
		private final String statusBarMessageConvertToObjects = CoreMessages.get().SearchProgress_StatusBarMessage_ConvertToObjects;
		private final String searchProgressError = CoreMessages.get().SearchProgress_Error;

		AdvancedSearchJob(final Display display) {
			super(CoreMessages.get().SearchProgress_JobTitle);
			this.display = display;
		}

		@Override
		protected IStatus run(final IProgressMonitor monitor) {
			IStatus result = Status.OK_STATUS;
			try {
				monitor.beginTask(statusBarMessageStartSearch, WORK_UNITS);
				
				if (monitor.isCanceled()) {
					return Status.CANCEL_STATUS;
				}

				performSearchRequest();

				monitor.worked(SEARCH_UNITS_WORK);
				monitor.setTaskName(statusBarMessageConvertToObjects);
				
				if (monitor.isCanceled()) {
					return Status.CANCEL_STATUS;
				}

				LOG.debug("Searching for items"); //$NON-NLS-1$

				List<T> items = Collections.emptyList();
				try {
					final List<Long> results = searchResult.getSearchResults(); 
					items = getItems(results);
					items = indexUtility.sortDomainList(results, items);					
				} catch (final Exception e) {
					LOG.error("Error converting UIDs to items", e); //$NON-NLS-1$
					result = new Status(IStatus.ERROR, AdvancedSearchPlugin.PLUGIN_ID, IStatus.ERROR, searchProgressError, null);
				} finally {
					LOG.debug("Searching complete. Found " + searchResult.getNumFound() + " items."); //$NON-NLS-1$ //$NON-NLS-2$
				}
				monitor.worked(DISPLAY_UNITS_WORK);

				final List<T> itemsList = items;
				display.asyncExec(() -> fireItemsUpdated(itemsList, searchRequestInternal.getStartIndex(), searchResult.getNumFound()));

				monitor.worked(FIRE_ITEMS_WORK);

			} catch (SearchExecutionException exception) {				
				result = new Status(IStatus.ERROR, AdvancedSearchPlugin.PLUGIN_ID, exception.getLocalizedMessage());
			} finally {
				monitor.done();
			}
			return result;
		}

		private void performSearchRequest() {
			synchronized (advancedSearchRequestQueue) {
				if (advancedSearchRequestQueue.peek() != null) {
					searchRequestInternal = advancedSearchRequestQueue.poll();
					searchResult = searchService.search(searchRequestInternal.getSearchQuery(), searchRequestInternal.getStartIndex(),
							getPageSize());
				}
			}
		}

		Queue<SearchRequest> getAdvancedSearchRequestQueue() {
			return advancedSearchRequestQueue;
		}

	}
}
