/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemselections;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.controls.SelectorEntity;
import com.elasticpath.rest.schema.ResourceState;


/**
 * Resource level Item Definitions Lookup interface.
 */
public interface ItemSelectionLookup {

	/**
	 * Get an option value choice for the item selection.
	 *
	 * @param scope the scope
	 * @param itemId the item id
	 * @param optionId the option id
	 * @return the execution result with the selected value ID
	 */
	ExecutionResult<String> getSelectedOptionChoiceForItemId(String scope, String itemId, String optionId);

	/**
	 * Gets the option value selection.
	 *
	 * @param scope the scope
	 * @param itemId the item id
	 * @param optionId the option id
	 * @return the option value selector representation
	 */
	ExecutionResult<ResourceState<SelectorEntity>> getOptionValueSelector(String scope, String itemId, String optionId);
}
