/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemdefinitions.components.integration;

import java.util.Collection;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionComponentEntity;

/**
 * Lookup strategy for item definition components.
 */
public interface ItemDefinitionComponentLookupStrategy {

	/**
	 * Indicates whether an item has components or not.
	 *
	 * @param scope the scope
	 * @param itemId the item id
	 * @return true if the item has components, false otherwise
	 */
	ExecutionResult<Boolean> hasComponents(String scope, String itemId);

	/**
	 * Returns component ids associated with the given {@code itemId}.
	 *
	 * @param scope the scope
	 * @param itemId the item id
	 * @return the execution result
	 */
	ExecutionResult<Collection<String>> findComponentIds(String scope, String itemId);

	/**
	 * Find component IDs.
	 *
	 * @param scope the scope
	 * @param itemId the item id
	 * @param decodedComponentId the decoded component id
	 * @return the execution result
	 */
	ExecutionResult<ItemDefinitionComponentEntity> findComponentById(String scope, String itemId,
			String decodedComponentId);
}
