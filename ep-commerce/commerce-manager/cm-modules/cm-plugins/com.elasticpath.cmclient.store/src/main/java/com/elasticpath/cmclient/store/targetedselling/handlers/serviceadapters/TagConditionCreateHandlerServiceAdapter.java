/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.targetedselling.handlers.serviceadapters;

import com.elasticpath.cmclient.store.targetedselling.handlers.CreateHandlerService;
import com.elasticpath.tags.domain.ConditionalExpression;
import com.elasticpath.tags.service.TagConditionService;

/**
 * Create handler service adapter for tag condition service. 
 * 
 * @author dpavlov
 *
 */
public class TagConditionCreateHandlerServiceAdapter implements
		CreateHandlerService<ConditionalExpression> {

	private final TagConditionService tagConditionService;
	
	/**
	 * default constructor.
	 * @param dynamicContentService the service that required adaptor
	 */
	public TagConditionCreateHandlerServiceAdapter(final TagConditionService dynamicContentService) {
		this.tagConditionService = dynamicContentService;
	}

	@Override
	public boolean exists(final ConditionalExpression domainObject) {
		if (domainObject != null && domainObject.getName() != null) {
			return this.tagConditionService.findByName(domainObject.getName()) != null;
		}
		return false;
	}

	@Override
	public void persist(final ConditionalExpression domainObject) {
		this.tagConditionService.saveOrUpdate(domainObject);
	}

}
