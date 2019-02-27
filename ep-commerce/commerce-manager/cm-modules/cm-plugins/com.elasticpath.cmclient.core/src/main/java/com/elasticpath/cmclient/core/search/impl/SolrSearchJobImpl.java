/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.core.search.impl;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.helpers.SearchItemsLocator;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.service.search.index.IndexSearchResult;
import com.elasticpath.service.search.index.IndexSearchService;
import com.elasticpath.service.search.query.SearchCriteria;
import com.elasticpath.service.search.solr.IndexUtility;

/**
 * A Search Job that uses Solr to search.
 */
public class SolrSearchJobImpl extends AbstractSearchJobImpl {

	private static final Logger LOG = Logger.getLogger(SolrSearchJobImpl.class);

	private static final int SEARCH_UNITS_WORK = 3;
	
	private static final int DISPLAY_UNITS_WORK = 5;
	
	private static final int FIRE_ITEMS_WORK = 1;

	private static final int WORK_UNITS = SEARCH_UNITS_WORK + DISPLAY_UNITS_WORK + FIRE_ITEMS_WORK;

	private IndexSearchResult searchResult;
	
	private IndexSearchService indexSearchService;

	private final IndexUtility indexUtility;

	private final Display display;
	private final String convertToObjectsTaskName;
	private final String progressError;
	private final String startSearchTaskName;

	/**
	 * Constructor that takes a search items locator.
	 *
	 * @param locator locator for items
	 * @param display the display
	 */
	public SolrSearchJobImpl(final SearchItemsLocator<?> locator, final Display display) {
		super(locator);
		this.display = display;
		indexSearchService = ServiceLocator.getService(ContextIdNames.INDEX_SEARCH_SERVICE);
		indexUtility = ServiceLocator.getService("indexUtility"); //$NON-NLS-1$
		startSearchTaskName = CoreMessages.get().SearchProgress_StatusBarMessage_StartSearch;
		convertToObjectsTaskName = CoreMessages.get().SearchProgress_StatusBarMessage_ConvertToObjects;
		progressError = CoreMessages.get().SearchProgress_Error;
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		IStatus result = Status.OK_STATUS;
		try {

			monitor.beginTask(startSearchTaskName, WORK_UNITS);
			checkMonitorCancelled(monitor);
			
			final int startIndex = getStartIndexQueue().poll();
			SearchCriteria searchCriteria;
			boolean newSearch = false;
			synchronized (getSearchCriteriaQueue()) {
				newSearch = getSearchCriteriaQueue().peek() != null;
				searchCriteria = getCriteria();
			}
			if (searchCriteria.getLocale() == null) {
				//calling CorePlugin.getDefault will cause an invalid thread access exception
				//so run this piece of code in the UI thread and wait for the result
				display.syncExec(() -> searchCriteria.setLocale(CorePlugin.getDefault().getDefaultLocale()));
			}
			if (newSearch) {
				searchResult = indexSearchService.search(searchCriteria, startIndex, getPagination());
			}
			
			final List<Long> uids = searchResult.getPageResults();
			monitor.worked(SEARCH_UNITS_WORK);

			monitor.setTaskName(convertToObjectsTaskName);

			checkMonitorCancelled(monitor);

			if (LOG.isDebugEnabled()) {
				LOG.debug("Searching for items"); //$NON-NLS-1$
			}
			List items = Collections.emptyList();
			try {
				items = getLocator().getItems(uids);
				items = indexUtility.sortDomainList(uids, items);
				monitor.worked(DISPLAY_UNITS_WORK);
			} catch (final Exception e) {
				LOG.error("Error converting UIDs to items", e); //$NON-NLS-1$
				result =  new Status(IStatus.ERROR,
							CorePlugin.PLUGIN_ID,
							IStatus.ERROR,
						progressError,
							null);
			} finally {
				if (LOG.isDebugEnabled()) {
					LOG.debug("Searching complete. Found " + searchResult.getLastNumFound() + " items."); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}

			final int numFound = searchResult.getLastNumFound();
			final List itemsList = items;
			display.asyncExec(() -> getLocator().fireItemsUpdated(itemsList, startIndex, numFound));
			monitor.worked(FIRE_ITEMS_WORK);
		} finally {
			monitor.done();
		}
		return result;
	}
	
	/**
	 * Set the search Service to use.
	 * Enables easier testing.
	 *
	 * @param searchService the search service.
	 */
	public void setSearchService(final IndexSearchService searchService) {
		this.indexSearchService = searchService;
	}
	
	/**
	 * Get the search Service.
	 *
	 * @return the search service.
	 */
	public IndexSearchService getSearchService() {
		return indexSearchService;
	}

}
