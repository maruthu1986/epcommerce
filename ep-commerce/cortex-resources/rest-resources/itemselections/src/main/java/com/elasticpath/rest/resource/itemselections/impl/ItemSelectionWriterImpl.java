/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemselections.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.itemselections.ItemSelectionWriter;
import com.elasticpath.rest.resource.itemselections.integration.ItemSelectionWriterStrategy;
import com.elasticpath.rest.id.util.Base32Util;

/**
 * Item Selection Writer.
 */
@Singleton
@Named("itemSelectionWriter")
public final class ItemSelectionWriterImpl implements ItemSelectionWriter {

	private final ItemSelectionWriterStrategy itemSelectionWriterStrategy;


	/**
	 * Default constructor.
	 *
	 * @param itemSelectionWriterStrategy the item selection writer strategy
	 */
	@Inject
	public ItemSelectionWriterImpl(
			@Named("itemSelectionWriterStrategy")
			final ItemSelectionWriterStrategy itemSelectionWriterStrategy) {

		this.itemSelectionWriterStrategy = itemSelectionWriterStrategy;
	}


	@Override
	public ExecutionResult<String> saveConfigurationSelection(final String scope, final String itemId,
			final String optionId, final String valueId) {

		String decodedOptionId = Base32Util.decode(optionId);
		String decodedValueId = Base32Util.decode(valueId);

		// When a new configuration is saved for an item, it now has a new item id that is returned
		String newItemId = Assign.ifSuccessful(
				itemSelectionWriterStrategy.saveItemConfiguration(scope, itemId, decodedOptionId, decodedValueId));

		return ExecutionResultFactory.createCreateOKWithData(newItemId, false);
	}
}
