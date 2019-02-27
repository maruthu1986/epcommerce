/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.targetedselling.delivery.controller.impl;

import java.util.ArrayList;
import java.util.List;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.controller.impl.AbstractBaseControllerImpl;
import com.elasticpath.cmclient.core.event.EventType;
import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.cmclient.core.event.UIEvent;
import com.elasticpath.cmclient.store.targetedselling.delivery.model.DynamicContentDeliverySearchTabModel;
import com.elasticpath.cmclient.store.targetedselling.delivery.wizard.model.DynamicContentDeliveryModelAdapter;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.targetedselling.DynamicContentDelivery;
import com.elasticpath.service.targetedselling.DynamicContentDeliveryService;
/**
 * Controller for get the list of existing {@link DynamicContentDelivery}. 
 *
 */
public class DynamicContentDeliveryListController extends AbstractBaseControllerImpl<DynamicContentDeliveryModelAdapter> {
	
	private DynamicContentDeliveryService dynamicContentDeliveryService;
	
	private DynamicContentDeliverySearchTabModel model;

	@Override
	public void onEvent(final UIEvent<?> eventObject) {
		
		if (EventType.SEARCH == eventObject.getEventType()) {
			model = (DynamicContentDeliverySearchTabModel) eventObject.getSource();
		}
		if (model == null) {
			return;
		}

		List<DynamicContentDelivery> dcaList = getDynamicContentAssignmentService().findBy(
				model.getName(), 
				model.getDynamicContentName(), 
				model.getContentspaceId());

		List<DynamicContentDeliveryModelAdapter> dcaWrapperList = new ArrayList<>();
		
		for (DynamicContentDelivery  dynamicContentDelivery : dcaList) {
			DynamicContentDeliveryModelAdapter dcaWrapper = new DynamicContentDeliveryModelAdapter(dynamicContentDelivery);
			dcaWrapperList.add(dcaWrapper);
		}
		
		SearchResultEvent<DynamicContentDeliveryModelAdapter> searchResultEvent =
				new SearchResultEvent<>(this,
						dcaWrapperList,
						0,
						dcaWrapperList.size(),
						eventObject.isStartFromFirstPage(),
						eventObject.getEventType());
		
		this.fireEvent(searchResultEvent);		
		
	}
	
	/**
	 * Get the DynamicContentAssignmentService.
	 * @return instance of DynamicContentAssignmentService
	 */
	private DynamicContentDeliveryService getDynamicContentAssignmentService() {

		if (dynamicContentDeliveryService == null) {
			dynamicContentDeliveryService = ServiceLocator.getService(
					ContextIdNames.DYNAMIC_CONTENT_DELIVERY_SERVICE);
		}
		return dynamicContentDeliveryService;
		
	}
	
}