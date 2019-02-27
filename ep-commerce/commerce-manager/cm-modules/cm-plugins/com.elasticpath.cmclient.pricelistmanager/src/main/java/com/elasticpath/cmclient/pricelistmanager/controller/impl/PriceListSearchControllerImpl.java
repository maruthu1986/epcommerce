/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
/**
 * 
 */
package com.elasticpath.cmclient.pricelistmanager.controller.impl;

import java.util.Collection;

import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.pricelistmanager.controller.PriceListManagerSearchController;
import com.elasticpath.cmclient.pricelistmanager.event.PriceListSearchEvent;
import com.elasticpath.cmclient.pricelistmanager.model.PriceListManagerSearchResultsModel;
import com.elasticpath.cmclient.pricelistmanager.model.impl.PriceListManagerSearchResultsModelImpl;
import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;
import com.elasticpath.common.pricing.service.PriceListService;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.cmuser.CmUser;

/**
 * Controller for PriceList Management.
 *
 */
public class PriceListSearchControllerImpl implements PriceListManagerSearchController {

	/**
	 * @return a list of all price list descriptors.
	 */
	public Collection<PriceListDescriptorDTO> getPriceLists() {
		CmUser currentUser = LoginManager.getCmUser();
		if (currentUser.isAllPriceListsAccess()) {
			return getPriceListService().getPriceListDescriptors(false);
		}
		return getPriceListService().getPriceListDescriptors(currentUser.getPriceLists());
	}

	/**
	 * Searches for price lists based on the given event's search criteria
	 * and updates the search results model with the results.
	 * Calls {@link #updateSearchResultsModel(List)} and {@link #getPriceLists()}.
	 * @param event the search event
	 */
	@Override
	public void searchPriceList(final PriceListSearchEvent event) {
		updateSearchResultsModel(getPriceLists());
	}
	
	/**
	 * Updates the search results model.
	 * Calls {@link #getSearchResultsModel()}.
	 * @param priceLists the price lists returned as the result of a search
	 */
	void updateSearchResultsModel(final Collection<PriceListDescriptorDTO> priceLists) {
		getSearchResultsModel().setPriceListDescriptorSearchResults(priceLists);
	}
	
	/**
	 * @return the search results model object
	 */
	PriceListManagerSearchResultsModel getSearchResultsModel() {
		return PriceListManagerSearchResultsModelImpl.getInstance();
	}
	
	/**
	 * @return the PriceListService for client applications
	 */
	PriceListService getPriceListService() {
		return ServiceLocator.getService(ContextIdNames.PRICE_LIST_CLIENT_SERVICE);
	}

}
