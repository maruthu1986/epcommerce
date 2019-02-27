/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemdefinitions.integration;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionEntity;

/**
 * Lookup strategy form item definition.
 */
public interface ItemDefinitionLookupStrategy {

	/**
	 * Find the item definition.
	 *
	 * @param scope the scope
	 * @param itemId the item id
	 * @return the execution result
	 */
	ExecutionResult<ItemDefinitionEntity> find(String scope, String itemId);
}
