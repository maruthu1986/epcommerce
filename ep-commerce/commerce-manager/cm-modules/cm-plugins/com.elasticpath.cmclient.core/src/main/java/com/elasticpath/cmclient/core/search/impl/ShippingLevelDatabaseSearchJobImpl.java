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

import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.helpers.SearchItemsLocator;
import com.elasticpath.cmclient.core.search.SearchJob;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.service.search.query.ShippingServiceLevelSearchCriteria;
import com.elasticpath.service.shipping.ShippingServiceLevelService;

/**
 * Search for Shipping Service Levels using database queries.
 */
public class ShippingLevelDatabaseSearchJobImpl extends AbstractSearchJobImpl implements SearchJob {

	private static final Logger LOG = Logger.getLogger(ShippingLevelDatabaseSearchJobImpl.class);
	private static final int COUNT_UNITS_WORK = 2;
	private static final int SEARCH_UNITS_WORK = 5;
	private static final int FIRE_ITEMS_WORK = 1;
	private static final int WORK_UNITS = COUNT_UNITS_WORK + SEARCH_UNITS_WORK + FIRE_ITEMS_WORK;
	private static final String SHIPPING_SERVICE_LEVEL_SERVICE = "shippingServiceLevelService";
	private final ShippingServiceLevelService shippingServiceLevelService;
	private final Display display;
	private final String jobName;

	/**
	 * Constructor that takes a search items locator.
	 *
	 * @param locator locator for items
	 * @param display the display
	 */
	public ShippingLevelDatabaseSearchJobImpl(final SearchItemsLocator<ShippingServiceLevel> locator, final Display display) {
		super(locator);
		this.display = display;
		shippingServiceLevelService = ServiceLocator.getService(SHIPPING_SERVICE_LEVEL_SERVICE);
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
			ShippingServiceLevelSearchCriteria searchCriteria;
			synchronized (getSearchCriteriaQueue()) {
				searchCriteria = (ShippingServiceLevelSearchCriteria) getCriteria();
			}
			monitor.worked(COUNT_UNITS_WORK);
			if (LOG.isDebugEnabled()) {
				LOG.debug("Searching for items"); //$NON-NLS-1$
			}
			Long totalItemsCount = shippingServiceLevelService.findCountByCriteria(searchCriteria);
			List items = shippingServiceLevelService.findByCriteria(searchCriteria, startIndex, getPagination());
			monitor.worked(SEARCH_UNITS_WORK);
			if (LOG.isDebugEnabled()) {
				LOG.debug("Searching complete. Found " + totalItemsCount + " items."); //$NON-NLS-1$ //$NON-NLS-2$
			}
			display.asyncExec(() -> getLocator().fireItemsUpdated(items, startIndex, totalItemsCount.intValue()));
			monitor.worked(FIRE_ITEMS_WORK);
		} finally {
			monitor.done();
		}
		return result;
	}
	// ---- DOCrun
}
