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
import com.elasticpath.domain.targetedselling.DynamicContentDelivery;
import com.elasticpath.service.targetedselling.DynamicContentDeliveryService;

/**
 * DynamicContentDeliveryControllerImpl
 * - Controller to get the dynamic content deliveries.
 *
 */
public class DynamicContentDeliveryControllerImpl extends AbstractBaseControllerImpl<DynamicContentDelivery> {
	
	private DynamicContentDeliveryService dynamicContentService;

	@Override
	public void onEvent(final UIEvent<?> eventObject) {
		
		List<DynamicContentDelivery> dynamicContentDeliveryList = getService().findAll();

		SearchResultEvent<DynamicContentDelivery> resultEvent =
				new SearchResultEvent<>(this,
						dynamicContentDeliveryList,
						0,
						dynamicContentDeliveryList.size(),
						eventObject.isStartFromFirstPage(),
						eventObject.getEventType());

		this.fireEvent(resultEvent);
	}
	
	/**
	 * Get the DynamicContentDeliveryService.
	 * @return instance of DynamicContentDeliveryService
	 */
	private DynamicContentDeliveryService getService() {
		if (dynamicContentService == null) {
			dynamicContentService = ServiceLocator.getService(
					ContextIdNames.DYNAMIC_CONTENT_DELIVERY_SERVICE);			
		}
		return dynamicContentService;
	}
}
