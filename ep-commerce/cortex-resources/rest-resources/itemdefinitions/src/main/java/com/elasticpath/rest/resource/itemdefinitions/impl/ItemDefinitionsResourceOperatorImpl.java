/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemdefinitions.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.OperationResultFactory;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionEntity;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceId;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Scope;
import com.elasticpath.rest.resource.itemdefinitions.ItemDefinitionLookup;
import com.elasticpath.rest.schema.ResourceState;


/**
 * Processes the resource operation on item definitions.
 */
@Singleton
@Named("itemDefinitionsResourceOperator")
@Path({ResourceName.PATH_PART, Scope.PATH_PART})
public final class ItemDefinitionsResourceOperatorImpl implements ResourceOperator {

	private final ItemDefinitionLookup itemDefinitionLookup;

	/**
	 * Default constructor.
	 *
	 * @param itemDefinitionLookup the item definition lookup
	 */
	@Inject
	public ItemDefinitionsResourceOperatorImpl(
			@Named("itemDefinitionLookup")
			final ItemDefinitionLookup itemDefinitionLookup) {

		this.itemDefinitionLookup = itemDefinitionLookup;
	}


	/**
	 * Handles READ on item definition.
	 *
	 * @param scope the scope
	 * @param itemId the item id
	 * @param operation The resource Operation you are responding to.
	 * @return the operation result
	 */
	@Path(ResourceId.PATH_PART)
	@OperationType(Operation.READ)
	public OperationResult processRead(
			@Scope
			final String scope,
			@ResourceId
			final String itemId,
			final ResourceOperation operation) {

		ResourceState<ItemDefinitionEntity> definition = Assign.ifSuccessful(itemDefinitionLookup.findByItemId(scope, itemId));

		return OperationResultFactory.createReadOK(definition, operation);
	}

}
