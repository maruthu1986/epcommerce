/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.items.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.items.ItemEntity;
import com.elasticpath.rest.resource.items.ItemLookup;
import com.elasticpath.rest.resource.items.integration.ItemLookupStrategy;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.transform.TransformToResourceState;

/**
 * Item Lookup.
 */
@Singleton
@Named("itemLookup")
public final class ItemLookupImpl implements ItemLookup {

	private final ItemLookupStrategy itemLookupStrategy;
	private final TransformToResourceState<ItemEntity, ItemEntity> itemTransformer;


	/**
	 * Constructor.
	 *
	 * @param itemLookupStrategy the item lookup strategy
	 * @param itemTransformer the item transformer
	 */
	@Inject
	ItemLookupImpl(
			@Named("itemLookupStrategy")
			final ItemLookupStrategy itemLookupStrategy,
			@Named("itemTransformer")
			final TransformToResourceState<ItemEntity, ItemEntity> itemTransformer) {

		this.itemLookupStrategy = itemLookupStrategy;
		this.itemTransformer = itemTransformer;
	}


	@Override
	public ExecutionResult<ResourceState<ItemEntity>> getItem(final String scope, final String itemId) {
		ItemEntity itemEntity = Assign.ifSuccessful(itemLookupStrategy.getItem(scope, itemId));
		ResourceState<ItemEntity> itemRepresentation = itemTransformer.transform(scope, itemEntity);
		return ExecutionResultFactory.createReadOK(itemRepresentation);
	}
}