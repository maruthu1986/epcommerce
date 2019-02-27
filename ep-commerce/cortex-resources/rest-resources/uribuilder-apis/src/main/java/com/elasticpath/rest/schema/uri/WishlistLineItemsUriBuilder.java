/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.schema.uri;

/**
 * Builds a URI to favorites lineitems.
 */
public interface WishlistLineItemsUriBuilder extends ReadFromOtherUriBuilder<WishlistLineItemsUriBuilder> {
	/**
	 * Set the lineitem ID.
	 *
	 * @param lineItemId lineitem id
	 * @return the builder
	 */
	WishlistLineItemsUriBuilder setLineItemId(String lineItemId);
}
