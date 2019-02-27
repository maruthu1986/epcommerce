/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.validation.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.domain.cartmodifier.CartItemModifierField;


/**
 * Service for dynamic validation of {@link CartItemModifierField} fields.
 */
public interface CartItemModifierFieldValidationService {

	/**
	 * Dynamic validation of {@link CartItemModifierField} instances.
	 *
	 * @param itemsToValidate map with field name and field value entries to validate.
	 * @param referentFields  field definitions.
	 * @return set of constraint violations, if any.
	 */
	List<StructuredErrorMessage> validate(Map<String, String> itemsToValidate,
			Set<CartItemModifierField> referentFields);
}
