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
import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionComponentEntity;
import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionEntity;
import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionOptionValueEntity;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.AnyResourceUri;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OptionId;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Options;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ValueId;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Values;
import com.elasticpath.rest.resource.itemdefinitions.ItemDefinitionLookup;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.util.ResourceStateUtil;

/**
 * Command for read the option value of an item definition.
 */
@Singleton
@Named("readItemDefinitionOptionValueResourceOperator")
@Path({AnyResourceUri.PATH_PART, Options.PATH_PART, OptionId.PATH_PART, Values.PATH_PART, ValueId.PATH_PART})
public final class ReadItemDefinitionOptionValueResourceOperator implements ResourceOperator {

	private final ItemDefinitionLookup itemDefinitionLookup;

	/**
	 * Default constructor.
	 *
	 * @param itemDefinitionLookup the item definition lookup
	 */
	@Inject
	public ReadItemDefinitionOptionValueResourceOperator(
			@Named("itemDefinitionLookup")
			final ItemDefinitionLookup itemDefinitionLookup) {

		this.itemDefinitionLookup = itemDefinitionLookup;
	}


	/**
	 * Process read of an item definition option value.
	 *
	 * @param itemDefinition the item definition
	 * @param optionId the option id
	 * @param valueId the value id
	 * @param operation the operation
	 * @return the operation result
	 */
	@Path
	@OperationType(Operation.READ)
	public OperationResult processReadOptionValueForItemDefinition(
			@AnyResourceUri
			final ResourceState<ItemDefinitionEntity> itemDefinition,
			@OptionId
			final String optionId,
			@ValueId
			final String valueId,
			final ResourceOperation operation) {

		String scope = itemDefinition.getScope();
		String itemId = itemDefinition.getEntity().getItemId();

		String itemDefinitionUri = ResourceStateUtil.getSelfUri(itemDefinition);

		ResourceState<ItemDefinitionOptionValueEntity> optionValue = Assign.ifSuccessful(
				itemDefinitionLookup.findOptionValueForItem(scope, itemDefinitionUri, itemId, optionId, valueId));

		return OperationResultFactory.createReadOK(optionValue, operation);
	}

	/**
	 * Process read of an item definition component option value.
	 *
	 * @param itemDefinitionComponent the item definition
	 * @param optionId the option id
	 * @param valueId the value id
	 * @param operation the operation
	 * @return the operation result
	 */
	@Path
	@OperationType(Operation.READ)
	public OperationResult processReadOptionValueForComponent(
			@AnyResourceUri
			final ResourceState<ItemDefinitionComponentEntity> itemDefinitionComponent,
			@OptionId
			final String optionId,
			@ValueId
			final String valueId,
			final ResourceOperation operation) {

		String scope = itemDefinitionComponent.getScope();
		String itemId = itemDefinitionComponent.getEntity().getStandaloneItemId();

		String itemDefinitionUri = ResourceStateUtil.getSelfUri(itemDefinitionComponent);

		ResourceState<ItemDefinitionOptionValueEntity> optionValue = Assign.ifSuccessful(
				itemDefinitionLookup.findOptionValueForItem(scope, itemDefinitionUri, itemId, optionId, valueId));

		return OperationResultFactory.createReadOK(optionValue, operation);

	}
}
