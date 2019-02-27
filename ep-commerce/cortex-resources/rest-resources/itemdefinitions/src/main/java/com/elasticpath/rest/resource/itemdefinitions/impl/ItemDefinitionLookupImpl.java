/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemdefinitions.impl;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionEntity;
import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionOptionEntity;
import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionOptionValueEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.itemdefinitions.ItemDefinitionLookup;
import com.elasticpath.rest.resource.itemdefinitions.integration.ItemDefinitionLookupStrategy;
import com.elasticpath.rest.resource.itemdefinitions.options.integration.ItemDefinitionOptionLookupStrategy;
import com.elasticpath.rest.resource.itemdefinitions.transform.ItemDefinitionOptionTransformer;
import com.elasticpath.rest.resource.itemdefinitions.transform.ItemDefinitionOptionValueTransformer;
import com.elasticpath.rest.resource.itemdefinitions.transform.ItemDefinitionTransformer;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.util.collection.CollectionUtil;

/**
 * Implementation of the {@link ItemDefinitionLookup}.
 */
@Singleton
@Named("itemDefinitionLookup")
public final class ItemDefinitionLookupImpl implements ItemDefinitionLookup {

	private final ItemDefinitionLookupStrategy itemDefinitionLookupStrategy;
	private final ItemDefinitionOptionLookupStrategy itemDefinitionOptionLookupStrategy;
	private final ItemDefinitionTransformer itemDefinitionTransformer;
	private final ItemDefinitionOptionTransformer itemDefinitionOptionTransformer;
	private final ItemDefinitionOptionValueTransformer optionValueTransformer;


	/**
	 * Default constructor.
	 *
	 * @param itemDefinitionLookupStrategy the item definition lookup strategy
	 * @param itemDefinitionOptionLookupStrategy the item definition option lookup strategy
	 * @param itemDefinitionTransformer the item definition transformer
	 * @param itemDefinitionOptionTransformer the item definition option transformer
	 * @param optionValueTransformer the option value transformer
	 */
	@Inject
	public ItemDefinitionLookupImpl(
			@Named("itemDefinitionLookupStrategy")
			final ItemDefinitionLookupStrategy itemDefinitionLookupStrategy,
			@Named("itemDefinitionOptionLookupStrategy")
			final ItemDefinitionOptionLookupStrategy itemDefinitionOptionLookupStrategy,
			@Named("itemDefinitionTransformer")
			final ItemDefinitionTransformer itemDefinitionTransformer,
			@Named("itemDefinitionOptionTransformer")
			final ItemDefinitionOptionTransformer itemDefinitionOptionTransformer,
			@Named("itemDefinitionOptionValueTransformer")
			final ItemDefinitionOptionValueTransformer optionValueTransformer) {

		this.itemDefinitionLookupStrategy = itemDefinitionLookupStrategy;
		this.itemDefinitionOptionLookupStrategy = itemDefinitionOptionLookupStrategy;
		this.itemDefinitionTransformer = itemDefinitionTransformer;
		this.itemDefinitionOptionTransformer = itemDefinitionOptionTransformer;
		this.optionValueTransformer = optionValueTransformer;
	}


	@Override
	public ExecutionResult<ResourceState<ItemDefinitionEntity>> findByItemId(final String scope, final String itemId) {

		ItemDefinitionEntity itemDefinitionEntity = Assign.ifSuccessful(itemDefinitionLookupStrategy.find(scope, itemId));

		ExecutionResult<Collection<String>> optionIdsResult = itemDefinitionOptionLookupStrategy.findOptionIds(scope, itemId);
		boolean hasOptions = CollectionUtil.isNotEmpty(optionIdsResult.getData());

		ResourceState<ItemDefinitionEntity> itemDefinition =
				itemDefinitionTransformer.transformToRepresentation(itemDefinitionEntity, scope, hasOptions);
		return ExecutionResultFactory.createReadOK(itemDefinition);
	}

	@Override
	public ExecutionResult<Collection<String>> findOptionIdsForItem(final String scope, final String itemId) {

		Collection<String> optionIds = Assign.ifSuccessful(itemDefinitionOptionLookupStrategy.findOptionIds(scope, itemId));
		return ExecutionResultFactory.createReadOK(Base32Util.encodeAll(optionIds));
	}

	@Override
	public ExecutionResult<ResourceState<ItemDefinitionOptionValueEntity>> findOptionValueForItem(final String scope,
			final String itemDefinitionUri,	final String itemId, final String optionId, final String valueId) {

		String decodedOptionId = Base32Util.decode(optionId);
		String decodedValueId = Base32Util.decode(valueId);

		ItemDefinitionOptionValueEntity optionValueEntity = Assign.ifSuccessful(
				itemDefinitionOptionLookupStrategy.findOptionValue(scope, itemId, decodedOptionId, decodedValueId));

		ResourceState<ItemDefinitionOptionValueEntity> optionValue =
				optionValueTransformer.transformToRepresentation(optionValueEntity, itemDefinitionUri, optionId, valueId);

		return ExecutionResultFactory.createReadOK(optionValue);
	}

	@Override
	public ExecutionResult<ResourceState<ItemDefinitionOptionEntity>> findOption(final String scope, final String itemDefinitionUri,
			final String itemId, final String componentId, final String optionId) {

		String decodedOptionId = Base32Util.decode(optionId);

		ItemDefinitionOptionEntity optionEntity = Assign.ifSuccessful(
				itemDefinitionOptionLookupStrategy.findOption(scope, itemId, decodedOptionId));

		ResourceState<ItemDefinitionOptionEntity> option =
				itemDefinitionOptionTransformer.transformToRepresentation(optionEntity, itemDefinitionUri, scope, itemId, componentId);

		return ExecutionResultFactory.createReadOK(option);
	}
}
