/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.schema.uri;

/**
 * Builds an URI for item definitions option.
 */
public interface ItemDefinitionsOptionUriBuilder extends ScopedUriBuilder<ItemDefinitionsOptionUriBuilder> {

	/**
	 * Sets the item id.
	 *
	 * @param itemId the item id
	 * @return the item definitions uri builder
	 */
	ItemDefinitionsOptionUriBuilder setItemId(String itemId);

	/**
	 * Sets the option id.
	 *
	 * @param optionId the option id
	 * @return the item definitions uri builder
	 */
	ItemDefinitionsOptionUriBuilder setOptionId(String optionId);
}
