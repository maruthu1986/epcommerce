/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.targetedselling.handlers.serviceadapters;

import com.elasticpath.cmclient.store.targetedselling.handlers.CreateHandlerService;
import com.elasticpath.domain.targetedselling.DynamicContentDelivery;
import com.elasticpath.service.targetedselling.DynamicContentDeliveryService;

/**
 * Create handler service adapter for dynamic content delivery service. 
 * 
 * @author dpavlov
 *
 */
public class DynamicContentDeliveryCreateHandlerServiceAdapter implements
		CreateHandlerService<DynamicContentDelivery> {

	private final DynamicContentDeliveryService dynamicContentDeliveryService;
	
	/**
	 * default constructor.
	 * @param dynamicContentDeliveryService the service that required adaptor
	 */
	public DynamicContentDeliveryCreateHandlerServiceAdapter(final DynamicContentDeliveryService dynamicContentDeliveryService) {
		this.dynamicContentDeliveryService = dynamicContentDeliveryService;
	}

	@Override
	public boolean exists(final DynamicContentDelivery domainObject) {
		if (domainObject != null && domainObject.getName() != null) {
			return this.dynamicContentDeliveryService.findByName(domainObject.getName()) != null;
		}
		return false;
	}

	@Override
	public void persist(final DynamicContentDelivery domainObject) {
		this.dynamicContentDeliveryService.add(domainObject);
	}

	
	
}
