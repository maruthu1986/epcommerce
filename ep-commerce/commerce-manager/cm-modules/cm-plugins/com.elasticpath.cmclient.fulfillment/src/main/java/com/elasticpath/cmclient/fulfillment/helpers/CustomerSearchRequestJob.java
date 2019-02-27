/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.fulfillment.helpers;

import java.util.List;

import org.apache.log4j.Logger;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.event.EventType;
import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.cmclient.core.helpers.AbstractSearchRequestJob;
import com.elasticpath.cmclient.fulfillment.event.FulfillmentEventService;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.service.customer.CustomerService;

/**
 * This class represents a job responsible for retrieving customers from the database.
 */
public class CustomerSearchRequestJob extends AbstractSearchRequestJob<Customer> {
	
	private static final Logger LOG = Logger.getLogger(CustomerSearchRequestJob.class);

	private final CustomerService customerService;

	/**
	 * Default constructor.
	 * 
	 */
	public CustomerSearchRequestJob() {
		super();
		customerService = ServiceLocator.getService(ContextIdNames.CUSTOMER_SERVICE);
	}

	@Override
	public void fireItemsUpdated(final List<Customer> itemList, final int startIndex, final int totalFound) {
		SearchResultEvent<Customer> event = new SearchResultEvent<>(this, itemList, startIndex, totalFound, EventType.SEARCH);
		FulfillmentEventService.getInstance().fireCustomerSearchResultEvent(event);
	}

	/**
	 * Gets a list of {@link Customer} with the given <code>uidList</code>.
	 * 
	 * @param uidList a list of {@link Customer} UIDs
	 * @return a list of {@link Customer}s
	 */
	@Override
	public List<Customer> getItems(final List<Long> uidList) {
		if (LOG.isDebugEnabled()) {
			LOG.debug(String.format("Converting %1$S UID(s) to customers.", uidList.size())); //$NON-NLS-1$
		}
		return customerService.findByUids(uidList);
	}
}
