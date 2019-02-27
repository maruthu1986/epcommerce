/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.targetedselling.delivery.controller.impl;

import java.util.List;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.controller.impl.AbstractBaseControllerImpl;
import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.cmclient.core.event.UIEvent;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.contentspace.ContentSpace;
import com.elasticpath.service.contentspace.ContentSpaceService;
/**
 * 
 * Controller for obtains data about content spaces.
 *
 */
public class ContentSpacesController extends AbstractBaseControllerImpl<ContentSpace> {
	
	private ContentSpaceService contentSpaceService;

	@Override
	public void onEvent(final UIEvent<?> eventObject) {
		List<ContentSpace> assignmentTargetList = getContentSpaceService().findAll();

		SearchResultEvent<ContentSpace> resultEvent =
				new SearchResultEvent<>(this,
						assignmentTargetList,
						0,
						assignmentTargetList.size(),
						eventObject.isStartFromFirstPage(),
						eventObject.getEventType());

		this.fireEvent(resultEvent);
	}

	private ContentSpaceService getContentSpaceService() {
		if (contentSpaceService == null) {
			contentSpaceService = ServiceLocator.getService(ContextIdNames.CONTENTSPACE_SERVICE);
		}
		return contentSpaceService;
	}
	

}
