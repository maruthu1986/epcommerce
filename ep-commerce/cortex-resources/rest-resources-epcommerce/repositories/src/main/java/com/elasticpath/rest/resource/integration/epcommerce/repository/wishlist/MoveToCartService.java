/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist;

import io.reactivex.Single;

import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.definition.carts.LineItemIdentifier;
import com.elasticpath.rest.definition.wishlists.WishlistLineItemIdentifier;
import com.elasticpath.rest.form.SubmitResult;

/**
 * Service interface for moving an item from favorites to cart.
 */
public interface MoveToCartService {

	/**
	 * Move an item from favorites to cart.
	 *
	 * @param wishlistLineItemIdentifier the favorites line item identifier
	 * @param lineItemEntity the line item entity
	 * @return the line item identifier
	 */
	Single<SubmitResult<LineItemIdentifier>> move(WishlistLineItemIdentifier wishlistLineItemIdentifier, LineItemEntity lineItemEntity);
}
