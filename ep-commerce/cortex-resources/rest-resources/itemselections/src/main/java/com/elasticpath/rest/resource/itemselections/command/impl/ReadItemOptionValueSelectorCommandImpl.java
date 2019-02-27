/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemselections.command.impl;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.controls.SelectorEntity;
import com.elasticpath.rest.resource.itemselections.ItemSelectionLookup;
import com.elasticpath.rest.resource.itemselections.command.ReadItemOptionValueSelectorCommand;
import com.elasticpath.rest.schema.ResourceState;


/**
 * Implements {@link ReadItemOptionValueSelectorCommand}.
 */
@Named
public final class ReadItemOptionValueSelectorCommandImpl implements ReadItemOptionValueSelectorCommand {

	private final ItemSelectionLookup itemSelectionLookup;

	private String scope;
	private String itemId;
	private String optionId;


	/**
	 * Default constructor.
	 *
	 * @param itemSelectionLookup the item definition lookup
	 */
	@Inject
	public ReadItemOptionValueSelectorCommandImpl(
			@Named("itemSelectionLookup")
			final ItemSelectionLookup itemSelectionLookup) {

		this.itemSelectionLookup = itemSelectionLookup;
	}


	@Override
	public ExecutionResult<ResourceState<SelectorEntity>> execute() {
		return itemSelectionLookup.getOptionValueSelector(scope, itemId, optionId);
	}


	/**
	 * Constructs a {@link ReadItemOptionValueSelectorCommand}.
	 */
	@Named("readItemOptionValueSelectorCommandBuilder")
	static class BuilderImpl implements Builder {

		private final ReadItemOptionValueSelectorCommandImpl command;

		/**
		 * Default constructor.
		 *
		 * @param command the command
		 */
		@Inject
		BuilderImpl(final ReadItemOptionValueSelectorCommandImpl command) {
			this.command = command;
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
		public ReadItemOptionValueSelectorCommand build() {
			assert command.itemId != null : "Item id must be set.";
			assert command.optionId != null : "Option id must be set.";
			assert command.scope != null : "Scope must be set.";
			return command;
		}
	}
}
