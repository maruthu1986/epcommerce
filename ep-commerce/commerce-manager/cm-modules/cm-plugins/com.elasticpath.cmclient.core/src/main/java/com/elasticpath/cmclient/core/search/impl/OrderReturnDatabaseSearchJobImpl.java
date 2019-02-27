/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.core.search.impl;

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
import com.elasticpath.cmclient.core.search.SearchJob;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.service.order.ReturnAndExchangeService;
import com.elasticpath.service.search.query.OrderReturnSearchCriteria;

/**
 * Search for Orders using database queries.
 */
public class OrderReturnDatabaseSearchJobImpl extends AbstractSearchJobImpl implements SearchJob {

	private static final Logger LOG = Logger.getLogger(OrderReturnDatabaseSearchJobImpl.class);

	private static final int COUNT_UNITS_WORK = 2;
	
	private static final int SEARCH_UNITS_WORK = 5;
	
	private static final int FIRE_ITEMS_WORK = 1;

	private static final int WORK_UNITS = COUNT_UNITS_WORK + SEARCH_UNITS_WORK + FIRE_ITEMS_WORK;
	
	private final ReturnAndExchangeService returnAndExchangeService;

	private final Display display;
	private final String taskName;

	/**
	 * Constructor that takes a search items locator.
	 * 
	 * @param locator locator for items
	 * @param display the display
	 */
	public OrderReturnDatabaseSearchJobImpl(final SearchItemsLocator<OrderReturn> locator, final Display display) {
		super(locator);
		this.display = display;
		returnAndExchangeService = ServiceLocator.getService(ContextIdNames.ORDER_RETURN_SERVICE);
		taskName = CoreMessages.get().SearchProgress_StatusBarMessage_StartSearch;
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		IStatus result = Status.OK_STATUS;
		try {

			monitor.beginTask(taskName, WORK_UNITS);
			checkMonitorCancelled(monitor);
			
			final int startIndex = getStartIndexQueue().poll();
			OrderReturnSearchCriteria searchCriteria;
			synchronized (getSearchCriteriaQueue()) {
				searchCriteria = (OrderReturnSearchCriteria) getCriteria();
			}
			if (searchCriteria.getLocale() == null) {
				display.syncExec(() -> searchCriteria.setLocale(CorePlugin.getDefault().getDefaultLocale()));
			}

			long totalItemsCount = returnAndExchangeService.getOrderReturnCountBySearchCriteria(searchCriteria);
			monitor.worked(COUNT_UNITS_WORK);
			
			if (LOG.isDebugEnabled()) {
				LOG.debug("Searching for items"); //$NON-NLS-1$
			}
			List items = returnAndExchangeService.findOrderReturnBySearchCriteria(
					searchCriteria, startIndex, getPagination());
			monitor.worked(SEARCH_UNITS_WORK);
			if (LOG.isDebugEnabled()) {
				LOG.debug("Searching complete. Found " + items.size() + " items."); //$NON-NLS-1$ //$NON-NLS-2$
			}

			display.asyncExec(() -> getLocator().fireItemsUpdated(items, startIndex, (int) totalItemsCount));
			monitor.worked(FIRE_ITEMS_WORK);
		} finally {
			monitor.done();
		}
		return result;
	}


}
