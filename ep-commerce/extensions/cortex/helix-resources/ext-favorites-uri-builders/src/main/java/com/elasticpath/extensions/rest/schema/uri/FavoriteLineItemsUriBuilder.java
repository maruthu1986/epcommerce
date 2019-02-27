/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.extensions.rest.schema.uri;

import com.elasticpath.rest.schema.uri.ReadFromOtherUriBuilder;

/**
 * Builds a URI to favorites lineitems.
 */
public interface FavoriteLineItemsUriBuilder extends ReadFromOtherUriBuilder<FavoriteLineItemsUriBuilder> {
    /**
     * Set the lineitem ID.
     *
     * @param lineItemId lineitem id
     * @return the builder
     */
    FavoriteLineItemsUriBuilder setLineItemId(String lineItemId);
}
