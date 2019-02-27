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
import com.elasticpath.rest.definition.controls.SelectorEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.itemselections.ItemSelectionLookup;
import com.elasticpath.rest.resource.itemselections.integration.ItemSelectionLookupStrategy;
import com.elasticpath.rest.resource.itemselections.integration.ItemSelectionOptionValuesDto;
import com.elasticpath.rest.resource.itemselections.transform.ItemSelectionOptionValuesTransformer;
import com.elasticpath.rest.schema.ResourceState;


/**
 * Implementation of the {@link ItemSelectionLookup}.
 */
@Singleton
@Named("itemSelectionLookup")
public final class ItemSelectionLookupImpl implements ItemSelectionLookup {

	private final ItemSelectionLookupStrategy itemSelectionLookupStrategy;
	private final ItemSelectionOptionValuesTransformer itemSelectionOptionValuesTransformer;


	/**
	 * Default constructor.
	 *
	 * @param itemSelectionLookupStrategy the item selection lookup strategy
	 * @param itemSelectionOptionValuesTransformer the item selection option values transformer
	 */
	@Inject
	public ItemSelectionLookupImpl(
			@Named("itemSelectionLookupStrategy")
			final ItemSelectionLookupStrategy itemSelectionLookupStrategy,
			@Named("itemSelectionOptionValuesTransformer")
			final ItemSelectionOptionValuesTransformer itemSelectionOptionValuesTransformer) {

		this.itemSelectionLookupStrategy = itemSelectionLookupStrategy;
		this.itemSelectionOptionValuesTransformer = itemSelectionOptionValuesTransformer;
	}


	@Override
	public ExecutionResult<String> getSelectedOptionChoiceForItemId(final String scope, final String itemId, final String optionId) {

		String decodedOptionId = Base32Util.decode(optionId);

		String optionValueKey = Assign.ifSuccessful(
				itemSelectionLookupStrategy.findSelectedOptionValueForOption(scope, itemId, decodedOptionId));
		String valueId = null;
		if (optionValueKey != null) {
			valueId = Base32Util.encode(optionValueKey);
		}

		return ExecutionResultFactory.createReadOK(valueId);
	}

	@Override
	public ExecutionResult<ResourceState<SelectorEntity>> getOptionValueSelector(final String scope, final String itemId, final String optionId) {

		String decodedOptionId = Base32Util.decode(optionId);
		ItemSelectionOptionValuesDto optionValueSelections = Assign.ifSuccessful(
				itemSelectionLookupStrategy.findOptionValueSelections(scope, itemId, decodedOptionId));

		ResourceState<SelectorEntity> selectorRepresentation =
				itemSelectionOptionValuesTransformer.transformToRepresentation(optionValueSelections, scope, itemId, optionId);
		return ExecutionResultFactory.createReadOK(selectorRepresentation);
	}
}