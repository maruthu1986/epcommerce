/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.items.integration;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.items.ItemEntity;

/**
 * Queries for the Item information.
 */
public interface ItemLookupStrategy {

	/**
	 * Gets the item based on given scope and item ID.
	 *
	 * @param scope the scope
	 * @param itemId the item id
	 * @return the Item DTO
	 */
	ExecutionResult<ItemEntity> getItem(String scope, String itemId);
}