/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.conditionbuilder.adapter.service;

import com.elasticpath.cmclient.conditionbuilder.adapter.BaseModelAdapter;
import com.elasticpath.validation.domain.ValidationResult;

/**
 * Service that will perform validation on a single row composite.
 * @param <M> the base model adapter model type
 */
public interface ConditionModelValidationService<M> {

	/**
	 * @param model the model on which the validation should be performed.
	 * @param value the value for which to validate the model
	 * @return validation result
	 */
	ValidationResult validate(BaseModelAdapter<M> model, Object value);
	
}
