/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemdefinitions.components.impl;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionComponentEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.itemdefinitions.components.ItemDefinitionComponentLookup;
import com.elasticpath.rest.resource.itemdefinitions.components.integration.ItemDefinitionComponentLookupStrategy;
import com.elasticpath.rest.resource.itemdefinitions.components.transform.ItemDefinitionComponentTransformer;
import com.elasticpath.rest.resource.itemdefinitions.options.integration.ItemDefinitionOptionLookupStrategy;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.util.collection.CollectionUtil;

/**
 * Implementation of the {@link ItemDefinitionComponentLookup}.
 */
@Singleton
@Named("itemDefinitionComponentLookup")
public class ItemDefinitionComponentLookupImpl implements ItemDefinitionComponentLookup {

	private final ItemDefinitionOptionLookupStrategy itemDefinitionOptionLookupStrategy;
	private final ItemDefinitionComponentLookupStrategy itemDefinitionComponentLookupStrategy;
	private final ItemDefinitionComponentTransformer itemDefinitionComponentTransformer;


	/**
	 * Constructor.
	 *
	 * @param itemDefinitionOptionLookupStrategy the item definition option lookup strategy
	 * @param itemDefinitionComponentLookupStrategy the item definition component lookup strategy
	 * @param itemDefinitionComponentTransformer the item definition component transformer
	 */
	@Inject
	public ItemDefinitionComponentLookupImpl(
			@Named("itemDefinitionOptionLookupStrategy")
			final ItemDefinitionOptionLookupStrategy itemDefinitionOptionLookupStrategy,
			@Named("itemDefinitionComponentLookupStrategy")
			final ItemDefinitionComponentLookupStrategy itemDefinitionComponentLookupStrategy,
			@Named("itemDefinitionComponentTransformer")
			final ItemDefinitionComponentTransformer itemDefinitionComponentTransformer) {

		this.itemDefinitionOptionLookupStrategy = itemDefinitionOptionLookupStrategy;
		this.itemDefinitionComponentLookupStrategy = itemDefinitionComponentLookupStrategy;
		this.itemDefinitionComponentTransformer = itemDefinitionComponentTransformer;
	}


	@Override
	public ExecutionResult<Boolean> hasComponents(final String scope, final String itemId) {
		return itemDefinitionComponentLookupStrategy.hasComponents(scope, itemId);
	}

	@Override
	public ExecutionResult<Collection<String>> findComponentIds(final String scope, final String itemId) {

		Collection<String> componentIds = Assign.ifSuccessful(
				itemDefinitionComponentLookupStrategy.findComponentIds(scope, itemId));

		return ExecutionResultFactory.createReadOK(Base32Util.encodeAll(componentIds));
	}

	@Override
	public ExecutionResult<ResourceState<ItemDefinitionComponentEntity>> getComponent(final String scope, final String parentUri,
			final String itemId, final String componentId) {

		String decodedComponentId = Base32Util.decode(componentId);
		ItemDefinitionComponentEntity componentEntity = Assign.ifSuccessful(
				itemDefinitionComponentLookupStrategy.findComponentById(scope, itemId, decodedComponentId));
		String standaloneItemCorrelationId = componentEntity.getStandaloneItemId();
		Collection<String> optionIds = Assign.ifSuccessful(itemDefinitionOptionLookupStrategy.findOptionIds(scope, standaloneItemCorrelationId));
		boolean hasOptions = CollectionUtil.isNotEmpty(optionIds);
		ResourceState<ItemDefinitionComponentEntity> component = itemDefinitionComponentTransformer.transformToRepresentation(scope,
				parentUri, itemId, componentId, hasOptions, componentEntity);
		return ExecutionResultFactory.createReadOK(component);
	}
}
