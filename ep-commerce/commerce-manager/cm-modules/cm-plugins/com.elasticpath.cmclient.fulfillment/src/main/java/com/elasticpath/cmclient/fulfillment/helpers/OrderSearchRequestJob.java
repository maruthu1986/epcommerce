/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.fulfillment.helpers;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Display;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.event.EventType;
import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.cmclient.core.helpers.AbstractSearchRequestJob;
import com.elasticpath.cmclient.core.search.SearchJob;
import com.elasticpath.cmclient.core.search.impl.OrderDatabaseSearchJobImpl;
import com.elasticpath.cmclient.fulfillment.event.FulfillmentEventService;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.order.Order;
import com.elasticpath.service.order.OrderService;

/**
 * This class represents a job responsible for retrieving Orders.
 */
public class OrderSearchRequestJob extends AbstractSearchRequestJob<Order> {
	
	private static final Logger LOG = Logger.getLogger(OrderSearchRequestJob.class);
	
	private final OrderService orderService;

	/**
	 * Default constructor.
	 * 
	 */
	public OrderSearchRequestJob() {
		super();
		orderService = ServiceLocator.getService(ContextIdNames.ORDER_SERVICE);
	}

	@Override
	public void fireItemsUpdated(final List<Order> itemList, final int startIndex, final int totalFound) {
		SearchResultEvent<Order> event = new SearchResultEvent<>(this, itemList, startIndex, totalFound, EventType.SEARCH);
		FulfillmentEventService.getInstance().fireOrderSearchResultEvent(event);
	}

	/**
	 * Gets a list of {@link Order} with the given <code>uidList</code>.
	 * 
	 * @param uidList a list of {@link Order} UIDs
	 * @return a list of {@link Order}s
	 */
	@Override
	public List<Order> getItems(final List<Long> uidList) {
		if (LOG.isDebugEnabled()) {
			LOG.debug(String.format("Converting %1$S UID(s) to orders.", uidList.size())); //$NON-NLS-1$
		}
		return orderService.findByUids(uidList);
	}

	// ---- DOCgetSearchJob
	@Override
	protected SearchJob getSearchJob(final Display display) {
		return new OrderDatabaseSearchJobImpl(this, display);
	}
	// ---- DOCgetSearchJob



}
