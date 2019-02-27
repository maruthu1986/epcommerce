/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.items.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.items.ItemEntity;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceId;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Scope;
import com.elasticpath.rest.resource.items.ItemLookup;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Implements ItemResource.
 */
@Singleton
@Named("itemsResourceOperator")
@Path(ResourceName.PATH_PART)
public final class ItemsResourceOperatorImpl implements ResourceOperator {
	private final ItemLookup itemLookup;

	/**
	 * Constructor.
	 *
	 * @param itemLookup the {@link ItemLookup}
	 */
	@Inject
	ItemsResourceOperatorImpl(
			@Named("itemLookup")
			final ItemLookup itemLookup) {
		this.itemLookup = itemLookup;
	}


	/**
	 * Handles the READ operations.
	 *
	 * @param scope scope
	 * @param itemId item id
	 * @param operation the Resource Operation.
	 * @return the result
	 */
	@Path({Scope.PATH_PART, ResourceId.PATH_PART})
	@OperationType(Operation.READ)
	public OperationResult processRead(
			@Scope
			final String scope,
			@ResourceId
			final String itemId,
			final ResourceOperation operation) {

		ResourceState<ItemEntity> item = Assign.ifSuccessful(itemLookup.getItem(scope, itemId),
				OnFailure.returnNotFound("Item not found"));

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory
				.create(ExecutionResultFactory.createReadOK(item), operation);
	}
}
