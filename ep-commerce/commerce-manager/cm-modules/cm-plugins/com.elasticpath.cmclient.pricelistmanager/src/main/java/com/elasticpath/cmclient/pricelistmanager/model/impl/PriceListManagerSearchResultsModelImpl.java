/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.pricelistmanager.model.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.elasticpath.cmclient.core.CmSingletonUtil;
import com.elasticpath.cmclient.core.event.EventType;
import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.cmclient.pricelistmanager.event.PricingEventService;
import com.elasticpath.cmclient.pricelistmanager.model.PriceListManagerSearchResultsModel;
import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;

/**
 * Model to hold price list search results.
 */
public final class PriceListManagerSearchResultsModelImpl implements PriceListManagerSearchResultsModel {

	private Collection<PriceListDescriptorDTO> descriptorSearchResults = new ArrayList<>();
	
	private PriceListManagerSearchResultsModelImpl() {
		//singleton
	}
	
	/**
	 * @return the singleton instance of this class.
	 */
	public static PriceListManagerSearchResultsModel getInstance() {
		return CmSingletonUtil.getSessionInstance(PriceListManagerSearchResultsModelImpl.class);
	}

	@Override
	public void setPriceListDescriptorSearchResults(final Collection<PriceListDescriptorDTO> priceListDescriptors) {
		this.descriptorSearchResults = priceListDescriptors;
		fireSearchResultUpdated();
	}
	
	/**
	 * Creates an event indicating that price list search results have been updated,
	 * and notifies the event service.
	 */
	void fireSearchResultUpdated() {
		List<PriceListDescriptorDTO> searchResult = new ArrayList<>(getPriceListDescriptorSearchResults());
		SearchResultEvent<PriceListDescriptorDTO> event =
				new SearchResultEvent<>(this, searchResult, 0, searchResult.size(), EventType.SEARCH);
		getEventService().fireSearchResultUpdatedEvent(event);
	}
	
	/**
	 * @return the event service
	 */
	PricingEventService getEventService() {
		return PricingEventService.getInstance();
	}

	@Override
	public Collection<PriceListDescriptorDTO> getPriceListDescriptorSearchResults() {
		return this.descriptorSearchResults;
	}
}
