/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemselections.integration;

import com.elasticpath.rest.command.ExecutionResult;

/**
 * Lookup strategy for item selections.
 */
public interface ItemSelectionLookupStrategy {

	/**
	 * Find selected option value for option.
	 *
	 * @param scope the scope
	 * @param itemId the item id
	 * @param decodedOptionId the decoded option id
	 * @return the execution result
	 */
	ExecutionResult<String> findSelectedOptionValueForOption(String scope, String itemId, String decodedOptionId);

	/**
	 * Finds the IDs of selectable and selected option values, for a given item option.
	 *
	 * @param scope the scope
	 * @param itemId the item id
	 * @param decodedOptionId the decoded option id
	 * @return the execution result
	 */
	ExecutionResult<ItemSelectionOptionValuesDto> findOptionValueSelections(String scope, String itemId, String decodedOptionId);
}
