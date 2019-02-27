/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.targetedselling.handlers.serviceadapters;

import com.elasticpath.cmclient.store.targetedselling.handlers.CreateHandlerService;
import com.elasticpath.domain.contentspace.DynamicContent;
import com.elasticpath.service.contentspace.DynamicContentService;

/**
 * Create handler service adapter for dynamic content service. 
 * 
 * @author dpavlov
 *
 */
public class DynamicContentCreateHandlerServiceAdapter implements
		CreateHandlerService<DynamicContent> {

	private final DynamicContentService dynamicContentService;
	
	/**
	 * default constructor.
	 * @param dynamicContentService the service that required adaptor
	 */
	public DynamicContentCreateHandlerServiceAdapter(final DynamicContentService dynamicContentService) {
		this.dynamicContentService = dynamicContentService;
	}

	@Override
	public boolean exists(final DynamicContent domainObject) {
		if (domainObject != null && domainObject.getName() != null) {
			return this.dynamicContentService.findByName(domainObject.getName()) != null;
		}
		return false;
	}

	@Override
	public void persist(final DynamicContent domainObject) {
		this.dynamicContentService.add(domainObject);
	}

	
	
}
