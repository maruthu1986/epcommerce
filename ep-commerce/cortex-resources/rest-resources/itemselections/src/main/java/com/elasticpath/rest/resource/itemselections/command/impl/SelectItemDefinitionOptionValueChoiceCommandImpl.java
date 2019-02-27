/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemselections.command.impl;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.itemselections.ItemSelectionWriter;
import com.elasticpath.rest.resource.itemselections.command.SelectItemDefinitionOptionValueChoiceCommand;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.ItemsUriBuilderFactory;


/**
 * Select Item Definition Option Value Choice Command.
 */
@Named
public final class SelectItemDefinitionOptionValueChoiceCommandImpl implements SelectItemDefinitionOptionValueChoiceCommand {

	private final ItemSelectionWriter itemSelectionWriter;
	private final ItemsUriBuilderFactory itemsUriBuilderFactory;

	private String scope;
	private String itemId;
	private String optionId;
	private String valueId;


	/**
	 * Constructor.
	 *
	 * @param itemSelectionWriter the item selection writer
	 * @param itemsUriBuilderFactory the items uri builder factory
	 */
	@Inject
	public SelectItemDefinitionOptionValueChoiceCommandImpl(
			@Named("itemSelectionWriter")
			final ItemSelectionWriter itemSelectionWriter,
			@Named("itemsUriBuilderFactory")
			final ItemsUriBuilderFactory itemsUriBuilderFactory) {

		this.itemSelectionWriter = itemSelectionWriter;
		this.itemsUriBuilderFactory = itemsUriBuilderFactory;
	}


	@Override
	public ExecutionResult<ResourceState<ResourceEntity>> execute() {

		String newItemId = Assign.ifSuccessful(itemSelectionWriter.saveConfigurationSelection(scope, itemId, optionId, valueId));

		String itemUri = itemsUriBuilderFactory.get()
				.setItemId(newItemId)
				.setScope(scope)
				.build();

		return ExecutionResultFactory.createCreateOK(itemUri, true);
	}

	/**
	 * Builder.
	 */
	@Named("selectItemDefinitionOptionValueChoiceCommandBuilder")
	public static class BuilderImpl implements SelectItemDefinitionOptionValueChoiceCommand.Builder {
		private final SelectItemDefinitionOptionValueChoiceCommandImpl command;

		/**
		 * Constructor.
		 *
		 * @param command the command
		 */
		@Inject
		public BuilderImpl(final SelectItemDefinitionOptionValueChoiceCommandImpl command) {
			this.command = command;
		}

		@Override
		public SelectItemDefinitionOptionValueChoiceCommand build() {
			assert command.scope != null : "Scope ID not set";
			assert command.itemId != null : "Item ID not set";
			assert command.optionId != null : "Option ID not set";
			assert command.valueId != null : "Value ID not set";
			return command;
		}

		@Override
		public Builder setScope(final String scope) {
			command.scope = scope;
			return this;
		}

		@Override
		public Builder setItemId(final String itemId) {
			command.itemId = itemId;
			return this;
		}

		@Override
		public Builder setOptionId(final String optionId) {
			command.optionId = optionId;
			return this;
		}

		@Override
		public Builder setValueId(final String valueId) {
			command.valueId = valueId;
			return this;
		}
	}
}
