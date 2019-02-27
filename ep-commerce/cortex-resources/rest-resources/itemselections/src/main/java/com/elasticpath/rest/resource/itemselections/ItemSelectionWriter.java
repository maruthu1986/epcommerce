/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemselections;

import com.elasticpath.rest.command.ExecutionResult;

/**
 * The Item Selection Writer.
 */
public interface ItemSelectionWriter {

	/**
	 * Saves a configuration selection and returns an item id for the new selection.
	 *
	 * @param scope the scope
	 * @param itemId the currently selected item id
	 * @param optionId the option id
	 * @param valueId the value id
	 * @return the new item id that identifies the newly selected item.
	 */
	ExecutionResult<String> saveConfigurationSelection(String scope, String itemId, String optionId, String valueId);
}
