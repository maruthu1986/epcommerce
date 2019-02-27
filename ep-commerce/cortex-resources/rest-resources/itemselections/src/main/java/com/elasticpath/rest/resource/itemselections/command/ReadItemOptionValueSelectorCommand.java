/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemselections.command;

import com.elasticpath.rest.command.Command;
import com.elasticpath.rest.definition.controls.SelectorEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Command to read the item option value selector.
 */
public interface ReadItemOptionValueSelectorCommand extends Command<ResourceState<SelectorEntity>> {

	/**
	 * Constructs a {@link ReadItemOptionValueSelectorCommand}.
	 */
	interface Builder extends Command.Builder<ReadItemOptionValueSelectorCommand> {

		/**
		 * Sets the scope.
		 *
		 * @param scope the scope
		 * @return the builder
		 */
		Builder setScope(String scope);

		/**
		 * Sets the item id.
		 *
		 * @param itemId the item id
		 * @return the builder
		 */
		Builder setItemId(String itemId);

		/**
		 * Sets the option id.
		 *
		 * @param optionId the option id
		 * @return the builder
		 */
		Builder setOptionId(String optionId);
	}
}
