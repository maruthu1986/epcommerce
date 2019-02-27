/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.schema.uri;

/**
 * Builder for URIs to specific stock resources.
 */
public interface StockUriBuilder extends ScopedUriBuilder<StockUriBuilder> {

	/**
	 * Sets the itemId of the Item with which the Stock is related.
	 * 
	 * @param itemId the itemId
	 * @return this {@link StockUriBuilder}
	 */
	StockUriBuilder setItemId(String itemId);

}
