/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.shipping.helpers;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Display;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.event.EventType;
import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.cmclient.core.helpers.AbstractSearchRequestJob;
import com.elasticpath.cmclient.core.search.SearchJob;
import com.elasticpath.cmclient.core.search.impl.ShippingLevelDatabaseSearchJobImpl;
import com.elasticpath.cmclient.store.shipping.events.ShippingLevelsEventService;
import com.elasticpath.commons.constants.EpShippingContextIdNames;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.service.shipping.ShippingServiceLevelService;

/**
 * This class represents a job responsible for retrieving Shipping ServiceL levels from the database.
 */
public class ShippingLevelSearchRequestJob extends AbstractSearchRequestJob<ShippingServiceLevel> {

	private static final Logger LOG = Logger.getLogger(ShippingLevelSearchRequestJob.class);

	private final ShippingServiceLevelService shippingServiceLevelService;

	/**
	 * Default constructor.
	 */
	public ShippingLevelSearchRequestJob() {
		super();
		this.shippingServiceLevelService = ServiceLocator.getService(
				EpShippingContextIdNames.SHIPPING_SERVICE_LEVEL_SERVICE);
	}

	@Override
	public void fireItemsUpdated(final List<ShippingServiceLevel> itemList, final int startIndex, final int totalFound) {
		SearchResultEvent<ShippingServiceLevel> event =
				new SearchResultEvent<>(this, itemList, startIndex, totalFound, EventType.SEARCH);
		ShippingLevelsEventService.getInstance().fireShippingLevelsSearchResultEvent(event);
	}

	@Override
	public List<ShippingServiceLevel> getItems(final List<Long> uidList) {
		if (LOG.isDebugEnabled()) {
			LOG.debug(String.format("Converting %1$S UID(s) to ShippingServiceLevels.", uidList.size())); //$NON-NLS-1$
		}
		return new ArrayList<>(shippingServiceLevelService.findByUids(uidList));
	}

	@Override
	protected SearchJob getSearchJob(final Display display) {
		return new ShippingLevelDatabaseSearchJobImpl(this, display);
	}
}
