/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.schema.uri;

/**
 * Builds an URI for item definitions option value.
 */
public interface ItemDefinitionsOptionValueUriBuilder extends ScopedUriBuilder<ItemDefinitionsOptionValueUriBuilder> {

	/**
	 * Sets the item id.
	 *
	 * @param itemId the item id
	 * @return the item definitions uri builder
	 */
	ItemDefinitionsOptionValueUriBuilder setItemId(String itemId);

	/**
	 * Sets the option id.
	 *
	 * @param optionId the option id
	 * @return the item definitions uri builder
	 */
	ItemDefinitionsOptionValueUriBuilder setOptionId(String optionId);

	/**
	 * Sets the value id.
	 *
	 * @param valueId the value id
	 * @return the item definitions uri builder
	 */
	ItemDefinitionsOptionValueUriBuilder setValueId(String valueId);
}
