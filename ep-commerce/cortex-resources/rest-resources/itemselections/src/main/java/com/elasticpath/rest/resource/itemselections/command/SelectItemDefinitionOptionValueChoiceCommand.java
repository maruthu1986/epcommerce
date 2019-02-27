/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemselections.command;

import com.elasticpath.rest.command.Command;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * The Select Item Definition Option Value Choice Command.
 */
public interface SelectItemDefinitionOptionValueChoiceCommand extends Command<ResourceState<ResourceEntity>> {

	/**
	 * The Builder.
	 */
	interface Builder extends Command.Builder<SelectItemDefinitionOptionValueChoiceCommand> {

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

		/**
		 * Sets the value id.
		 *
		 * @param valueId the value id
		 * @return the builder
		 */
		Builder setValueId(String valueId);
	}
}
