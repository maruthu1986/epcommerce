/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemselections.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.controls.SelectorEntity;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OptionId;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Options;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceId;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Scope;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Selector;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ValueId;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Values;
import com.elasticpath.rest.resource.itemselections.command.ReadItemOptionValueSelectorCommand;
import com.elasticpath.rest.resource.itemselections.command.ReadItemSelectionChoiceCommand;
import com.elasticpath.rest.resource.itemselections.command.SelectItemDefinitionOptionValueChoiceCommand;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Processes the resource operations on item selections.
 */
@Singleton
@Named("itemSelectionsResourceOperator")
@Path(ResourceName.PATH_PART)
public final class ItemSelectionsResourceOperatorImpl implements ResourceOperator {

	private final Provider<ReadItemSelectionChoiceCommand.Builder> readItemSelectionChoiceCommandBuilder;
	private final Provider<ReadItemOptionValueSelectorCommand.Builder> readItemOptionValueSelectorCommandBuilder;
	private final Provider<SelectItemDefinitionOptionValueChoiceCommand.Builder> selectItemDefinitionOptionValueChoiceCommandBuilder;


	/**
	 * Constructor.
	 *
	 * @param readItemSelectionChoiceCommandBuilder the read item selection choice command builder
	 * @param readItemOptionValueSelectorCommandBuilder the read item option value selector command builder
	 * @param selectItemDefinitionOptionValueChoiceCommandBuilder the select item definition option value choice command builder
	 */
	@Inject
	public ItemSelectionsResourceOperatorImpl(
			@Named("readItemSelectionChoiceCommandBuilder")
			final Provider<ReadItemSelectionChoiceCommand.Builder> readItemSelectionChoiceCommandBuilder,
			@Named("readItemOptionValueSelectorCommandBuilder")
			final Provider<ReadItemOptionValueSelectorCommand.Builder> readItemOptionValueSelectorCommandBuilder,
			@Named("selectItemDefinitionOptionValueChoiceCommandBuilder")
			final Provider<SelectItemDefinitionOptionValueChoiceCommand.Builder> selectItemDefinitionOptionValueChoiceCommandBuilder) {

		this.readItemSelectionChoiceCommandBuilder = readItemSelectionChoiceCommandBuilder;
		this.readItemOptionValueSelectorCommandBuilder = readItemOptionValueSelectorCommandBuilder;
		this.selectItemDefinitionOptionValueChoiceCommandBuilder = selectItemDefinitionOptionValueChoiceCommandBuilder;
	}


	/**
	 * Process read of an item definition option value.
	 *
	 * @param scope the scope
	 * @param itemId the item id
	 * @param optionId the option id
	 * @param valueId the value id
	 * @param operation the operation
	 * @return the operation result
	 */
	@Path({Scope.PATH_PART, ResourceId.PATH_PART,
			Options.PATH_PART, OptionId.PATH_PART,
			Values.PATH_PART, ValueId.PATH_PART, Selector.PATH_PART})
	@OperationType(Operation.READ)
	public OperationResult processReadOptionValueChoice(
			@Scope
			final String scope,
			@ResourceId
			final String itemId,
			@OptionId
			final String optionId,
			@ValueId
			final String valueId,
			final ResourceOperation operation) {

		ExecutionResult<ResourceState<LinksEntity>> result = readItemSelectionChoiceCommandBuilder.get()
				.setItemId(itemId)
				.setOptionId(optionId)
				.setValueId(valueId)
				.setScope(scope)
				.build()
				.execute();

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}

	/**
	 * Process read option value selector.
	 *
	 * @param scope the scope
	 * @param itemId the item id
	 * @param optionId the option id
	 * @param operation the operation
	 * @return the operation result
	 */
	@Path({Scope.PATH_PART, ResourceId.PATH_PART,
			Options.PATH_PART, OptionId.PATH_PART,
			Selector.PATH_PART})
	@OperationType(Operation.READ)
	public OperationResult processReadOptionValueSelector(
			@Scope
			final String scope,
			@ResourceId
			final String itemId,
			@OptionId
			final String optionId,
			final ResourceOperation operation) {

		ExecutionResult<ResourceState<SelectorEntity>> result = readItemOptionValueSelectorCommandBuilder.get()
				.setItemId(itemId)
				.setOptionId(optionId)
				.setScope(scope)
				.build()
				.execute();

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}

	/**
	 * Process select choice.
	 *
	 * @param scope the scope
	 * @param itemId the item id
	 * @param optionId the option id
	 * @param valueId the value id
	 * @param operation the operation
	 * @return the operation result
	 */
	@Path({Scope.PATH_PART, ResourceId.PATH_PART,
			Options.PATH_PART, OptionId.PATH_PART,
			Values.PATH_PART, ValueId.PATH_PART, Selector.PATH_PART})
	@OperationType(Operation.CREATE)
	public OperationResult processSelectChoice(
			@Scope
			final String scope,
			@ResourceId
			final String itemId,
			@OptionId
			final String optionId,
			@ValueId
			final String valueId,
			final ResourceOperation operation) {

		ExecutionResult<ResourceState<ResourceEntity>> executionResult = selectItemDefinitionOptionValueChoiceCommandBuilder.get()
				.setScope(scope)
				.setItemId(itemId)
				.setOptionId(optionId)
				.setValueId(valueId)
				.build()
				.execute();

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(executionResult, operation);
	}
}
