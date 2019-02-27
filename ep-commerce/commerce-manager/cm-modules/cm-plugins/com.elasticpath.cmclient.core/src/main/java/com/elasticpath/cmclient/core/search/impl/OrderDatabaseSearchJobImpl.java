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
import com.elasticpath.domain.order.Order;
import com.elasticpath.service.order.OrderService;
import com.elasticpath.service.search.query.OrderSearchCriteria;

/**
 * Search for Orders using database queries.
 */
public class OrderDatabaseSearchJobImpl extends AbstractSearchJobImpl implements SearchJob {

	private static final Logger LOG = Logger.getLogger(OrderDatabaseSearchJobImpl.class);

	private static final int COUNT_UNITS_WORK = 2;
	
	private static final int SEARCH_UNITS_WORK = 5;
	
	private static final int FIRE_ITEMS_WORK = 1;

	private static final int WORK_UNITS = COUNT_UNITS_WORK + SEARCH_UNITS_WORK + FIRE_ITEMS_WORK;
	
	private final OrderService orderService;
	private final Display display;
	private final String jobName;

	/**
	 * Constructor that takes a search items locator.
	 * 
	 * @param locator locator for items
	 * @param display the display
	 */
	public OrderDatabaseSearchJobImpl(final SearchItemsLocator<Order> locator, final Display display) {
		super(locator);
		this.display = display;
		orderService = ServiceLocator.getService(ContextIdNames.ORDER_SERVICE);
		jobName = CoreMessages.get().SearchProgress_StatusBarMessage_StartSearch;
	}

	// ---- DOCrun
	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		IStatus result = Status.OK_STATUS;
		try {

			monitor.beginTask(jobName, WORK_UNITS);
			checkMonitorCancelled(monitor);
			
			final int startIndex = getStartIndexQueue().poll();
			OrderSearchCriteria searchCriteria;
			synchronized (getSearchCriteriaQueue()) {
				searchCriteria = (OrderSearchCriteria) getCriteria();
			}
			if (searchCriteria.getLocale() == null) {
				display.syncExec(() -> searchCriteria.setLocale(CorePlugin.getDefault().getDefaultLocale()));
			}

			long totalItemsCount = orderService.getOrderCountBySearchCriteria(searchCriteria);
			monitor.worked(COUNT_UNITS_WORK);
			
			if (LOG.isDebugEnabled()) {
				LOG.debug("Searching for items"); //$NON-NLS-1$
			}
			List items = orderService.findOrdersBySearchCriteria(searchCriteria, startIndex, getPagination());
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
	// ---- DOCrun


}
