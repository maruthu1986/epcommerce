/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.items;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.items.ItemEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Queries for the Item information.
 */
public interface ItemLookup {

	/**
	 * Gets the item based on given scope and item ID.
	 *
	 *
	 * @param scope the scope.
	 * @param itemId the item ID.
	 * @return the ItemObject.
	 */
	ExecutionResult<ResourceState<ItemEntity>> getItem(String scope, String itemId);
}