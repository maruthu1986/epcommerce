/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemdefinitions.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.OperationResultFactory;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionComponentEntity;
import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionEntity;
import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionOptionEntity;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.AnyResourceUri;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OptionId;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Options;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.itemdefinitions.ItemDefinitionLookup;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;

/**
 * Command for reading an item definition option.
 */
@Singleton
@Named("readItemDefinitionOptionResourceOperator")
@Path({AnyResourceUri.PATH_PART, Options.PATH_PART, OptionId.PATH_PART})
public final class ReadItemDefinitionOptionResourceOperator implements ResourceOperator {

	private final ItemDefinitionLookup itemDefinitionLookup;

	/**
	 * Default constructor.
	 *
	 * @param itemDefinitionLookup the item definition lookup
	 */
	@Inject
	public ReadItemDefinitionOptionResourceOperator(
			@Named("itemDefinitionLookup")
			final ItemDefinitionLookup itemDefinitionLookup) {

		this.itemDefinitionLookup = itemDefinitionLookup;
	}

	/**
	 * Process read of an item definition option.
	 *
	 * @param itemDefinition the item definition
	 * @param optionId the option id
	 * @param operation the operation
	 * @return the operation result
	 */
	@Path
	@OperationType(Operation.READ)
	public OperationResult processReadOptionForItemDefinition(
			@AnyResourceUri
			final ResourceState<ItemDefinitionEntity> itemDefinition,
			@OptionId
			final String optionId,
			final ResourceOperation operation) {

		String itemId = itemDefinition.getEntity().getItemId();
		String scope = itemDefinition.getScope();
		String componentId = StringUtils.EMPTY;
		Self parentSelf = itemDefinition.getSelf();

		ResourceState<ItemDefinitionOptionEntity> findOption = Assign.ifSuccessful(
				itemDefinitionLookup.findOption(scope, parentSelf.getUri(), itemId, componentId, optionId));

		return OperationResultFactory.createReadOK(findOption, operation);
	}


	/**
	 * Process read of an item definition component option.
	 *
	 * @param itemDefinitionComponent the item definition
	 * @param optionId the option id
	 * @param operation the operation
	 * @return the operation result
	 */
	@Path
	@OperationType(Operation.READ)
	public OperationResult processReadOptionForComponent(
			@AnyResourceUri
			final ResourceState<ItemDefinitionComponentEntity> itemDefinitionComponent,
			@OptionId
			final String optionId,
			final ResourceOperation operation) {

		String itemId = itemDefinitionComponent.getEntity().getStandaloneItemId();
		String scope = itemDefinitionComponent.getScope();
		String componentId = itemDefinitionComponent.getEntity().getComponentId();
		Self parentSelf = itemDefinitionComponent.getSelf();

		ResourceState<ItemDefinitionOptionEntity> findOption = Assign.ifSuccessful(
				itemDefinitionLookup.findOption(scope, parentSelf.getUri(), itemId, componentId, optionId));

		return OperationResultFactory.createReadOK(findOption, operation);
	}
}
