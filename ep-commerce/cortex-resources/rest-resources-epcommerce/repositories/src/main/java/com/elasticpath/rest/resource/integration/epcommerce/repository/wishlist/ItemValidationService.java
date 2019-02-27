/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist;

import io.reactivex.Observable;

import com.elasticpath.rest.advise.Message;
import com.elasticpath.rest.definition.wishlists.WishlistLineItemIdentifier;

/**
 * Validation service for wishlists.
 */
public interface ItemValidationService {

	/**
	 * Check if the item is purchasable.
	 *
	 * @param wishlistLineItemIdentifier wishlistLineItemIdentifier
	 * @return the execution result with the boolean result
	 */
	Observable<Message> isItemPurchasable(WishlistLineItemIdentifier wishlistLineItemIdentifier);

}
