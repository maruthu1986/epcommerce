/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.conditionbuilder.adapter.service.tag.impl;

import com.elasticpath.cmclient.conditionbuilder.adapter.BaseModelAdapter;
import com.elasticpath.cmclient.conditionbuilder.adapter.service.ConditionModelValidationService;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.tags.domain.Condition;
import com.elasticpath.tags.service.ConditionValidationFacade;
import com.elasticpath.validation.domain.ValidationResult;

/**
 * Wraps a condition validation facade in order to test a condition model.
 * The model represents a single condition.
 */
public class TagConditionModelValidationService implements
		ConditionModelValidationService<Condition> {
	
	private final ConditionValidationFacade validationFacade;
	
	/**
	 * Constructor that initialises the underlying core service that will 
	 * perform the validation. 
	 */
	public TagConditionModelValidationService() {
		
		validationFacade = ServiceLocator.getService(ContextIdNames.TAG_CONDITION_VALIDATION_FACADE);
		
	}

	@Override
	public ValidationResult validate(final BaseModelAdapter<Condition> model, final Object value) {
		
		return validationFacade.validate(model.getModel(), value);

	}

}
