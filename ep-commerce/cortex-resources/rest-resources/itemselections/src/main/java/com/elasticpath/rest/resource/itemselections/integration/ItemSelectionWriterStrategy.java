/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemselections.integration;

import com.elasticpath.rest.command.ExecutionResult;

/**
 * The Item Selection Writer Strategy.
 */
public interface ItemSelectionWriterStrategy {

	/**
	 * Saves a configuration selection and returns an item id for the new selection.
	 *
	 * @param scope the scope
	 * @param itemId the currently selected item id
	 * @param decodedOptionId the decoded option id
	 * @param decodedValueId the decoded value id
	 * @return the new item id that identifies the newly selected item.
	 */
	ExecutionResult<String> saveItemConfiguration(String scope, String itemId,
			String decodedOptionId, String decodedValueId);
}
