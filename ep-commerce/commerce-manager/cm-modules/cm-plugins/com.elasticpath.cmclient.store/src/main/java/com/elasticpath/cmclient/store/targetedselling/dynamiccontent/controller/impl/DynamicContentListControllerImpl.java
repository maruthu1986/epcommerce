/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.targetedselling.dynamiccontent.controller.impl;

import java.util.List;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.controller.impl.AbstractBaseControllerImpl;
import com.elasticpath.cmclient.core.event.EventType;
import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.cmclient.core.event.UIEvent;
import com.elasticpath.cmclient.store.targetedselling.dynamiccontent.model.DynamicContentSearchTabModel;
import com.elasticpath.cmclient.store.targetedselling.dynamiccontent.model.impl.AssignedStatus;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.contentspace.DynamicContent;
import com.elasticpath.service.contentspace.DynamicContentService;
import com.elasticpath.service.targetedselling.DynamicContentDeliveryService;
/**
 * Controller for get the list of existing dynamic content spaces. 
 *
 */
public class DynamicContentListControllerImpl 
		extends    AbstractBaseControllerImpl<DynamicContent> {
	
	private DynamicContentSearchTabModel model;

	@Override
	public void onEvent(final UIEvent<?> eventObject) {
		
		if (EventType.SEARCH == eventObject.getEventType()) {
			model = (DynamicContentSearchTabModel) eventObject.getSource();
		}
		if (model == null) {
			return;
		}

		DynamicContentService dynamicContentService = getDynamicContentService();
		List<DynamicContent> collection = dynamicContentService.findByNameLike(model.getName());
		final String nameToSearch = model.getName();

		DynamicContentDeliveryService dcaService = getDynamicContentAssignmentService();
		if (model.getAssignedStatus() == AssignedStatus.ASSIGNED) {
			collection = dcaService.findAssignedDynamicContentByPartialName(nameToSearch);
		} else if (model.getAssignedStatus() == AssignedStatus.NOTASSIGNED) { 
			collection = dcaService.findUnAssignedDynamicContentByPartialName(nameToSearch);
		} else {
			collection = dynamicContentService.findByNameLike(nameToSearch);
		}

		SearchResultEvent<DynamicContent> resultEvent =
				new SearchResultEvent<>(this,
						collection,
						0,
						collection.size(),
						eventObject.isStartFromFirstPage(), eventObject.getEventType());

		this.fireEvent(resultEvent);
	}
	
	private DynamicContentDeliveryService getDynamicContentAssignmentService() {
		return ServiceLocator.getService(ContextIdNames.DYNAMIC_CONTENT_DELIVERY_SERVICE);
	}
	
	private DynamicContentService getDynamicContentService() {
		return ServiceLocator.getService(ContextIdNames.DYNAMIC_CONTENT_SERVICE);
	}
}
