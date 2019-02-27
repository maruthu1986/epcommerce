/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.schema.uri;

/**
 * Builds a URI to items.
 */
public interface ItemsUriBuilder extends ScopedUriBuilder<ItemsUriBuilder> {

	/**
	 * Set the item ID.
	 *
	 * @param itemId item id
	 * @return the builder
	 */
	ItemsUriBuilder setItemId(String itemId);

	/**
	 * Set the item ID in raw decoded form.
	 *
	 * @param decodedItemId the raw item id
	 * @return the builder
	 * @deprecated use {@link #setItemId(String)} instead.
	 */
	@Deprecated
	default ItemsUriBuilder setDecodedItemId(String decodedItemId) {
		return setItemId(decodedItemId);
	}
}
