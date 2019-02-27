/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.warehouse.helpers;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Display;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.event.EventType;
import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.cmclient.core.helpers.AbstractSearchRequestJob;
import com.elasticpath.cmclient.core.search.SearchJob;
import com.elasticpath.cmclient.core.search.impl.OrderReturnDatabaseSearchJobImpl;
import com.elasticpath.cmclient.warehouse.event.WarehouseEventService;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.service.order.ReturnAndExchangeService;

/**
 * This class represents a job responsible for retrieving order returns from the database.
 */
public class OrderReturnSearchRequestJob extends AbstractSearchRequestJob<OrderReturn> {

	private static final Logger LOG = Logger.getLogger(OrderReturnSearchRequestJob.class);

	private final ReturnAndExchangeService orderReturnService;

	/**
	 * Default constructor.
	 * 
	 */
	public OrderReturnSearchRequestJob() {
		super();
		orderReturnService = ServiceLocator.getService(ContextIdNames.ORDER_RETURN_SERVICE);
	}

	@Override
	public void fireItemsUpdated(final List<OrderReturn> itemList, final int startIndex, final int totalFound) {
		SearchResultEvent<OrderReturn> event = new SearchResultEvent<>(this, itemList, startIndex, totalFound, EventType.SEARCH);
		WarehouseEventService.getInstance().fireOrderReturnSearchResultEvent(event);
	}

	/**
	 * Gets a list of {@link OrderReturn} with the given <code>uidList</code>.
	 * 
	 * @param uidList a list of {@link OrderReturn} UIDs
	 * @return a list of {@link OrderReturn}s
	 */
	@Override
	public List<OrderReturn> getItems(final List<Long> uidList) {
		if (LOG.isDebugEnabled()) {
			LOG.debug(String.format("Converting %1$S UID(s) to orders.", uidList.size())); //$NON-NLS-1$
			for (Long uid : uidList) {
				LOG.debug(uid);
			}
		}
		return orderReturnService.findByUids(uidList);
	}
	
	@Override
	protected SearchJob getSearchJob(final Display display) {
		return new OrderReturnDatabaseSearchJobImpl(this, display);
	}
}
