/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemdefinitions;

import java.util.Collection;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionEntity;
import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionOptionEntity;
import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionOptionValueEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Resource level Item Definitions Lookup interface.
 */
public interface ItemDefinitionLookup {

	/**
	 * Find an ItemDefinition by Item id.
	 *
	 * @param scope the scope
	 * @param itemId the item id
	 * @return the {@link ResourceState}
	 */
	ExecutionResult<ResourceState<ItemDefinitionEntity>> findByItemId(String scope, String itemId);

	/**
	 * Find option IDs for the item definition.
	 *
	 * @param scope the scope
	 * @param itemId the item id
	 * @return the execution result
	 */
	ExecutionResult<Collection<String>> findOptionIdsForItem(String scope, String itemId);

	/**
	 * Find an option value for the item definition.
	 *
	 * @param scope the scope
	 * @param itemDefinitionUri the item definition uri
	 * @param itemId the item id
	 * @param optionId the option id
	 * @param valueId the value id
	 * @return the execution result
	 */
	ExecutionResult<ResourceState<ItemDefinitionOptionValueEntity>> findOptionValueForItem(String scope, String itemDefinitionUri, String itemId,
			String optionId, String valueId);

	/**
	 * Find option for item definition with the given option ID.
	 *
	 * @param scope the scope
	 * @param itemDefinitionUri the item definition uri
	 * @param itemId the item id
	 * @param componentId the component id
	 * @param optionId the option id
	 * @return the execution result
	 */
	ExecutionResult<ResourceState<ItemDefinitionOptionEntity>> findOption(String scope, String itemDefinitionUri, String itemId, String componentId,
			String optionId);
}
