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
import com.elasticpath.domain.contentspace.DynamicContent;
import com.elasticpath.service.contentspace.DynamicContentService;
/**
 * 
 * Controller for obtains data about dynamic contents and assigned content spaces.
 *
 */
public class DynamicContentsController extends AbstractBaseControllerImpl<DynamicContent> {
	
	private DynamicContentService dynamicContentService;

	@Override
	public void onEvent(final UIEvent<?> eventObject) {
		
		List<DynamicContent> dynamicContentList = getDynamicContentService().findAll();

		SearchResultEvent<DynamicContent> resultEvent =
				new SearchResultEvent<>(this,
						dynamicContentList,
						0,
						dynamicContentList.size(),
						eventObject.isStartFromFirstPage(),
						eventObject.getEventType());

		this.fireEvent(resultEvent);
		
		
	}
	
	/**
	 * Get the DynamicContentService.
	 * @return instance of DynamicContentService
	 */
	private DynamicContentService getDynamicContentService() {
		if (dynamicContentService == null) {
			dynamicContentService = ServiceLocator.getService(
					ContextIdNames.DYNAMIC_CONTENT_SERVICE);			
		}
		return dynamicContentService;
	}
	
	
	

}
