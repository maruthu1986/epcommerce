/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.schema.uri;

/**
 * Builds a URI for an item definition.
 */
public interface ItemDefinitionsUriBuilder extends ScopedUriBuilder<ItemDefinitionsUriBuilder> {
	/**
	 * Sets the item id.
	 *
	 * @param itemId the item id
	 * @return the item definitions uri builder
	 */
	ItemDefinitionsUriBuilder setItemId(String itemId);
}
