/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemdefinitions.components;

import java.util.Collection;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionComponentEntity;
import com.elasticpath.rest.schema.ResourceState;


/**
 * Resource level Item Definitions Component Lookup interface.
 */
public interface ItemDefinitionComponentLookup {

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
	 * Returns the component with {@code componentId}.
	 *
	 * @param scope the scope.
	 * @param parentUri the parent Uri
	 * @param itemId the item id
	 * @param componentId the component id of the component to get
	 * @return the execution result
	 */
	ExecutionResult<ResourceState<ItemDefinitionComponentEntity>> getComponent(String scope, String parentUri,
			String itemId, String componentId);
}
