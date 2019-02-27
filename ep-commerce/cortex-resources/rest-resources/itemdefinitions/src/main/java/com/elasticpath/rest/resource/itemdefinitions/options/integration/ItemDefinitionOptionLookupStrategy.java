/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemdefinitions.options.integration;

import java.util.Collection;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionOptionEntity;
import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionOptionValueEntity;

/**
 * Resource level lookup interface for item definition option.
 */
public interface ItemDefinitionOptionLookupStrategy {

	/**
	 * Find option IDs for the item definition.
	 *
	 * @param scope the scope
	 * @param itemId the item id
	 * @return the execution result
	 */
	ExecutionResult<Collection<String>> findOptionIds(String scope, String itemId);

	/**
	 * Find an option of the item definition.
	 *
	 * @param scope the scope
	 * @param itemId the item id
	 * @param decodedOptionId the decoded option id
	 * @return the execution result
	 */
	ExecutionResult<ItemDefinitionOptionEntity> findOption(String scope, String itemId, String decodedOptionId);

	/**
	 * Find an option value of the item definition.
	 *
	 * @param scope the scope
	 * @param itemId the item id
	 * @param decodedOptionId the decoded option id
	 * @param decodedValueId the decoded value id
	 * @return the execution result
	 */
	ExecutionResult<ItemDefinitionOptionValueEntity> findOptionValue(String scope, String itemId, String decodedOptionId, String decodedValueId);
}
