/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.pricelistassignments.controller;

import java.util.List;

import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.controller.impl.AbstractBaseControllerImpl;
import com.elasticpath.cmclient.core.event.EventType;
import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.cmclient.core.event.UIEvent;
import com.elasticpath.cmclient.pricelistassignments.model.PriceListAssigmentsSearchTabModel;
import com.elasticpath.common.dto.pricing.PriceListAssignmentsDTO;
import com.elasticpath.common.pricing.service.PriceListAssignmentHelperService;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.cmuser.CmUser;

/**
 * Perform search operation by search criteria.
 *
 */
public class PriceListAssignmentsSearchController extends AbstractBaseControllerImpl<PriceListAssignmentsDTO> {
	
	private PriceListAssignmentHelperService priceListAssignmentHelperService;
	
	private PriceListAssigmentsSearchTabModel model;
	
	/**
	 * Constructor.
	 */
	public PriceListAssignmentsSearchController() {
		super();
		
	}
	
	
	/**
	 * Perform search operation by search criteria and fire {@link SearchResultEvent}.
	 * @param eventObject carrier for search criteria.
	 */
	@Override
	public void onEvent(final UIEvent<?> eventObject) {

		if (EventType.SEARCH == eventObject.getEventType()) {
			model = (PriceListAssigmentsSearchTabModel) eventObject.getSource();
		}
		if (model == null) {
			return;
		}

		CmUser currentUser = LoginManager.getCmUser();
		
		List<PriceListAssignmentsDTO> resultList = 
			getPriceListAssignmentHelperService().getPriceListAssignmentsDTO(
					model.getCatalogName(), 
					model.getPriceListName(), currentUser);
		
		
		SearchResultEvent<PriceListAssignmentsDTO> searchResultEvent =
				new SearchResultEvent<>(
						eventObject,
						resultList,
						0,
						resultList.size(),
						eventObject.isStartFromFirstPage(),
						eventObject.getEventType());
		
		
		this.fireEvent(searchResultEvent);
			
		
	}	

	
	private PriceListAssignmentHelperService getPriceListAssignmentHelperService() {
		if (priceListAssignmentHelperService == null) {
			priceListAssignmentHelperService = ServiceLocator.getService(ContextIdNames.PRICE_LIST_ASSIGNMENT_HELPER_SERVICE);
		}
		return priceListAssignmentHelperService;
	}

}
